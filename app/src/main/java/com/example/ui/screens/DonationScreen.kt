package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.BibleViewModel
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    navController: NavController,
    viewModel: BibleViewModel
) {
    val context = LocalContext.current
    val adManager = remember { com.example.ads.AdManager.getInstance(context) }
    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val appLanguage by viewModel.appLanguage.collectAsState()

    val paypalAddress by adManager.paypalAddress.collectAsState()
    val wiseAddress by adManager.wiseAddress.collectAsState()
    val binanceId by adManager.binanceId.collectAsState()

    val wiseCleanTag = wiseAddress.removePrefix("@")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (appLanguage == "fr") "Soutenir le Ministère (Dons)" else "Sipòte Ministè a (Donasyon)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = if (appLanguage == "fr") "Retour" else "Retounen")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    cornerRadius = 24.dp,
                    elevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.VolunteerActivism,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (appLanguage == "fr") "Faire un Don" else "Fè Yon Donasyon",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (appLanguage == "fr") {
                                "\"Car Dieu aime celui qui donne avec joie.\" — 2 Corinthiens 9:7"
                            } else {
                                "\"Paske Bondye renmen moun ki bay ak kè kontan.\" — 2 Korentyen 9:7"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (appLanguage == "fr") {
                                "Votre don aide au soutien, à la maintenance et au développement de la Bible pour toucher toujours plus d'âmes à travers le monde entier."
                            } else {
                                "Aksyon donasyon ou an ede antrennman ak devlopman Bib La pou sa ka touche plis nanm toupatou atravè mond lan."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // PayPal Option
            item {
                DonationPlatformCard(
                    isDarkTheme = isDarkTheme,
                    platformName = "PayPal",
                    accountDetails = paypalAddress,
                    displayLink = "paypal.me/$paypalAddress",
                    brandColor = Color(0xFF0079C1),
                    iconVector = Icons.Default.Payment,
                    appLanguage = appLanguage,
                    onCopy = {
                        copyToClipboard(context, "PayPal", "https://paypal.me/$paypalAddress", appLanguage)
                    },
                    onOpenPlatform = {
                        openUrl(context, "https://paypal.me/$paypalAddress", appLanguage)
                    }
                )
            }

            // Wise Option
            item {
                DonationPlatformCard(
                    isDarkTheme = isDarkTheme,
                    platformName = "Wise",
                    accountDetails = if (wiseAddress.startsWith("@")) wiseAddress else "@$wiseAddress",
                    displayLink = "wise.com/pay/me/$wiseCleanTag",
                    brandColor = Color(0xFF2563EB),
                    iconVector = Icons.Default.AccountBalance,
                    appLanguage = appLanguage,
                    onCopy = {
                        copyToClipboard(context, "Wise Tag", if (wiseAddress.startsWith("@")) wiseAddress else "@$wiseAddress", appLanguage)
                    },
                    onOpenPlatform = {
                        openUrl(context, "https://wise.com/pay/me/$wiseCleanTag", appLanguage)
                    }
                )
            }

            // Binance ID Option
            item {
                DonationPlatformCard(
                    isDarkTheme = isDarkTheme,
                    platformName = "Binance Pay",
                    accountDetails = "ID: $binanceId",
                    displayLink = "Binance ID: $binanceId",
                    brandColor = Color(0xFFF59E0B),
                    iconVector = Icons.Default.CurrencyExchange,
                    appLanguage = appLanguage,
                    onCopy = {
                        copyToClipboard(context, "Binance ID", binanceId, appLanguage)
                    },
                    onOpenPlatform = {
                        openUrl(context, "https://pay.binance.com", appLanguage)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DonationPlatformCard(
    isDarkTheme: Boolean,
    platformName: String,
    accountDetails: String,
    displayLink: String,
    brandColor: Color,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    appLanguage: String = "ht",
    onCopy: () -> Unit,
    onOpenPlatform: () -> Unit
) {
    NeumorphicCard(
        isDarkTheme = isDarkTheme,
        cornerRadius = 20.dp,
        elevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = brandColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            iconVector,
                            contentDescription = null,
                            tint = brandColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = platformName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = accountDetails,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = brandColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayLink,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicButton(
                    onClick = onCopy,
                    cornerRadius = 12.dp,
                    elevation = 4.dp,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appLanguage == "fr") "Copier" else "Kopye",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                NeumorphicButton(
                    onClick = onOpenPlatform,
                    cornerRadius = 12.dp,
                    elevation = 4.dp,
                    isDarkTheme = isDarkTheme,
                    backgroundColor = brandColor,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appLanguage == "fr") "Ouvrir" else "Ouvri Platfòm",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String, appLanguage: String = "ht") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    val msg = if (appLanguage == "fr") "$label copié dans le presse-papier !" else "$label kopye nan clipboard!"
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

private fun openUrl(context: Context, url: String, appLanguage: String = "ht") {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        val msg = if (appLanguage == "fr") "Impossible d'ouvrir le lien pour le moment" else "Enposib pou ouvri lyen an kounye a"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
