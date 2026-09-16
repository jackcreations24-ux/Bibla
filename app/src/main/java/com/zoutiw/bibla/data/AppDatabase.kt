package com.zoutiw.bibla.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Verse::class, Note::class, UserProgress::class, DailyVerse::class, NotificationItem::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao
    abstract fun noteDao(): NoteDao
    abstract fun progressDao(): ProgressDao
    abstract fun dailyVerseDao(): DailyVerseDao
    abstract fun notificationDao(): NotificationDao
}

