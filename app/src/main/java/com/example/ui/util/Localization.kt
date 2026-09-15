package com.example.ui.util

object AppLanguage {
    const val HT = "ht"
    const val FR = "fr"

    fun getLanguageLabel(code: String): String = when (code) {
        FR -> "Français"
        else -> "Kreyòl Ayisyen"
    }
}

object BibleVersion {
    const val KREYOL = "ht_kreyol"
    const val FRANCAIS_LSG = "fr_lsg"
    const val BILINGUAL = "bilingual"

    fun getVersionLabel(code: String, lang: String): String = when (code) {
        FRANCAIS_LSG -> if (lang == AppLanguage.FR) "Français (Louis Segond 1910)" else "Fransè (Louis Segond 1910)"
        BILINGUAL -> if (lang == AppLanguage.FR) "Bilingue (Créole + Français)" else "Kòt a kòt (Kreyòl + Fransè)"
        else -> if (lang == AppLanguage.FR) "Créole Haïtien (Bib La)" else "Kreyòl Ayisyen (Bib La)"
    }
}

object BibleBookNames {
    // Maps Canonical Creole Name -> French Name
    val creoleToFrench = mapOf(
        "Jenèz" to "Genèse",
        "Egzòd" to "Exode",
        "Levitik" to "Lévitique",
        "Nonm" to "Nombres",
        "Detewonòm" to "Deutéronome",
        "Jozye" to "Josué",
        "Jij" to "Juges",
        "Rit" to "Ruth",
        "1 Samyèl" to "1 Samuel",
        "2 Samyèl" to "2 Samuel",
        "1 Wa" to "1 Rois",
        "2 Wa" to "2 Rois",
        "1 Kwonik" to "1 Chroniques",
        "2 Kwonik" to "2 Chroniques",
        "Esdras" to "Esdras",
        "Neemi" to "Néhémie",
        "Estè" to "Esther",
        "Jòb" to "Job",
        "Sòm" to "Psaumes",
        "Pwovèb" to "Proverbes",
        "Eklezyas" to "Ecclésiaste",
        "Chante Salomon" to "Cantique des Cantiques",
        "Ezayi" to "Ésaïe",
        "Jeremi" to "Jérémie",
        "Lamantasyon" to "Lamentations",
        "Ezekyèl" to "Ézéchiel",
        "Danyèl" to "Daniel",
        "Oze" to "Osée",
        "Jowèl" to "Joël",
        "Amòs" to "Amos",
        "Abdyas" to "Abdias",
        "Jonas" to "Jonas",
        "Miche" to "Michée",
        "Naoum" to "Nahum",
        "Abakouk" to "Habacuc",
        "Sofoni" to "Sophonie",
        "Aje" to "Aggée",
        "Zakari" to "Zacharie",
        "Malaki" to "Malachie",
        "Matye" to "Matthieu",
        "Mak" to "Marc",
        "Lik" to "Luc",
        "Jan" to "Jean",
        "Travay" to "Actes",
        "Women" to "Romains",
        "1 Korentyen" to "1 Corinthiens",
        "2 Korentyen" to "2 Corinthiens",
        "Galat" to "Galates",
        "Efèzyen" to "Éphésiens",
        "Filipyen" to "Philippiens",
        "Kolosyen" to "Colossiens",
        "1 Tesalonisyen" to "1 Thessaloniciens",
        "2 Tesalonisyen" to "2 Thessaloniciens",
        "1 Timote" to "1 Timothée",
        "2 Timote" to "2 Timothée",
        "Tit" to "Tite",
        "Filemon" to "Philémon",
        "Ebre" to "Hébreux",
        "Jak" to "Jacques",
        "1 Pyè" to "1 Pierre",
        "2 Pyè" to "2 Pierre",
        "1 Jan" to "1 Jean",
        "2 Jan" to "2 Jean",
        "3 Jan" to "3 Jean",
        "Jid" to "Jude",
        "Revelasyon" to "Apocalypse"
    )

