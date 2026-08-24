package com.example.data

data class ReadingPassage(
    val book: String,
    val startChapter: Int,
    val endChapter: Int = startChapter,
    val displayReference: String = if (startChapter == endChapter) "$book $startChapter" else "$book $startChapter-$endChapter"
)

data class ReadingPlanDay(
    val dayNumber: Int,
    val title: String,
    val passages: List<ReadingPassage>
)

data class ReadingPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val totalDays: Int,
    val category: String,
    val iconName: String,
    val colorPrimaryHex: Long,
    val colorSecondaryHex: Long,
    val days: List<ReadingPlanDay>
)

object ReadingPlanRepository {

    val allPlans: List<ReadingPlan> by lazy {
        listOf(
            create365DayPlan(),
            createNewTestament90Plan(),
            createPsalmsProverbs30Plan(),
            createGospels30Plan(),
            createFaithAndPeace14Plan()
        )
    }

    fun getPlanById(id: String): ReadingPlan {
        return allPlans.find { it.id == id } ?: allPlans.first()
    }

    // 1. 365 Days Plan - Whole Bible
    private fun create365DayPlan(): ReadingPlan {
        val days = mutableListOf<ReadingPlanDay>()
        
        val otBooks = listOf(
            "Jenèz" to 50, "Egzòd" to 40, "Levitik" to 27, "Nonb" to 36, "Deteronòm" to 34,
            "Jochwa" to 24, "Jij" to 21, "Rout" to 4, "1 Samyèl" to 31, "2 Samyèl" to 24,
            "1 Wa" to 22, "2 Wa" to 25, "1 Kwonik" to 29, "2 Kwonik" to 36, "Esdras" to 10,
            "Neyemi" to 13, "Estè" to 10, "Jòb" to 42, "Sòm" to 150, "Pwovèb" to 31,
            "Eklezyas" to 12, "Chante Salomon" to 8, "Ezayi" to 66, "Jeremi" to 52,
            "Lamantasyon" to 5, "Ezekyèl" to 48, "Danyèl" to 12, "Oze" to 14, "Jowèl" to 3,
            "Amòs" to 9, "Abdyas" to 1, "Jonas" to 4, "Miche" to 7, "Naoum" to 3,
            "Abakouk" to 3, "Sofoni" to 3, "Aje" to 2, "Zakari" to 14, "Malaki" to 4
        )

        val ntBooks = listOf(
            "Matye" to 28, "Mak" to 16, "Lik" to 24, "Jan" to 21, "Travay" to 28,
            "Women" to 16, "1 Korentyen" to 16, "2 Korentyen" to 13, "Galat" to 6,
            "Efèzyen" to 6, "Filipyen" to 4, "Kolosyen" to 4, "1 Tesalonik" to 5,
            "2 Tesalonik" to 3, "1 Timote" to 6, "2 Timote" to 4, "Tit" to 3,
            "Filemon" to 1, "Ebre" to 13, "Jak" to 5, "1 Pyè" to 5, "2 Pyè" to 3,
            "1 Jan" to 5, "2 Jan" to 1, "3 Jan" to 1, "Jid" to 1, "Revelasyon" to 22
        )

        var otBookIndex = 0
        var otChap = 1
        var ntBookIndex = 0
        var ntChap = 1

        for (day in 1..365) {
            val passages = mutableListOf<ReadingPassage>()

            // 2-3 OT chapters
            if (otBookIndex < otBooks.size) {
                val (otBook, totalOt) = otBooks[otBookIndex]
                val endOt = Math.min(otChap + 2, totalOt)
                passages.add(ReadingPassage(book = otBook, startChapter = otChap, endChapter = endOt))
                if (endOt >= totalOt) {
                    otBookIndex++
                    otChap = 1
                } else {
                    otChap = endOt + 1
                }
            }

            // 1 NT chapter
            if (ntBookIndex < ntBooks.size) {
                val (ntBook, totalNt) = ntBooks[ntBookIndex]
                passages.add(ReadingPassage(book = ntBook, startChapter = ntChap, endChapter = ntChap))
                if (ntChap >= totalNt) {
                    ntBookIndex++
                    ntChap = 1
                } else {
                    ntChap++
                }
            }

            val title = "Jou $day: ${passages.joinToString(", ") { it.displayReference }}"
            days.add(ReadingPlanDay(dayNumber = day, title = title, passages = passages))
        }

        return ReadingPlan(
            id = "full_bible_365",
            title = "Tout Bib la nan 1 An",
            subtitle = "365 Jou • Ansyen ak Nouvo Testaman",
            description = "Li tout Bib la nan yon ane avèk yon lekti ekilibre chak jou nan Ansyen Testaman an ak Nouvo Testaman an.",
            totalDays = 365,
            category = "Konplè",
            iconName = "Book",
            colorPrimaryHex = 0xFF10B981, // Emerald
            colorSecondaryHex = 0xFF059669,
            days = days
        )
    }

