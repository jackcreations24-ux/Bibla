package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.components.NeumorphicCard
import com.example.ui.theme.PrimaryColor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.BibleViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.BibLaTheme
import com.example.ui.theme.LightBackground
import com.example.ui.theme.DarkBackground

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notifications.DailyReminderWorker
import java.util.concurrent.TimeUnit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val pendingRoute = mutableStateOf<String?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupNotifications()
        }
    }

    private fun setupNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val target = intent?.getStringExtra("navigate_to")
            ?: intent?.extras?.getString("navigate_to")
            ?: intent?.extras?.getString("target_screen")
        if (!target.isNullOrBlank()) {
            pendingRoute.value = target
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                setupNotifications()
            }
        } else {
            setupNotifications()
        }

        setContent {
            val viewModel: BibleViewModel = viewModel()
            val systemInDarkTheme = isSystemInDarkTheme()
            val isDarkModePreference by viewModel.isDarkMode.collectAsState()
            val isDarkTheme = isDarkModePreference ?: systemInDarkTheme

            BibLaTheme(darkTheme = isDarkTheme) {
                val isVisualComfort by viewModel.isVisualComfortEnabled.collectAsState()
                val navController = rememberNavController()
                val pendingTargetRoute by pendingRoute

                LaunchedEffect(pendingTargetRoute) {
                    val route = pendingTargetRoute
                    if (!route.isNullOrEmpty()) {
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                        pendingRoute.value = null
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isVisualComfort) {
                                Modifier.drawWithContent {
                                    drawContent()
                                    drawRect(Color(0xFFFFB74D).copy(alpha = 0.12f))
                                }
                            } else Modifier
                        )
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            val currentRoute = navBackStackEntry?.destination?.route
                            if (currentRoute != "welcome") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 16.dp)
                                        .navigationBarsPadding()
                                ) {
                                    NeumorphicCard(
                                        cornerRadius = 32.dp,
                                        elevation = 8.dp,
                                        isDarkTheme = isDarkTheme,
                                        modifier = Modifier.fillMaxWidth().height(64.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val appLanguage by viewModel.appLanguage.collectAsState()
                                            val items = listOf(
                                                Triple("home", Icons.Default.Home, if (appLanguage == "fr") "Accueil" else "Akèy"),
                                                Triple("reader", Icons.Default.Book, if (appLanguage == "fr") "Lecture" else "Lekti"),
                                                Triple("notes", Icons.Default.Notes, if (appLanguage == "fr") "Notes" else "Nòt"),
                                                Triple("bookmarks", Icons.Default.Bookmark, if (appLanguage == "fr") "Favoris" else "Favori")
                                            )

                                            items.forEach { (route, icon, label) ->
                                                val isSelected = currentRoute == route || (route == "reader" && currentRoute?.startsWith("reader") == true)
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable { navController.navigate(route) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = label,
                                                        tint = if (isSelected) PrimaryColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "welcome",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("welcome") { WelcomeScreen(navController, viewModel) }
                            composable("home") { HomeScreen(navController, viewModel) }
                            composable("reader") { ReaderScreen(navController, viewModel) }
                            composable("reader/{book}/{chapter}") { backStackEntry ->
                                val book = backStackEntry.arguments?.getString("book")
                                val chapter = backStackEntry.arguments?.getString("chapter")?.toIntOrNull() ?: 1
                                ReaderScreen(navController, viewModel, initialBook = book, initialChapter = chapter)
                            }
                            composable("search") { SearchScreen(navController, viewModel) }
                            composable("notes") { NotesScreen(navController, viewModel) }
                            composable("note_detail/{noteId}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: 0L
                                com.example.ui.screens.NoteDetailScreen(navController, viewModel, id)
                            }
                            composable("bookmarks") { BookmarksScreen(navController, viewModel) }
                            composable("profile") { ProfileScreen(navController, viewModel) }
                            composable("donation") { com.example.ui.screens.DonationScreen(navController, viewModel) }
                            composable("notifications") { com.example.ui.screens.NotificationsScreen(navController, viewModel) }
                            composable("reading_plans") { com.example.ui.screens.ReadingPlansScreen(navController, viewModel) }
                            composable("admin_dashboard") { com.example.ui.screens.AdminDashboardScreen(navController, viewModel) }
                            composable("notification_detail/{notificationId}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("notificationId")?.toLongOrNull() ?: 0L
                                com.example.ui.screens.NotificationDetailScreen(navController, viewModel, id)
                            }
                            composable("book_selection") { com.example.ui.screens.BookSelectionScreen(navController, viewModel) }
                        }
                    }
                }
            }
        }
    }
}