    // Maps Canonical Creole Name -> USFM Code for French API
    val creoleToUsfm = mapOf(
        "Jenèz" to "GEN", "Egzòd" to "EXO", "Levitik" to "LEV", "Nonm" to "NUM", "Detewonòm" to "DEU",
        "Jozye" to "JOS", "Jij" to "JDG", "Rit" to "RUT", "1 Samyèl" to "1SA", "2 Samyèl" to "2SA",
        "1 Wa" to "1KI", "2 Wa" to "2KI", "1 Kwonik" to "1CH", "2 Kwonik" to "2CH", "Esdras" to "EZR",
        "Neemi" to "NEH", "Estè" to "EST", "Jòb" to "JOB", "Sòm" to "PSA", "Pwovèb" to "PRO",
        "Eklezyas" to "ECC", "Chante Salomon" to "SNG", "Ezayi" to "ISA", "Jeremi" to "JER",
        "Lamantasyon" to "LAM", "Ezekyèl" to "EZK", "Danyèl" to "DAN", "Oze" to "HOS", "Jowèl" to "JOL",
        "Amòs" to "AMO", "Abdyas" to "OBA", "Jonas" to "JON", "Miche" to "MIC", "Naoum" to "NAM",
        "Abakouk" to "HAB", "Sofoni" to "ZEP", "Aje" to "HAG", "Zakari" to "ZEC", "Malaki" to "MAL",
        "Matye" to "MAT", "Mak" to "MRK", "Lik" to "LUK", "Jan" to "JHN", "Travay" to "ACT",
        "Women" to "ROM", "1 Korentyen" to "1CO", "2 Korentyen" to "2CO", "Galat" to "GAL",
        "Efèzyen" to "EPH", "Filipyen" to "PHP", "Kolosyen" to "COL", "1 Tesalonisyen" to "1TH",
        "2 Tesalonisyen" to "2TH", "1 Timote" to "1TI", "2 Timote" to "2TI", "Tit" to "TIT",
        "Filemon" to "PHM", "Ebre" to "HEB", "Jak" to "JAS", "1 Pyè" to "1PE", "2 Pyè" to "2PE",
        "1 Jan" to "1JN", "2 Jan" to "2JN", "3 Jan" to "3JN", "Jid" to "JUD", "Revelasyon" to "REV"
    )

    private val frenchToCreole: Map<String, String> by lazy {
        creoleToFrench.entries.associate { (k, v) -> v.lowercase() to k }
    }

    fun getDisplayName(creoleBook: String, lang: String): String {
        return if (lang == AppLanguage.FR) {
            creoleToFrench[creoleBook] ?: creoleBook
        } else {
            creoleBook
        }
    }

    fun getSubtitleName(creoleBook: String, lang: String): String {
        return if (lang == AppLanguage.FR) {
            creoleBook
        } else {
            creoleToFrench[creoleBook] ?: ""
        }
    }

    fun getUsfmCode(bookName: String): String {
        // Direct match from creole
        creoleToUsfm[bookName]?.let { return it }
        // Match from French
        val canonical = toCanonical(bookName)
        return creoleToUsfm[canonical] ?: "GEN"
    }

    fun toCanonical(rawName: String): String {
        val trimmed = rawName.trim()
        if (creoleToFrench.containsKey(trimmed)) return trimmed
        val lower = trimmed.lowercase()
        return frenchToCreole[lower] ?: trimmed
    }
}

object AppStrings {
    // Navigation
    fun navHome(lang: String) = if (lang == AppLanguage.FR) "Accueil" else "Akèy"
    fun navReader(lang: String) = if (lang == AppLanguage.FR) "Lecture" else "Lekti"
    fun navNotes(lang: String) = if (lang == AppLanguage.FR) "Notes" else "Nòt"
    fun navBookmarks(lang: String) = if (lang == AppLanguage.FR) "Favoris" else "Favori"

