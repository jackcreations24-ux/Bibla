package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.BibleData
import com.example.data.Verse
import com.example.ui.BibleViewModel
import com.example.ui.BibleViewModel.SearchTestamentFilter
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.getBibleFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: BibleViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val currentTestamentFilter by viewModel.searchTestamentFilter.collectAsState()
    val selectedBookFilter by viewModel.selectedBookFilter.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.id }.toSet() }

    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val fontFamilyType by viewModel.fontFamilyType.collectAsState()

    var showBookFilterDialog by remember { mutableStateOf(false) }

    fun executeSearch(newQuery: String) {
        query = newQuery
        viewModel.setSearchQuery(newQuery)
        if (newQuery.trim().length >= 2) {
            viewModel.addRecentSearch(newQuery.trim())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retounen",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "Rechèch nan Bib la",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Jwenn nenpòt vèsè oswa mo kle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            if (query.isNotBlank() && searchResults.isNotEmpty()) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "${searchResults.size} jwenn",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // --- Search Input Box ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            NeumorphicCard(
                isDarkTheme = isDarkTheme,
                isPressed = true,
                cornerRadius = 16.dp,
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.setSearchQuery(it)
                    },
                    placeholder = {
                        Text(
                            text = "Tape yon mo oswa referans (egz: Jan 3:16, lapè)...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Chèche",
                            tint = if (query.isNotBlank()) PrimaryColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
                                    viewModel.setSearchQuery("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Efase",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.trim().length >= 2) {
                                viewModel.addRecentSearch(query.trim())
                            }
                            focusManager.clearFocus()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        // --- Filter Chips Row ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(SearchTestamentFilter.values()) { filter ->
                val isSelected = currentTestamentFilter == filter && selectedBookFilter == null
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setSelectedBookFilter(null)
                        viewModel.setSearchTestamentFilter(filter)
                    },
                    label = {
                        Text(
                            text = when (filter) {
                                SearchTestamentFilter.ALL -> "📖 Tout Bib la"
                                SearchTestamentFilter.OLD_TESTAMENT -> "📜 Ansyen Testaman"
                                SearchTestamentFilter.NEW_TESTAMENT -> "✝️ Nouvo Testaman"
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Book Filter Button
            item {
                val isBookSelected = selectedBookFilter != null
                FilterChip(
                    selected = isBookSelected,
                    onClick = { showBookFilterDialog = true },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = selectedBookFilter ?: "Filtre pa Liv",
                                fontWeight = if (isBookSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    },
                    trailingIcon = if (isBookSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Retire filtre liv",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { viewModel.setSelectedBookFilter(null) }
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // --- Content Area ---
        if (query.isBlank()) {
            // Empty Search View: Suggestions, Recent Searches, Popular References
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Dènye rechèch yo",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.clearRecentSearches() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Efase tout",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recentSearches.forEach { term ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDarkTheme) Color(0xFF262626) else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                        modifier = Modifier.clickable {
                                            executeSearch(term)
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = term,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Efase",
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { viewModel.removeRecentSearch(term) },
                                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Popular Spiritual Themes
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "Tèm Espirityèl Popilè",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val popularThemes = listOf(
                            ThemeSearchItem("❤️ Lanmou", "renmen", Color(0xFFEC4899)),
                            ThemeSearchItem("🕊️ Lapè", "lapè", Color(0xFF0EA5E9)),
                            ThemeSearchItem("🛡️ Lafwa", "lafwa", Color(0xFFF59E0B)),
                            ThemeSearchItem("🌿 Sajès", "sajès", Color(0xFF10B981)),
                            ThemeSearchItem("🌅 Espwa", "espwa", Color(0xFFF97316)),
                            ThemeSearchItem("🙌 Gras", "gras", Color(0xFF8B5CF6)),
                            ThemeSearchItem("🙏 Lapriyè", "priye", Color(0xFF6366F1)),
                            ThemeSearchItem("⚔️ Fòs & Kouraj", "kouraj", Color(0xFFEF4444)),
                            ThemeSearchItem("🤝 Padon", "padon", Color(0xFF14B8A6)),
                            ThemeSearchItem("💡 Limyè", "limyè", Color(0xFFEAB308)),
                            ThemeSearchItem("👑 Wayòm", "wayòm", Color(0xFF9333EA)),
                            ThemeSearchItem("🩸 Delivrans", "delivrans", Color(0xFFDC2626))
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            popularThemes.forEach { themeItem ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = themeItem.color.copy(alpha = if (isDarkTheme) 0.18f else 0.12f),
                                    border = BorderStroke(1.dp, themeItem.color.copy(alpha = 0.35f)),
                                    modifier = Modifier.clickable {
                                        executeSearch(themeItem.keyword)
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = themeItem.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDarkTheme) themeItem.color.copy(alpha = 0.9f) else themeItem.color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Famous Key Scripture References
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Vèsè Kle yo renmen anpil",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val popularVerses = listOf(
                            PopularVerseRef("Jan 3:16", "Paske Bondye sitèlman renmen lemonn..."),
                            PopularVerseRef("Sòm 23:1", "Seyè a se gadò mwen, mwen p'ap janm manke anyen..."),
                            PopularVerseRef("Filipyen 4:13", "Mwen kapab fè tout bagay grasa Kris la..."),
                            PopularVerseRef("Jeremi 29:11", "Plan mwen genyen pou nou se plan lapè..."),
                            PopularVerseRef("Pwovèb 3:5", "Mete tout konfyans ou nan Seyè a..."),
                            PopularVerseRef("Matye 6:33", "Chèche premyèman wayòm Bondye a..."),
                            PopularVerseRef("Women 8:28", "Tout bagay travay ansanm pou byen moun ki renmen Bondye..."),
                            PopularVerseRef("Ezayi 40:31", "Moun ki mete konfyans yo nan Seyè a jwenn nouvo fòs...")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            popularVerses.forEach { ref ->
                                NeumorphicCard(
                                    isDarkTheme = isDarkTheme,
                                    cornerRadius = 14.dp,
                                    elevation = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            executeSearch(ref.reference)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = ref.reference,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = ref.preview,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Search Results Active View
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "N'ap chèche nan tèks Bib la...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                // No Results State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = "Nou pa jwenn okenn vèsè",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Pa gen vèsè ki gen mo \"$query\" nan filtè ou chwazi a. Eseye chanje filtè a oswa tape yon lòt mo kle (egz: renmen, lapè, gras).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.setSearchTestamentFilter(SearchTestamentFilter.ALL)
                                viewModel.setSelectedBookFilter(null)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Chèche nan Tout Bib la")
                        }
                    }
                }
            } else {
                // List of Found Verses
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${searchResults.size} vèsè jwenn",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Klike sou yon vèsè pou li chapit la",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    items(searchResults, key = { it.id }) { verse ->
                        SearchResultVerseCard(
                            verse = verse,
                            searchQuery = query,
                            isDarkTheme = isDarkTheme,
                            textSizeMultiplier = textSizeMultiplier,
                            fontFamilyType = fontFamilyType,
                            isBookmarked = bookmarkedIds.contains(verse.id),
                            onCardClick = {
                                val encodedBook = android.net.Uri.encode(verse.book)
                                navController.navigate("reader/$encodedBook/${verse.chapter}")
                            },
                            onCopyClick = {
                                val textToCopy = "${verse.book} ${verse.chapter}:${verse.verseNumber}\n\"${verse.text}\""
                                clipboardManager.setText(AnnotatedString(textToCopy))
                                Toast.makeText(context, "Vèsè kopye: ${verse.book} ${verse.chapter}:${verse.verseNumber}", Toast.LENGTH_SHORT).show()
                            },
                            onShareClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${verse.book} ${verse.chapter}:${verse.verseNumber}\n\"${verse.text}\"\n\n— Bib La an Kreyòl")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Pataje vèsè"))
                            },
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(verse)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- Book Filter Selector Dialog ---
    if (showBookFilterDialog) {
        AlertDialog(
            onDismissRequest = { showBookFilterDialog = false },
            title = {
                Text(
                    text = "Chwazi yon liv pou filtre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedBookFilter == null) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedBookFilter(null)
                                    showBookFilterDialog = false
                                }
                        ) {
                            Text(
                                text = "📖 Tout liv yo (Pa gen filtre)",
                                fontWeight = if (selectedBookFilter == null) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedBookFilter == null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    item {
                        Text(
                            text = "ANSYEN TESTAMAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    items(BibleData.oldTestament) { book ->
                        val isSelected = selectedBookFilter == book
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedBookFilter(book)
                                    showBookFilterDialog = false
                                }
                        ) {
                            Text(
                                text = book,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "NOUVO TESTAMAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }

                    items(BibleData.newTestament) { book ->
                        val isSelected = selectedBookFilter == book
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedBookFilter(book)
                                    showBookFilterDialog = false
                                }
                        ) {
                            Text(
                                text = book,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookFilterDialog = false }) {
                    Text("Fèmen")
                }
            }
        )
    }
}

@Composable
fun SearchResultVerseCard(
    verse: Verse,
    searchQuery: String,
    isDarkTheme: Boolean,
    textSizeMultiplier: Float,
    fontFamilyType: String,
    isBookmarked: Boolean,
    onCardClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    val isNewTestament = remember(verse.book) {
        verse.book in BibleData.newTestament
    }

    val highlightedText = remember(verse.text, searchQuery, isDarkTheme) {
        buildHighlightedVerseText(verse.text, searchQuery, isDarkTheme)
    }

    NeumorphicCard(
        isDarkTheme = isDarkTheme,
        cornerRadius = 16.dp,
        elevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Reference Header with Testament Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${verse.book} ${verse.chapter}:${verse.verseNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp * textSizeMultiplier
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isNewTestament) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isNewTestament) "Nouvo Testaman" else "Ansyen Testaman",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isNewTestament) Color(0xFF3B82F6) else Color(0xFFD97706),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Bookmark toggle
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Favori",
                        tint = if (isBookmarked) PrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verse Text with Keyword Highlight
            Text(
                text = highlightedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = getBibleFontFamily(fontFamilyType),
                    fontSize = 15.sp * textSizeMultiplier,
                    lineHeight = (22 * textSizeMultiplier).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigate to reader
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onCardClick() }
                ) {
                    Text(
                        text = "Li nan chapit la",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Copy & Share buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeumorphicButton(
                        onClick = onCopyClick,
                        cornerRadius = 8.dp,
                        elevation = 1.dp,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.size(width = 72.dp, height = 30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Kopye",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Kopye",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    NeumorphicButton(
                        onClick = onShareClick,
                        cornerRadius = 8.dp,
                        elevation = 1.dp,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.size(width = 72.dp, height = 30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Pataje",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Pataje",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Builds an AnnotatedString highlighting matching keywords in the verse text.
 */
private fun buildHighlightedVerseText(
    text: String,
    query: String,
    isDarkTheme: Boolean
): AnnotatedString {
    val cleanQuery = query.trim()
    if (cleanQuery.length < 2) {
        return AnnotatedString(text)
    }

    // Split multiple search keywords
    val keywords = cleanQuery.split(Regex("\\s+")).filter { it.length >= 2 }
    if (keywords.isEmpty()) {
        return AnnotatedString(text)
    }

    val highlightColor = if (isDarkTheme) Color(0xFFFBBF24).copy(alpha = 0.35f) else Color(0xFFFEF08A)
    val highlightTextColor = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF854D0E)

    return buildAnnotatedString {
        append(text)
        keywords.forEach { keyword ->
            val pattern = Regex(Regex.escape(keyword), RegexOption.IGNORE_CASE)
            pattern.findAll(text).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1
                addStyle(
                    style = SpanStyle(
                        background = highlightColor,
                        color = highlightTextColor,
                        fontWeight = FontWeight.Bold
                    ),
                    start = start,
                    end = end
                )
            }
        }
    }
}

private data class ThemeSearchItem(
    val label: String,
    val keyword: String,
    val color: Color
)

private data class PopularVerseRef(
    val reference: String,
    val preview: String
)
