package com.zoutiw.bibla.data

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class BibleRepository(private val db: AppDatabase) {
    private val bibleDao = db.bibleDao()
    private val noteDao = db.noteDao()
    private val progressDao = db.progressDao()
    private val dailyVerseDao = db.dailyVerseDao()
    private val notificationDao = db.notificationDao()

    fun getAllNotifications() = notificationDao.getAllNotifications()

    suspend fun addNotification(title: String, message: String) {
        if (notificationDao.hasNotification(title, message) == 0) {
            notificationDao.insertNotification(NotificationItem(title = title, message = message))
        }
    }

    suspend fun hasNotification(title: String, message: String): Boolean {
        return notificationDao.hasNotification(title, message) > 0
    }

    suspend fun clearAllNotifications() {
        notificationDao.clearAllNotifications()
    }

    suspend fun deleteNotification(id: Long) {
        notificationDao.deleteNotification(id)
    }

    fun getVerses(book: String, chapter: Int) = bibleDao.getVerses(book, chapter)
    fun search(query: String) = bibleDao.searchVerses(query)
    suspend fun getExactVerse(book: String, chapter: Int, verseNumber: Int) = bibleDao.getExactVerse(book, chapter, verseNumber)
    suspend fun getVersesForChapterDirect(book: String, chapter: Int) = bibleDao.getVersesForChapterDirect(book, chapter)
    fun getBooks() = bibleDao.getBooks()
    fun getChapters(book: String) = bibleDao.getChapters(book)

    val progress = progressDao.getProgress()
    suspend fun saveProgress(book: String, chapter: Int, verse: Int) {
        progressDao.saveProgress(UserProgress(book = book, chapter = chapter, verseNumber = verse))
    }

    fun getNotes(verseId: Long) = noteDao.getNotesForVerse(verseId)
    fun getAllNotes() = noteDao.getAllNotes()
    suspend fun getNoteById(id: Long) = noteDao.getNoteById(id)
    suspend fun addNote(verseId: Long, content: String) {
        noteDao.insertNote(Note(verseId = verseId, content = content))
    }
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun toggleBookmark(verse: Verse, color: Int = 0) {
        if (verse.isBookmarked && color == 0) {
            // Simple toggle off if no color specified and it was on
            bibleDao.updateVerse(verse.copy(isBookmarked = false, bookmarkColor = 0))
        } else if (verse.isBookmarked && verse.bookmarkColor == color) {
            // Toggle off if same color
            bibleDao.updateVerse(verse.copy(isBookmarked = false, bookmarkColor = 0))
        } else {
            // Set bookmark with color
            bibleDao.updateVerse(verse.copy(isBookmarked = true, bookmarkColor = color))
        }
    }

    fun getBookmarkedVerses() = bibleDao.getBookmarkedVerses()
    suspend fun getVerseById(id: Long) = bibleDao.getVerseById(id)

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun getDailyVerse(): Verse? {
        val calendar = Calendar.getInstance()
        val localDaysSinceEpoch = ((calendar.timeInMillis + calendar.timeZone.getOffset(calendar.timeInMillis)) / (1000 * 60 * 60 * 24L)).toInt()
        val count = bibleDao.getVerseCount()
        if (count <= 0) return null

        val offset = ((localDaysSinceEpoch.toLong() * 2654435761L and 0x7FFFFFFF) % count).toInt()
        val globalVerse = bibleDao.getVerseAtOffset(offset) ?: bibleDao.getVerseById(1)
        if (globalVerse != null) {
            dailyVerseDao.saveDailyVerse(DailyVerse(verseId = globalVerse.id, date = localDaysSinceEpoch.toLong()))
        }
        return globalVerse
    }

    suspend fun refreshDailyVerse(): Verse? {
        return getDailyVerse()
    }

    private fun sanitizeText(text: String): String {
        return text
            .replace(Regex("(?i)\\s*(Jenèz|Egzòd|Levitik|Nonm|Nòm|Detewonòm|Jozye|Jij|Rit|1\\s*Samyèl|2\\s*Samyèl|1\\s*Wa|2\\s*Wa|1\\s*Kwonik|2\\s*Kwonik|1\\s*Istwa|2\\s*Istwa|Istwa|Esdras|Ezra|Neemi|Nehemiah|Estè|Esther|Jòb|Job|Sòm|Psalms|Pwovèb|Proverbs|Eklezyas|Ecclesiastes|Kantik|Song\\s*of\\s*Solomon|Chante\\s*Salomon|Ezayi|Isaiah|Jeremi|Jeremiah|Lamantasyon|Lamentasyon|Lamentations|Ezekyèl|Ezekiel|Danyèl|Daniel|Oze|Hosea|Jowèl|Joel|Amòs|Amos|Abdyas|Obadiah|Jonas|Jonah|Miche|Mika|Micah|Naoum|Nahum|Abakouk|Abakik|Habakkuk|Sofoni|Zephaniah|Aje|Agée|Haggai|Zakari|Zacharie|Zechariah|Makachi|Malaki|Malachi|Malachie|Matye|Matthew|Matthieu|Mak|Mark|Marc|Lik|Luke|Luc|Jan|John|Jean|Travay|Akt|Acts|Actes|Women|Romans|Romains|1\\s*Korentyen|1\\s*Corinthians|1\\s*Corinthiens|2\\s*Korentyen|2\\s*Corinthians|2\\s*Corinthiens|Galat|Galasi|Galatians|Galates|Efèzyen|Efèz|Ephesians|Éphésiens|Filipyen|Filip|Philippians|Philippiens|Kolosyen|Kolos|Colossians|Colossiens|1\\s*Tesalonisyen|2\\s*Tesalonisyen|1\\s*Thessalonians|2\\s*Thessalonians|1\\s*Thessaloniciens|2\\s*Thessaloniciens|1\\s*Timote|1\\s*Timothy|1\\s*Timothée|2\\s*Timote|2\\s*Timothy|2\\s*Timothée|Tit|Titus|Tite|Filemon|Philemon|Philémon|Ebre|Hebrews|Hébreux|Jak|James|Jacques|1\\s*Pyè|2\\s*Pyè|1\\s*Peter|2\\s*Peter|1\\s*Pierre|2\\s*Pierre|1\\s*Jan|2\\s*Jan|3\\s*Jan|1\\s*John|2\\s*John|3\\s*John|1\\s*Jean|2\\s*Jean|3\\s*Jean|Jid|Jude|Revelasyon|Revelation|Apocalypse)\\s*<\\s*\\d+\\s*>\\s*Public\\s*Domain"), "")
            .replace(Regex("(?i)\\s*Public\\s*Domain"), "")
            .replace(Regex("(?i)\\s*(Jenèz|Egzòd|Levitik|Nonm|Nòm|Detewonòm|Jozye|Jij|Rit|1\\s*Samyèl|2\\s*Samyèl|1\\s*Wa|2\\s*Wa|1\\s*Kwonik|2\\s*Kwonik|1\\s*Istwa|2\\s*Istwa|Istwa|Esdras|Ezra|Neemi|Nehemiah|Estè|Esther|Jòb|Job|Sòm|Psalms|Pwovèb|Proverbs|Eklezyas|Ecclesiastes|Kantik|Song\\s*of\\s*Solomon|Chante\\s*Salomon|Ezayi|Isaiah|Jeremi|Jeremiah|Lamantasyon|Lamentasyon|Lamentations|Ezekyèl|Ezekiel|Danyèl|Daniel|Oze|Hosea|Jowèl|Joel|Amòs|Amos|Abdyas|Obadiah|Jonas|Jonah|Miche|Mika|Micah|Naoum|Nahum|Abakouk|Abakik|Habakkuk|Sofoni|Zephaniah|Aje|Agée|Haggai|Zakari|Zacharie|Zechariah|Makachi|Malaki|Malachi|Malachie|Matye|Matthew|Matthieu|Mak|Mark|Marc|Lik|Luke|Luc|Jan|John|Jean|Travay|Akt|Acts|Actes|Women|Romans|Romains|1\\s*Korentyen|1\\s*Corinthians|1\\s*Corinthiens|2\\s*Korentyen|2\\s*Corinthians|2\\s*Corinthiens|Galat|Galasi|Galatians|Galates|Efèzyen|Efèz|Ephesians|Éphésiens|Filipyen|Filip|Philippians|Philippiens|Kolosyen|Kolos|Colossians|Colossiens|1\\s*Tesalonisyen|2\\s*Tesalonisyen|1\\s*Thessalonians|2\\s*Thessalonians|1\\s*Thessaloniciens|2\\s*Thessaloniciens|1\\s*Timote|1\\s*Timothy|1\\s*Timothée|2\\s*Timote|2\\s*Timothy|2\\s*Timothée|Tit|Titus|Tite|Filemon|Philemon|Philémon|Ebre|Hebrews|Hébreux|Jak|James|Jacques|1\\s*Pyè|2\\s*Pyè|1\\s*Peter|2\\s*Peter|1\\s*Pierre|2\\s*Pierre|1\\s*Jan|2\\s*Jan|3\\s*Jan|1\\s*John|2\\s*John|3\\s*John|1\\s*Jean|2\\s*Jean|3\\s*Jean|Jid|Jude|Revelasyon|Revelation|Apocalypse)\\s*<\\s*\\d+\\s*>"), "")
            .replace(Regex("(?i)\\s*Resansman\\s*<\\s*\\d+\\s*>"), "")
            .replace(Regex("(?i)\\s*(Deteronòm|Detewonòm|Jozye|Jij|Rit|1\\s*Samyèl|2\\s*Samyèl|1\\s*Wa|2\\s*Wa|1\\s*Kwonik|2\\s*Kwonik|1\\s*Istwa|2\\s*Istwa|Istwa|Esdras|Ezra|Neemi|Nehemiah|Estè|Esther|Jòb|Job|Sòm|Psalms|Pwovèb|Proverbs|Eklezyas|Ecclesiastes|Kantik|Song\\s*of\\s*Solomon|Chante\\s*Salomon|Ezayi|Isaiah|Jeremi|Jeremiah|Lamantasyon|Lamentasyon|Lamentations|Ezekyèl|Ezekiel|Danyèl|Daniel|Oze|Hosea|Jowèl|Joel|Amòs|Amos|Abdyas|Obadiah|Jonas|Jonah|Miche|Mika|Micah|Naoum|Nahum|Abakouk|Abakik|Habakkuk|Sofoni|Zephaniah|Aje|Agée|Haggai|Zakari|Zacharie|Zechariah|Makachi|Malaki|Malachi|Malachie|Matye|Matthew|Matthieu|Mak|Mark|Marc|Lik|Luke|Luc|Jan|John|Jean|Travay|Akt|Acts|Actes|Women|Romans|Romains|1\\s*Korentyen|1\\s*Corinthians|1\\s*Corinthiens|2\\s*Korentyen|2\\s*Corinthians|2\\s*Corinthiens|Galat|Galasi|Galatians|Galates|Efèzyen|Efèz|Ephesians|Éphésiens|Filipyen|Filip|Philippians|Philippiens|Kolosyen|Kolos|Colossians|Colossiens|1\\s*Tesalonisyen|2\\s*Tesalonisyen|1\\s*Thessalonians|2\\s*Thessalonians|1\\s*Thessaloniciens|2\\s*Thessaloniciens|1\\s*Timote|1\\s*Timothy|1\\s*Timothée|2\\s*Timote|2\\s*Timothy|2\\s*Timothée|Tit|Titus|Tite|Filemon|Philemon|Philémon|Ebre|Hebrews|Hébreux|Jak|James|Jacques|1\\s*Pyè|2\\s*Pyè|1\\s*Peter|2\\s*Peter|1\\s*Pierre|2\\s*Pierre|1\\s*Jan|2\\s*Jan|3\\s*Jan|1\\s*John|2\\s*John|3\\s*John|1\\s*Jean|2\\s*Jean|3\\s*Jean|Jid|Jude|Revelasyon|Revelation|Apocalypse)$"), "")
            .replace(Regex("\\(0+(\\d+)"), "($1")
            .trim()
    }

    suspend fun prepopulateIfNeeded() = withContext(Dispatchers.IO) {
        val count = bibleDao.getVerseCount()
        val badCount = if (count > 0) bibleDao.getBadVerseCount() else 0
        val sosCount = if (count > 0) bibleDao.getSongOfSolomonCount() else 0
        val lamCount = if (count > 0) bibleDao.getLamentationsCount() else 0
        val ezeCount = if (count > 0) bibleDao.getEzekielCount() else 0
        val danCount = if (count > 0) bibleDao.getDanielCount() else 0
        val hosCount = if (count > 0) bibleDao.getHoseaCount() else 0
        val joeCount = if (count > 0) bibleDao.getJoelCount() else 0
        val amoCount = if (count > 0) bibleDao.getAmosCount() else 0
        val obaCount = if (count > 0) bibleDao.getObadiahCount() else 0
        val jonCount = if (count > 0) bibleDao.getJonahCount() else 0
        val micCount = if (count > 0) bibleDao.getMicahCount() else 0
        val nahCount = if (count > 0) bibleDao.getNahumCount() else 0
        val habCount = if (count > 0) bibleDao.getHabakkukCount() else 0
        val zepCount = if (count > 0) bibleDao.getZephaniahCount() else 0
        val hagCount = if (count > 0) bibleDao.getHaggaiCount() else 0
        val zecCount = if (count > 0) bibleDao.getZechariahCount() else 0
        val malCount = if (count > 0) bibleDao.getMalachiCount() else 0
        val matCount = if (count > 0) bibleDao.getMatthewCount() else 0
        val marCount = if (count > 0) bibleDao.getMarkCount() else 0
        val lukCount = if (count > 0) bibleDao.getLukeCount() else 0
        val jhnCount = if (count > 0) bibleDao.getJohnCount() else 0
        val actCount = if (count > 0) bibleDao.getActsCount() else 0
        val romCount = if (count > 0) bibleDao.getRomansCount() else 0
        val cor1Count = if (count > 0) bibleDao.getFirstCorinthiansCount() else 0
        val cor2Count = if (count > 0) bibleDao.getSecondCorinthiansCount() else 0
        val galCount = if (count > 0) bibleDao.getGalatiansCount() else 0
        val ephCount = if (count > 0) bibleDao.getEphesiansCount() else 0
        val phpCount = if (count > 0) bibleDao.getPhilippiansCount() else 0
        val colCount = if (count > 0) bibleDao.getColossiansCount() else 0
        val th1Count = if (count > 0) bibleDao.getFirstThessaloniansCount() else 0
        val th2Count = if (count > 0) bibleDao.getSecondThessaloniansCount() else 0
        val tim1Count = if (count > 0) bibleDao.getFirstTimothyCount() else 0
        val tim2Count = if (count > 0) bibleDao.getSecondTimothyCount() else 0
        val titCount = if (count > 0) bibleDao.getTitusCount() else 0
        val phmCount = if (count > 0) bibleDao.getPhilemonCount() else 0
        val hebCount = if (count > 0) bibleDao.getHebrewsCount() else 0
        val jasCount = if (count > 0) bibleDao.getJamesCount() else 0
        val pet1Count = if (count > 0) bibleDao.getFirstPeterCount() else 0
        val pet2Count = if (count > 0) bibleDao.getSecondPeterCount() else 0
        val jhn1Count = if (count > 0) bibleDao.getFirstJohnCount() else 0
        val jhn2Count = if (count > 0) bibleDao.getSecondJohnCount() else 0
        val jhn3Count = if (count > 0) bibleDao.getThirdJohnCount() else 0
        val judCount = if (count > 0) bibleDao.getJudeCount() else 0
        val revCount = if (count > 0) bibleDao.getRevelationCount() else 0

        if (count < 31102 || badCount > 0 || sosCount < 117 || lamCount < 154 || ezeCount < 1273 || danCount < 357 || hosCount < 197 || joeCount < 73 || amoCount < 146 || obaCount < 21 || jonCount < 48 || micCount < 105 || nahCount < 47 || habCount < 56 || zepCount < 53 || hagCount < 38 || zecCount < 211 || malCount < 55 || matCount < 1071 || marCount < 678 || lukCount < 1151 || jhnCount < 879 || actCount < 1007 || romCount < 433 || cor1Count < 437 || cor2Count < 257 || galCount < 149 || ephCount < 155 || phpCount < 104 || colCount < 95 || th1Count < 89 || th2Count < 47 || tim1Count < 113 || tim2Count < 83 || titCount < 46 || phmCount < 25 || hebCount < 303 || jasCount < 108 || pet1Count < 105 || pet2Count < 61 || jhn1Count < 105 || jhn2Count < 13 || jhn3Count < 14 || judCount < 25 || revCount < 404) {
            val allBookLists: List<List<Verse>> = listOf(
                BibleData.genesisVerses, BibleData.exodusVerses, BibleData.leviticusVerses,
                BibleData.numbersVerses, BibleData.deuteronomyVerses, BibleData.joshuaVerses,
                BibleData.judgesVerses, BibleData.ruthVerses, BibleData.firstSamuelVerses,
                BibleData.secondSamuelVerses, BibleData.firstKingsVerses, BibleData.secondKingsVerses,
                BibleData.firstChroniclesVerses, BibleData.secondChroniclesVerses, BibleData.ezraVerses,
                BibleData.nehemiahVerses, BibleData.estherVerses, BibleData.jobVerses,
                BibleData.psalmsVerses, BibleData.proverbsVerses, BibleData.ecclesiastesVerses,
                BibleData.songOfSolomonVerses, BibleData.isaiahVerses, BibleData.jeremiahVerses,
                BibleData.lamentationsVerses, BibleData.ezekielVerses, BibleData.danielVerses,
                BibleData.hoseaVerses, BibleData.joelVerses, BibleData.amosVerses,
                BibleData.obadiahVerses, BibleData.jonahVerses, BibleData.micahVerses,
                BibleData.nahumVerses, BibleData.habakkukVerses, BibleData.zephaniahVerses,
                BibleData.haggaiVerses, BibleData.zechariahVerses, BibleData.malachiVerses,
                BibleData.matthewVerses, BibleData.markVerses, BibleData.lukeVerses,
                BibleData.johnVerses, BibleData.actsVerses, BibleData.romansVerses,
                BibleData.firstCorinthiansVerses, BibleData.secondCorinthiansVerses, BibleData.galatiansVerses,
                BibleData.ephesiansVerses, BibleData.philippiansVerses, BibleData.colossiansVerses,
                BibleData.firstThessaloniansVerses, BibleData.secondThessaloniansVerses, BibleData.firstTimothyVerses,
                BibleData.secondTimothyVerses, BibleData.titusVerses, BibleData.philemonVerses,
                BibleData.hebrewsVerses, BibleData.jamesVerses, BibleData.firstPeterVerses,
                BibleData.secondPeterVerses, BibleData.firstJohnVerses, BibleData.secondJohnVerses,
                BibleData.thirdJohnVerses, BibleData.judeVerses, BibleData.revelationVerses
            )

            db.withTransaction {
                bibleDao.deleteAllVerses()
                for (bookVerses in allBookLists) {
                    val cleanBook = bookVerses.map { v ->
                        val cleaned = sanitizeText(v.text)
                        if (cleaned != v.text) v.copy(text = cleaned) else v
                    }
                    bibleDao.insertVerses(cleanBook)
                }
            }
        }
    }
}

