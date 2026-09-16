package com.zoutiw.bibla.data

import android.content.Context
import com.zoutiw.bibla.ui.util.BibleBookNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class FrenchBibleRepository(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val cacheDir = File(context.cacheDir, "french_bible").apply {
        if (!exists()) mkdirs()
    }

    // In-memory cache for ultra-fast chapter switching
    private val memCache = mutableMapOf<String, Map<Int, String>>()

    // In-memory cache for parsed asset books: usfm -> (chapter -> (verseNum -> verseText))
    private val bookCache = mutableMapOf<String, Map<Int, Map<Int, String>>>()

    private fun loadBookFromAssets(usfm: String): Map<Int, Map<Int, String>>? {
        bookCache[usfm]?.let { return it }
        return try {
            val assetPath = "french_bible/$usfm.json"
            context.assets.open(assetPath).use { inputStream ->
                val jsonStr = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val root = JSONObject(jsonStr)
                val bookMap = mutableMapOf<Int, Map<Int, String>>()
                val chapterKeys = root.keys()
                while (chapterKeys.hasNext()) {
                    val chKey = chapterKeys.next()
                    val chNum = chKey.toIntOrNull() ?: continue
                    val chObj = root.optJSONObject(chKey) ?: continue
                    val versesMap = mutableMapOf<Int, String>()
                    val verseKeys = chObj.keys()
                    while (verseKeys.hasNext()) {
                        val vKey = verseKeys.next()
                        val vNum = vKey.toIntOrNull() ?: continue
                        val vText = chObj.optString(vKey, "")
                        if (vText.isNotBlank()) {
                            versesMap[vNum] = vText
                        }
                    }
                    if (versesMap.isNotEmpty()) {
                        bookMap[chNum] = versesMap
                    }
                }
                if (bookMap.isNotEmpty()) {
                    bookCache[usfm] = bookMap
                    bookMap
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Pre-bundled key chapters for instant offline guarantee
    private val prebundledChapters: Map<String, Map<Int, String>> = mapOf(
        "GEN_1" to mapOf(
            1 to "Au commencement, Dieu créa les cieux et la terre.",
            2 to "La terre était informe et vide: il y avait des ténèbres à la surface de l'abîme, et l'esprit de Dieu se mouvait au-dessus des eaux.",
            3 to "Dieu dit: Que la lumière soit! Et la lumière fut.",
            4 to "Dieu vit que la lumière était bonne; et Dieu sépara la lumière d'avec les ténèbres.",
            5 to "Dieu appela la lumière jour, et il appela les ténèbres nuit. Ainsi, il y eut un soir, et il y eut un matin: ce fut le premier jour."
        ),
        "PSA_23" to mapOf(
            1 to "L'Éternel est mon berger: je ne manquerai de rien.",
            2 to "Il me fait reposer dans de verts pâturages, Il me dirige près des eaux paisibles.",
            3 to "Il restaure mon âme, Il me conduit dans les sentiers de la justice, À cause de son nom.",
            4 to "Quand je marche dans la vallée de l'ombre de la mort, Je ne crains aucun mal, car tu es avec moi: Ta houlette et ton bâton me rassurent.",
            5 to "Tu dresses devant moi une table, En face de mes adversaires; Tu oins d'huile ma tête, Et ma coupe déborde.",
            6 to "Oui, le bonheur et la grâce m'accompagneront Tous les jours de ma vie, Et j'habiterai dans la maison de l'Éternel Jusqu'à la fin de mes jours."
        ),
        "JHN_1" to mapOf(
            1 to "Au commencement était la Parole, et la Parole était avec Dieu, et la Parole était Dieu.",
            2 to "Elle était au commencement avec Dieu.",
            3 to "Toutes choses ont été faites par elle, et rien de ce qui a été fait n'a été fait sans elle.",
            4 to "En elle était la vie, et la vie était la lumière des hommes.",
            5 to "La lumière luit dans les ténèbres, et les ténèbres ne l'ont point reçue.",
            14 to "Et la parole a été faite chair, et elle a habité parmi nous, pleine de grâce et de vérité; et nous avons contemplé sa gloire, une gloire comme la gloire du Fils unique venu du Père."
        ),
        "JHN_3" to mapOf(
            16 to "Car Dieu a tant aimé le monde qu'il a donné son Fils unique, afin que quiconque croit en lui ne périsse point, mais qu'il ait la vie éternelle.",
            17 to "Dieu, en effet, n'a pas envoyé son Fils dans le monde pour qu'il juge le monde, mais pour que le monde soit sauvé par lui."
        )
    )

    fun getFrenchVerseSync(bookName: String, chapter: Int, verseNumber: Int): String? {
        val usfm = BibleBookNames.getUsfmCode(bookName)
        val key = "${usfm}_$chapter"
        memCache[key]?.get(verseNumber)?.let { return it }
        val loadedBook = loadBookFromAssets(usfm)
        val verseText = loadedBook?.get(chapter)?.get(verseNumber)
        if (verseText != null) {
            return verseText
        }
        return prebundledChapters[key]?.get(verseNumber)
    }

    fun searchFrenchBible(
        query: String,
        testament: String = "ALL",
        limit: Int = 40
    ): List<Triple<String, Int, Int>> {
        val cleanQuery = query.trim()
        if (cleanQuery.length < 2) return emptyList()

        val results = mutableListOf<Triple<String, Int, Int>>()
        val ntUsfms = listOf(
            "MAT", "MRK", "LUK", "JHN", "ACT", "ROM", "1CO", "2CO", "GAL", "EPH",
            "PHP", "COL", "1TH", "2TH", "1TI", "2TI", "TIT", "PHM", "HEB", "JAS",
            "1PE", "2PE", "1JN", "2JN", "3JN", "JUD", "REV"
        )
        val otUsfms = listOf(
            "GEN", "EXO", "LEV", "NUM", "DEU", "JOS", "JDG", "RUT", "1SA", "2SA",
            "1KI", "2KI", "1CH", "2CH", "EZR", "NEH", "EST", "JOB", "PSA", "PRO",
            "ECC", "SNG", "ISA", "JER", "LAM", "EZK", "DAN", "HOS", "JOL", "AMO",
            "OBA", "JON", "MIC", "NAM", "HAB", "ZEP", "HAG", "ZEC", "MAL"
        )

        val targetUsfms = when (testament) {
            "OT" -> otUsfms
            "NT" -> ntUsfms
            else -> ntUsfms + otUsfms
        }

        for (usfm in targetUsfms) {
            val bookMap = loadBookFromAssets(usfm) ?: continue
            val creoleBook = BibleBookNames.usfmToCreole[usfm] ?: continue
            for ((chNum, verses) in bookMap) {
                for ((vNum, text) in verses) {
                    if (text.contains(cleanQuery, ignoreCase = true)) {
                        results.add(Triple(creoleBook, chNum, vNum))
                        if (results.size >= limit) return results
                    }
                }
            }
        }
        return results
    }

    fun searchNewTestament(query: String, limit: Int = 40): List<Triple<String, Int, Int>> {
        return searchFrenchBible(query, "NT", limit)
    }

    suspend fun getFrenchVersesForChapter(bookName: String, chapter: Int): Map<Int, String> = withContext(Dispatchers.IO) {
        val usfm = BibleBookNames.getUsfmCode(bookName)
        val key = "${usfm}_$chapter"

        // 1. Check memory cache
        memCache[key]?.let { return@withContext it }

        // 2. Check bundled offline assets (All 27 New Testament books: 260 chapters)
        val loadedBook = loadBookFromAssets(usfm)
        val assetChapter = loadedBook?.get(chapter)
        if (!assetChapter.isNullOrEmpty()) {
            memCache[key] = assetChapter
            return@withContext assetChapter
        }

        // 3. Check disk cache
        val cacheFile = File(cacheDir, "$key.json")
        if (cacheFile.exists()) {
            try {
                val jsonString = cacheFile.readText()
                val parsed = parseVersesFromJson(jsonString)
                if (parsed.isNotEmpty()) {
                    memCache[key] = parsed
                    return@withContext parsed
                }
            } catch (e: Exception) {
                // Ignore and proceed to fetch
            }
        }

        // 4. Fetch from HelloAO Open Bible API (Louis Segond 1910)
        try {
            val url = "https://bible.helloao.org/api/fra_lsg/$usfm/$chapter.json"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val parsed = parseVersesFromJson(body)
                    if (parsed.isNotEmpty()) {
                        try {
                            cacheFile.writeText(body)
                        } catch (e: Exception) {
                            // Non-fatal cache write error
                        }
                        memCache[key] = parsed
                        return@withContext parsed
                    }
                }
            }
        } catch (e: Exception) {
            // Network error or offline
        }

        // 5. Pre-bundled fallback if available
        prebundledChapters[key]?.let { return@withContext it }

        emptyMap()
    }

    private fun parseVersesFromJson(jsonStr: String): Map<Int, String> {
        val result = mutableMapOf<Int, String>()
        try {
            val root = JSONObject(jsonStr)
            val chapterObj = root.optJSONObject("chapter") ?: return emptyMap()
            val contentArray = chapterObj.optJSONArray("content") ?: return emptyMap()

            for (i in 0 until contentArray.length()) {
                val item = contentArray.optJSONObject(i) ?: continue
                if (item.optString("type") == "verse") {
                    val verseNum = item.optInt("number", -1)
                    if (verseNum > 0) {
                        val cArray = item.optJSONArray("content")
                        val sb = StringBuilder()
                        if (cArray != null) {
                            for (j in 0 until cArray.length()) {
                                val elem = cArray.opt(j)
                                if (elem is String) {
                                    sb.append(elem)
                                } else if (elem is JSONObject) {
                                    sb.append(elem.optString("text", ""))
                                }
                            }
                        } else {
                            sb.append(item.optString("text", ""))
                        }
                        val text = sb.toString().trim()
                        if (text.isNotBlank()) {
                            result[verseNum] = text
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}