    // Reader Screen
    fun readerTextSize(lang: String) = if (lang == AppLanguage.FR) "Taille du texte" else "Gwosè Tèks"
    fun readerShare(lang: String) = if (lang == AppLanguage.FR) "Partager" else "Pataje"
    fun readerAddBookmark(lang: String) = if (lang == AppLanguage.FR) "Ajouter aux favoris" else "Ajoute nan favori"
    fun readerRemoveBookmark(lang: String) = if (lang == AppLanguage.FR) "Retirer des favoris" else "Retire nan favori"
    fun readerAddNote(lang: String) = if (lang == AppLanguage.FR) "Ajouter une note" else "Ajoute yon nòt"
    fun readerCopy(lang: String) = if (lang == AppLanguage.FR) "Copier le texte" else "Kopye tèks la"
    fun readerChapterCopied(lang: String) = if (lang == AppLanguage.FR) "Texte copié dans le presse-papiers" else "Tèks kopye nan papye"
    fun readerNoText(lang: String) = if (lang == AppLanguage.FR) "Aucun texte disponible pour ce chapitre." else "Poko gen tèks pou chapit sa a nan baz done a."
    fun readerChooseBook(lang: String) = if (lang == AppLanguage.FR) "Choisir un livre" else "Chwazi yon liv"
    fun readerVersion(lang: String) = if (lang == AppLanguage.FR) "Version" else "Vèsyon"
    fun readerChapter(lang: String) = if (lang == AppLanguage.FR) "Chapitre" else "Chapit"
    fun readerNoteSaved(lang: String) = if (lang == AppLanguage.FR) "Note enregistrée" else "Nòt sove"

    // Book Selection
    fun bookSelectionTitle(lang: String) = if (lang == AppLanguage.FR) "Livres de la Bible" else "Chwazi yon Liv"
    fun oldTestament(lang: String) = if (lang == AppLanguage.FR) "Ancien Testament" else "Ansyen Testaman"
    fun newTestament(lang: String) = if (lang == AppLanguage.FR) "Nouveau Testament" else "Nouvo Testaman"
    fun chaptersCount(count: Int, lang: String) = if (lang == AppLanguage.FR) "$count chapitres" else "$count chapit"

    // Home Screen
    fun dailyVerseTitle(lang: String) = if (lang == AppLanguage.FR) "Verset du Jour" else "Vèsè pou Jounen an"
    fun resumeReading(lang: String) = if (lang == AppLanguage.FR) "Reprendre la Lecture" else "Kontinye Lekti"
    fun searchBiblePrompt(lang: String) = if (lang == AppLanguage.FR) "Rechercher verset, livre ou mot..." else "Chèche vèsè, liv oswa mo..."
    fun readingStreak(days: Int, lang: String) = if (lang == AppLanguage.FR) "$days jours consécutifs" else "$days jou youn apre lòt"
    fun streakLabel(lang: String) = if (lang == AppLanguage.FR) "Série de lecture" else "Lekti san rete"
    fun todayReading(lang: String) = if (lang == AppLanguage.FR) "Lecture d'aujourd'hui" else "Lekti pou jodi a"
    fun commentsCount(count: Int, lang: String) = if (lang == AppLanguage.FR) "$count commentaires" else "$count kòmantè"
    fun writeComment(lang: String) = if (lang == AppLanguage.FR) "Écrire une réflexion..." else "Ekri yon refleksyon..."

