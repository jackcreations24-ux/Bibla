package com.zoutiw.bibla.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import com.zoutiw.bibla.MainActivity
import com.zoutiw.bibla.R
import com.zoutiw.bibla.data.AppDatabase
import com.zoutiw.bibla.data.NotificationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("user_settings_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("pref_notifications", true)
        if (!notificationsEnabled) {
            return Result.success()
        }

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastNotifDate = prefs.getString("last_daily_reminder_shown_date", null)

        // Only show and save daily reminder ONCE per calendar day
        if (lastNotifDate != todayDate) {
            showNotification()
            saveNotificationToDb()
            prefs.edit().putString("last_daily_reminder_shown_date", todayDate).apply()
        }

        syncFirebaseAnnouncementsInBackground(prefs)
        return Result.success()
    }

    private suspend fun syncFirebaseAnnouncementsInBackground(prefs: android.content.SharedPreferences) {
        try {
            val client = okhttp3.OkHttpClient()
            val firebaseUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/announcements.json",
                "https://bib-laht.firebaseio.com/announcements.json"
            )
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "bible-db"
            ).fallbackToDestructiveMigration().build()

            val shownSet = prefs.getStringSet("shown_announcements_keys", emptySet())?.toMutableSet() ?: mutableSetOf()

            for (url in firebaseUrls) {
                try {
                    val req = okhttp3.Request.Builder().url(url).get().build()
                    val resp = client.newCall(req).execute()
                    val body = resp.body?.string() ?: ""
                    resp.close()
                    if (body.isNotBlank() && body != "null") {
                        val trimmed = body.trim()
                        val newItems = mutableListOf<Pair<String, String>>()
                        if (trimmed.startsWith("{")) {
                            val obj = org.json.JSONObject(trimmed)
                            val keys = obj.keys()
                            while (keys.hasNext()) {
                                val k = keys.next()
                                val item = obj.optJSONObject(k) ?: continue
                                val t = item.optString("title", "").trim()
                                val m = item.optString("message", "").trim()
                                if (t.isNotBlank() && m.isNotBlank()) newItems.add(t to m)
                            }
                        } else if (trimmed.startsWith("[")) {
                            val arr = org.json.JSONArray(trimmed)
                            for (i in 0 until arr.length()) {
                                val item = arr.optJSONObject(i) ?: continue
                                val t = item.optString("title", "").trim()
                                val m = item.optString("message", "").trim()
                                if (t.isNotBlank() && m.isNotBlank()) newItems.add(t to m)
                            }
                        }

                        for ((title, msg) in newItems) {
                            val notifKey = "ann_${title.hashCode()}_${msg.hashCode()}"
                            val alreadyInDb = db.notificationDao().hasNotification(title, msg) > 0
                            val alreadyShown = shownSet.contains(notifKey)

                            if (!alreadyInDb) {
                                db.notificationDao().insertNotification(
                                    NotificationItem(title = title, message = msg)
                                )
                            }

                            if (!alreadyShown) {
                                showAnnouncementNotification(title, msg)
                                shownSet.add(notifKey)
                                prefs.edit().putStringSet("shown_announcements_keys", shownSet).apply()
                            }
                        }
                        if (newItems.isNotEmpty()) break
                    }
                } catch (e: Exception) {
                    // Next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showAnnouncementNotification(title: String, message: String) {
        try {
            val channelId = "daily_reminder_channel"
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Bib La Rapèl ak Anons", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "notifications")
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val notifKey = "ann_${title.hashCode()}_${message.hashCode()}"
            val notifId = 2000 + Math.abs(notifKey.hashCode() % 10000)

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                notifId,
                intent,
                pendingIntentFlags
            )

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification_bib_la)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(notifId, notification)
                }
            } else {
                notificationManager.notify(notifId, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotification() {
        try {
            val channelId = "daily_reading_reminder"
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Bib la - Rapèl Lekti", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            val prefs = applicationContext.getSharedPreferences("user_settings_prefs", Context.MODE_PRIVATE)
            val activePlanId = prefs.getString("pref_active_reading_plan", "full_bible_365") ?: "full_bible_365"
            val plan = com.zoutiw.bibla.data.ReadingPlanRepository.getPlanById(activePlanId)
            val completedDays = prefs.getStringSet("pref_plan_completed_$activePlanId", emptySet())
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
            val nextDayNum = (1..plan.totalDays).firstOrNull { !completedDays.contains(it) } ?: 1
            val dayPlan = plan.days.getOrNull(nextDayNum - 1)
            val passageText = dayPlan?.passages?.joinToString(", ") { it.displayReference } ?: "Lekti jodi a"

            val title = "Bib La • Rapèl Lekti"
            val message = "${plan.title} (Jou $nextDayNum): $passageText"

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "reading_plans")
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                1001,
                intent,
                pendingIntentFlags
            )

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification_bib_la)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$message\nKlike la a pou w li pasaj jodi a epi make pwogrè ou!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(1001, notification)
                }
            } else {
                notificationManager.notify(1001, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun saveNotificationToDb() {
        try {
            val prefs = applicationContext.getSharedPreferences("user_settings_prefs", Context.MODE_PRIVATE)
            val activePlanId = prefs.getString("pref_active_reading_plan", "full_bible_365") ?: "full_bible_365"
            val plan = com.zoutiw.bibla.data.ReadingPlanRepository.getPlanById(activePlanId)
            val completedDays = prefs.getStringSet("pref_plan_completed_$activePlanId", emptySet())
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
            val nextDayNum = (1..plan.totalDays).firstOrNull { !completedDays.contains(it) } ?: 1
            val dayPlan = plan.days.getOrNull(nextDayNum - 1)
            val passageText = dayPlan?.passages?.joinToString(", ") { it.displayReference } ?: "Lekti jodi a"

            val title = "Bib La • Rapèl Lekti"
            val message = "${plan.title} (Jou $nextDayNum): $passageText"

            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "bible-db"
            ).fallbackToDestructiveMigration().build()

            if (db.notificationDao().hasNotification(title, message) == 0) {
                db.notificationDao().insertNotification(
                    NotificationItem(
                        title = title,
                        message = message
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

