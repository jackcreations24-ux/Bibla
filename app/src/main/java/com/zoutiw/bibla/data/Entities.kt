package com.zoutiw.bibla.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class Verse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String,
    val bookId: Int, // For sorting/searching
    val isBookmarked: Boolean = false,
    val bookmarkColor: Int = 0 // 0 for none, 1-5 for colors
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val verseId: Long,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Only one record
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_verse")
data class DailyVerse(
    @PrimaryKey val id: Int = 1,
    val verseId: Long,
    val date: Long // Day timestamp
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class VerseComment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val verseId: Long,
    val userName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val parentId: String? = null,
    val likes: Int = 0,
    val likedByUsers: List<String> = emptyList(),
    val reportedByUsers: List<String> = emptyList()
)

