package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.BibleRepository
import com.example.data.UserProgress
import com.example.data.Verse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class BibleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "bible-db"
    ).fallbackToDestructiveMigration().build()
    
    private val repository = BibleRepository(db)

    private val _dailyVerse = MutableStateFlow<Verse?>(null)
    val dailyVerse: StateFlow<Verse?> = _dailyVerse

    private val _dailyVerseFrench = MutableStateFlow<String?>(null)
    val dailyVerseFrench: StateFlow<String?> = _dailyVerseFrench

    val dailyVerseIsLiked = MutableStateFlow(false)
    val dailyVerseLikeCount = MutableStateFlow(142)
    val dailyVerseViewCount = MutableStateFlow(285)

    private val _comments = MutableStateFlow<List<com.example.data.VerseComment>>(emptyList())
    val comments: StateFlow<List<com.example.data.VerseComment>> = _comments.asStateFlow()

    val savedUserName = MutableStateFlow("")

    val deviceId: String by lazy {
        prefs.getString("device_uuid", null) ?: java.util.UUID.randomUUID().toString().also {
            prefs.edit().putString("device_uuid", it).apply()
        }
    }

    private val _currentBookAndChapter = MutableStateFlow<Pair<String, Int>?>(null)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentVerses: StateFlow<List<Verse>> = _currentBookAndChapter
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { (book, chapter) ->
            repository.getVerses(book, chapter)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _books = repository.getBooks().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val books: StateFlow<List<String>> = _books

    val progress = repository.progress.stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    val allNotes = repository.getAllNotes().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val bookmarks = repository.getBookmarkedVerses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    enum class SearchTestamentFilter(val label: String) {
        ALL("Tout Bib la"),
        OLD_TESTAMENT("Ansyen Testaman"),
        NEW_TESTAMENT("Nouvo Testaman")
    }

    data class ScriptureReference(
        val book: String,
        val chapter: Int,
        val verseNumber: Int? = null
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchTestamentFilter = MutableStateFlow(SearchTestamentFilter.ALL)
    val selectedBookFilter = MutableStateFlow<String?>(null)
    val isSearching = MutableStateFlow(false)

    private fun resolveCanonicalBook(rawInput: String): String? {
        val canonicalFromName = com.example.ui.util.BibleBookNames.toCanonical(rawInput)
        if (com.example.data.BibleData.allBooks.contains(canonicalFromName)) return canonicalFromName

        val clean = rawInput.trim().lowercase()
            .replace("è", "e").replace("é", "e").replace("ò", "o").replace("à", "a")
        
        return when {
            clean.startsWith("jen") || clean.startsWith("gen") -> "Jenèz"
            clean.startsWith("egz") || clean.startsWith("exo") -> "Egzòd"
            clean.startsWith("lev") -> "Levitik"
            clean.startsWith("nonm") || clean.startsWith("nom") || clean.startsWith("num") -> "Nonm"
            clean.startsWith("det") || clean.startsWith("deu") -> "Detewonòm"
            clean.startsWith("joz") || clean.startsWith("jos") -> "Jozye"
            clean.startsWith("jij") || clean.startsWith("judg") || clean.startsWith("jug") -> "Jij"
            clean.startsWith("rit") || clean.startsWith("rut") -> "Rit"
            clean.startsWith("1 sam") -> "1 Samyèl"
            clean.startsWith("2 sam") -> "2 Samyèl"
            clean.startsWith("1 wa") || clean.startsWith("1 king") || clean.startsWith("1 roi") -> "1 Wa"
            clean.startsWith("2 wa") || clean.startsWith("2 king") || clean.startsWith("2 roi") -> "2 Wa"
            clean.startsWith("1 kwon") || clean.startsWith("1 ist") || clean.startsWith("1 chr") -> "1 Kwonik"
            clean.startsWith("2 kwon") || clean.startsWith("2 ist") || clean.startsWith("2 chr") -> "2 Kwonik"
            clean.startsWith("esd") || clean.startsWith("ezr") -> "Esdras"
            clean.startsWith("neem") || clean.startsWith("neh") -> "Neemi"
            clean.startsWith("est") -> "Estè"
            clean.startsWith("job") -> "Jòb"
            clean.startsWith("som") || clean.startsWith("ps") -> "Sòm"
            clean.startsWith("pwo") || clean.startsWith("pro") -> "Pwovèb"
            clean.startsWith("ekl") || clean.startsWith("ecc") -> "Eklezyas"
            clean.startsWith("chant") || clean.startsWith("kan") || clean.startsWith("song") -> "Chante Salomon"
            clean.startsWith("ezay") || clean.startsWith("isa") || clean.startsWith("esai") -> "Ezayi"
            clean.startsWith("jer") -> "Jeremi"
            clean.startsWith("lam") -> "Lamantasyon"
            clean.startsWith("ezek") || clean.startsWith("eze") -> "Ezekyèl"
            clean.startsWith("dan") -> "Danyèl"
            clean.startsWith("oze") || clean.startsWith("hos") -> "Oze"
            clean.startsWith("jow") || clean.startsWith("joe") -> "Jowèl"
            clean.startsWith("amo") -> "Amòs"
            clean.startsWith("abd") || clean.startsWith("oba") -> "Abdyas"
            clean.startsWith("jon") -> "Jonas"
            clean.startsWith("mich") || clean.startsWith("mik") -> "Miche"
            clean.startsWith("nao") || clean.startsWith("nah") -> "Naoum"
            clean.startsWith("aba") || clean.startsWith("hab") -> "Abakouk"
            clean.startsWith("sof") || clean.startsWith("zep") -> "Sofoni"
            clean.startsWith("aje") || clean.startsWith("hag") || clean.startsWith("age") -> "Aje"
            clean.startsWith("zak") || clean.startsWith("zec") -> "Zakari"
            clean.startsWith("mal") || clean.startsWith("makach") -> "Malaki"

            clean.startsWith("mat") -> "Matye"
            clean.startsWith("mak") || clean.startsWith("mar") -> "Mak"
            clean.startsWith("lik") || clean.startsWith("luk") || clean.startsWith("luc") -> "Lik"
            clean.startsWith("1 jan") || clean.startsWith("1 joh") || clean.startsWith("1 jea") -> "1 Jan"
            clean.startsWith("2 jan") || clean.startsWith("2 joh") || clean.startsWith("2 jea") -> "2 Jan"
            clean.startsWith("3 jan") || clean.startsWith("3 joh") || clean.startsWith("3 jea") -> "3 Jan"
            clean.startsWith("jan") || clean.startsWith("joh") || clean.startsWith("jea") -> "Jan"
            clean.startsWith("trav") || clean.startsWith("akt") || clean.startsWith("act") -> "Travay"
            clean.startsWith("wom") || clean.startsWith("rom") -> "Women"
            clean.startsWith("1 kor") || clean.startsWith("1 co") -> "1 Korentyen"
            clean.startsWith("2 kor") || clean.startsWith("2 co") -> "2 Korentyen"
            clean.startsWith("gal") -> "Galat"
            clean.startsWith("efe") || clean.startsWith("eph") -> "Efèzyen"
            clean.startsWith("fil") || clean.startsWith("phi") -> "Filipyen"
            clean.startsWith("kol") || clean.startsWith("col") -> "Kolosyen"
            clean.startsWith("1 tes") || clean.startsWith("1 the") -> "1 Tesalonisyen"
            clean.startsWith("2 tes") || clean.startsWith("2 the") -> "2 Tesalonisyen"
            clean.startsWith("1 tim") -> "1 Timote"
            clean.startsWith("2 tim") -> "2 Timote"
            clean.startsWith("tit") -> "Tit"
            clean.startsWith("fil") || clean.startsWith("phi") -> "Filemon"
            clean.startsWith("ebr") || clean.startsWith("heb") -> "Ebre"
            clean.startsWith("jak") || clean.startsWith("jam") || clean.startsWith("jac") -> "Jak"
            clean.startsWith("1 pye") || clean.startsWith("1 pet") || clean.startsWith("1 pie") -> "1 Pyè"
            clean.startsWith("2 pye") || clean.startsWith("2 pet") || clean.startsWith("2 pie") -> "2 Pyè"
            clean.startsWith("jid") || clean.startsWith("jud") -> "Jid"
            clean.startsWith("rev") || clean.startsWith("apo") -> "Revelasyon"
            else -> null
        }
    }

    private fun parseScriptureReference(input: String): ScriptureReference? {
        val trimmed = input.trim()
        val pattern = Regex("""^([1-3]?\s*[A-Za-zÀ-ÿ]+)\s+(\d+)(?:\s*[:,\.]\s*(\d+))?$""")
        val match = pattern.find(trimmed) ?: return null
        val rawBook = match.groupValues[1]
        val chapter = match.groupValues[2].toIntOrNull() ?: return null
        val verseNumber = match.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }?.toIntOrNull()
        val canonicalBook = resolveCanonicalBook(rawBook) ?: return null
        return ScriptureReference(canonicalBook, chapter, verseNumber)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Verse>> = combine(
        _searchQuery.debounce(150),
        searchTestamentFilter,
        selectedBookFilter
    ) { query, testamentFilter, bookFilter ->
        Triple(query.trim(), testamentFilter, bookFilter)
    }.flatMapLatest { (query, testamentFilter, bookFilter) ->
        flow {
            if (query.isBlank() || (query.length < 2 && !query[0].isDigit())) {
                isSearching.value = false
                emit(emptyList())
                return@flow
            }
            isSearching.value = true

            val directMatches = mutableListOf<Verse>()
            val ref = parseScriptureReference(query)
            if (ref != null) {
                try {
                    if (ref.verseNumber != null) {
                        val exact = repository.getExactVerse(ref.book, ref.chapter, ref.verseNumber)
                        if (exact != null) {
                            directMatches.add(exact)
                        }
                    } else {
                        val chapterVerses = repository.getVersesForChapterDirect(ref.book, ref.chapter)
                        directMatches.addAll(chapterVerses)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val keywordResults = try {
                repository.search(query).firstOrNull() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val directIds = directMatches.map { it.id }.toSet()
            val combined = (directMatches + keywordResults.filter { it.id !in directIds })

            val filtered = combined.filter { verse ->
                val matchesBook = bookFilter == null || verse.book.equals(bookFilter, ignoreCase = true)
                val matchesTestament = when (testamentFilter) {
                    SearchTestamentFilter.ALL -> true
                    SearchTestamentFilter.OLD_TESTAMENT -> verse.book in com.example.data.BibleData.oldTestament
                    SearchTestamentFilter.NEW_TESTAMENT -> verse.book in com.example.data.BibleData.newTestament
                }
                matchesBook && matchesTestament
            }

            isSearching.value = false
            emit(filtered)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val prefs = application.getSharedPreferences("user_settings_prefs", android.content.Context.MODE_PRIVATE)

    private val _recentSearches = MutableStateFlow<List<String>>(loadRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private fun loadRecentSearches(): List<String> {
        val raw = prefs.getString("recent_searches_list", "") ?: ""
        if (raw.isBlank()) return listOf("Jan 3:16", "lapè", "renmen", "Sòm 23", "fòs")
        return raw.split("|||").filter { it.isNotBlank() }
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return
        val current = _recentSearches.value.filter { !it.equals(trimmed, ignoreCase = true) }.toMutableList()
        current.add(0, trimmed)
        val limited = current.take(12)
        _recentSearches.value = limited
        prefs.edit().putString("recent_searches_list", limited.joinToString("|||")).apply()
    }

    fun removeRecentSearch(query: String) {
        val updated = _recentSearches.value.filter { !it.equals(query, ignoreCase = true) }
        _recentSearches.value = updated
        prefs.edit().putString("recent_searches_list", updated.joinToString("|||")).apply()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        prefs.edit().remove("recent_searches_list").apply()
    }

    fun setSearchTestamentFilter(filter: SearchTestamentFilter) {
        searchTestamentFilter.value = filter
    }

    fun setSelectedBookFilter(book: String?) {
        selectedBookFilter.value = book
    }

    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (prefs.contains("pref_dark_mode")) prefs.getBoolean("pref_dark_mode", false) else null
    )
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode

    private val _textSizeMultiplier = MutableStateFlow(
        prefs.getFloat("pref_text_size", 1.2f)
    )
    val textSizeMultiplier: StateFlow<Float> = _textSizeMultiplier

    private val _fontFamilyType = MutableStateFlow(
        prefs.getString("pref_font_family", "sans_serif") ?: "sans_serif"
    )
    val fontFamilyType: StateFlow<String> = _fontFamilyType

    private val _isVisualComfortEnabled = MutableStateFlow(
        prefs.getBoolean("pref_visual_comfort", false)
    )
    val isVisualComfortEnabled: StateFlow<Boolean> = _isVisualComfortEnabled

    private val _notificationsEnabled = MutableStateFlow(
        prefs.getBoolean("pref_notifications", true)
    )
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // Note Auto-save Draft state
    private val frenchRepository = com.example.data.FrenchBibleRepository(application)

    private val _appLanguage = MutableStateFlow(
        prefs.getString("pref_app_language", com.example.ui.util.AppLanguage.HT) ?: com.example.ui.util.AppLanguage.HT
    )
    val appLanguage: StateFlow<String> = _appLanguage

    private val _bibleVersion = MutableStateFlow(
        run {
            val savedLang = prefs.getString("pref_app_language", com.example.ui.util.AppLanguage.HT) ?: com.example.ui.util.AppLanguage.HT
            val savedVersion = prefs.getString("pref_bible_version", null)
            when {
                savedVersion == com.example.ui.util.BibleVersion.BILINGUAL -> com.example.ui.util.BibleVersion.BILINGUAL
                savedLang == com.example.ui.util.AppLanguage.FR -> com.example.ui.util.BibleVersion.FRANCAIS_LSG
                savedVersion != null -> savedVersion
                else -> com.example.ui.util.BibleVersion.KREYOL
            }
        }
    )
    val bibleVersion: StateFlow<String> = _bibleVersion

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
        prefs.edit().putString("pref_app_language", lang).apply()

        // Automatically synchronize Bible version with app language
        val targetVersion = if (lang == com.example.ui.util.AppLanguage.FR) {
            com.example.ui.util.BibleVersion.FRANCAIS_LSG
        } else {
            com.example.ui.util.BibleVersion.KREYOL
        }
        setBibleVersion(targetVersion)

        // Reload daily verse translation immediately
        _dailyVerse.value?.let { daily ->
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val frMap = frenchRepository.getFrenchVersesForChapter(daily.book, daily.chapter)
                    _dailyVerseFrench.value = frMap[daily.verseNumber]
                } catch (e: Exception) {
                    _dailyVerseFrench.value = null
                }
            }
        }
    }

    fun setBibleVersion(version: String) {
        _bibleVersion.value = version
        prefs.edit().putString("pref_bible_version", version).apply()
        _currentBookAndChapter.value?.let { (book, chapter) ->
            loadFrenchVersesIfNeeded(book, chapter, version)
        }
    }

    private val _frenchVersesForCurrentChapter = MutableStateFlow<Map<Int, String>>(emptyMap())
    val frenchVersesForCurrentChapter: StateFlow<Map<Int, String>> = _frenchVersesForCurrentChapter

    fun loadFrenchVersesIfNeeded(book: String, chapter: Int, version: String = _bibleVersion.value) {
        if (version == com.example.ui.util.BibleVersion.FRANCAIS_LSG || version == com.example.ui.util.BibleVersion.BILINGUAL) {
            viewModelScope.launch {
                val verses = frenchRepository.getFrenchVersesForChapter(book, chapter)
                _frenchVersesForCurrentChapter.value = verses
            }
        }
    }

    private val _noteDraftTitle = MutableStateFlow(
        prefs.getString("draft_note_title", "") ?: ""
    )
    val noteDraftTitle: StateFlow<String> = _noteDraftTitle

    private val _noteDraftText = MutableStateFlow(
        prefs.getString("draft_note_text", "") ?: ""
    )
    val noteDraftText: StateFlow<String> = _noteDraftText

    fun updateNoteDraft(title: String, text: String) {
        _noteDraftTitle.value = title
        _noteDraftText.value = text
        prefs.edit()
            .putString("draft_note_title", title)
            .putString("draft_note_text", text)
            .apply()
    }

    fun clearNoteDraft() {
        _noteDraftTitle.value = ""
        _noteDraftText.value = ""
        prefs.edit()
            .remove("draft_note_title")
            .remove("draft_note_text")
            .apply()
    }

    val notifications = repository.getAllNotifications().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Reading Plan State & Operations ---
    private val _activeReadingPlanId = MutableStateFlow(
        prefs.getString("pref_active_reading_plan", "full_bible_365") ?: "full_bible_365"
    )
    val activeReadingPlanId: StateFlow<String> = _activeReadingPlanId

    private val _completedPlanDays = MutableStateFlow<Map<String, Set<Int>>>(loadAllCompletedPlanDays())
    val completedPlanDays: StateFlow<Map<String, Set<Int>>> = _completedPlanDays

    private val _readingPlanReminderHour = MutableStateFlow(
        prefs.getInt("pref_plan_reminder_hour", 7)
    )
    val readingPlanReminderHour: StateFlow<Int> = _readingPlanReminderHour

    private val _readingPlanReminderMinute = MutableStateFlow(
        prefs.getInt("pref_plan_reminder_minute", 0)
    )
    val readingPlanReminderMinute: StateFlow<Int> = _readingPlanReminderMinute

    private val _readingPlanReminderEnabled = MutableStateFlow(
        prefs.getBoolean("pref_plan_reminder_enabled", true)
    )
    val readingPlanReminderEnabled: StateFlow<Boolean> = _readingPlanReminderEnabled

    private val _readingStreak = MutableStateFlow(
        prefs.getInt("pref_reading_streak", calculateCurrentStreak())
    )
    val readingStreak: StateFlow<Int> = _readingStreak

    private fun loadAllCompletedPlanDays(): Map<String, Set<Int>> {
        val map = mutableMapOf<String, Set<Int>>()
        for (plan in com.example.data.ReadingPlanRepository.allPlans) {
            val set = prefs.getStringSet("pref_plan_completed_${plan.id}", emptySet())
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
            map[plan.id] = set
        }
        return map
    }

    private fun calculateCurrentStreak(): Int {
        val lastDate = prefs.getLong("pref_last_reading_date", 0L)
        if (lastDate == 0L) return 0
        val now = System.currentTimeMillis()
        val diffDays = (now - lastDate) / (1000 * 60 * 60 * 24)
        return if (diffDays <= 1) prefs.getInt("pref_reading_streak", 1) else 0
    }

    fun setActiveReadingPlan(planId: String) {
        _activeReadingPlanId.value = planId
        prefs.edit().putString("pref_active_reading_plan", planId).apply()
        scheduleReadingPlanReminderWork()
    }

    fun toggleReadingPlanDayCompleted(planId: String, dayNumber: Int) {
        val currentMap = _completedPlanDays.value.toMutableMap()
        val currentSet = currentMap[planId]?.toMutableSet() ?: mutableSetOf()
        val isNowCompleted = !currentSet.contains(dayNumber)

        if (isNowCompleted) {
            currentSet.add(dayNumber)
            // Update streak and last completion time
            val today = System.currentTimeMillis()
            val lastDate = prefs.getLong("pref_last_reading_date", 0L)
            val diffDays = if (lastDate == 0L) 0 else (today - lastDate) / (1000 * 60 * 60 * 24)
            val newStreak = if (diffDays <= 1) (_readingStreak.value + 1).coerceAtLeast(1) else 1
            _readingStreak.value = newStreak
            prefs.edit()
                .putLong("pref_last_reading_date", today)
                .putInt("pref_reading_streak", newStreak)
                .apply()
        } else {
            currentSet.remove(dayNumber)
        }

        currentMap[planId] = currentSet
        _completedPlanDays.value = currentMap

        val stringSet = currentSet.map { it.toString() }.toSet()
        prefs.edit().putStringSet("pref_plan_completed_$planId", stringSet).apply()
    }

    fun resetReadingPlanProgress(planId: String) {
        val currentMap = _completedPlanDays.value.toMutableMap()
        currentMap[planId] = emptySet()
        _completedPlanDays.value = currentMap
        prefs.edit().remove("pref_plan_completed_$planId").apply()
    }

    fun setReadingPlanReminder(enabled: Boolean, hour: Int, minute: Int) {
        _readingPlanReminderEnabled.value = enabled
        _readingPlanReminderHour.value = hour
        _readingPlanReminderMinute.value = minute
        prefs.edit()
            .putBoolean("pref_plan_reminder_enabled", enabled)
            .putInt("pref_plan_reminder_hour", hour)
            .putInt("pref_plan_reminder_minute", minute)
            .apply()
        scheduleReadingPlanReminderWork()
    }

    private fun scheduleReadingPlanReminderWork() {
        val context = getApplication<Application>().applicationContext
        if (!_readingPlanReminderEnabled.value) {
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
            return
        }

        val hour = _readingPlanReminderHour.value
        val minute = _readingPlanReminderMinute.value

        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            if (before(now)) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = target.timeInMillis - now.timeInMillis
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.notifications.DailyReminderWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("pref_notifications", enabled).apply()
        val context = getApplication<Application>().applicationContext
        if (enabled) {
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.notifications.DailyReminderWorker>(24, java.util.concurrent.TimeUnit.HOURS)
                .build()
            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_reminder",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } else {
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun sendDeveloperAnnouncement(title: String, message: String) {
        viewModelScope.launch {
            repository.addNotification(title, message)
            val context = getApplication<Application>().applicationContext
            triggerSystemNotification(context, title, message)

            // Direct Global Firebase and Cloud Broadcast sync
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val client = okhttp3.OkHttpClient()
                    val notifId = System.currentTimeMillis()

                    // 1. Push directly to Firebase Realtime Database
                    pushAnnouncementToFirebase(client, notifId, title, message)

                    // 2. Dual-redundancy syncUrl
                    val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
                    val getRequest = okhttp3.Request.Builder().url(syncUrl).get().build()
                    val response = client.newCall(getRequest).execute()
                    val responseBody = response.body?.string() ?: ""
                    response.close()

                    val existingArray = org.json.JSONArray()
                    var existingLikes = org.json.JSONObject()
                    if (responseBody.isNotBlank()) {
                        val rootObj = org.json.JSONObject(responseBody)
                        val dataObj = rootObj.optJSONObject("data")
                        if (dataObj != null) {
                            val arr = dataObj.optJSONArray("notifications")
                            if (arr != null) {
                                for (i in 0 until arr.length()) {
                                    existingArray.put(arr.get(i))
                                }
                            }
                            val likesObj = dataObj.optJSONObject("likes")
                            if (likesObj != null) {
                                existingLikes = likesObj
                            }
                        }
                    }

                    val newNotif = org.json.JSONObject().apply {
                        put("id", notifId)
                        put("title", title)
                        put("message", message)
                        put("timestamp", notifId)
                    }
                    existingArray.put(newNotif)

                    val payload = org.json.JSONObject().apply {
                        put("name", "BibLa Cloud Notifications Sync")
                        put("data", org.json.JSONObject().apply {
                            put("notifications", existingArray)
                            put("likes", existingLikes)
                        })
                    }

                    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                    val body = payload.toString().toRequestBody(mediaType)
                    val putRequest = okhttp3.Request.Builder()
                        .url(syncUrl)
                        .put(body)
                        .build()
                    client.newCall(putRequest).execute().close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun pushAnnouncementToFirebase(client: okhttp3.OkHttpClient, id: Long, title: String, message: String) {
        try {
            val json = org.json.JSONObject().apply {
                put("id", id)
                put("title", title)
                put("message", message)
                put("timestamp", System.currentTimeMillis())
            }
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = json.toString().toRequestBody(mediaType)
            val firebaseUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/announcements/$id.json",
                "https://bib-laht.firebaseio.com/announcements/$id.json"
            )
            for (url in firebaseUrls) {
                try {
                    val putReq = okhttp3.Request.Builder().url(url).put(body).build()
                    client.newCall(putReq).execute().close()
                } catch (e: Exception) {
                    // Try next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchAnnouncementsFromFirebase(client: okhttp3.OkHttpClient): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val firebaseUrls = listOf(
            "https://bib-laht-default-rtdb.firebaseio.com/announcements.json",
            "https://bib-laht.firebaseio.com/announcements.json"
        )
        for (url in firebaseUrls) {
            try {
                val req = okhttp3.Request.Builder().url(url).get().build()
                val resp = client.newCall(req).execute()
                val body = resp.body?.string() ?: ""
                resp.close()
                if (body.isNotBlank() && body != "null") {
                    val trimmed = body.trim()
                    if (trimmed.startsWith("{")) {
                        val obj = org.json.JSONObject(trimmed)
                        val keys = obj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val item = obj.optJSONObject(key) ?: continue
                            val t = item.optString("title", "")
                            val m = item.optString("message", "")
                            if (t.isNotBlank() && m.isNotBlank()) {
                                list.add(t to m)
                            }
                        }
                    } else if (trimmed.startsWith("[")) {
                        val arr = org.json.JSONArray(trimmed)
                        for (i in 0 until arr.length()) {
                            val item = arr.optJSONObject(i) ?: continue
                            val t = item.optString("title", "")
                            val m = item.optString("message", "")
                            if (t.isNotBlank() && m.isNotBlank()) {
                                list.add(t to m)
                            }
                        }
                    }
                    if (list.isNotEmpty()) break
                }
            } catch (e: Exception) {
                // Next
            }
        }
        return list
    }

    private val isSyncingNotifications = java.util.concurrent.atomic.AtomicBoolean(false)

    fun syncCloudNotifications() {
        if (!isSyncingNotifications.compareAndSet(false, true)) return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val context = getApplication<Application>().applicationContext
                val shownSet = prefs.getStringSet("shown_announcements_keys", emptySet())?.toMutableSet() ?: mutableSetOf()

                // 1. Fetch from Firebase Realtime Database
                val firebaseList = fetchAnnouncementsFromFirebase(client)
                for (item in firebaseList) {
                    val (titleRaw, messageRaw) = item
                    val title = titleRaw.trim()
                    val message = messageRaw.trim()
                    if (title.isNotBlank() && message.isNotBlank()) {
                        val notifKey = "ann_${title.hashCode()}_${message.hashCode()}"
                        val alreadyInDb = repository.hasNotification(title, message)
                        val alreadyShown = shownSet.contains(notifKey)

                        if (!alreadyInDb) {
                            repository.addNotification(title, message)
                        }

                        if (!alreadyShown) {
                            triggerSystemNotification(context, title, message)
                            shownSet.add(notifKey)
                            prefs.edit().putStringSet("shown_announcements_keys", shownSet).apply()
                        }
                    }
                }

                // 2. Fetch from Cloud Sync URL (backup)
                val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
                val request = okhttp3.Request.Builder().url(syncUrl).get().build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                response.close()

                if (responseBody.isNotBlank()) {
                    val rootObj = org.json.JSONObject(responseBody)
                    val dataObj = rootObj.optJSONObject("data")
                    if (dataObj != null) {
                        val arr = dataObj.optJSONArray("notifications")
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                val item = arr.optJSONObject(i) ?: continue
                                val title = item.optString("title", "").trim()
                                val message = item.optString("message", "").trim()
                                if (title.isNotBlank() && message.isNotBlank()) {
                                    val notifKey = "ann_${title.hashCode()}_${message.hashCode()}"
                                    val alreadyInDb = repository.hasNotification(title, message)
                                    val alreadyShown = shownSet.contains(notifKey)

                                    if (!alreadyInDb) {
                                        repository.addNotification(title, message)
                                    }

                                    if (!alreadyShown) {
                                        triggerSystemNotification(context, title, message)
                                        shownSet.add(notifKey)
                                        prefs.edit().putStringSet("shown_announcements_keys", shownSet).apply()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSyncingNotifications.set(false)
            }
        }
    }

    private fun triggerSystemNotification(context: android.content.Context, title: String, message: String) {
        try {
            val notificationsEnabled = prefs.getBoolean("pref_notifications", true)
            if (!notificationsEnabled) return

            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
            if (notificationManager != null) {
                val channelId = "daily_reminder_channel"
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val channel = android.app.NotificationChannel(
                        channelId,
                        "Bib La Rapèl ak Anons",
                        android.app.NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                val intent = android.content.Intent(context, com.example.MainActivity::class.java).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("navigate_to", "notifications")
                }
                val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                } else {
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT
                }

                val notifKey = "ann_${title.trim().hashCode()}_${message.trim().hashCode()}"
                val notifId = 2000 + Math.abs(notifKey.hashCode() % 10000)

                val pendingIntent = android.app.PendingIntent.getActivity(
                    context,
                    notifId,
                    intent,
                    pendingIntentFlags
                )

                val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(com.example.R.drawable.ic_notification_bib_la)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationManager.notify(notifId, builder.build())
                    }
                } else {
                    notificationManager.notify(notifId, builder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleDarkMode() {
        val newValue = !(_isDarkMode.value ?: false)
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("pref_dark_mode", newValue).apply()
    }

    fun toggleVisualComfort() {
        val newValue = !_isVisualComfortEnabled.value
        _isVisualComfortEnabled.value = newValue
        prefs.edit().putBoolean("pref_visual_comfort", newValue).apply()
    }

    fun setTextSizeMultiplier(value: Float) {
        val clamped = value.coerceIn(0.7f, 2.2f)
        _textSizeMultiplier.value = clamped
        prefs.edit().putFloat("pref_text_size", clamped).apply()
    }

    fun setFontFamilyType(type: String) {
        val valid = when (type.lowercase()) {
            "serif" -> "serif"
            "monospaced", "monospace" -> "monospaced"
            else -> "sans_serif"
        }
        _fontFamilyType.value = valid
        prefs.edit().putString("pref_font_family", valid).apply()
    }

    fun increaseTextSize() {
        if (_textSizeMultiplier.value < 2.0f) {
            val newValue = (_textSizeMultiplier.value + 0.1f).coerceAtMost(2.2f)
            _textSizeMultiplier.value = newValue
            prefs.edit().putFloat("pref_text_size", newValue).apply()
        }
    }

    fun decreaseTextSize() {
        if (_textSizeMultiplier.value > 0.7f) {
            val newValue = (_textSizeMultiplier.value - 0.1f).coerceAtLeast(0.7f)
            _textSizeMultiplier.value = newValue
            prefs.edit().putFloat("pref_text_size", newValue).apply()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
    }

    fun saveProgress(book: String, chapter: Int, verse: Int) {
        viewModelScope.launch {
            repository.saveProgress(book, chapter, verse)
        }
    }

    private fun getInitialSeedComments(): List<com.example.data.VerseComment> {
        val now = System.currentTimeMillis()
        return listOf(
            com.example.data.VerseComment(
                id = "seed_comm_1",
                verseId = 1L,
                userName = "Frè Jean-Pierre",
                text = "Glwa pou Bondye! Vèsè sa a ban mwen anpil fòs ak kouraj jodi a. 🙏✨",
                timestamp = now - (1000L * 60 * 60 * 4), // 4h de sa
                likes = 14,
                likedByUsers = emptyList()
            ),
            com.example.data.VerseComment(
                id = "seed_comm_2",
                verseId = 1L,
                userName = "Sè Mirlande",
                text = "Amèn! Pawòl Bondye se yon veritab limyè sou chemen nou ak yon gid pou fanmi nou. 🙌❤️",
                timestamp = now - (1000L * 60 * 60 * 2), // 2h de sa
                likes = 9,
                likedByUsers = emptyList()
            ),
            com.example.data.VerseComment(
                id = "seed_comm_3",
                verseId = 1L,
                userName = "Pastè Emmanuel",
                text = "Beni swa Letènèl! Gras li renouvle chak maten nan lavi nou tout. Ann kontinye medite sou pawòl la. 📖🕊️",
                timestamp = now - (1000L * 60 * 35), // 35 min de sa
                likes = 18,
                likedByUsers = emptyList()
            )
        )
    }

    init {
        savedUserName.value = prefs.getString("saved_user_name", "") ?: ""
        val cachedCommentsStr = prefs.getString("cached_comments_json", null)
        if (!cachedCommentsStr.isNullOrBlank()) {
            try {
                val arr = org.json.JSONArray(cachedCommentsStr)
                val parsed = parseCommentsJson(arr)
                _comments.value = if (parsed.isNotEmpty()) parsed else getInitialSeedComments()
            } catch (e: Exception) {
                e.printStackTrace()
                _comments.value = getInitialSeedComments()
            }
        } else {
            val initial = getInitialSeedComments()
            _comments.value = initial
            prefs.edit().putString("cached_comments_json", commentsToJson(initial).toString()).apply()
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            syncCloudNotifications()
            syncCloudComments()
            repository.prepopulateIfNeeded()
            val daily = repository.getDailyVerse()
            _dailyVerse.value = daily
            if (daily != null) {
                try {
                    val frMap = frenchRepository.getFrenchVersesForChapter(daily.book, daily.chapter)
                    _dailyVerseFrench.value = frMap[daily.verseNumber]
                } catch (e: Exception) {
                    _dailyVerseFrench.value = null
                }

                val keyLiked = "daily_verse_liked_${daily.id}"
                val keyCount = "daily_verse_like_count_${daily.id}"
                val keyViewCount = "daily_verse_view_count_${daily.id}"
                
                dailyVerseIsLiked.value = prefs.getBoolean(keyLiked, false)
                dailyVerseLikeCount.value = prefs.getInt(keyCount, 142)
                
                // Increment and sync real view count on app start
                val currentViews = prefs.getInt(keyViewCount, 285) + 1
                prefs.edit().putInt(keyViewCount, currentViews).apply()
                dailyVerseViewCount.value = currentViews

                val client = okhttp3.OkHttpClient()
                recordDailyVerseViewInFirebase(client, daily.id)
                fetchDailyVerseStatsFromFirebase(client, daily.id)

                startCloudLikesPolling()
            }
        }
    }

    private var cloudPollingJob: kotlinx.coroutines.Job? = null
    @Volatile
    private var lastUserActionTime = 0L

    fun refreshDailyVerse() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val daily = repository.getDailyVerse()
            if (daily != null && daily.id != _dailyVerse.value?.id) {
                _dailyVerse.value = daily
                try {
                    val frMap = frenchRepository.getFrenchVersesForChapter(daily.book, daily.chapter)
                    _dailyVerseFrench.value = frMap[daily.verseNumber]
                } catch (e: Exception) {
                    _dailyVerseFrench.value = null
                }
                val keyLiked = "daily_verse_liked_${daily.id}"
                val keyCount = "daily_verse_like_count_${daily.id}"
                val keyViewCount = "daily_verse_view_count_${daily.id}"
                
                dailyVerseIsLiked.value = prefs.getBoolean(keyLiked, false)
                dailyVerseLikeCount.value = prefs.getInt(keyCount, 142)
                
                val currentViews = prefs.getInt(keyViewCount, 285) + 1
                prefs.edit().putInt(keyViewCount, currentViews).apply()
                dailyVerseViewCount.value = currentViews

                val client = okhttp3.OkHttpClient()
                recordDailyVerseViewInFirebase(client, daily.id)
                fetchDailyVerseStatsFromFirebase(client, daily.id)

                startCloudLikesPolling()
            }
        }
    }

    fun startCloudLikesPolling() {
        cloudPollingJob?.cancel()
        cloudPollingJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val client = okhttp3.OkHttpClient()
            val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
            while (true) {
                val daily = _dailyVerse.value
                if (daily != null && (System.currentTimeMillis() - lastUserActionTime > 1500L)) {
                    val keyVerseId = "verse_${daily.id}"
                    val keyCount = "daily_verse_like_count_${daily.id}"
                    val keyViewCount = "daily_verse_view_count_${daily.id}"
                    try {
                        val request = okhttp3.Request.Builder().url(syncUrl).get().build()
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string() ?: ""
                        response.close()

                        if (responseBody.isNotBlank()) {
                            val rootObj = org.json.JSONObject(responseBody)
                            val dataObj = rootObj.optJSONObject("data")
                            if (dataObj != null) {
                                // Likes sync
                                val likesObj = dataObj.optJSONObject("likes")
                                val cloudCount = if (likesObj != null && likesObj.has(keyVerseId)) {
                                    likesObj.optInt(keyVerseId, 142)
                                } else 142
                                
                                val isLikedLocally = dailyVerseIsLiked.value
                                val localCount = prefs.getInt(keyCount, 142)
                                val finalCount = if (isLikedLocally) {
                                    Math.max(cloudCount, localCount)
                                } else {
                                    cloudCount
                                }

                                if (dailyVerseLikeCount.value != finalCount) {
                                    dailyVerseLikeCount.value = finalCount
                                }
                                prefs.edit().putInt(keyCount, finalCount).apply()

                                // Views sync
                                val viewsObj = dataObj.optJSONObject("views")
                                val cloudViews = if (viewsObj != null && viewsObj.has(keyVerseId)) {
                                    viewsObj.optInt(keyVerseId, 285)
                                } else 285
                                val localViews = prefs.getInt(keyViewCount, 285)
                                val finalViews = Math.max(cloudViews, localViews)
                                if (dailyVerseViewCount.value != finalViews) {
                                    dailyVerseViewCount.value = finalViews
                                }
                                prefs.edit().putInt(keyViewCount, finalViews).apply()

                                // Comments sync with smart local-cloud merge
                                val commentsArr = dataObj.optJSONArray("comments")
                                val cloudList = parseCommentsJson(commentsArr)
                                mergeAndSaveComments(cloudList)
                            }
                        }

                        // Also poll Firebase directly for live stats
                        fetchDailyVerseStatsFromFirebase(client, daily.id)
                        val firebaseComments = fetchCommentsFromFirebase(client)
                        if (firebaseComments.isNotEmpty()) {
                            mergeAndSaveComments(firebaseComments)
                        }
                        syncCloudNotifications()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Ultra-responsive real-time live sync every 1.5 seconds
                kotlinx.coroutines.delay(1500)
            }
        }
    }

    private fun recordDailyVerseViewInFirebase(client: okhttp3.OkHttpClient, verseId: Long) {
        try {
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = "${System.currentTimeMillis()}".toRequestBody(mediaType)
            val firebaseUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/daily_verse_views/verse_$verseId/$deviceId.json",
                "https://bib-laht.firebaseio.com/daily_verse_views/verse_$verseId/$deviceId.json"
            )
            for (url in firebaseUrls) {
                try {
                    val putReq = okhttp3.Request.Builder().url(url).put(body).build()
                    client.newCall(putReq).execute().close()
                } catch (e: Exception) {
                    // Try next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pushDailyVerseLikeToFirebase(client: okhttp3.OkHttpClient, verseId: Long, isLiked: Boolean) {
        try {
            val firebaseUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/daily_verse_likes/verse_$verseId/$deviceId.json",
                "https://bib-laht.firebaseio.com/daily_verse_likes/verse_$verseId/$deviceId.json"
            )
            for (url in firebaseUrls) {
                try {
                    val req = if (isLiked) {
                        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                        val body = "true".toRequestBody(mediaType)
                        okhttp3.Request.Builder().url(url).put(body).build()
                    } else {
                        okhttp3.Request.Builder().url(url).delete().build()
                    }
                    client.newCall(req).execute().close()
                } catch (e: Exception) {
                    // Try next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchDailyVerseStatsFromFirebase(client: okhttp3.OkHttpClient, verseId: Long) {
        try {
            val keyCount = "daily_verse_like_count_$verseId"
            val keyViewCount = "daily_verse_view_count_$verseId"
            val keyLiked = "daily_verse_liked_$verseId"

            // 1. Fetch Likes
            val likesUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/daily_verse_likes/verse_$verseId.json",
                "https://bib-laht.firebaseio.com/daily_verse_likes/verse_$verseId.json"
            )
            for (url in likesUrls) {
                try {
                    val req = okhttp3.Request.Builder().url(url).get().build()
                    val resp = client.newCall(req).execute()
                    val body = resp.body?.string() ?: ""
                    resp.close()
                    if (body.isNotBlank() && body != "null") {
                        val trimmed = body.trim()
                        if (trimmed.startsWith("{")) {
                            val likesObj = org.json.JSONObject(trimmed)
                            val realLikesCount = likesObj.length()
                            val totalLikes = 142 + realLikesCount
                            val isLikedByMe = likesObj.has(deviceId)

                            dailyVerseLikeCount.value = totalLikes
                            prefs.edit().putInt(keyCount, totalLikes).apply()

                            if (isLikedByMe) {
                                dailyVerseIsLiked.value = true
                                prefs.edit().putBoolean(keyLiked, true).apply()
                            }
                            break
                        }
                    }
                } catch (e: Exception) {
                    // Next
                }
            }

            // 2. Fetch Views
            val viewsUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/daily_verse_views/verse_$verseId.json",
                "https://bib-laht.firebaseio.com/daily_verse_views/verse_$verseId.json"
            )
            for (url in viewsUrls) {
                try {
                    val req = okhttp3.Request.Builder().url(url).get().build()
                    val resp = client.newCall(req).execute()
                    val body = resp.body?.string() ?: ""
                    resp.close()
                    if (body.isNotBlank() && body != "null") {
                        val trimmed = body.trim()
                        if (trimmed.startsWith("{")) {
                            val viewsObj = org.json.JSONObject(trimmed)
                            val realViewsCount = viewsObj.length()
                            val totalViews = 285 + realViewsCount

                            dailyVerseViewCount.value = totalViews
                            prefs.edit().putInt(keyViewCount, totalViews).apply()
                            break
                        }
                    }
                } catch (e: Exception) {
                    // Next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseCommentsFromFirebase(jsonStr: String): List<com.example.data.VerseComment> {
        if (jsonStr.isBlank() || jsonStr == "null") return emptyList()
        val trimmed = jsonStr.trim()
        if (trimmed.startsWith("[")) {
            return parseCommentsJson(org.json.JSONArray(trimmed))
        } else if (trimmed.startsWith("{")) {
            val obj = org.json.JSONObject(trimmed)
            val list = mutableListOf<com.example.data.VerseComment>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val commentObj = obj.optJSONObject(key) ?: continue
                val likesArr = commentObj.optJSONArray("likedByUsers")
                val likedBy = mutableListOf<String>()
                if (likesArr != null) {
                    for (j in 0 until likesArr.length()) {
                        likedBy.add(likesArr.optString(j))
                    }
                }
                val reportsArr = commentObj.optJSONArray("reportedByUsers")
                val reportedBy = mutableListOf<String>()
                if (reportsArr != null) {
                    for (j in 0 until reportsArr.length()) {
                        reportedBy.add(reportsArr.optString(j))
                    }
                }
                val parentRaw = if (commentObj.isNull("parentId") || !commentObj.has("parentId")) null else commentObj.optString("parentId")
                val cleanParentId = if (parentRaw.isNullOrBlank() || parentRaw == "null") null else parentRaw
                list.add(
                    com.example.data.VerseComment(
                        id = commentObj.optString("id", key),
                        verseId = commentObj.optLong("verseId", 0L),
                        userName = commentObj.optString("userName", "Moun"),
                        text = commentObj.optString("text", ""),
                        timestamp = commentObj.optLong("timestamp", System.currentTimeMillis()),
                        parentId = cleanParentId,
                        likes = commentObj.optInt("likes", 0),
                        likedByUsers = likedBy,
                        reportedByUsers = reportedBy
                    )
                )
            }
            return list
        }
        return emptyList()
    }

    private fun fetchCommentsFromFirebase(client: okhttp3.OkHttpClient): List<com.example.data.VerseComment> {
        val firebaseUrls = listOf(
            "https://bib-laht-default-rtdb.firebaseio.com/comments.json",
            "https://bib-laht.firebaseio.com/comments.json"
        )
        for (url in firebaseUrls) {
            try {
                val req = okhttp3.Request.Builder().url(url).get().build()
                val resp = client.newCall(req).execute()
                val body = resp.body?.string() ?: ""
                resp.close()
                if (body.isNotBlank() && body != "null") {
                    val parsed = parseCommentsFromFirebase(body)
                    if (parsed.isNotEmpty()) {
                        return parsed
                    }
                }
            } catch (e: Exception) {
                // Next
            }
        }
        return emptyList()
    }

    private fun pushCommentToFirebase(client: okhttp3.OkHttpClient, comment: com.example.data.VerseComment) {
        try {
            val commentJson = org.json.JSONObject().apply {
                put("id", comment.id)
                put("verseId", comment.verseId)
                put("userName", comment.userName)
                put("text", comment.text)
                put("timestamp", comment.timestamp)
                if (comment.parentId != null) put("parentId", comment.parentId) else put("parentId", org.json.JSONObject.NULL)
                put("likes", comment.likes)
                put("likedByUsers", org.json.JSONArray(comment.likedByUsers))
                put("reportedByUsers", org.json.JSONArray(comment.reportedByUsers))
            }
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = commentJson.toString().toRequestBody(mediaType)

            val firebaseUrls = listOf(
                "https://bib-laht-default-rtdb.firebaseio.com/comments/${comment.id}.json",
                "https://bib-laht.firebaseio.com/comments/${comment.id}.json"
            )
            for (url in firebaseUrls) {
                try {
                    val putReq = okhttp3.Request.Builder().url(url).put(body).build()
                    client.newCall(putReq).execute().close()
                } catch (e: Exception) {
                    // Try next
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mergeAndSaveComments(cloudList: List<com.example.data.VerseComment>) {
        if (cloudList.isEmpty()) return
        val currentLocal = _comments.value
        val mergedMap = mutableMapOf<String, com.example.data.VerseComment>()
        currentLocal.forEach { mergedMap[it.id] = it }
        cloudList.forEach { cloudItem ->
            val existing = mergedMap[cloudItem.id]
            if (existing == null) {
                mergedMap[cloudItem.id] = cloudItem
            } else {
                val mergedLikedBy = (existing.likedByUsers + cloudItem.likedByUsers).distinct()
                val mergedReportedBy = (existing.reportedByUsers + cloudItem.reportedByUsers).distinct()
                mergedMap[cloudItem.id] = cloudItem.copy(
                    likes = Math.max(existing.likes, cloudItem.likes),
                    likedByUsers = mergedLikedBy,
                    reportedByUsers = mergedReportedBy
                )
            }
        }
        val finalCommentsList = mergedMap.values.sortedBy { it.timestamp }
        _comments.value = finalCommentsList
        prefs.edit().putString("cached_comments_json", commentsToJson(finalCommentsList).toString()).apply()
    }

    private fun parseCommentsJson(arr: org.json.JSONArray?): List<com.example.data.VerseComment> {
        if (arr == null) return emptyList()
        val list = mutableListOf<com.example.data.VerseComment>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val likesArr = obj.optJSONArray("likedByUsers")
            val likedBy = mutableListOf<String>()
            if (likesArr != null) {
                for (j in 0 until likesArr.length()) {
                    likedBy.add(likesArr.optString(j))
                }
            }
            val reportsArr = obj.optJSONArray("reportedByUsers")
            val reportedBy = mutableListOf<String>()
            if (reportsArr != null) {
                for (j in 0 until reportsArr.length()) {
                    reportedBy.add(reportsArr.optString(j))
                }
            }
            val parentRaw = if (obj.isNull("parentId") || !obj.has("parentId")) null else obj.optString("parentId")
            val cleanParentId = if (parentRaw.isNullOrBlank() || parentRaw == "null") null else parentRaw
            list.add(
                com.example.data.VerseComment(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    verseId = obj.optLong("verseId", 0L),
                    userName = obj.optString("userName", "Moun"),
                    text = obj.optString("text", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    parentId = cleanParentId,
                    likes = obj.optInt("likes", 0),
                    likedByUsers = likedBy,
                    reportedByUsers = reportedBy
                )
            )
        }
        return list
    }

    private fun commentsToJson(list: List<com.example.data.VerseComment>): org.json.JSONArray {
        val arr = org.json.JSONArray()
        for (c in list) {
            val obj = org.json.JSONObject().apply {
                put("id", c.id)
                put("verseId", c.verseId)
                put("userName", c.userName)
                put("text", c.text)
                put("timestamp", c.timestamp)
                if (c.parentId != null) put("parentId", c.parentId) else put("parentId", org.json.JSONObject.NULL)
                put("likes", c.likes)
                put("likedByUsers", org.json.JSONArray(c.likedByUsers))
                put("reportedByUsers", org.json.JSONArray(c.reportedByUsers))
            }
            arr.put(obj)
        }
        return arr
    }

    fun saveUserName(name: String) {
        val trimmed = name.trim()
        savedUserName.value = trimmed
        prefs.edit().putString("saved_user_name", trimmed).apply()
    }

    fun addComment(text: String, parentId: String? = null, userName: String) {
        val trimmedText = text.trim().take(700)
        val trimmedName = userName.trim()
        if (trimmedText.isBlank() || trimmedName.isBlank()) return

        saveUserName(trimmedName)
        val verseId = _dailyVerse.value?.id ?: 1L
        val cleanParentId = if (parentId.isNullOrBlank() || parentId == "null") null else parentId
        val newComment = com.example.data.VerseComment(
            id = java.util.UUID.randomUUID().toString(),
            verseId = verseId,
            userName = trimmedName,
            text = trimmedText,
            timestamp = System.currentTimeMillis(),
            parentId = cleanParentId
        )

        val updatedComments = _comments.value + newComment
        _comments.value = updatedComments
        prefs.edit().putString("cached_comments_json", commentsToJson(updatedComments).toString()).apply()
        lastUserActionTime = System.currentTimeMillis()

        // Direct Global Cloud & Firebase Sync
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                // 1. Direct Firebase Realtime Database save
                pushCommentToFirebase(client, newComment)

                // 2. Cloud Broadcast sync
                val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
                val getRequest = okhttp3.Request.Builder().url(syncUrl).get().build()
                val response = client.newCall(getRequest).execute()
                val responseBody = response.body?.string() ?: ""
                response.close()

                var existingNotifications = org.json.JSONArray()
                var existingLikes = org.json.JSONObject()
                var existingViews = org.json.JSONObject()
                var existingCommentsArr = org.json.JSONArray()

                if (responseBody.isNotBlank()) {
                    val rootObj = org.json.JSONObject(responseBody)
                    val dataObj = rootObj.optJSONObject("data")
                    if (dataObj != null) {
                        dataObj.optJSONArray("notifications")?.let { existingNotifications = it }
                        dataObj.optJSONObject("likes")?.let { existingLikes = it }
                        dataObj.optJSONObject("views")?.let { existingViews = it }
                        dataObj.optJSONArray("comments")?.let { existingCommentsArr = it }
                    }
                }

                val cloudList = parseCommentsJson(existingCommentsArr)
                val mergeMap = mutableMapOf<String, com.example.data.VerseComment>()
                cloudList.forEach { mergeMap[it.id] = it }
                mergeMap[newComment.id] = newComment

                val finalCloudComments = mergeMap.values.sortedBy { it.timestamp }

                val payload = org.json.JSONObject().apply {
                    put("name", "BibLa Cloud Notifications Sync")
                    put("data", org.json.JSONObject().apply {
                        put("notifications", existingNotifications)
                        put("likes", existingLikes)
                        put("views", existingViews)
                        put("comments", commentsToJson(finalCloudComments))
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = payload.toString().toRequestBody(mediaType)
                val putRequest = okhttp3.Request.Builder()
                    .url(syncUrl)
                    .put(body)
                    .build()
                client.newCall(putRequest).execute().close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleCommentLike(commentId: String) {
        val current = _comments.value
        val updated = current.map { c ->
            if (c.id == commentId) {
                val alreadyLiked = c.likedByUsers.contains(deviceId)
                val newLikedBy = if (alreadyLiked) c.likedByUsers - deviceId else c.likedByUsers + deviceId
                val newLikes = if (alreadyLiked) (c.likes - 1).coerceAtLeast(0) else c.likes + 1
                c.copy(likes = newLikes, likedByUsers = newLikedBy)
            } else c
        }
        _comments.value = updated
        prefs.edit().putString("cached_comments_json", commentsToJson(updated).toString()).apply()
        lastUserActionTime = System.currentTimeMillis()
        val targetComment = updated.find { it.id == commentId }
        if (targetComment != null) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val client = okhttp3.OkHttpClient()
                pushCommentToFirebase(client, targetComment)
            }
        }
        syncAllToCloud(updatedComments = updated)
    }

    fun reportComment(commentId: String) {
        val current = _comments.value
        val updated = current.map { c ->
            if (c.id == commentId) {
                val alreadyReported = c.reportedByUsers.contains(deviceId)
                val newReportedBy = if (alreadyReported) c.reportedByUsers else c.reportedByUsers + deviceId
                c.copy(reportedByUsers = newReportedBy)
            } else c
        }
        _comments.value = updated
        prefs.edit().putString("cached_comments_json", commentsToJson(updated).toString()).apply()
        lastUserActionTime = System.currentTimeMillis()
        val targetComment = updated.find { it.id == commentId }
        if (targetComment != null) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val client = okhttp3.OkHttpClient()
                pushCommentToFirebase(client, targetComment)
            }
        }
        syncAllToCloud(updatedComments = updated)
    }

    fun syncCloudComments(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                // 1. Fetch from Firebase
                val firebaseComments = fetchCommentsFromFirebase(client)
                if (firebaseComments.isNotEmpty()) {
                    mergeAndSaveComments(firebaseComments)
                }

                // 2. Fetch from Cloud API
                val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
                val request = okhttp3.Request.Builder().url(syncUrl).get().build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                response.close()

                if (responseBody.isNotBlank()) {
                    val rootObj = org.json.JSONObject(responseBody)
                    val dataObj = rootObj.optJSONObject("data")
                    if (dataObj != null) {
                        val commentsArr = dataObj.optJSONArray("comments")
                        val cloudList = parseCommentsJson(commentsArr)
                        if (cloudList.isNotEmpty()) {
                            mergeAndSaveComments(cloudList)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete?.invoke()
                }
            }
        }
    }

    private fun syncAllToCloud(updatedComments: List<com.example.data.VerseComment>? = null) {
        val daily = _dailyVerse.value ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val syncUrl = "https://api.restful-api.dev/objects/ff8081819ff5b110019ffaa36958104d"
                val getRequest = okhttp3.Request.Builder().url(syncUrl).get().build()
                val response = client.newCall(getRequest).execute()
                val responseBody = response.body?.string() ?: ""
                response.close()

                var existingNotifications = org.json.JSONArray()
                var existingLikes = org.json.JSONObject()
                var existingViews = org.json.JSONObject()
                var existingCommentsArr = org.json.JSONArray()

                if (responseBody.isNotBlank()) {
                    val rootObj = org.json.JSONObject(responseBody)
                    val dataObj = rootObj.optJSONObject("data")
                    if (dataObj != null) {
                        dataObj.optJSONArray("notifications")?.let { existingNotifications = it }
                        dataObj.optJSONObject("likes")?.let { existingLikes = it }
                        dataObj.optJSONObject("views")?.let { existingViews = it }
                        dataObj.optJSONArray("comments")?.let { existingCommentsArr = it }
                    }
                }

                val keyVerseId = "verse_${daily.id}"
                existingLikes.put(keyVerseId, dailyVerseLikeCount.value)
                existingViews.put(keyVerseId, dailyVerseViewCount.value)

                val commentsToSave = if (updatedComments != null) {
                    val cloudList = parseCommentsJson(existingCommentsArr)
                    val mergeMap = mutableMapOf<String, com.example.data.VerseComment>()
                    cloudList.forEach { mergeMap[it.id] = it }
                    updatedComments.forEach { item ->
                        val exist = mergeMap[item.id]
                        if (exist == null) {
                            mergeMap[item.id] = item
                        } else {
                            val mergedLiked = (exist.likedByUsers + item.likedByUsers).distinct()
                            val mergedRep = (exist.reportedByUsers + item.reportedByUsers).distinct()
                            mergeMap[item.id] = item.copy(
                                likes = Math.max(exist.likes, item.likes),
                                likedByUsers = mergedLiked,
                                reportedByUsers = mergedRep
                            )
                        }
                    }
                    mergeMap.values.sortedBy { it.timestamp }
                } else {
                    parseCommentsJson(existingCommentsArr)
                }

                val payload = org.json.JSONObject().apply {
                    put("name", "BibLa Cloud Notifications Sync")
                    put("data", org.json.JSONObject().apply {
                        put("notifications", existingNotifications)
                        put("likes", existingLikes)
                        put("views", existingViews)
                        put("comments", commentsToJson(commentsToSave))
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = payload.toString().toRequestBody(mediaType)
                val putRequest = okhttp3.Request.Builder()
                    .url(syncUrl)
                    .put(body)
                    .build()
                client.newCall(putRequest).execute().close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun syncCloudDailyVerseLikes() {
        startCloudLikesPolling()
    }

    fun refreshComments(onComplete: (() -> Unit)? = null) {
        syncCloudComments(onComplete)
    }

    fun toggleDailyVerseLike() {
        val daily = _dailyVerse.value ?: return
        val keyLiked = "daily_verse_liked_${daily.id}"
        val keyCount = "daily_verse_like_count_${daily.id}"
        val currentlyLiked = dailyVerseIsLiked.value
        val newLiked = !currentlyLiked
        val currentCount = dailyVerseLikeCount.value
        val newCount = if (newLiked) currentCount + 1 else (currentCount - 1).coerceAtLeast(0)

        dailyVerseIsLiked.value = newLiked
        dailyVerseLikeCount.value = newCount
        lastUserActionTime = System.currentTimeMillis()

        prefs.edit()
            .putBoolean(keyLiked, newLiked)
            .putInt(keyCount, newCount)
            .apply()

        // Push directly to Firebase Realtime Database
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val client = okhttp3.OkHttpClient()
            pushDailyVerseLikeToFirebase(client, daily.id, newLiked)
            fetchDailyVerseStatsFromFirebase(client, daily.id)
        }

        syncAllToCloud()
    }

    fun setVerses(book: String, chapter: Int) {
        val canonical = com.example.ui.util.BibleBookNames.toCanonical(book)
        _currentBookAndChapter.value = Pair(canonical, chapter)
        loadFrenchVersesIfNeeded(canonical, chapter)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(verseId: Long, content: String) {
        viewModelScope.launch {
            repository.addNote(verseId, content)
        }
    }

    fun deleteNote(note: com.example.data.Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    suspend fun getNoteById(id: Long): com.example.data.Note? {
        return repository.getNoteById(id)
    }

    fun updateNote(note: com.example.data.Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun toggleBookmark(verse: com.example.data.Verse, color: Int = 0) {
        viewModelScope.launch {
            repository.toggleBookmark(verse, color)
        }
    }

    suspend fun getVerseById(id: Long): com.example.data.Verse? {
        return repository.getVerseById(id)
    }
}
