package com.zoutiw.bibla.ui.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.zoutiw.bibla.ui.BibleViewModel
import com.zoutiw.bibla.ui.components.NeumorphicCard
import com.zoutiw.bibla.ui.theme.getBibleFontFamily
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

import androidx.compose.material.icons.filled.*
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.zoutiw.bibla.ui.components.NeumorphicCard
import com.zoutiw.bibla.data.BibleData

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    navController: NavController,
    viewModel: BibleViewModel,
    initialBook: String? = null,
    initialChapter: Int? = null
) {
    val context = LocalContext.current
    val currentVerses by viewModel.currentVerses.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val fontFamilyType by viewModel.fontFamilyType.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val bibleVersion by viewModel.bibleVersion.collectAsState()
    val frenchVerses by viewModel.frenchVersesForCurrentChapter.collectAsState()

    var showFontSizeSheet by remember { mutableStateOf(false) }
    var showVersionMenu by remember { mutableStateOf(false) }

    var selectedBook by remember { mutableStateOf(initialBook ?: progress?.book ?: "Jenèz") }
    var selectedChapter by remember { mutableStateOf(initialChapter ?: progress?.chapter ?: 1) }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = (initialChapter ?: progress?.chapter ?: 1) - 1,
        pageCount = { BibleData.getChapterCount(selectedBook) }
    )

    // Sync selectedChapter with pager state
    LaunchedEffect(pagerState.currentPage) {
        selectedChapter = pagerState.currentPage + 1
        viewModel.setVerses(selectedBook, selectedChapter)
        viewModel.saveProgress(selectedBook, selectedChapter, 1)
    }

    var isHeaderVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isHeaderVisible,
            enter = slideInVertically { -it } + expandVertically(),
            exit = slideOutVertically { -it } + shrinkVertically()
        ) {
            val displayedBook = com.zoutiw.bibla.ui.util.BibleBookNames.getDisplayName(
                selectedBook,
                if (bibleVersion == com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG) "fr" else appLanguage
            )

            TopAppBar(
                title = {
                    TextButton(onClick = { navController.navigate("book_selection") }) {
                        Text(
                            text = "$displayedBook $selectedChapter",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Quick Bible Version Switcher
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { showVersionMenu = true }
                        ) {
                            Text(
                                text = when (bibleVersion) {
                                    com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG -> "FR"
                                    com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL -> "FR+HT"
                                    else -> "HT"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showVersionMenu,
                            onDismissRequest = { showVersionMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🇭🇹 Bib Kreyòl Ayisyen") },
                                onClick = {
                                    viewModel.setBibleVersion(com.zoutiw.bibla.ui.util.BibleVersion.KREYOL)
                                    showVersionMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🇫🇷 Français (Louis Segond 1910)") },
                                onClick = {
                                    viewModel.setBibleVersion(com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG)
                                    showVersionMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📖 Kòt a kòt (Bilingue)") },
                                onClick = {
                                    viewModel.setBibleVersion(com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL)
                                    showVersionMenu = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = { showFontSizeSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = if (appLanguage == "fr") "Taille du Texte" else "Gwosè Tèks",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            val text = currentVerses.joinToString("\n") { v ->
                                val fr = frenchVerses[v.verseNumber]
                                when (bibleVersion) {
                                    com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG -> "${v.verseNumber}. ${fr ?: v.text}"
                                    com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL -> if (!fr.isNullOrBlank()) "${v.verseNumber}. ${v.text}\n[FR] $fr" else "${v.verseNumber}. ${v.text}"
                                    else -> "${v.verseNumber}. ${v.text}"
                                }
                            }
                            putExtra(Intent.EXTRA_TEXT, "$displayedBook $selectedChapter\n\n$text")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        val totalChapters = BibleData.getChapterCount(selectedBook)
        val bookProgress = if (totalChapters > 0) {
            ((pagerState.currentPage.toFloat() + 1f + pagerState.currentPageOffsetFraction) / totalChapters.toFloat()).coerceIn(0f, 1f)
        } else 0f

        LinearProgressIndicator(
            progress = { bookProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            val listState = rememberLazyListState()
            var prevIndex by remember { mutableIntStateOf(0) }
            var prevOffset by remember { mutableIntStateOf(0) }

            LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
                val curIndex = listState.firstVisibleItemIndex
                val curOffset = listState.firstVisibleItemScrollOffset
                if (curIndex > prevIndex || (curIndex == prevIndex && curOffset > prevOffset + 30)) {
                    isHeaderVisible = false
                } else if (curIndex < prevIndex || (curIndex == prevIndex && curOffset < prevOffset - 30)) {
                    isHeaderVisible = true
                }
                prevIndex = curIndex
                prevOffset = curOffset
            }

            // Page content
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        alpha = lerp(start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                        scaleY = lerp(start = 0.9f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                        rotationY = lerp(start = 10f, stop = 0f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(currentVerses, key = { it.id }) { verse ->
                            VerseItem(
                                verse = verse,
                                context = context,
                                isDarkTheme = isDarkTheme,
                                viewModel = viewModel,
                                frenchText = frenchVerses[verse.verseNumber],
                                bibleVersion = bibleVersion,
                                appLanguage = appLanguage
                            )
                        }
                        if (currentVerses.isEmpty()) {
                            item {
                                Text(
                                    if (appLanguage == "fr") "Aucun texte trouvé pour ce chapitre dans la base de données." else "Poko gen tèks pou chapit sa a nan baz done a.",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFontSizeSheet) {
        FontSizeSelectionSheet(
            textSizeMultiplier = textSizeMultiplier,
            fontFamilyType = fontFamilyType,
            isDarkTheme = isDarkTheme,
            appLanguage = appLanguage,
            onSetSize = { viewModel.setTextSizeMultiplier(it) },
            onSetFontFamily = { viewModel.setFontFamilyType(it) },
            onDismiss = { showFontSizeSheet = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerseItem(
    verse: com.zoutiw.bibla.data.Verse,
    context: android.content.Context,
    isDarkTheme: Boolean,
    viewModel: BibleViewModel,
    frenchText: String? = null,
    bibleVersion: String = com.zoutiw.bibla.ui.util.BibleVersion.KREYOL,
    appLanguage: String = "ht"
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val fontFamilyType by viewModel.fontFamilyType.collectAsState()
    val bibleFont = getBibleFontFamily(fontFamilyType)

    val highlightColors = listOf(
        Color(0xFFFFF176), // Yellow
        Color(0xFFA2F3A2), // Green
        Color(0xFFB3E5FC), // Blue
        Color(0xFFFFCDD2), // Pink
        Color(0xFFFFE0B2)  // Orange
    )

    val highlightColor = if (verse.bookmarkColor in 1..5) {
        highlightColors[verse.bookmarkColor - 1].copy(alpha = 0.5f)
    } else {
        if (isDarkTheme) Color(0xFFFDD835).copy(alpha = 0.4f) else Color(0xFFFFF176).copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (verse.isBookmarked) {
                    val path = Path().apply {
                        val strokeHeight = size.height * 0.8f
                        val topY = (size.height - strokeHeight) / 2
                        val bottomY = topY + strokeHeight
                        
                        moveTo(0f, topY + 2.dp.toPx())
                        
                        // Top wavy edge
                        val segments = 8
                        val segmentWidth = size.width / segments
                        for (i in 1..segments) {
                            val x = i * segmentWidth
                            val variance = if (i % 2 == 0) 3.dp.toPx() else -2.dp.toPx()
                            lineTo(x, topY + variance)
                        }
                        
                        // Right edge variation
                        lineTo(size.width + 1.dp.toPx(), bottomY - 2.dp.toPx())
                        
                        // Bottom wavy edge
                        for (i in segments downTo 0) {
                            val x = i * segmentWidth
                            val variance = if (i % 2 == 0) -2.dp.toPx() else 3.dp.toPx()
                            lineTo(x, bottomY + variance)
                        }
                        
                        close()
                    }
                    drawPath(
                        path = path,
                        color = highlightColor
                    )
                }
            }
            .combinedClickable(
                onClick = { /* Could toggle selection or highlights here in the future */ },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMenu = true
                }
            )
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${verse.verseNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp * textSizeMultiplier,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp, top = (4 * textSizeMultiplier).dp)
                )

                val displayText = when (bibleVersion) {
                    com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG -> frenchText ?: verse.text
                    else -> verse.text
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = bibleFont,
                        fontSize = 16.sp * textSizeMultiplier,
                        lineHeight = (28 * textSizeMultiplier).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // In Bilingual mode, show the French verse right below the Creole verse
                    if (bibleVersion == com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL && !frenchText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🇫🇷 $frenchText",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = bibleFont,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 14.5.sp * textSizeMultiplier,
                            lineHeight = (23 * textSizeMultiplier).sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                highlightColors.forEachIndexed { index, color ->
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .combinedClickable(
                                onClick = {
                                    showMenu = false
                                    viewModel.toggleBookmark(verse, index + 1)
                                }
                            ),
                        color = color,
                        border = if (verse.bookmarkColor == index + 1) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {}
                }
            }
            Divider()
            DropdownMenuItem(
                text = {
                    Text(
                        if (appLanguage == "fr") {
                            if (verse.isBookmarked) "Retirer des favoris" else "Ajouter aux favoris"
                        } else {
                            if (verse.isBookmarked) "Retire nan favori" else "Ajoute nan favori"
                        }
                    )
                },
                onClick = {
                    showMenu = false
                    viewModel.toggleBookmark(verse)
                },
                leadingIcon = { 
                    Icon(
                        if (verse.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                        contentDescription = null,
                        tint = if (verse.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ) 
                }
            )
            DropdownMenuItem(
                text = { Text(if (appLanguage == "fr") "Ajouter une note" else "Ajoute nòt") },
                onClick = {
                    showMenu = false
                    showAddNoteDialog = true
                },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text(if (appLanguage == "fr") "Partager le verset" else "Pataje vèsè") },
                onClick = {
                    showMenu = false
                    val shareBook = com.zoutiw.bibla.ui.util.BibleBookNames.getDisplayName(verse.book, if (bibleVersion == com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG) "fr" else appLanguage)
                    val shareVerseText = when (bibleVersion) {
                        com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG -> frenchText ?: verse.text
                        com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL -> if (!frenchText.isNullOrBlank()) "${verse.text}\n\n[FR] $frenchText" else verse.text
                        else -> verse.text
                    }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$shareBook ${verse.chapter}:${verse.verseNumber}\n\n$shareVerseText")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                },
                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
            )
        }

        if (showAddNoteDialog) {
            var noteTitle by remember { mutableStateOf("") }
            var noteContent by remember { mutableStateOf("") }
            val noteBookName = com.zoutiw.bibla.ui.util.BibleBookNames.getDisplayName(verse.book, if (bibleVersion == com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG) "fr" else appLanguage)
            AlertDialog(
                onDismissRequest = { showAddNoteDialog = false },
                title = { Text(if (appLanguage == "fr") "Note pour $noteBookName ${verse.chapter}:${verse.verseNumber}" else "Nòt pou $noteBookName ${verse.chapter}:${verse.verseNumber}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        NeumorphicCard(isDarkTheme = isDarkTheme) {
                            val dialogVerseText = when (bibleVersion) {
                                com.zoutiw.bibla.ui.util.BibleVersion.FRANCAIS_LSG -> frenchText ?: verse.text
                                com.zoutiw.bibla.ui.util.BibleVersion.BILINGUAL -> if (!frenchText.isNullOrBlank()) "${verse.text}\n\n[FR] $frenchText" else verse.text
                                else -> verse.text
                            }
                            Text(
                                text = dialogVerseText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        OutlinedTextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(if (appLanguage == "fr") "TITRE DE LA NOTE (OPTIONNEL)" else "TIT NÒT LA (OPSYONÈL)") },
                            placeholder = { Text(if (appLanguage == "fr") "Donnez un titre à cette note..." else "Mete yon tit pou nòt sa a...") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            label = { Text(if (appLanguage == "fr") "CORPS DE LA NOTE" else "KÒ NÒT LA") },
                            placeholder = { Text(if (appLanguage == "fr") "Écrivez votre note ici..." else "Tape kontni nòt ou la...") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (noteContent.isNotBlank() || noteTitle.isNotBlank()) {
                            val fullContent = formatNoteContent(noteTitle, "", noteContent)
                            viewModel.addNote(verse.id, fullContent)
                            showAddNoteDialog = false
                        }
                    }) {
                        Text(if (appLanguage == "fr") "Enregistrer" else "Anrejistre")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNoteDialog = false }) {
                        Text(if (appLanguage == "fr") "Annuler" else "Anile")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontSizeSelectionSheet(
    textSizeMultiplier: Float,
    fontFamilyType: String,
    isDarkTheme: Boolean,
    appLanguage: String = "ht",
    onSetSize: (Float) -> Unit,
    onSetFontFamily: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bibleFont = getBibleFontFamily(fontFamilyType)

    val fontPresets = if (appLanguage == "fr") {
        listOf(
            0.8f to "80% (Petit)",
            1.0f to "100% (Normal)",
            1.2f to "120% (Moyen)",
            1.4f to "140% (Grand)",
            1.6f to "160% (Très Grand)",
            1.8f to "180% (Plus Grand)",
            2.0f to "200% (Max)"
        )
    } else {
        listOf(
            0.8f to "80% (Ti)",
            1.0f to "100% (Nòmal)",
            1.2f to "120% (Mwayen)",
            1.4f to "140% (Gwo)",
            1.6f to "160% (Trè Gwo)",
            1.8f to "180% (Pli Gwo)",
            2.0f to "200% (Maks)"
        )
    }

    val fontStyleOptions = listOf(
        Triple("sans_serif", "Sans-Serif", FontFamily.SansSerif),
        Triple("serif", "Serif", FontFamily.Serif),
        Triple("monospaced", "Monospaced", FontFamily.Monospace)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (appLanguage == "fr") "Format et Police du Texte" else "Fòma ak Font Tèks Bib la",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (appLanguage == "fr") "Fermer" else "Fèmen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (appLanguage == "fr") "Aperçu (Jean 3:16) :" else "Apèsi (Jan 3:16) :",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (appLanguage == "fr") {
                            "Car Dieu a tant aimé le monde qu'il a donné son Fils unique, afin que quiconque croit en lui ne périsse point, mais qu'il ait la vie éternelle."
                        } else {
                            "Paske Bondye sitèlman renmen lèzòm, li bay sèl Pitit li a pou yo, pou tout moun ki kwè nan li pa peri, men pou yo gen lavi ki p'ap janm fini an."
                        },
                        fontFamily = bibleFont,
                        fontSize = (16 * textSizeMultiplier).sp,
                        lineHeight = (26 * textSizeMultiplier).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Style Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (appLanguage == "fr") "Style de Police (Typographie) :" else "Style Font (Tipografi):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fontStyleOptions.forEach { (key, label, fontFam) ->
                    val isSelected = fontFamilyType.equals(key, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetFontFamily(key) },
                        modifier = Modifier.weight(1f),
                        label = {
                            Text(
                                text = label,
                                fontFamily = fontFam,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Size Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (appLanguage == "fr") "Taille du Texte actuelle :" else "Gwosè Tèks kounye a:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(textSizeMultiplier * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSetSize((textSizeMultiplier - 0.1f).coerceAtLeast(0.7f)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = if (appLanguage == "fr") "Diminuer" else "Diminye",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = textSizeMultiplier,
                    onValueChange = { onSetSize((it * 10).roundToInt() / 10f) },
                    valueRange = 0.7f..2.2f,
                    steps = 14,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = { onSetSize((textSizeMultiplier + 0.1f).coerceAtMost(2.2f)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = if (appLanguage == "fr") "Augmenter" else "Ogmante",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(fontPresets) { (size, label) ->
                    val isSelected = (textSizeMultiplier - size).absoluteValue < 0.05f
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetSize(size) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


