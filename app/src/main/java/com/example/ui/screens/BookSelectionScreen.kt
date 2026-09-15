package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.BibleData
import com.example.ui.BibleViewModel
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSelectionScreen(
    navController: NavController,
    viewModel: BibleViewModel
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val appLanguage by viewModel.appLanguage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBook by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedBook == null) {
                            if (appLanguage == "fr") "Choisir un Livre" else "Chwazi yon Liv"
                        } else {
                            com.example.ui.util.BibleBookNames.getDisplayName(selectedBook!!, appLanguage)
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedBook != null) {
                            selectedBook = null
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedBook == null) {
                // Two parts: Ansyen Testaman & Nouvo Testaman
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    if (appLanguage == "fr") "Ancien Testament" else "Ansyen Testaman",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoStories,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    if (appLanguage == "fr") "Nouveau Testament" else "Nouvo Testaman",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }

                val currentBooks = if (selectedTab == 0) BibleData.oldTestament else BibleData.newTestament

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = if (appLanguage == "fr") {
                                if (selectedTab == 0) "ANCIEN TESTAMENT (39 LIVRES)" else "NOUVEAU TESTAMENT (27 LIVRES)"
                            } else {
                                if (selectedTab == 0) "ANSYEN TESTAMAN (39 LIV)" else "NOUVO TESTAMAN (27 LIV)"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(currentBooks) { book ->
                        val chapterCount = BibleData.getChapterCount(book)
                        val primaryName = com.example.ui.util.BibleBookNames.getDisplayName(book, appLanguage)
                        val subtitleName = com.example.ui.util.BibleBookNames.getSubtitleName(book, appLanguage)

                        NeumorphicCard(
                            isDarkTheme = isDarkTheme,
                            cornerRadius = 14.dp,
                            elevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = { selectedBook = book },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = primaryName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (subtitleName.isNotBlank()) {
                                            Text(
                                                text = subtitleName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (appLanguage == "fr") "$chapterCount chapitres" else "$chapterCount chapit",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Chapter selection for the selected book
                val book = selectedBook!!
                val chapterCount = BibleData.getChapterCount(book)
                val bookDisplayName = com.example.ui.util.BibleBookNames.getDisplayName(book, appLanguage)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (appLanguage == "fr") "Choisir le chapitre dans $bookDisplayName" else "Chwazi chapit nan $bookDisplayName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(chapterCount) { i ->
                            val chapter = i + 1
                            NeumorphicButton(
                                onClick = {
                                    val encodedBook = Uri.encode(book)
                                    navController.navigate("reader/$encodedBook/$chapter") {
                                        popUpTo("reader") { inclusive = true }
                                    }
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Text(
                                    text = "$chapter",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
