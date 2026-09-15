package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ads.AdManager
import com.example.ads.PremiumUpgradeDialog
import com.example.ads.RewardedAdDialog
import com.example.ui.BibleViewModel
import com.example.ui.components.LegalInfoDialog
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.TermsOfUseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: BibleViewModel) {
    val context = LocalContext.current
    val adManager = remember { AdManager.getInstance(context) }
    val isPremiumUser by adManager.isPremiumUser.collectAsState()
    val rewardCount by adManager.rewardCount.collectAsState()

    val playStorePackage by adManager.playStorePackage.collectAsState()
    val youtubeUrl by adManager.youtubeUrl.collectAsState()
    val facebookUrl by adManager.facebookUrl.collectAsState()
    val whatsappUrl by adManager.whatsappUrl.collectAsState()
    val supportEmail by adManager.supportEmail.collectAsState()
    val developerPhone by adManager.developerPhone.collectAsState()
    val privacyPolicyUrl by adManager.privacyPolicyUrl.collectAsState()

    var showPremiumDialog by remember { mutableStateOf(false) }
    var showRewardedDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showLegalDialog by remember { mutableStateOf(false) }

    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme

    val appLanguage by viewModel.appLanguage.collectAsState()
    val bibleVersion by viewModel.bibleVersion.collectAsState()

    val reminderHour by viewModel.readingPlanReminderHour.collectAsState()
    val reminderMinute by viewModel.readingPlanReminderMinute.collectAsState()
    val reminderEnabled by viewModel.readingPlanReminderEnabled.collectAsState()
    var showReminderDialog by remember { mutableStateOf(false) }

    val reminderFormattedTime = remember(reminderHour, reminderMinute) {
        val h = if (reminderHour == 0) 12 else if (reminderHour > 12) reminderHour - 12 else reminderHour
        val amPm = if (reminderHour >= 12) "PM" else "AM"
        String.format("%02d:%02d %s", h, reminderMinute, amPm)
    }

    if (showReminderDialog) {
        com.example.ui.components.ExactTimePickerDialog(
            currentEnabled = reminderEnabled,
            currentHour = reminderHour,
            currentMinute = reminderMinute,
            onSave = { enabled, hour, minute ->
                viewModel.setReadingPlanReminder(enabled, hour, minute)
                showReminderDialog = false
                val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                val amPm = if (hour >= 12) "PM" else "AM"
                val timeFormatted = String.format("%02d:%02d %s", h, minute, amPm)
                val msg = if (enabled) "Rapèl lekti aktif pou chak jou a $timeFormatted!" else "Rapèl lekti dezaktive."
                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    if (showPremiumDialog) {
        PremiumUpgradeDialog(
            adManager = adManager,
            isDarkTheme = isDarkTheme,
            onDismiss = { showPremiumDialog = false },
            onUpgraded = { showPremiumDialog = false }
        )
    }

    if (showRewardedDialog) {
        RewardedAdDialog(
            adManager = adManager,
            isDarkTheme = isDarkTheme,
            onDismiss = { showRewardedDialog = false },
            onRewardEarned = { showRewardedDialog = false }
        )
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(
            isDarkTheme = isDarkTheme,
            onlineUrl = privacyPolicyUrl,
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showTermsDialog) {
        TermsOfUseDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { showTermsDialog = false }
        )
    }

    if (showLegalDialog) {
        LegalInfoDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { showLegalDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (appLanguage == "fr") "Profil & Paramètres" else "Pwofil & Anviwònman") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section: App Vision & Faith
            item {
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (appLanguage == "fr") "Chrétiens dans la foi en Jésus-Christ" else "Kretyen nan la fwa nan Jezi-Kris",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (appLanguage == "fr") "La vision de l'application est de propager l'Évangile selon Matthieu 28." else "Vizyon app la se pou fè levanjil ale pi lwen selon Matye 28.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section: Settings
            item {
                Text(
                    if (appLanguage == "fr") "Paramètres de l'application" else "Anviwònman",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Language Selection
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    if (appLanguage == "fr") "Langue de l'application & de la Bible" else "Lang Aplikasyon an & Bib la",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (appLanguage == "fr") "Ce choix s'applique à toute l'application et sélectionne automatiquement la version de la Bible."
                                else "Chwa sa a aplike pou tout aplikasyon an e li chanje vèsyon Bib la otomatikman.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val langOptions = listOf(
                                    Pair("ht", "🇭🇹 Kreyòl"),
                                    Pair("fr", "🇫🇷 Français")
                                )
                                langOptions.forEach { (code, label) ->
                                    val isSelected = appLanguage == code
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.setAppLanguage(code)
                                            val msg = if (code == "fr") {
                                                "Langue et Bible appliquées en Français !"
                                            } else {
                                                "Lang aplikasyon an ak Bib la chanje an Kreyòl !"
                                            }
                                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Bible Version Selection
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    if (appLanguage == "fr") "Version de la Bible" else "Vèsyon Bib la",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val versionOptions = listOf(
                                    Pair(com.example.ui.util.BibleVersion.KREYOL, if (appLanguage == "fr") "Créole" else "Kreyòl"),
                                    Pair(com.example.ui.util.BibleVersion.FRANCAIS_LSG, if (appLanguage == "fr") "Français (LSG)" else "Fransè (LSG)"),
                                    Pair(com.example.ui.util.BibleVersion.BILINGUAL, if (appLanguage == "fr") "Bilingue" else "Kòt a kòt")
                                )
                                versionOptions.forEach { (code, label) ->
                                    val isSelected = bibleVersion == code
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setBibleVersion(code) },
                                        modifier = Modifier.weight(1f),
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsToggleItem(
                            icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            label = if (appLanguage == "fr") "Mode Sombre" else "Mòd Tèm",
                            value = isDarkTheme,
                            onToggle = { viewModel.toggleDarkMode() }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        // Text Size Setting
                        val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FormatSize, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(if (appLanguage == "fr") "Taille du Texte" else "Gwosè Tèks", style = MaterialTheme.typography.bodyLarge)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.decreaseTextSize() }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text("${(textSizeMultiplier * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { viewModel.increaseTextSize() }) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Font Family Setting
                        val fontFamilyType by viewModel.fontFamilyType.collectAsState()
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TextFields, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(if (appLanguage == "fr") "Style de Police de la Bible" else "Style Font Bib la", style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val fontOptions = listOf(
                                    Triple("sans_serif", "Sans-Serif", androidx.compose.ui.text.font.FontFamily.SansSerif),
                                    Triple("serif", "Serif", androidx.compose.ui.text.font.FontFamily.Serif),
                                    Triple("monospaced", "Monospaced", androidx.compose.ui.text.font.FontFamily.Monospace)
                                )
                                fontOptions.forEach { (key, label, fontFam) ->
                                    val isSelected = fontFamilyType.equals(key, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setFontFamilyType(key) },
                                        modifier = Modifier.weight(1f),
                                        label = {
                                            Text(
                                                text = label,
                                                fontFamily = fontFam,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        )
                                    )
                                }
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        val isVisualComfort by viewModel.isVisualComfortEnabled.collectAsState()
                        SettingsToggleItem(
                            icon = Icons.Default.Visibility,
                            label = if (appLanguage == "fr") "Confort Visuel" else "Konfò Vizyèl",
                            value = isVisualComfort,
                            onToggle = { viewModel.toggleVisualComfort() }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsToggleItem(
                            icon = Icons.Default.Notifications,
                            label = if (appLanguage == "fr") "Rappel de Lecture Quotidien" else "Rapèl Lekti Chak Jou",
                            value = reminderEnabled,
                            onToggle = {
                                val newState = !reminderEnabled
                                viewModel.setReadingPlanReminder(newState, reminderHour, reminderMinute)
                                val msg = if (newState) {
                                    if (appLanguage == "fr") "Rappel de lecture actif pour $reminderFormattedTime" else "Rapèl lekti aktif pou $reminderFormattedTime"
                                } else {
                                    if (appLanguage == "fr") "Rappel de lecture désactivé" else "Rapèl lekti dezaktive"
                                }
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )

                        if (reminderEnabled) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showReminderDialog = true }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            if (appLanguage == "fr") "Heure Exacte du Rappel" else "Lè Egzak Pou Rapèl la",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            if (appLanguage == "fr") "Cliquez ici pour changer l'heure" else "Klike la a pou w chanje lè a",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = reminderFormattedTime,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = if (appLanguage == "fr") "Modifier l'heure" else "Chanje lè a",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsActionItem(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            label = if (appLanguage == "fr") "Plans de Lecture & Progression" else "Plan Lekti Bib la & Pwogrè",
                            onClick = { navController.navigate("reading_plans") }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsActionItem(
                            icon = Icons.Default.NotificationsActive,
                            label = if (appLanguage == "fr") "Voir les Notifications" else "Gade Notifikasyon Yo",
                            onClick = { navController.navigate("notifications") }
                        )
                    }
                }
            }

            // Section: Monetization & Bib Premium
            item {
                Text(
                    if (appLanguage == "fr") "Monétisation & Premium" else "Monetizasyon & Premium",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Reward Counter Status
                        Surface(
                            color = androidx.compose.ui.graphics.Color(0xFF10B981).copy(alpha = 0.12f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (appLanguage == "fr") "Vos Récompenses" else "Rekonpans Ou Yo",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (appLanguage == "fr") "Chaque vidéo vue = 1 récompense" else "Chak videyo jwenn = 1 rekonpans",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    color = androidx.compose.ui.graphics.Color(0xFF10B981),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "$rewardCount ${if (appLanguage == "fr") (if (rewardCount > 1) "récompenses" else "récompense") else "rekonpans"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        if (isPremiumUser) {
                            Surface(
                                color = androidx.compose.ui.graphics.Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        if (appLanguage == "fr") "Vous êtes membre Bible Premium ($2.99) • Zéro Publicité !" else "Ou se yon manm Bib Premium ($2.99) • Zero Anons!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color(0xFF10B981)
                                    )
                                }
                            }
                        } else {
                            NeumorphicButton(
                                onClick = { showPremiumDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                isDarkTheme = isDarkTheme,
                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF2563EB)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(if (appLanguage == "fr") "Débloquer Bible Premium ($2.99)" else "Debloke Bib Premium ($2.99)", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                                }
                            }

                            NeumorphicButton(
                                onClick = { showRewardedDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                isDarkTheme = isDarkTheme
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(if (appLanguage == "fr") "Regarder une vidéo de 15s pour une récompense" else "Gade yon anons 15s pou rekonpans", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Section: App Features
            item {
                Text(
                    if (appLanguage == "fr") "Fonctionnalités de l'Application" else "Karakteristik App la",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FeatureItem(if (appLanguage == "fr") "Lecture de la Bible (Créole & Français)" else "Lekti Bib la an Kreyòl")
                        FeatureItem(if (appLanguage == "fr") "Verset pour la journée" else "Vèsè pou jounen an")
                        FeatureItem(if (appLanguage == "fr") "Système de notes personnelles" else "Sistèm nòt pèsonèl")
                        FeatureItem(if (appLanguage == "fr") "Recherche rapide & thèmes" else "Rechèch rapid")
                        FeatureItem(if (appLanguage == "fr") "Favoris et surlignages" else "Favori")
                    }
                }
            }

            // Section: Rate & Share App (Play Store)
            item {
                Text(
                    if (appLanguage == "fr") "Soutenir la Mission & Avis" else "Sipòte Misyon an & Evalyasyon",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$playStorePackage"))
                                        context.startActivity(marketIntent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$playStorePackage"))
                                        context.startActivity(webIntent)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (appLanguage == "fr") "Notez-nous sur Google Play Store" else "Evalye Nou sou Google Play Store",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (appLanguage == "fr") "Mettez 5 étoiles ⭐⭐⭐⭐⭐ pour propager l'Évangile" else "Mete 5 zetwal ⭐⭐⭐⭐⭐ pou ede levanjil la pwopaje",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            if (appLanguage == "fr") {
                                                "Je vous recommande de télécharger l'application \"La Sainte Bible • Édition Impact\" gratuitement sur Google Play Store: https://play.google.com/store/apps/details?id=$playStorePackage"
                                            } else {
                                                "Mwen rekòmande w telechaje aplikasyon \"Bib La • Edisyon Enpakt\" an Kreyòl Ayisyen gratis sou Google Play Store: https://play.google.com/store/apps/details?id=$playStorePackage"
                                            }
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, if (appLanguage == "fr") "Partager la Bible" else "Pataje Bib La")
                                    context.startActivity(shareIntent)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (appLanguage == "fr") "Partager la Bible avec Famille & Amis" else "Pataje Bib La ak Fanmi & Zanmi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (appLanguage == "fr") "Bénissez une personne aujourd'hui avec la Parole de Dieu" else "Beni yon moun jodi a avèk Pawòl Bondye a",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Section: Follow Us (Rezo Sosyo)
            item {
                Text(
                    if (appLanguage == "fr") "Suivez-nous sur les Réseaux Sociaux" else "Swiv Nou sou Rezo Sosyo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DeveloperContactItem(
                            icon = Icons.Default.SmartDisplay,
                            text = if (appLanguage == "fr") "YouTube • Chaîne Officielle" else "YouTube • Chanèl Ofisyèl Bib La"
                        ) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DeveloperContactItem(
                            icon = Icons.Default.Public,
                            text = if (appLanguage == "fr") "Facebook • Communauté" else "Facebook • Kominote Bib La"
                        ) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DeveloperContactItem(
                            icon = Icons.Default.Chat,
                            text = if (appLanguage == "fr") "WhatsApp • Communauté & Prière" else "WhatsApp • Kominote & Priyè"
                        ) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            // Section: Legal & Compliance (Play Store Standards)
            item {
                Text(
                    if (appLanguage == "fr") "Mentions Légales & Confidentialité" else "Legal & Konfidansyalite",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionItem(
                            icon = Icons.Default.PrivacyTip,
                            label = if (appLanguage == "fr") "Politique de Confidentialité (Privacy Policy)" else "Politik Konfidansyalite (Privacy Policy)",
                            onClick = { showPrivacyDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsActionItem(
                            icon = Icons.Default.Description,
                            label = if (appLanguage == "fr") "Conditions d'Utilisation (Terms of Use)" else "Kondisyon Itilizasyon (Terms of Use)",
                            onClick = { showTermsDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SettingsActionItem(
                            icon = Icons.Default.Gavel,
                            label = if (appLanguage == "fr") "Informations Légales & Droits d'Auteur" else "Enfòmasyon Legal & Dwa Otè",
                            onClick = { showLegalDialog = true }
                        )
                    }
                }
            }

            // Section: Developer Info
            item {
                Text(
                    if (appLanguage == "fr") "Contact Développeur" else "Kontak Devloper",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                NeumorphicCard(isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DeveloperContactItem(Icons.Default.Person, if (appLanguage == "fr") "Jackson Charles (Développeur)" else "Jackson Charles (Devlopè)")
                        DeveloperContactItem(Icons.Default.Phone, developerPhone) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$developerPhone"))
                            context.startActivity(intent)
                        }
                        DeveloperContactItem(Icons.Default.Email, supportEmail) {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$supportEmail"))
                            context.startActivity(intent)
                        }
                    }
                }
            }

            // Section: Donation
            item {
                NeumorphicButton(
                    onClick = { navController.navigate("donation") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    isDarkTheme = isDarkTheme,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (appLanguage == "fr") "Faire un don" else "Fè yon donasyon", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section: Dynamic App Version & Build Footer
            item {
                val versionName = com.example.BuildConfig.VERSION_NAME
                val versionCode = com.example.BuildConfig.VERSION_CODE

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (appLanguage == "fr") "La Sainte Bible • Édition Impact" else "Bib La • Edisyon Enpakt",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (appLanguage == "fr") "Version" else "Vèsyon"} $versionName (Build $versionCode)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (appLanguage == "fr") "Toute la gloire à Dieu • Matthieu 28:19-20" else "Tout glwa pou Bondye • Matye 28:19-20",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, label: String, value: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(checked = value, onCheckedChange = { onToggle() })
    }
}

@Composable
fun SettingsActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DeveloperContactItem(icon: ImageVector, text: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
