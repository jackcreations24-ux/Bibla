package com.zoutiw.bibla.data

object BibleData {
    val oldTestament = listOf(
        "Jenèz", "Egzòd", "Levitik", "Nonm", "Detewonòm",
        "Jozye", "Jij", "Rit", "1 Samyèl", "2 Samyèl",
        "1 Wa", "2 Wa", "1 Kwonik", "2 Kwonik", "Esdras",
        "Neemi", "Estè", "Jòb", "Sòm", "Pwovèb",
        "Eklezyas", "Chante Salomon", "Ezayi", "Jeremi", "Lamantasyon",
        "Ezekyèl", "Danyèl", "Oze", "Jowèl", "Amòs",
        "Abdyas", "Jonas", "Miche", "Naoum", "Abakouk",
        "Sofoni", "Aje", "Zakari", "Malaki"
    )

    val newTestament = listOf(
        "Matye", "Mak", "Lik", "Jan", "Travay",
        "Women", "1 Korentyen", "2 Korentyen", "Galat", "Efèzyen",
        "Filipyen", "Kolosyen", "1 Tesalonisyen", "2 Tesalonisyen", "1 Timote",
        "2 Timote", "Tit", "Filemon", "Ebre", "Jak",
        "1 Pyè", "2 Pyè", "1 Jan", "2 Jan", "3 Jan",
        "Jid", "Revelasyon"
    )

    val allBooks = oldTestament + newTestament

    val genesisVerses = GenesisData.verses
    val exodusVerses = ExodusData.verses
    val leviticusVerses = LeviticusData.verses
    val numbersVerses = NumbersData.verses
    val deuteronomyVerses = DeuteronomyData.verses
    val joshuaVerses = JoshuaData.verses
    val judgesVerses = JudgesData.verses
    val ruthVerses = RuthData.verses
    val firstSamuelVerses = FirstSamuelData.verses
    val secondSamuelVerses = SecondSamuelData.verses
    val firstKingsVerses = FirstKingsData.verses
    val secondKingsVerses = SecondKingsData.verses
    val firstChroniclesVerses = FirstChroniclesData.verses
    val secondChroniclesVerses = SecondChroniclesData.verses
    val ezraVerses = EzraData.verses
    val nehemiahVerses = NehemiahData.verses
    val estherVerses = EstherData.verses
    val jobVerses = JobData.verses
    val psalmsVerses = PsalmsData.verses
    val proverbsVerses = ProverbsData.verses
    val ecclesiastesVerses = EcclesiastesData.verses
    val songOfSolomonVerses = SongOfSolomonData.verses
    val isaiahVerses = IsaiahData.verses
    val jeremiahVerses = JeremiahData.verses
    val lamentationsVerses = LamentationsData.verses
    val ezekielVerses = EzekielData.verses
    val danielVerses = DanielData.verses
    val hoseaVerses = HoseaData.verses
    val joelVerses = JoelData.verses
    val amosVerses = AmosData.verses
    val obadiahVerses = ObadiahData.verses
    val jonahVerses = JonahData.verses
    val micahVerses = MicahData.verses
    val nahumVerses = NahumData.verses
    val habakkukVerses = HabakkukData.verses
    val zephaniahVerses = ZephaniahData.verses
    val haggaiVerses = HaggaiData.verses
    val zechariahVerses = ZechariahData.verses
    val malachiVerses = MalachiData.verses
    val matthewVerses = MatthewData.verses
    val markVerses = MarkData.verses
    val lukeVerses = LukeData.verses
    val johnVerses = JohnData.verses
    val actsVerses = ActsData.verses
    val romansVerses = RomansData.verses
    val firstCorinthiansVerses = FirstCorinthiansData.verses
    val secondCorinthiansVerses = SecondCorinthiansData.verses
    val galatiansVerses = GalatiansData.verses
    val ephesiansVerses = EphesiansData.verses
    val philippiansVerses = PhilippiansData.verses
    val colossiansVerses = ColossiansData.verses
    val firstThessaloniansVerses = FirstThessaloniansData.verses
    val secondThessaloniansVerses = SecondThessaloniansData.verses
    val firstTimothyVerses = FirstTimothyData.verses
    val secondTimothyVerses = SecondTimothyData.verses
    val titusVerses = TitusData.verses
    val philemonVerses = PhilemonData.verses
    val hebrewsVerses = HebrewsData.verses
    val jamesVerses = JamesData.verses
    val firstPeterVerses = FirstPeterData.verses
    val secondPeterVerses = SecondPeterData.verses
    val firstJohnVerses = FirstJohnData.verses
    val secondJohnVerses = SecondJohnData.verses
    val thirdJohnVerses = ThirdJohnData.verses
    val judeVerses = JudeData.verses
    val revelationVerses = RevelationData.verses