    // 2. New Testament in 90 Days
    private fun createNewTestament90Plan(): ReadingPlan {
        val days = mutableListOf<ReadingPlanDay>()
        val ntBooks = listOf(
            "Matye" to 28, "Mak" to 16, "Lik" to 24, "Jan" to 21, "Travay" to 28,
            "Women" to 16, "1 Korentyen" to 16, "2 Korentyen" to 13, "Galat" to 6,
            "Efèzyen" to 6, "Filipyen" to 4, "Kolosyen" to 4, "1 Tesalonik" to 5,
            "2 Tesalonik" to 3, "1 Timote" to 6, "2 Timote" to 4, "Tit" to 3,
            "Filemon" to 1, "Ebre" to 13, "Jak" to 5, "1 Pyè" to 5, "2 Pyè" to 3,
            "1 Jan" to 5, "2 Jan" to 1, "3 Jan" to 1, "Jid" to 1, "Revelasyon" to 22
        )

        var bookIndex = 0
        var chap = 1

        for (day in 1..90) {
            val passages = mutableListOf<ReadingPassage>()
            var chaptersRemainingForDay = 3

            while (chaptersRemainingForDay > 0 && bookIndex < ntBooks.size) {
                val (book, totalChaps) = ntBooks[bookIndex]
                val chaptersCanRead = Math.min(chaptersRemainingForDay, totalChaps - chap + 1)
                val endChap = chap + chaptersCanRead - 1

                passages.add(ReadingPassage(book = book, startChapter = chap, endChapter = endChap))
                chaptersRemainingForDay -= chaptersCanRead

                if (endChap >= totalChaps) {
                    bookIndex++
                    chap = 1
                } else {
                    chap = endChap + 1
                }
            }

            if (passages.isEmpty() && days.isNotEmpty()) {
                passages.addAll(days.last().passages)
            }

            val title = "Jou $day: ${passages.joinToString(", ") { it.displayReference }}"
            days.add(ReadingPlanDay(dayNumber = day, title = title, passages = passages))
        }

        return ReadingPlan(
            id = "new_testament_90",
            title = "Nouvo Testaman nan 90 Jou",
            subtitle = "90 Jou • Matye rive Revelasyon",
            description = "Dekouvri tout lavi Jezi, istwa premye legliz la, ak lèt apòt yo nan 3 mwa sèlman.",
            totalDays = 90,
            category = "Nouvo Testaman",
            iconName = "MenuBook",
            colorPrimaryHex = 0xFF3B82F6, // Blue
            colorSecondaryHex = 0xFF1D4ED8,
            days = days
        )
    }

    // 3. Psalms and Proverbs in 30 Days
    private fun createPsalmsProverbs30Plan(): ReadingPlan {
        val days = mutableListOf<ReadingPlanDay>()
        for (day in 1..30) {
            val psalmStart = (day - 1) * 5 + 1
            val psalmEnd = day * 5
            val passages = listOf(
                ReadingPassage(book = "Sòm", startChapter = psalmStart, endChapter = psalmEnd),
                ReadingPassage(book = "Pwovèb", startChapter = Math.min(day, 31), endChapter = Math.min(day, 31))
            )
            val title = "Jou $day: Sòm $psalmStart-$psalmEnd & Pwovèb $day"
            days.add(ReadingPlanDay(dayNumber = day, title = title, passages = passages))
        }

        return ReadingPlan(
            id = "psalms_proverbs_30",
            title = "Sòm ak Pwovèb nan 30 Jou",
            subtitle = "30 Jou • Lapriyè, Lwanj ak Sajès",
            description = "Nouri nanm ou chak jou avèk 5 chapit nan liv Sòm yo pou lapriyè epi 1 chapit nan Pwovèb pou sajès.",
            totalDays = 30,
            category = "Sajès & Lwanj",
            iconName = "Favorite",
            colorPrimaryHex = 0xFF8B5CF6, // Purple
            colorSecondaryHex = 0xFF6D28D9,
            days = days
        )
    }

