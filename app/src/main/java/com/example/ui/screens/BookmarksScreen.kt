package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BibleViewModel
import com.example.ui.components.NeumorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(navController: NavController, viewModel: BibleViewModel) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favori mwen yo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Ou poko gen okenn favori.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookmarks) { verse ->
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

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                if (verse.isBookmarked) {
                                    val path = Path().apply {
                                        val strokeHeight = size.height * 0.8f
                                        val topY = (size.height - strokeHeight) / 2
                                        val bottomY = topY + strokeHeight
                                        
                                        moveTo(0f, topY + 2.dp.toPx())
                                        
                                        val segments = 8
                                        val segmentWidth = size.width / segments
                                        for (i in 1..segments) {
                                            val x = i * segmentWidth
                                            val variance = if (i % 2 == 0) 3.dp.toPx() else -2.dp.toPx()
                                            lineTo(x, topY + variance)
                                        }
                                        
                                        lineTo(size.width + 1.dp.toPx(), bottomY - 2.dp.toPx())
                                        
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
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${verse.book} ${verse.chapter}:${verse.verseNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = verse.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    // Navigate to Reader at this verse
                                    navController.navigate("reader/${verse.book}/${verse.chapter}")
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Li vèsè a")
                            }
                        }
                    }
                }
            }
        }
    }
}