    // Profile & Settings
    fun profileTitle(lang: String) = if (lang == AppLanguage.FR) "Profil & Paramètres" else "Pwofil & Konfigirasyon"
    fun sectionSettings(lang: String) = if (lang == AppLanguage.FR) "Paramètres & Préférences" else "Anviwònman"
    fun appLanguageSetting(lang: String) = if (lang == AppLanguage.FR) "Langue de l'application" else "Lang Aplikasyon an"
    fun bibleVersionSetting(lang: String) = if (lang == AppLanguage.FR) "Version de la Bible" else "Vèsyon Bib la"
    fun themeMode(lang: String) = if (lang == AppLanguage.FR) "Mode Thème Sombre" else "Mòd Tèm Fènwa"
    fun textSize(lang: String) = if (lang == AppLanguage.FR) "Taille du Texte" else "Gwosè Tèks"
    fun fontStyle(lang: String) = if (lang == AppLanguage.FR) "Style de Police" else "Style Font Bib la"
    fun visualComfort(lang: String) = if (lang == AppLanguage.FR) "Confort Visuel (Filtre Chaud)" else "Konfò Vizyèl"
    fun dailyReminder(lang: String) = if (lang == AppLanguage.FR) "Rappel Quotidien de Lecture" else "Rapèl Lekti Chak Jou"
    fun reminderTimeExact(lang: String) = if (lang == AppLanguage.FR) "Heure du Rappel" else "Lè Egzak Pou Rapèl la"
    fun reminderClickToChange(lang: String) = if (lang == AppLanguage.FR) "Touchez pour modifier l'heure" else "Klike la a pou w chanje lè a"
    fun readingPlansAndProgress(lang: String) = if (lang == AppLanguage.FR) "Plans de Lecture & Progression" else "Plan Lekti Bib la & Pwogrè"
    fun viewNotifications(lang: String) = if (lang == AppLanguage.FR) "Voir les Notifications" else "Gade Notifikasyon Yo"
    fun monetizationAndPremium(lang: String) = if (lang == AppLanguage.FR) "Monétisation & Premium" else "Monetizasyon & Premium"
    fun yourRewards(lang: String) = if (lang == AppLanguage.FR) "Vos Récompenses" else "Rekonpans Ou Yo"
    fun rewardDescription(lang: String) = if (lang == AppLanguage.FR) "Chaque vidéo regardée = 1 récompense" else "Chak videyo jwenn = 1 rekonpans"
    fun unlockPremium(lang: String) = if (lang == AppLanguage.FR) "Débloquer Bible Premium ($2.99)" else "Debloke Bib Premium ($2.99)"
    fun donateSupport(lang: String) = if (lang == AppLanguage.FR) "Faire un don pour soutenir le projet" else "Fè yon Don pou Sipòte Pwojè a"
    fun donateSub(lang: String) = if (lang == AppLanguage.FR) "Participez au développement de l'application" else "Patisipasyon w ap ede n kenbe app la gratis"
    fun privacyPolicy(lang: String) = if (lang == AppLanguage.FR) "Politique de Confidentialité" else "Règleman sou Konfidansyalite"
    fun privacyPolicySubtitle(lang: String) = if (lang == AppLanguage.FR) "Consultez nos règles de protection des données" else "Klike pou w li règleman nou yo"
    fun aboutApp(lang: String) = if (lang == AppLanguage.FR) "À Propos de l'Application" else "Konsènan Aplikasyon an"

    // Notes
    fun notesTitle(lang: String) = if (lang == AppLanguage.FR) "Mes Notes" else "Nòt Mwen Yo"
    fun noNotes(lang: String) = if (lang == AppLanguage.FR) "Vous n'avez pas encore de notes." else "Ou poko kreye okenn nòt."
    fun newNote(lang: String) = if (lang == AppLanguage.FR) "Nouvelle Note" else "Nouvo Nòt"
    fun noteTitleHint(lang: String) = if (lang == AppLanguage.FR) "Titre de la note" else "Tit nòt la"
    fun noteContentHint(lang: String) = if (lang == AppLanguage.FR) "Écrivez votre note ici..." else "Ekri nòt ou la a..."
    fun save(lang: String) = if (lang == AppLanguage.FR) "Enregistrer" else "Sove"
    fun cancel(lang: String) = if (lang == AppLanguage.FR) "Annuler" else "Anile"
    fun delete(lang: String) = if (lang == AppLanguage.FR) "Supprimer" else "Efase"

    // Bookmarks
    fun bookmarksTitle(lang: String) = if (lang == AppLanguage.FR) "Mes Favoris" else "Favori mwen yo"
    fun noBookmarks(lang: String) = if (lang == AppLanguage.FR) "Vous n'avez encore aucun favori." else "Ou poko gen okenn favori."

    // Search
    fun searchTitle(lang: String) = if (lang == AppLanguage.FR) "Recherche Biblique" else "Rechèch nan Bib la"
    fun searchAll(lang: String) = if (lang == AppLanguage.FR) "Toute la Bible" else "Tout Bib la"
    fun searchNoResults(lang: String) = if (lang == AppLanguage.FR) "Aucun verset trouvé pour cette recherche." else "Pa gen vèsè ki koresponn ak rechèch sa a."
    fun recentSearches(lang: String) = if (lang == AppLanguage.FR) "Recherches Récentes" else "Rechèch Resan Yo"
}
