package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.Note
import com.example.data.Verse
import com.example.ui.BibleViewModel
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewModel: BibleViewModel,
    noteId: Long
) {
    val context = LocalContext.current
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var note by remember { mutableStateOf<Note?>(null) }
    var associatedVerse by remember { mutableStateOf<Verse?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedSubtitle by remember { mutableStateOf("") }
    var editedBody by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showActionMenu by remember { mutableStateOf(false) }

    var textToExport by remember { mutableStateOf("") }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(textToExport.toByteArray(Charsets.UTF_8))
                }
                val successMsg = if (appLanguage == "fr") "Note enregistrée dans le fichier .txt avec succès !" else "Nòt la sove nan fichye .txt avèk siksè!"
                Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                val errorMsg = if (appLanguage == "fr") "Erreur lors de l'enregistrement : ${e.message}" else "Erè lè w ap anregistre fichye a: ${e.message}"
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun prepareFormattedText(currentNote: Note, verse: Verse?): String {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy - HH:mm", if (appLanguage == "fr") Locale.FRENCH else Locale.getDefault())
        val dateStr = dateFormat.format(Date(currentNote.timestamp))
        val parsed = parseNoteContent(currentNote.content)

        return buildString {
            append("========================================\n")
            append(if (appLanguage == "fr") "           NOTE BIBLIQUE PERSONNELLE\n" else "           NÒT PÈSONÈL BIBLIK\n")
            append("========================================\n")
            append(if (appLanguage == "fr") "Date: $dateStr\n" else "Dat: $dateStr\n")
            if (verse != null) {
                append(if (appLanguage == "fr") "Verset: ${verse.book} ${verse.chapter}:${verse.verseNumber}\n" else "Vèsè: ${verse.book} ${verse.chapter}:${verse.verseNumber}\n")
                append(if (appLanguage == "fr") "Texte du verset: \"${verse.text}\"\n" else "Tèks vèsè: \"${verse.text}\"\n")
            }
            if (parsed.title.isNotBlank()) {
                append(if (appLanguage == "fr") "Titre: ${parsed.title}\n" else "Tit: ${parsed.title}\n")
            }
            if (parsed.subtitle.isNotBlank()) {
                append(if (appLanguage == "fr") "Sous-titre: ${parsed.subtitle}\n" else "Soutit: ${parsed.subtitle}\n")
            }
            append("\n----------------------------------------\n")
            append(if (appLanguage == "fr") "CONTENU DE LA NOTE:\n" else "KONTNI NÒT LA:\n")
            append("----------------------------------------\n")
            append(if (parsed.title.isBlank() && parsed.subtitle.isBlank()) currentNote.content else parsed.body)
            append("\n========================================\n")
        }
    }

    fun handleSaveAsTxt() {
        note?.let { n ->
            textToExport = prepareFormattedText(n, associatedVerse)
            val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
            val defaultFileName = if (appLanguage == "fr") "Note_Biblique_${fileNameFormat.format(Date(n.timestamp))}.txt" else "Not_Biblik_${fileNameFormat.format(Date(n.timestamp))}.txt"
            createDocumentLauncher.launch(defaultFileName)
        }
    }

    fun handleShareNote() {
        note?.let { n ->
            val formattedText = prepareFormattedText(n, associatedVerse)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, if (appLanguage == "fr") "Note Biblique Personnelle" else "Nòt Biblik Pèsonèl")
                putExtra(Intent.EXTRA_TEXT, formattedText)
            }
            context.startActivity(Intent.createChooser(shareIntent, if (appLanguage == "fr") "Partager votre note personnelle" else "Pataje nòt pèsonèl ou"))
        }
    }

    LaunchedEffect(noteId) {
        val loadedNote = viewModel.getNoteById(noteId)
        note = loadedNote
        if (loadedNote != null) {
            val parsed = parseNoteContent(loadedNote.content)
            editedTitle = parsed.title
            editedSubtitle = parsed.subtitle
            editedBody = if (parsed.title.isBlank() && parsed.subtitle.isBlank()) loadedNote.content else parsed.body
            if (loadedNote.verseId != 0L) {
                associatedVerse = viewModel.getVerseById(loadedNote.verseId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (appLanguage == "fr") "Détail de la Note" else "Detay Nòt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = if (appLanguage == "fr") "Retour" else "Retounen")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            note?.let { n ->
                                val fullContent = formatNoteContent(editedTitle, editedSubtitle, editedBody)
                                val updated = n.copy(content = fullContent)
                                viewModel.updateNote(updated)
                                note = updated
                            }
                            isEditing = false
                        }) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = if (appLanguage == "fr") "Enregistrer" else "Anregistre",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (note == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (appLanguage == "fr") "Cette note n'existe pas." else "Nòt sa a pa egziste.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Book-style Note Page Card
                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    isPressed = isEditing,
                    cornerRadius = 24.dp,
                    elevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    if (!isEditing) {
                                        showActionMenu = true
                                    }
                                }
                            )
                            .padding(24.dp)
                    ) {
                        // Date & Time Header inside book page
                        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy - HH:mm", if (appLanguage == "fr") Locale.FRENCH else Locale.getDefault())
                        Text(
                            text = dateFormat.format(Date(note!!.timestamp)).uppercase(if (appLanguage == "fr") Locale.FRENCH else Locale.getDefault()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Associated Verse if exists
                        associatedVerse?.let { verse ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${verse.book} ${verse.chapter}:${verse.verseNumber}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        TextButton(
                                            onClick = {
                                                val encodedBook = Uri.encode(verse.book)
                                                navController.navigate("reader/$encodedBook/${verse.chapter}")
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Book,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (appLanguage == "fr") "Lire dans la Bible" else "Li nan Bib la", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "\"${verse.text}\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 15.sp * textSizeMultiplier,
                                            lineHeight = (22 * textSizeMultiplier).sp
                                        ),
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Main Content (Editable or Clean Display)
                        if (isEditing) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = editedTitle,
                                    onValueChange = { editedTitle = it },
                                    label = { Text(if (appLanguage == "fr") "TITRE DE LA NOTE" else "TIT NÒT LA", fontSize = 12.sp * textSizeMultiplier) },
                                    placeholder = { Text(if (appLanguage == "fr") "Exemple: Méditation sur la Foi..." else "Egzanp: Meditasyon sou Lafwa...", fontSize = 15.sp * textSizeMultiplier) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp * textSizeMultiplier
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                )

                                OutlinedTextField(
                                    value = editedSubtitle,
                                    onValueChange = { editedSubtitle = it },
                                    label = { Text(if (appLanguage == "fr") "SOUS-TITRE / THÈME" else "SOUTIT / TÈM", fontSize = 12.sp * textSizeMultiplier) },
                                    placeholder = { Text(if (appLanguage == "fr") "Exemple: Confiance et force dans l'espérance..." else "Egzanp: Kwayans ak fòs nan lespwa...", fontSize = 15.sp * textSizeMultiplier) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp * textSizeMultiplier
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                )

                                OutlinedTextField(
                                    value = editedBody,
                                    onValueChange = { editedBody = it },
                                    label = { Text(if (appLanguage == "fr") "CORPS DE LA NOTE" else "KÒ NÒT LA", fontSize = 12.sp * textSizeMultiplier) },
                                    placeholder = { Text(if (appLanguage == "fr") "Écrivez ici toutes vos réflexions, notes et points clés..." else "Ekri tout detay, refleksyon, ak pwen kle yo la...", fontSize = 15.sp * textSizeMultiplier) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 160.dp),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 17.sp * textSizeMultiplier,
                                        lineHeight = (26 * textSizeMultiplier).sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        } else {
                            val parsedNote = parseNoteContent(note!!.content)

                            Column(modifier = Modifier.fillMaxWidth()) {
                                // 1. Title Section
                                if (parsedNote.title.isNotBlank()) {
                                    Text(
                                        text = parsedNote.title,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp * textSizeMultiplier,
                                            lineHeight = (28 * textSizeMultiplier).sp,
                                            letterSpacing = (-0.5).sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // 2. Subtitle Section
                                if (parsedNote.subtitle.isNotBlank()) {
                                    Text(
                                        text = parsedNote.subtitle,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp * textSizeMultiplier,
                                            lineHeight = (22 * textSizeMultiplier).sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Divider line if title or subtitle exists
                                if (parsedNote.title.isNotBlank() || parsedNote.subtitle.isNotBlank()) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }

                                // 3. Body Section
                                val displayBody = if (parsedNote.title.isBlank() && parsedNote.subtitle.isBlank()) {
                                    note!!.content
                                } else {
                                    parsedNote.body
                                }

                                Text(
                                    text = displayBody,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 18.sp * textSizeMultiplier,
                                        lineHeight = (28 * textSizeMultiplier).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Long press hint indicator when not editing
                if (!isEditing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appLanguage == "fr") "Appuyez sur la note pour afficher les options (Modifier, Partager, Sauvegarder, Supprimer)" else "Peze sou paj nòt la pou opsyon (Modifye, Pataje, Sove, Efase)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                // Save button when editing
                if (isEditing) {
                    NeumorphicButton(
                        onClick = {
                            note?.let { n ->
                                val fullContent = formatNoteContent(editedTitle, editedSubtitle, editedBody)
                                val updated = n.copy(content = fullContent)
                                viewModel.updateNote(updated)
                                note = updated
                            }
                            isEditing = false
                        },
                        cornerRadius = 16.dp,
                        elevation = 4.dp,
                        isDarkTheme = isDarkTheme,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (appLanguage == "fr") "Enregistrer les modifications" else "Anregistre Chanjman Yo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // Long Press Actions Dialog / Modal
        if (showActionMenu) {
            AlertDialog(
                onDismissRequest = { showActionMenu = false },
                title = {
                    Text(
                        text = if (appLanguage == "fr") "Options de la Note" else "Opsyon Nòt",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Action 1: Edit Note
                        Surface(
                            onClick = {
                                showActionMenu = false
                                isEditing = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = if (appLanguage == "fr") "Modifier la Note" else "Modifye Nòt La",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Action 2: Share Note
                        Surface(
                            onClick = {
                                showActionMenu = false
                                handleShareNote()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = if (appLanguage == "fr") "Partager la Note" else "Pataje Nòt La",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Action 3: Save as .TXT
                        Surface(
                            onClick = {
                                showActionMenu = false
                                handleSaveAsTxt()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = if (appLanguage == "fr") "Sauvegarder en .TXT" else "Sove kòm Fichye .TXT",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Action 4: Delete Note
                        Surface(
                            onClick = {
                                showActionMenu = false
                                showDeleteDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = if (appLanguage == "fr") "Supprimer la Note" else "Efase Nòt La",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showActionMenu = false }) {
                        Text(if (appLanguage == "fr") "Annuler" else "Anile")
                    }
                }
            )
        }

        // Confirm Delete Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(if (appLanguage == "fr") "Supprimer la note ?" else "Efase nòt?") },
                text = { Text(if (appLanguage == "fr") "Êtes-vous sûr de vouloir supprimer cette note ?" else "Èske ou sèten ou vle efase nòt sa a?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            note?.let { viewModel.deleteNote(it) }
                            showDeleteDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (appLanguage == "fr") "Supprimer" else "Efase")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(if (appLanguage == "fr") "Annuler" else "Anile")
                    }
                }
            )
        }
    }
}

data class ParsedNoteContent(
    val title: String,
    val subtitle: String,
    val body: String
)

fun parseNoteContent(rawContent: String): ParsedNoteContent {
    var title = ""
    var subtitle = ""
    val bodyLines = mutableListOf<String>()

    val lines = rawContent.lines()
    var hasTags = false

    for (line in lines) {
        when {
            line.startsWith("[TIT] ") -> {
                title = line.removePrefix("[TIT] ").trim()
                hasTags = true
            }
            line.startsWith("[SOUTIT] ") -> {
                subtitle = line.removePrefix("[SOUTIT] ").trim()
                hasTags = true
            }
            else -> {
                bodyLines.add(line)
            }
        }
    }

    if (!hasTags) {
        return ParsedNoteContent(title = "", subtitle = "", body = rawContent)
    }

    return ParsedNoteContent(
        title = title,
        subtitle = subtitle,
        body = bodyLines.joinToString("\n").trim()
    )
}

fun formatNoteContent(title: String, subtitle: String, body: String): String {
    return buildString {
        if (title.isNotBlank()) {
            append("[TIT] ").append(title.trim()).append("\n")
        }
        if (subtitle.isNotBlank()) {
            append("[SOUTIT] ").append(subtitle.trim()).append("\n")
        }
        append(body.trim())
    }
}


