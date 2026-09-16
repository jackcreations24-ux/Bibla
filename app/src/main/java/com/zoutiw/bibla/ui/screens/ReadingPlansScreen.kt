package com.zoutiw.bibla.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zoutiw.bibla.data.ReadingPlan
import com.zoutiw.bibla.data.ReadingPlanDay
import com.zoutiw.bibla.data.ReadingPlanRepository
import com.zoutiw.bibla.ui.BibleViewModel
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.zoutiw.bibla.ui.components.NeumorphicCard
import com.zoutiw.bibla.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPlansScreen(
    navController: NavController,
    viewModel: BibleViewModel
) {
    val context = LocalContext.current
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme

    val activePlanId by viewModel.activeReadingPlanId.collectAsState()
    val completedPlanDaysMap by viewModel.completedPlanDays.collectAsState()
    val readingStreak by viewModel.readingStreak.collectAsState()
    val reminderHour by viewModel.readingPlanReminderHour.collectAsState()
    val reminderMinute by viewModel.readingPlanReminderMinute.collectAsState()
    val reminderEnabled by viewModel.readingPlanReminderEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var selectedPlanId by remember(activePlanId) { mutableStateOf(activePlanId) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Remaining, 2: Done
    var searchQuery by remember { mutableStateOf("") }

    val currentPlan = remember(selectedPlanId) {
        ReadingPlanRepository.getPlanById(selectedPlanId)
    }

    val completedDays = completedPlanDaysMap[currentPlan.id] ?: emptySet()
    val progressPercent = if (currentPlan.totalDays > 0) {
        (completedDays.size.toFloat() / currentPlan.totalDays.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val nextUnreadDay = (1..currentPlan.totalDays).firstOrNull { !completedDays.contains(it) } ?: 1
    val todayDayPlan = currentPlan.days.getOrNull(nextUnreadDay - 1)

    val filteredDays = remember(currentPlan, completedDays, selectedFilterIndex, searchQuery) {
        currentPlan.days.filter { day ->
            val matchesFilter = when (selectedFilterIndex) {
                2 -> completedDays.contains(day.dayNumber)
                1 -> !completedDays.contains(day.dayNumber)
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                day.title.contains(searchQuery, ignoreCase = true) ||
                day.passages.any { it.displayReference.contains(searchQuery, ignoreCase = true) }
            }
            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (appLanguage == "fr") "Plans de Lecture de la Bible" else "Plan Lekti Bib la",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (appLanguage == "fr") "Suivez votre progression quotidienne" else "Swiv pwogrè w chak jou",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (appLanguage == "fr") "Retour" else "Tounen"
                        )
                    }
                },
                actions = {
                    // Streak badge
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$readingStreak ${if (appLanguage == "fr") (if (readingStreak > 1) "jours" else "jour") else "jou"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    // Reminder Button
                    IconButton(onClick = { showReminderDialog = true }) {
                        Icon(
                            imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = if (appLanguage == "fr") "Rappels Quotidiens" else "Rapèl Otomatik",
                            tint = if (reminderEnabled) PrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Reading Plan Selector Horizontal Row
            item {
                Text(
                    text = if (appLanguage == "fr") "CHOISIR UN PLAN DE LECTURE" else "CHWAZI YON PLAN LEKTI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(ReadingPlanRepository.allPlans) { plan ->
                        val isSelected = plan.id == selectedPlanId
                        val isActive = plan.id == activePlanId
                        val planCompleted = completedPlanDaysMap[plan.id]?.size ?: 0
                        val planProgress = if (plan.totalDays > 0) planCompleted.toFloat() / plan.totalDays else 0f

                        NeumorphicCard(
                            isDarkTheme = isDarkTheme,
                            cornerRadius = 18.dp,
                            elevation = if (isSelected) 6.dp else 2.dp,
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { selectedPlanId = plan.id }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(plan.colorPrimaryHex).copy(alpha = 0.18f),
                                                        Color(plan.colorSecondaryHex).copy(alpha = 0.06f)
                                                    )
                                                )
                                            )
                                        } else Modifier
                                    )
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(plan.colorPrimaryHex).copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = getIconForName(plan.iconName),
                                                contentDescription = null,
                                                tint = Color(plan.colorPrimaryHex),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (isActive) {
                                        Surface(
                                            color = Color(0xFF10B981),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (appLanguage == "fr") "ACTIF" else "AKTIF",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = plan.getTitle(appLanguage),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = if (appLanguage == "fr") "${plan.totalDays} Jours • ${plan.getCategory(appLanguage)}" else "${plan.totalDays} Jou • ${plan.getCategory(appLanguage)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { planProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color(plan.colorPrimaryHex),
                                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Active Plan Hero Banner
            item {
                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    cornerRadius = 24.dp,
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(currentPlan.colorPrimaryHex).copy(alpha = if (isDarkTheme) 0.3f else 0.15f),
                                        Color(currentPlan.colorSecondaryHex).copy(alpha = if (isDarkTheme) 0.15f else 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentPlan.getTitle(appLanguage),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = currentPlan.getSubtitle(appLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedPlanId != activePlanId) {
                                NeumorphicButton(
                                    onClick = {
                                        viewModel.setActiveReadingPlan(selectedPlanId)
                                        val toastMsg = if (appLanguage == "fr") "${currentPlan.getTitle(appLanguage)} défini comme votre plan actif !" else "${currentPlan.getTitle(appLanguage)} defini kòm plan prensipal ou!"
                                        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                    },
                                    cornerRadius = 12.dp,
                                    elevation = 4.dp,
                                    backgroundColor = Color(currentPlan.colorPrimaryHex),
                                    isDarkTheme = isDarkTheme
                                ) {
                                    Text(
                                        text = if (appLanguage == "fr") "Définir Actif" else "Fè l Aktif",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentPlan.getDescription(appLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar and Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (appLanguage == "fr") "Progression: ${completedDays.size} / ${currentPlan.totalDays} Jours" else "Pwogrè: ${completedDays.size} / ${currentPlan.totalDays} Jou",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(progressPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(currentPlan.colorPrimaryHex)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(currentPlan.colorPrimaryHex),
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next Reading for Today Action Card
                        todayDayPlan?.let { day ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = Color(currentPlan.colorPrimaryHex),
                                                shape = CircleShape,
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = if (appLanguage == "fr") "PROCHAINE LECTURE (JOUR ${day.dayNumber})" else "PWÒCHEN LEKTI OU (JOU ${day.dayNumber})",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(currentPlan.colorPrimaryHex)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Passages Pills
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        day.passages.forEach { passage ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .clickable {
                                                        navController.navigate("reader/${passage.book}/${passage.startChapter}")
                                                    }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = passage.getDisplayReference(appLanguage),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        val isCompleted = completedDays.contains(day.dayNumber)
                                        TextButton(
                                            onClick = {
                                                viewModel.toggleReadingPlanDayCompleted(currentPlan.id, day.dayNumber)
                                                val msg = if (appLanguage == "fr") {
                                                    if (!isCompleted) "Jour ${day.dayNumber} marqué comme lu !" else "Jour ${day.dayNumber} marqué comme non lu"
                                                } else {
                                                    if (!isCompleted) "Jou ${day.dayNumber} make kòm fini!" else "Jou ${day.dayNumber} remèt kòm ki pa fini"
                                                }
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isCompleted) {
                                                    if (appLanguage == "fr") "Terminé" else "Fini"
                                                } else {
                                                    if (appLanguage == "fr") "Marquer comme lu" else "Make kòm fini"
                                                },
                                                style = MaterialTheme.typography.labelMedium,
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

            // 3. Filter and Search Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (appLanguage == "fr") "LISTE DES JOURS (${filteredDays.size})" else "LIS TOUT JOU YO (${filteredDays.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Filter chips
                    val filterLabels = if (appLanguage == "fr") listOf("Tous", "Restants", "Terminés") else listOf("Tout", "Ki Rete", "Ki Fini")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        filterLabels.forEachIndexed { index, filterText ->
                            val isFilterSelected = selectedFilterIndex == index
                            Surface(
                                color = if (isFilterSelected) PrimaryColor else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { selectedFilterIndex = index }
                            ) {
                                Text(
                                    text = filterText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isFilterSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFilterSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Days List
            items(filteredDays, key = { "${currentPlan.id}_${it.dayNumber}" }) { day ->
                val isCompleted = completedDays.contains(day.dayNumber)

                DayReadingItemCard(
                    day = day,
                    isCompleted = isCompleted,
                    isDarkTheme = isDarkTheme,
                    appLanguage = appLanguage,
                    planPrimaryColor = Color(currentPlan.colorPrimaryHex),
                    onPassageClick = { passage ->
                        navController.navigate("reader/${passage.book}/${passage.startChapter}")
                    },
                    onToggleComplete = {
                        viewModel.toggleReadingPlanDayCompleted(currentPlan.id, day.dayNumber)
                        val msg = if (appLanguage == "fr") {
                            if (!isCompleted) "Jour ${day.dayNumber} complété !" else "Jour ${day.dayNumber} décoché"
                        } else {
                            if (!isCompleted) "Jou ${day.dayNumber} konplete!" else "Jou ${day.dayNumber} demake"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Reset plan button
            if (completedDays.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (appLanguage == "fr") "Recommencer ce plan" else "Rekòmanse plan sa a",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Reminder Settings Dialog
    if (showReminderDialog) {
        com.zoutiw.bibla.ui.components.ExactTimePickerDialog(
            currentEnabled = reminderEnabled,
            currentHour = reminderHour,
            currentMinute = reminderMinute,
            language = appLanguage,
            onSave = { enabled, hour, minute ->
                viewModel.setReadingPlanReminder(enabled, hour, minute)
                showReminderDialog = false
                val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                val amPm = if (hour >= 12) "PM" else "AM"
                val timeFormatted = String.format("%02d:%02d %s", h, minute, amPm)
                val msg = if (appLanguage == "fr") {
                    if (enabled) "Rappel de lecture activé pour chaque jour à $timeFormatted !" else "Rappels désactivés."
                } else {
                    if (enabled) "Rapèl lekti aktif pou chak jou a $timeFormatted!" else "Rapèl lekti dezaktive."
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    // Reset Progress Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(if (appLanguage == "fr") "Réinitialiser le Plan ?" else "Rekòmanse Plan an?") },
            text = { Text(if (appLanguage == "fr") "Êtes-vous sûr de vouloir réinitialiser vos progrès pour le plan '${currentPlan.title}' et recommencer à zéro ?" else "Èske w sèten ou vle efase pwogrè pou plan '${currentPlan.title}' la epi rekòmanse a zewo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetReadingPlanProgress(currentPlan.id)
                        showResetDialog = false
                        Toast.makeText(context, if (appLanguage == "fr") "Le plan a été réinitialisé à zéro !" else "Plan an rekòmanse a zewo!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text(if (appLanguage == "fr") "Oui, Réinitialiser" else "Wi, Rekòmanse", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(if (appLanguage == "fr") "Annuler" else "Anile")
                }
            }
        )
    }
}

@Composable
private fun DayReadingItemCard(
    day: ReadingPlanDay,
    isCompleted: Boolean,
    isDarkTheme: Boolean,
    appLanguage: String = "ht",
    planPrimaryColor: Color,
    onPassageClick: (com.zoutiw.bibla.data.ReadingPassage) -> Unit,
    onToggleComplete: () -> Unit
) {
    NeumorphicCard(
        isDarkTheme = isDarkTheme,
        cornerRadius = 16.dp,
        elevation = if (isCompleted) 2.dp else 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isCompleted) Color(0xFF10B981).copy(alpha = if (isDarkTheme) 0.12f else 0.06f)
                    else Color.Transparent
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (appLanguage == "fr") (if (isCompleted) "Lu" else "Non lu") else (if (isCompleted) "Fini" else "Ki pa fini"),
                    tint = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Day Info & Passages
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (appLanguage == "fr") "Jour ${day.dayNumber}" else "Jou ${day.dayNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color(0xFF10B981) else planPrimaryColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Passages
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    day.passages.forEach { passage ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onPassageClick(passage) }
                        ) {
                            Text(
                                text = passage.getDisplayReference(appLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Quick Read Arrow
            IconButton(
                onClick = { day.passages.firstOrNull()?.let { onPassageClick(it) } },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = if (appLanguage == "fr") "Lire" else "Li",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Book" -> Icons.Default.Book
        "MenuBook" -> Icons.AutoMirrored.Filled.MenuBook
        "Favorite" -> Icons.Default.Favorite
        "WbSunny" -> Icons.Default.WbSunny
        "Shield" -> Icons.Default.Shield
        else -> Icons.Default.Bookmark
    }
}

