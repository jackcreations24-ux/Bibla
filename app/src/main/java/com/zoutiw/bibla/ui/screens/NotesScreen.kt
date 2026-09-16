package com.zoutiw.bibla.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zoutiw.bibla.ui.BibleViewModel
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.zoutiw.bibla.ui.components.NeumorphicCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: com.zoutiw.bibla.data.Note,
    viewModel: BibleViewModel,
    isDarkTheme: Boolean,
    navController: NavController,
    appLanguage: String,
    onLongClick: () -> Unit
) {
    var associatedVerse by remember { mutableStateOf<com.zoutiw.bibla.data.Verse?>(null) }
    
    LaunchedEffect(note.verseId) {
        if (note.verseId != 0L) {
            associatedVerse = viewModel.getVerseById(note.verseId)
        }
    }

    NeumorphicCard(
        isDarkTheme = isDarkTheme,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    navController.navigate("note_detail/${note.id}")
                },
                onLongClick = onLongClick
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val locale = if (appLanguage == "fr") Locale.FRENCH else Locale.getDefault()
                Text(
                    text = SimpleDateFormat("d MMM yyyy, HH:mm", locale).format(Date(note.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                associatedVerse?.let { verse ->
                    val bookName = com.zoutiw.bibla.ui.util.BibleBookNames.getDisplayName(verse.book, appLanguage)
                    Text(
                        text = "$bookName ${verse.chapter}:${verse.verseNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            associatedVerse?.let { verse ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val parsedNote = parseNoteContent(note.content)
            if (parsedNote.title.isNotBlank()) {
                Text(
                    text = parsedNote.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            if (parsedNote.subtitle.isNotBlank()) {
                Text(
                    text = parsedNote.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            val displayBody = if (parsedNote.title.isBlank() && parsedNote.subtitle.isBlank()) note.content else parsedNote.body
            if (displayBody.isNotBlank()) {
                Text(
                    text = displayBody,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = if (parsedNote.title.isNotBlank() || parsedNote.subtitle.isNotBlank()) 4.dp else 0.dp)
                )
            }
        }
    }
}

@Composable
fun NotesScreen(navController: NavController, viewModel: BibleViewModel) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val notes by viewModel.allNotes.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val haptic = LocalHapticFeedback.current

    val draftTitle by viewModel.noteDraftTitle.collectAsState()
    val draftText by viewModel.noteDraftText.collectAsState()

    var newNoteTitle by remember { mutableStateOf(draftTitle) }
    var newNoteText by remember { mutableStateOf(draftText) }
    var noteToDelete by remember { mutableStateOf<com.zoutiw.bibla.data.Note?>(null) }

    LaunchedEffect(draftTitle, draftText) {
        if (newNoteTitle != draftTitle) newNoteTitle = draftTitle
        if (newNoteText != draftText) newNoteText = draftText
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // List area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = if (appLanguage == "fr") "Mes Notes" else "Nòt mwen yo",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
            )

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (appLanguage == "fr") "Vous n'avez pas encore de note." else "Ou poko gen okenn nòt.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes) { note ->
                        NoteItem(note, viewModel, isDarkTheme, navController, appLanguage) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            noteToDelete = note
                        }
                    }
                }
            }
        }

        // Chat style input bar - Tightened up
        val canSend = newNoteText.isNotBlank() || newNoteTitle.isNotBlank()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicCard(
                isDarkTheme = isDarkTheme,
                isPressed = true,
                cornerRadius = 20.dp,
                elevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    TextField(
                        value = newNoteTitle,
                        onValueChange = {
                            newNoteTitle = it
                            viewModel.updateNoteDraft(it, newNoteText)
                        },
                        placeholder = { Text(if (appLanguage == "fr") "Titre de la note (optionnel)..." else "Tit nòt la (opsyonèl)...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    TextField(
                        value = newNoteText,
                        onValueChange = {
                            newNoteText = it
                            viewModel.updateNoteDraft(newNoteTitle, it)
                        },
                        placeholder = { Text(if (appLanguage == "fr") "Écrivez votre note ici..." else "Tape kontni nòt ou la...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            NeumorphicButton(
                onClick = {
                    if (canSend) {
                        val fullContent = formatNoteContent(newNoteTitle, "", newNoteText)
                        viewModel.addNote(0L, fullContent)
                        newNoteTitle = ""
                        newNoteText = ""
                        viewModel.clearNoteDraft()
                    }
                },
                modifier = Modifier.size(48.dp),
                cornerRadius = 24.dp,
                elevation = 4.dp,
                isDarkTheme = isDarkTheme,
                backgroundColor = if (canSend) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (canSend) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (noteToDelete != null) {
            AlertDialog(
                onDismissRequest = { noteToDelete = null },
                title = { Text(if (appLanguage == "fr") "Supprimer la note ?" else "Efase nòt?") },
                text = { Text(if (appLanguage == "fr") "Êtes-vous sûr de vouloir supprimer cette note ?" else "Èske ou sèten ou vle efase nòt sa a?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            noteToDelete?.let { viewModel.deleteNote(it) }
                            noteToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (appLanguage == "fr") "Supprimer" else "Efase")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteToDelete = null }) {
                        Text(if (appLanguage == "fr") "Annuler" else "Anile")
                    }
                }
            )
        }
    }
}

