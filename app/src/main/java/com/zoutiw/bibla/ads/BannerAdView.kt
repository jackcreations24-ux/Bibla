package com.zoutiw.bibla.ads

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.viewinterop.AndroidView
import com.zoutiw.bibla.ui.components.NeumorphicCard
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BottomBannerAdView(
    adManager: AdManager,
    isDarkTheme: Boolean,
    onOpenPremiumDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBannerVisible = adManager.isBannerVisible()
    val adUnitId = adManager.getEffectiveBannerAdUnitId()
    val isTestMode by adManager.isTestMode.collectAsState()

    if (!isBannerVisible) return

    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var adLoadError by remember { mutableStateOf(false) }

    NeumorphicCard(
        isDarkTheme = isDarkTheme,
        cornerRadius = 20.dp,
        elevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Label Header for Ad Transparency
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isTestMode) "RÈKLAM (ADMOB TEST)" else "RÈKLAM",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.clickable { onOpenPremiumDialog() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Retire anons ($2.99)",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }

            // Real AdMob AdView with Fallback Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            this.adUnitId = adUnitId
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isAdLoaded = true
                                    adLoadError = false
                                    adManager.incrementImpression()
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    isAdLoaded = false
                                    adLoadError = true
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        }
                    },
                    update = { adView ->
                        // Re-load if ad unit changes
                        if (adView.adUnitId != adUnitId) {
                            adView.adUnitId = adUnitId
                            adView.loadAd(AdRequest.Builder().build())
                        }
                    },
                    modifier = Modifier.wrapContentSize()
                )

                // High quality styled banner card fallback if AdMob is loading/offline
                if (!isAdLoaded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .clickable { onOpenPremiumDialog() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bib La • Edisyon Enpakt",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pase nan Mòd Premium pou lekti san okenn entèripsyon!",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF2563EB),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "DEBLOKE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