    // 4. 4 Gospels in 30 Days
    private fun createGospels30Plan(): ReadingPlan {
        val days = mutableListOf<ReadingPlanDay>()
        val gospels = listOf(
            "Matye" to 28, "Mak" to 16, "Lik" to 24, "Jan" to 21
        )

        var gIndex = 0
        var chap = 1

        for (day in 1..30) {
            val passages = mutableListOf<ReadingPassage>()
            var quota = 3

            while (quota > 0 && gIndex < gospels.size) {
                val (gBook, maxChaps) = gospels[gIndex]
                val take = Math.min(quota, maxChaps - chap + 1)
                val endChap = chap + take - 1
                passages.add(ReadingPassage(book = gBook, startChapter = chap, endChapter = endChap))
                quota -= take

                if (endChap >= maxChaps) {
                    gIndex++
                    chap = 1
                } else {
                    chap = endChap + 1
                }
            }

            val title = "Jou $day: ${passages.joinToString(", ") { it.displayReference }}"
            days.add(ReadingPlanDay(dayNumber = day, title = title, passages = passages))
        }

        return ReadingPlan(
            id = "gospels_30",
            title = "4 Evanjil yo nan 30 Jou",
            subtitle = "30 Jou • Lavi ak Ansèyman Jezi",
            description = "Fikse je w sou Jezi Kris atravè lekti Matye, Mak, Lik ak Jan nan yon mwa.",
            totalDays = 30,
            category = "Evanjil",
            iconName = "WbSunny",
            colorPrimaryHex = 0xFFF59E0B, // Amber
            colorSecondaryHex = 0xFFD97706,
            days = days
        )
    }

    // 5. Faith, Peace and Hope in 14 Days
    private fun createFaithAndPeace14Plan(): ReadingPlan {
        val curatedDays = listOf(
            ReadingPlanDay(1, "Jou 1: Pwoteksyon ak Konfyans nan Bondye", listOf(ReadingPassage("Sòm", 91), ReadingPassage("Sòm", 23))),
            ReadingPlanDay(2, "Jou 2: Renmen Bondye ak Lapè Li", listOf(ReadingPassage("Jan", 14), ReadingPassage("Women", 8))),
            ReadingPlanDay(3, "Jou 3: Lafwa ki Deplase Mòn", listOf(ReadingPassage("Ebre", 11))),
            ReadingPlanDay(4, "Jou 4: Lapriyè ki Gen Pouvwa", listOf(ReadingPassage("Jak", 5), ReadingPassage("Matye", 6))),
            ReadingPlanDay(5, "Jou 5: Renmen san Kondisyon", listOf(ReadingPassage("1 Korentyen", 13))),
            ReadingPlanDay(6, "Jou 6: Fòs nan Mitan Eprèv yo", listOf(ReadingPassage("Ezayi", 40), ReadingPassage("Ezayi", 41))),
            ReadingPlanDay(7, "Jou 7: Joye ak Lapè nan Kè w", listOf(ReadingPassage("Filipyen", 4))),
            ReadingPlanDay(8, "Jou 8: Zam Espirityèl Bondye yo", listOf(ReadingPassage("Efèzyen", 6))),
            ReadingPlanDay(9, "Jou 9: Gran Fòs Bondye nan Feblès Nou", listOf(ReadingPassage("2 Korentyen", 12), ReadingPassage("Sòm", 46))),
            ReadingPlanDay(10, "Jou 10: Grace ak Delivrans", listOf(ReadingPassage("Efèzyen", 2), ReadingPassage("Tit", 3))),
            ReadingPlanDay(11, "Jou 11: Bondye se Gadò Mwen", listOf(ReadingPassage("Sòm", 121), ReadingPassage("Jan", 10))),
            ReadingPlanDay(12, "Jou 12: Mache nan Lespri Sen an", listOf(ReadingPassage("Galat", 5))),
            ReadingPlanDay(13, "Jou 13: Yon Nouvo Kreyasyon", listOf(ReadingPassage("2 Korentyen", 5), ReadingPassage("Kolosyen", 3))),
            ReadingPlanDay(14, "Jou 14: Viktwa Total nan Jezi Kris", listOf(ReadingPassage("Revelasyon", 21), ReadingPassage("Revelasyon", 22)))
        )

        return ReadingPlan(
            id = "faith_hope_14",
            title = "Lafwa ak Espwa nan 14 Jou",
            subtitle = "14 Jou • Kouraj, Lapè ak Fòs Espirityèl",
            description = "Ranfòse lafwa w epi jwenn lapè nan mitan nenpòt sitiyasyon avèk vèsè ak chapit ki pi ankourajan nan Bib la.",
            totalDays = 14,
            category = "Kwasans",
            iconName = "Shield",
            colorPrimaryHex = 0xFFEC4899, // Pink
            colorSecondaryHex = 0xFFDB2777,
            days = curatedDays
        )
    }
}
