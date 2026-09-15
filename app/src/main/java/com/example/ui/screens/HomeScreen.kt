package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.ads.AdManager
import com.example.ads.BottomBannerAdView
import com.example.ads.PremiumUpgradeDialog
import com.example.ads.RewardedAdDialog
import com.example.ui.BibleViewModel
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.theme.getBibleFontFamily

@Composable
fun HomeScreen(navController: NavController, viewModel: BibleViewModel) {
    val dailyVerse by viewModel.dailyVerse.collectAsState()
    val dailyVerseFrench by viewModel.dailyVerseFrench.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val bibleVersion by viewModel.bibleVersion.collectAsState()
    val isLiked by viewModel.dailyVerseIsLiked.collectAsState()
    val likeCount by viewModel.dailyVerseLikeCount.collectAsState()
    val viewCount by viewModel.dailyVerseViewCount.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val savedUserName by viewModel.savedUserName.collectAsState()
    var showCommentSheet by remember { mutableStateOf(false) }

    val activePlanId by viewModel.activeReadingPlanId.collectAsState()
    val completedPlanDaysMap by viewModel.completedPlanDays.collectAsState()
    val readingStreak by viewModel.readingStreak.collectAsState()
    val activePlan = remember(activePlanId) { com.example.data.ReadingPlanRepository.getPlanById(activePlanId) }
    val completedDays = completedPlanDaysMap[activePlan.id] ?: emptySet()
    val nextPlanDayNumber = (1..activePlan.totalDays).firstOrNull { !completedDays.contains(it) } ?: 1
    val nextPlanDay = activePlan.days.getOrNull(nextPlanDayNumber - 1)
    val planProgress = if (activePlan.totalDays > 0) completedDays.size.toFloat() / activePlan.totalDays else 0f

    val progress by viewModel.progress.collectAsState()
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val fontFamilyType by viewModel.fontFamilyType.collectAsState()
    val context = LocalContext.current
    
    val adManager = remember { AdManager.getInstance(context) }
    var showPremiumDialog by remember { mutableStateOf(false) }
    var showRewardedDialog by remember { mutableStateOf(false) }

    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme

    LaunchedEffect(Unit) {
        viewModel.refreshDailyVerse()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastTapTime < 800L) {
                                tapCount++
                            } else {
                                tapCount = 1
                            }
                            lastTapTime = currentTime
                        },
                        onLongPress = {
                            val currentTime = System.currentTimeMillis()
                            if (tapCount >= 3 && (currentTime - lastTapTime < 1500L)) {
                                tapCount = 0
                                navController.navigate("admin_dashboard")
                            } else {
                                tapCount = 0
                            }
                        }
                    )
                }
            ) {
                Text(
                    text = if (appLanguage == "fr") "La Sainte Bible" else "Bib La",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (appLanguage == "fr") "ÉDITION IMPACT" else "EDISYON ENPAKT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NeumorphicButton(
                    onClick = { navController.navigate("notifications") },
                    cornerRadius = 24.dp,
                    elevation = 4.dp,
                    isDarkTheme = isDarkTheme
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                NeumorphicButton(
                    onClick = { navController.navigate("profile") },
                    cornerRadius = 24.dp,
                    elevation = 4.dp,
                    isDarkTheme = isDarkTheme
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Search Bar
            item {
                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    isPressed = true,
                    cornerRadius = 16.dp,
                    elevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { navController.navigate("search") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (appLanguage == "fr") "Rechercher des versets, des thèmes..." else "Chèche vèsè oswa tèm...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Daily Verse Card
            item {
                val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
                val isAfterSunset = currentHour >= 18 || currentHour < 6

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isAfterSunset) Icons.Default.Nightlight else Icons.Default.WbSunny,
                        contentDescription = if (isAfterSunset) "Lalin" else "Solèy",
                        modifier = Modifier.size(15.dp),
                        tint = if (isAfterSunset) Color(0xFF818CF8) else Color(0xFFF59E0B)
                    )
                    Text(
                        text = if (appLanguage == "fr") "VERSET DU JOUR" else "VÈSÈ POU JODI A",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFF2563EB) // Blue-600
                    )
                }

                // Animation for the glow effect - pulsing wave style
                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                val pulse by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )
                val waveOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "waveOffset"
                )

                val glowColors = listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.7f * pulse), // Crystal Sky Blue
                    Color(0xFF67E8F9).copy(alpha = 0.85f * pulse), // Liquid Cyan
                    Color.White.copy(alpha = 0.95f * pulse),       // Pure Prism Diamond White
                    Color(0xFFFACC15).copy(alpha = 0.85f * pulse), // Warm Sunbeam Golden Glow
                    Color(0xFF38BDF8).copy(alpha = 0.7f * pulse)   // Crystal Sky Blue
                )

                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    cornerRadius = 24.dp,
                    elevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val brush = Brush.linearGradient(
                                colors = glowColors,
                                start = androidx.compose.ui.geometry.Offset(size.width * (waveOffset - 0.5f), 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width * (waveOffset + 0.5f), size.height)
                            )
                            drawRoundRect(
                                brush = brush,
                                size = size,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                                style = Stroke(width = (3.dp + (2.dp * pulse)).toPx())
                            )
                        }
                ) {
                    dailyVerse?.let { verse ->
                        val spiritualTheme = remember(verse.text, verse.book) {
                            com.example.ui.components.VerseSpiritualTheme.detectTheme(verse)
                        }
                        val encodedBook = android.net.Uri.encode(verse.book)

                        Box(modifier = Modifier.fillMaxWidth()) {
                            // 1. Adaptive Canvas Spiritual Motif Background
                            com.example.ui.components.AdaptiveDailyVerseBackground(
                                theme = spiritualTheme,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.matchParentSize()
                            )

                            // 2. Ultra Pro Max Liquid Crystal Water Glass Vessel Effect (iOS 26 Liquid Glassmorphism)
                            com.example.ui.components.CrystalWaterGlassBackground(
                                spiritualTheme = spiritualTheme,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.matchParentSize()
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Theme Category Badge
                                Surface(
                                    color = spiritualTheme.accentColor.copy(alpha = if (isDarkTheme) 0.25f else 0.15f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = spiritualTheme.iconEmoji,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = spiritualTheme.badgeLabel.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.8.sp,
                                            fontSize = 9.sp,
                                            color = spiritualTheme.accentColor
                                        )
                                    }
                                }

                                // Clickable verse text & reference area leading to direct reader
                                val primaryVerseText = when (bibleVersion) {
                                    com.example.ui.util.BibleVersion.FRANCAIS_LSG -> dailyVerseFrench ?: verse.text
                                    else -> verse.text
                                }
                                val localizedBookName = com.example.ui.util.BibleBookNames.getDisplayName(
                                    verse.book,
                                    if (bibleVersion == com.example.ui.util.BibleVersion.FRANCAIS_LSG) "fr" else appLanguage
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("reader/$encodedBook/${verse.chapter}")
                                        }
                                ) {
                                    Text(
                                        text = "\"$primaryVerseText\"",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = getBibleFontFamily(fontFamilyType),
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            fontSize = 20.sp * textSizeMultiplier,
                                            lineHeight = (28 * textSizeMultiplier).sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (bibleVersion == com.example.ui.util.BibleVersion.BILINGUAL && !dailyVerseFrench.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "\"$dailyVerseFrench\"",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                fontSize = 15.sp * textSizeMultiplier,
                                                lineHeight = (22 * textSizeMultiplier).sp
                                            ),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$localizedBookName ${verse.chapter}:${verse.verseNumber}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 16.sp * textSizeMultiplier,
                                            fontWeight = FontWeight.Bold,
                                            color = spiritualTheme.accentColor
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (appLanguage == "fr") "Lecture complète" else "Lekti konplè",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = spiritualTheme.accentColor.copy(alpha = 0.9f)
                                            )
                                            Icon(
                                                Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = spiritualTheme.accentColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Reduced Size Like Button
                                val haptic = LocalHapticFeedback.current
                                val heartScale by animateFloatAsState(
                                    targetValue = if (isLiked) 1.2f else 1.0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "heart_scale"
                                )
                                val animatedBgColor by animateColorAsState(
                                    targetValue = if (isLiked) Color(0xFFEF4444).copy(alpha = 0.18f) else Color.Transparent,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "like_bg_color"
                                )

                                NeumorphicButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.toggleDailyVerseLike()
                                    },
                                    cornerRadius = 8.dp,
                                    elevation = if (isLiked) 2.dp else 3.dp,
                                    isDarkTheme = isDarkTheme,
                                    backgroundColor = animatedBgColor
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Sipòte / Like",
                                            modifier = Modifier
                                                .size(12.dp)
                                                .scale(heartScale),
                                            tint = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        AnimatedContent(
                                            targetState = likeCount,
                                            transitionSpec = {
                                                if (targetState > initialState) {
                                                    slideInVertically { height -> height } + fadeIn() togetherWith
                                                            slideOutVertically { height -> -height } + fadeOut()
                                                } else {
                                                    slideInVertically { height -> -height } + fadeIn() togetherWith
                                                            slideOutVertically { height -> height } + fadeOut()
                                                }
                                            },
                                            label = "live_likes_anim"
                                        ) { count ->
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                // Views Counter Button (Live)
                                NeumorphicButton(
                                    onClick = { },
                                    cornerRadius = 8.dp,
                                    elevation = 3.dp,
                                    isDarkTheme = isDarkTheme
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RemoveRedEye,
                                            contentDescription = "Vue",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        AnimatedContent(
                                            targetState = viewCount,
                                            transitionSpec = {
                                                if (targetState > initialState) {
                                                    slideInVertically { height -> height } + fadeIn() togetherWith
                                                            slideOutVertically { height -> -height } + fadeOut()
                                                } else {
                                                    slideInVertically { height -> -height } + fadeIn() togetherWith
                                                            slideOutVertically { height -> height } + fadeOut()
                                                }
                                            },
                                            label = "live_views_anim"
                                        ) { count ->
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                // Glassmorphic Comments Button
                                NeumorphicButton(
                                    onClick = { showCommentSheet = true },
                                    cornerRadius = 8.dp,
                                    elevation = 3.dp,
                                    isDarkTheme = isDarkTheme
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = "Kòmantè",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "${comments.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // 3-Dots More Options Menu (Fav, Copy, Share)
                                var showVerseOptions by remember { mutableStateOf(false) }
                                Box {
                                    NeumorphicButton(
                                        onClick = { showVerseOptions = true },
                                        modifier = Modifier.size(28.dp),
                                        cornerRadius = 8.dp,
                                        elevation = 3.dp,
                                        isDarkTheme = isDarkTheme
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Plis opsyon",
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showVerseOptions,
                                        onDismissRequest = { showVerseOptions = false },
                                        modifier = Modifier
                                            .background(
                                                if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFFFFFF),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (appLanguage == "fr") {
                                                        if (verse.isBookmarked) "Retirer des favoris" else "Ajouter aux favoris"
                                                    } else {
                                                        if (verse.isBookmarked) "Retire nan favori" else "Mete nan favori"
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = if (verse.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                    contentDescription = null,
                                                    tint = if (verse.isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            onClick = {
                                                showVerseOptions = false
                                                viewModel.toggleBookmark(verse)
                                            }
                                        )
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (appLanguage == "fr") "Copier le verset" else "Kopye vèsè",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            onClick = {
                                                showVerseOptions = false
                                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Vèsè", "\"$primaryVerseText\"\n— $localizedBookName ${verse.chapter}:${verse.verseNumber}")
                                                clipboard.setPrimaryClip(clip)
                                                val toastMsg = if (appLanguage == "fr") "Verset copié !" else "Vèsè kopye nan panyen!"
                                                android.widget.Toast.makeText(context, toastMsg, android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (appLanguage == "fr") "Partager le verset" else "Pataje vèsè",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            onClick = {
                                                showVerseOptions = false
                                                val shareIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "\"$primaryVerseText\"\n\n— $localizedBookName ${verse.chapter}:${verse.verseNumber}")
                                                    type = "text/plain"
                                                }
                                                val chooserTitle = if (appLanguage == "fr") "Partager ce verset" else "Pataje vèsè sa"
                                                context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

            // Dedicated "Tèm jodi a" Section
            item {
                val todayTheme = rememberTodayTheme()

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (appLanguage == "fr") "THÈME DU JOUR" else "TÈM JODI A",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFF10B981), // Emerald-500
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 24.dp,
                        elevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = todayTheme.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = todayTheme.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = todayTheme.reference,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = todayTheme.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Theme tags
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                todayTheme.tags.forEach { tag ->
                                    NeumorphicButton(
                                        onClick = {
                                            viewModel.setSearchQuery(tag)
                                            navController.navigate("search")
                                        },
                                        cornerRadius = 12.dp,
                                        elevation = 3.dp,
                                        isDarkTheme = isDarkTheme
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Rewarded video button stretched full width
                            NeumorphicButton(
                                onClick = { showRewardedDialog = true },
                                cornerRadius = 12.dp,
                                elevation = 3.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFF10B981),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (appLanguage == "fr") "Regarder une vidéo de 15s pour une récompense" else "Gade yon anons 15s pou rekonpans",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Banner Ad placed strictly under Tèm jodi a
            item {
                BottomBannerAdView(
                    adManager = adManager,
                    isDarkTheme = isDarkTheme,
                    onOpenPremiumDialog = { showPremiumDialog = true }
                )
            }

            // Reading Plan Card (Plan Lekti Bib la)
            item {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    elevation = 8.dp,
                    isDarkTheme = isDarkTheme
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(activePlan.colorPrimaryHex).copy(alpha = if (isDarkTheme) 0.22f else 0.12f),
                                        Color(activePlan.colorSecondaryHex).copy(alpha = if (isDarkTheme) 0.10f else 0.04f)
                                    )
                                )
                            )
                            .padding(18.dp)
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
                                Surface(
                                    color = Color(activePlan.colorPrimaryHex).copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                            contentDescription = null,
                                            tint = Color(activePlan.colorPrimaryHex),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = if (appLanguage == "fr") "PLAN DE LECTURE BIBLIQUE" else "PLAN LEKTI BIB LA",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(activePlan.colorPrimaryHex),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = activePlan.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Streak pill
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "🔥", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$readingStreak ${if (appLanguage == "fr") (if (readingStreak > 1) "jours" else "jour") else "jou"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (appLanguage == "fr") "Progression: ${completedDays.size} / ${activePlan.totalDays} Jours" else "Pwogrè: ${completedDays.size} / ${activePlan.totalDays} Jou",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(planProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(activePlan.colorPrimaryHex)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { planProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(activePlan.colorPrimaryHex),
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Today's reading summary
                        nextPlanDay?.let { day ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (appLanguage == "fr") "Aujourd'hui (Jour ${day.dayNumber}):" else "Jodi a (Jou ${day.dayNumber}):",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = day.passages.joinToString(", ") { it.displayReference },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            day.passages.firstOrNull()?.let { p ->
                                                navController.navigate("reader/${p.book}/${p.startChapter}")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(activePlan.colorPrimaryHex)),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (appLanguage == "fr") "Lire Maintenant" else "Li Kounye a",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
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
                            TextButton(
                                onClick = { navController.navigate("reading_plans") }
                            ) {
                                Icon(
                                    Icons.Default.FormatListNumbered,
                                    contentDescription = null,
                                    tint = Color(activePlan.colorPrimaryHex),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (appLanguage == "fr") "Voir tous les plans & rappels" else "Gade tout plan yo & rapèl",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(activePlan.colorPrimaryHex)
                                )
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(activePlan.colorPrimaryHex),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Progress Row
            item {
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    elevation = 8.dp,
                    isDarkTheme = isDarkTheme
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (appLanguage == "fr") "DERNIÈRE LECTURE" else "DÈNYE LEKTI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val progressText = progress?.let {
                            val b = com.example.ui.util.BibleBookNames.getDisplayName(it.book, appLanguage)
                            "$b ${it.chapter}:${it.verseNumber}"
                        } ?: (if (appLanguage == "fr") "Jean 3:16" else "Jan 3:16")
                        Text(
                            text = progressText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 0.66f },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF3B82F6),
                            trackColor = Color(0xFFD1D9E6)
                        )
                    }
                }
            }
        }
    }

    if (showCommentSheet) {
        GlassmorphicCommentSheet(
            comments = comments,
            savedUserName = savedUserName,
            viewModel = viewModel,
            isDarkTheme = isDarkTheme,
            onDismiss = { showCommentSheet = false }
        )
    }
}

private data class DailyThemeInfo(
    val title: String,
    val reference: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tags: List<String>
)

@Composable
private fun rememberTodayTheme(): DailyThemeInfo {
    return remember {
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        when (dayOfWeek) {
            java.util.Calendar.SUNDAY -> DailyThemeInfo(
                title = "Gras ak Adorasyon",
                reference = "Sòm 100:1-5",
                description = "Jodi a se yon jou benediksyon pou rann Bondye aksyon de gras ak lajwa nan kè nou.",
                icon = Icons.Default.Favorite,
                tags = listOf("Gras", "Lajwa", "Adorasyon")
            )
            java.util.Calendar.MONDAY -> DailyThemeInfo(
                title = "Lafwa ak Konfyans",
                reference = "Ebre 11:1",
                description = "Mete tout konfyans ou nan Bondye pou semèn sa a. Lafwa se asirans bagay nou espere yo.",
                icon = Icons.Default.Lightbulb,
                tags = listOf("Lafwa", "Konfyans", "Espwa")
            )
            java.util.Calendar.TUESDAY -> DailyThemeInfo(
                title = "Lapè Bondye",
                reference = "Jan 14:27",
                description = "Kris la ban nou yon lapè ki depase tout konpreyansyon. Pa kite kè w boulvèse.",
                icon = Icons.Default.Star,
                tags = listOf("Lapè", "Kouray", "Poze")
            )
            java.util.Calendar.WEDNESDAY -> DailyThemeInfo(
                title = "Lanmou ak Sèvis",
                reference = "1 Korentyen 13:13",
                description = "Pi gwo kòmandman an se lanmou. Se pou nou renmen youn lòt menm jan Kris la renmen nou.",
                icon = Icons.Default.Favorite,
                tags = listOf("Lanmou", "Fratènite", "Sèvis")
            )
            java.util.Calendar.THURSDAY -> DailyThemeInfo(
                title = "Pardon ak Rekonsilyasyon",
                reference = "Efèzyen 4:32",
                description = "Padone moun ki fè w mal menm jan Bondye padone w nan Kris la, pou kè w ka lib ak trankil.",
                icon = Icons.Default.CheckCircle,
                tags = listOf("Pardon", "Kè bon", "Rekonsilyasyon")
            )
            java.util.Calendar.FRIDAY -> DailyThemeInfo(
                title = "Lajwa ak Rekonesans",
                reference = "1 Tesalonisyen 5:16-18",
                description = "Toujou gen kè kontan, lapriyè san rete, epi di Bondye mèsi nan tout sikonstans lavi a.",
                icon = Icons.Default.ThumbUp,
                tags = listOf("Lajwa", "Mèsi", "Lapriyè")
            )
            else -> DailyThemeInfo(
                title = "Kouray ak Fòs",
                reference = "Ezayi 40:31",
                description = "Moun ki mete konfyans yo nan Seyè a ap jwenn nouvo fòs. Yo p'ap janm fatige.",
                icon = Icons.Default.Bookmark,
                tags = listOf("Fòs", "Kouray", "Viktwa")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphicCommentSheet(
    comments: List<com.example.data.VerseComment>,
    savedUserName: String,
    viewModel: BibleViewModel,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val deviceId = remember { viewModel.deviceId }

    var commentText by remember { mutableStateOf("") }
    var nameText by remember { mutableStateOf(savedUserName) }
    var isEditingName by remember { mutableStateOf(savedUserName.isBlank()) }
    var replyingTo by remember { mutableStateOf<com.example.data.VerseComment?>(null) }

    LaunchedEffect(savedUserName) {
        if (savedUserName.isNotBlank() && nameText.isBlank()) {
            nameText = savedUserName
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.55f),
        dragHandle = null
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    if (isDarkTheme) Color(0xFF0F172A).copy(alpha = 0.94f)
                    else Color(0xFFF8FAFC).copy(alpha = 0.94f)
                )
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDarkTheme) 0.3f else 0.8f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                },
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Header
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
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Kòmantè (${comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        var isRefreshing by remember { mutableStateOf(false) }
                        val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(750, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "spin"
                        )

                        IconButton(
                            onClick = {
                                if (!isRefreshing) {
                                    isRefreshing = true
                                    viewModel.refreshComments {
                                        isRefreshing = false
                                        android.widget.Toast.makeText(context, "Dènye kòmantè yo chaje!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rafrechi kòmantè",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer {
                                        rotationZ = if (isRefreshing) rotation else 0f
                                    }
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fèmen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                // Comments List
                if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pa gen kòmantè ankò. Se ou menm ki pou premye ekri!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val topLevelComments = comments.filter { it.parentId.isNullOrBlank() || it.parentId == "null" }
                        items(topLevelComments.size, key = { topLevelComments[it].id }) { index ->
                            val comment = topLevelComments[index]
                            CommentItemCard(
                                comment = comment,
                                deviceId = deviceId,
                                isDarkTheme = isDarkTheme,
                                onLike = { viewModel.toggleCommentLike(comment.id) },
                                onReply = { replyingTo = comment },
                                onReport = {
                                    viewModel.reportComment(comment.id)
                                    android.widget.Toast.makeText(context, "Kòmantè sa sinyale!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )

                            val replies = comments.filter { it.parentId == comment.id }
                            replies.forEach { reply ->
                                Row(modifier = Modifier.padding(start = 24.dp, top = 6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(36.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CommentItemCard(
                                        comment = reply,
                                        deviceId = deviceId,
                                        isDarkTheme = isDarkTheme,
                                        onLike = { viewModel.toggleCommentLike(reply.id) },
                                        onReply = { replyingTo = comment },
                                        onReport = {
                                            viewModel.reportComment(reply.id)
                                            android.widget.Toast.makeText(context, "Kòmantè sa sinyale!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Replying To Banner
                replyingTo?.let { reply ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reponn @${reply.userName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { replyingTo = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Kansle",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Chat Input Section Glass Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.85f)
                            else Color.White.copy(alpha = 0.85f)
                        )
                        .padding(10.dp)
                ) {
                    if (isEditingName || nameText.isBlank()) {
                        OutlinedTextField(
                            value = nameText,
                            onValueChange = { 
                                nameText = it
                                viewModel.saveUserName(it)
                            },
                            placeholder = { Text("Non ou (egzanp: Frè Pòl)", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Kòmantè kòm: ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = nameText,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Chanje non",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.clickable { isEditingName = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = {
                                if (it.length <= 700) {
                                    commentText = it
                                }
                            },
                            placeholder = { Text("Ekri yon kòmantè... (max 700 lèt)", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp, max = 100.dp),
                            maxLines = 4,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            supportingText = {
                                Text(
                                    text = "${commentText.length}/700",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (commentText.length >= 680) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank() && nameText.isNotBlank()) {
                                    viewModel.addComment(
                                        text = commentText,
                                        parentId = replyingTo?.id,
                                        userName = nameText
                                    )
                                    commentText = ""
                                    replyingTo = null
                                    isEditingName = false
                                }
                            },
                            enabled = commentText.isNotBlank() && nameText.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (commentText.isNotBlank() && nameText.isNotBlank()) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Voye",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItemCard(
    comment: com.example.data.VerseComment,
    deviceId: String,
    isDarkTheme: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onReport: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isLiked = comment.likedByUsers.contains(deviceId)

    val diffSeconds = (System.currentTimeMillis() - comment.timestamp) / 1000
    val timeAgo = when {
        diffSeconds < 60 -> "Kounye a"
        diffSeconds < 3600 -> "${diffSeconds / 60} mn de sa"
        diffSeconds < 86400 -> "${diffSeconds / 3600} èdtan de sa"
        else -> "${diffSeconds / 86400} jou de sa"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.6f)
                else Color.White.copy(alpha = 0.7f)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = comment.userName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = timeAgo,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Opsyon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Sinyale kòmantè sa a", color = Color.Red)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onReport()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onLike() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${comment.likes}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Reponn",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onReply() }
                )
            }
        }
    }
}
