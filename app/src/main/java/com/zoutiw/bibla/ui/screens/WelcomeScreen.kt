package com.zoutiw.bibla.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zoutiw.bibla.R
import com.zoutiw.bibla.ui.BibleViewModel
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.zoutiw.bibla.ui.components.NeumorphicCard

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.zoutiw.bibla.ads.AdManager
import coil.compose.AsyncImage

@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: BibleViewModel
) {
    val context = LocalContext.current
    val adManager = remember { AdManager.getInstance(context) }
    val customLogoUrl by adManager.customLogoUrl.collectAsState()
    val logoTheme by adManager.logoTheme.collectAsState()

    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val appLanguage by viewModel.appLanguage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Serene Background Image
        Image(
            painter = painterResource(id = R.drawable.img_welcome_peace),
            contentDescription = "Imaj Lapè ak Espwa",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay for smooth readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.60f),
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // Main Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Badge / Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24), // Gold/Amber
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (appLanguage == "fr") "LA SAINTE BIBLE" else "BIB LA AN KREYÒL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Middle Card - Title, Verse & Welcome Message
            NeumorphicCard(
                isDarkTheme = true, // Dark card overlay for high contrast over photo
                cornerRadius = 28.dp,
                elevation = 12.dp,
                backgroundColor = Color(0xFF0F172A).copy(alpha = 0.88f), // Slate 900 semi-transparent
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dynamic Custom Logo or Golden Emblem
                    if (customLogoUrl.isNotBlank()) {
                        AsyncImage(
                            model = customLogoUrl,
                            contentDescription = "Bib La Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_splash_bible_logo_1786786447926),
                            contentDescription = "Bib La Logo",
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (appLanguage == "fr") "Paix et Espérance" else "Lapè ak Espwa",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (appLanguage == "fr") {
                            "« Je vous laisse la paix, je vous donne ma paix. Je ne vous donne pas comme le monde donne. Que votre cœur ne se trouble point, et ne s'alarme point. »"
                        } else {
                            "\"Mwen kite lapè pou nou. Mwen ban nou pwòp lapè pa m lan. Kè nou pa bezwen boulvèse, ni nou pa bezwen pè.\""
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            lineHeight = 24.sp
                        ),
                        color = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (appLanguage == "fr") "— Jean 14:27" else "— Jan 14:27",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA) // Light blue accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Continue Action Button
            NeumorphicButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                cornerRadius = 20.dp,
                elevation = 8.dp,
                isDarkTheme = true,
                backgroundColor = Color(0xFF2563EB), // Blue primary
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = if (appLanguage == "fr") "CONTINUER" else "KONTINYE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

