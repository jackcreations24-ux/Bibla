package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM verses WHERE (book = :book OR (book = 'Malaki' AND (:book = 'Malachi' OR :book = 'Makachi' OR :book = 'Malachie')) OR (book = 'Abakouk' AND :book = 'Abakik') OR (book = '1 Korentyen' AND :book = '1 Korent') OR (book = '2 Korentyen' AND :book = '2 Korent') OR (book = 'Efèzyen' AND (:book = 'Efez' OR :book = 'Efèz')) OR (book = 'Kolosyen' AND :book = 'Kolos')) AND chapter = :chapter ORDER BY verseNumber ASC")
    fun getVerses(book: String, chapter: Int): Flow<List<Verse>>

    @Query("SELECT * FROM verses WHERE id = :id")
    suspend fun getVerseById(id: Long): Verse?

    @Query("SELECT * FROM verses WHERE (book = :book OR (book = 'Malaki' AND (:book = 'Malachi' OR :book = 'Makachi' OR :book = 'Malachie')) OR (book = 'Abakouk' AND :book = 'Abakik') OR (book = '1 Korentyen' AND :book = '1 Korent') OR (book = '2 Korentyen' AND :book = '2 Korent') OR (book = 'Efèzyen' AND (:book = 'Efez' OR :book = 'Efèz')) OR (book = 'Kolosyen' AND :book = 'Kolos')) AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getExactVerse(book: String, chapter: Int, verseNumber: Int): Verse?

    @Query("SELECT * FROM verses WHERE (book = :book OR (book = 'Malaki' AND (:book = 'Malachi' OR :book = 'Makachi' OR :book = 'Malachie')) OR (book = 'Abakouk' AND :book = 'Abakik') OR (book = '1 Korentyen' AND :book = '1 Korent') OR (book = '2 Korentyen' AND :book = '2 Korent') OR (book = 'Efèzyen' AND (:book = 'Efez' OR :book = 'Efèz')) OR (book = 'Kolosyen' AND :book = 'Kolos')) AND chapter = :chapter ORDER BY verseNumber ASC")
    suspend fun getVersesForChapterDirect(book: String, chapter: Int): List<Verse>

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :query || '%' OR book LIKE '%' || :query || '%'")
    fun searchVerses(query: String): Flow<List<Verse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<Verse>)

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVerseCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE text LIKE '%Public Domain%' OR text LIKE '%<%' OR text LIKE '%(0%'")
    suspend fun getBadVerseCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Chante Salomon'")
    suspend fun getSongOfSolomonCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Lamantasyon' OR book = 'Lamentasyon'")
    suspend fun getLamentationsCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Ezekyèl'")
    suspend fun getEzekielCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Danyèl' OR book = 'Daniel'")
    suspend fun getDanielCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Oze' OR book = 'Hosea'")
    suspend fun getHoseaCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Jowèl' OR book = 'Joel'")
    suspend fun getJoelCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Amòs' OR book = 'Amos'")
    suspend fun getAmosCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Abdyas' OR book = 'Obadiah'")
    suspend fun getObadiahCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Jonas' OR book = 'Jonah'")
    suspend fun getJonahCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Miche' OR book = 'Mika' OR book = 'Micah'")
    suspend fun getMicahCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Naoum' OR book = 'Nahum'")
    suspend fun getNahumCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Abakouk' OR book = 'Abakik' OR book = 'Habakkuk'")
    suspend fun getHabakkukCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Sofoni' OR book = 'Zephaniah'")
    suspend fun getZephaniahCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Aje' OR book = 'Agée' OR book = 'Haggai'")
    suspend fun getHaggaiCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Zakari' OR book = 'Zacharie' OR book = 'Zechariah'")
    suspend fun getZechariahCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Makachi' OR book = 'Malaki' OR book = 'Malachi' OR book = 'Malachie'")
    suspend fun getMalachiCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Matye' OR book = 'Matthew' OR book = 'Matthieu'")
    suspend fun getMatthewCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Mak' OR book = 'Mark' OR book = 'Marc'")
    suspend fun getMarkCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Lik' OR book = 'Luke' OR book = 'Luc'")
    suspend fun getLukeCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Jan' OR book = 'John' OR book = 'Jean'")
    suspend fun getJohnCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Travay' OR book = 'Akt' OR book = 'Acts' OR book = 'Actes'")
    suspend fun getActsCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Women' OR book = 'Romans' OR book = 'Romains'")
    suspend fun getRomansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '1 Korentyen' OR book = '1 Corinthians' OR book = '1 Corinthiens'")
    suspend fun getFirstCorinthiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '2 Korentyen' OR book = '2 Corinthians' OR book = '2 Corinthiens'")
    suspend fun getSecondCorinthiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Galat' OR book = 'Galasi' OR book = 'Galatians' OR book = 'Galates'")
    suspend fun getGalatiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Efèzyen' OR book = 'Efèz' OR book = 'Ephesians' OR book = 'Éphésiens'")
    suspend fun getEphesiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Filipyen' OR book = 'Filip' OR book = 'Philippians' OR book = 'Philippiens'")
    suspend fun getPhilippiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Kolosyen' OR book = 'Kolos' OR book = 'Colossians' OR book = 'Colossiens'")
    suspend fun getColossiansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '1 Tesalonisyen' OR book = '1 Thessalonians' OR book = '1 Thessaloniciens'")
    suspend fun getFirstThessaloniansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '2 Tesalonisyen' OR book = '2 Thessalonians' OR book = '2 Thessaloniciens'")
    suspend fun getSecondThessaloniansCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '1 Timote' OR book = '1 Timothy' OR book = '1 Timothée'")
    suspend fun getFirstTimothyCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '2 Timote' OR book = '2 Timothy' OR book = '2 Timothée'")
    suspend fun getSecondTimothyCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Tit' OR book = 'Titus' OR book = 'Tite'")
    suspend fun getTitusCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Filemon' OR book = 'Philemon' OR book = 'Philémon'")
    suspend fun getPhilemonCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Ebre' OR book = 'Hebrews' OR book = 'Hébreux'")
    suspend fun getHebrewsCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Jak' OR book = 'James' OR book = 'Jacques'")
    suspend fun getJamesCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '1 Pyè' OR book = '1 Peter' OR book = '1 Pierre'")
    suspend fun getFirstPeterCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '2 Pyè' OR book = '2 Peter' OR book = '2 Pierre'")
    suspend fun getSecondPeterCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '1 Jan' OR book = '1 John' OR book = '1 Jean'")
    suspend fun getFirstJohnCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '2 Jan' OR book = '2 John' OR book = '2 Jean'")
    suspend fun getSecondJohnCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = '3 Jan' OR book = '3 John' OR book = '3 Jean'")
    suspend fun getThirdJohnCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Jid' OR book = 'Jude'")
    suspend fun getJudeCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE book = 'Revelasyon' OR book = 'Revelation' OR book = 'Apocalypse'")
    suspend fun getRevelationCount(): Int

    @Query("DELETE FROM verses")
    suspend fun deleteAllVerses()

    @Query("SELECT DISTINCT book FROM verses ORDER BY bookId ASC")
    fun getBooks(): Flow<List<String>>

    @Query("SELECT DISTINCT chapter FROM verses WHERE book = :book ORDER BY chapter ASC")
    fun getChapters(book: String): Flow<List<Int>>

    @Query("SELECT * FROM verses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(): Verse?

    @Query("SELECT * FROM verses LIMIT 1 OFFSET :offset")
    suspend fun getVerseAtOffset(offset: Int): Verse?

    @Update
    suspend fun updateVerse(verse: Verse)

    @Query("SELECT * FROM verses WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedVerses(): Flow<List<Verse>>
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE verseId = :verseId ORDER BY timestamp DESC")
    fun getNotesForVerse(verseId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgress)
}

@Dao
interface DailyVerseDao {
    @Query("SELECT * FROM daily_verse WHERE id = 1")
    suspend fun getDailyVerse(): DailyVerse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyVerse(dailyVerse: DailyVerse)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE title = :title AND message = :message")
    suspend fun hasNotification(title: String, message: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}