    fun getChapterCount(book: String): Int = when(book) {
        "Jenèz" -> 50
        "Egzòd" -> 40
        "Levitik" -> 27
        "Nonm", "Nòm" -> 36
        "Detewonòm" -> 34
        "Jozye" -> 24
        "Jij" -> 21
        "Rit" -> 4
        "1 Samyèl" -> 31
        "2 Samyèl" -> 24
        "1 Wa" -> 22
        "2 Wa" -> 25
        "1 Kwonik" -> 29
        "2 Kwonik" -> 36
        "Esdras" -> 10
        "Neemi" -> 13
        "Estè" -> 10
        "Jòb" -> 42
        "Sòm" -> 150
        "Pwovèb" -> 31
        "Eklezyas" -> 12
        "Chante Salomon", "Kantik" -> 8
        "Ezayi" -> 66
        "Jeremi" -> 52
        "Lamantasyon", "Lamentasyon" -> 5
        "Ezekyèl" -> 48
        "Danyèl", "Daniel" -> 12
        "Oze", "Hosea" -> 14
        "Jowèl", "Joel" -> 3
        "Amòs", "Amos" -> 9
        "Abdyas", "Obadiah" -> 1
        "Jonas", "Jonah" -> 4
        "Miche", "Mika", "Micah" -> 7
        "Naoum", "Nahum" -> 3
        "Abakouk", "Abakik", "Habakkuk" -> 3
        "Sofoni", "Zephaniah" -> 3
        "Aje", "Agée", "Haggai" -> 2
        "Zakari", "Zacharie", "Zechariah" -> 14
        "Makachi", "Malaki", "Malachi", "Malachie" -> 4
        "Matye" -> 28
        "Mak", "Mark", "Marc" -> 16
        "Lik", "Luke", "Luc" -> 24
        "Jan", "John", "Jean" -> 21
        "Travay", "Akt", "Acts", "Actes" -> 28
        "Women", "Romans", "Romains" -> 16
        "1 Korentyen", "1 Corinthians", "1 Corinthiens" -> 16
        "2 Korentyen", "2 Corinthians", "2 Corinthiens" -> 13
        "Galat", "Galasi", "Galatians", "Galates" -> 6
        "Efèzyen", "Efèz", "Ephesians", "Éphésiens" -> 6
        "Filipyen", "Filip", "Philippians", "Philippiens" -> 4
        "Kolosyen", "Kolos", "Colossians", "Colossiens" -> 4
        "1 Tesalonisyen", "1 Thessalonians", "1 Thessaloniciens" -> 5
        "2 Tesalonisyen", "2 Thessalonians", "2 Thessaloniciens" -> 3
        "1 Timote", "1 Timothy", "1 Timothée" -> 6
        "2 Timote", "2 Timothy", "2 Timothée" -> 4
        "Tit", "Titus", "Tite" -> 3
        "Filemon", "Philemon", "Philémon" -> 1
        "Ebre", "Hebrews", "Hébreux" -> 13
        "Jak", "James", "Jacques" -> 5
        "1 Pyè", "1 Peter", "1 Pierre" -> 5
        "2 Pyè", "2 Peter", "2 Pierre" -> 3
        "1 Jan", "1 John", "1 Jean" -> 5
        "2 Jan", "2 John", "2 Jean" -> 1
        "3 Jan", "3 John", "3 Jean" -> 1
        "Jid", "Jude" -> 1
        "Revelasyon", "Revelation", "Apocalypse" -> 22
        else -> 10
    }
}

