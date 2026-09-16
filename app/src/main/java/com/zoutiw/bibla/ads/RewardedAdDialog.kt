package com.zoutiw.bibla.ads

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    adManager: AdManager,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onRewardEarned: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val rewardedAdUnitId = adManager.getEffectiveRewardedAdUnitId()

    var isSimulatingAd by remember { mutableStateOf(true) }
    var simulationSecondsLeft by remember { mutableIntStateOf(15) }
    var rewardedAdInstance by remember { mutableStateOf<RewardedAd?>(null) }
    var isAdLoading by remember { mutableStateOf(false) }

    // Preload Rewarded Ad if available
    LaunchedEffect(Unit) {
        if (adManager.rewardedAdsEnabled.value && !adManager.isTestMode.value && rewardedAdUnitId.isNotBlank()) {
            isAdLoading = true
            RewardedAd.load(
                context,
                rewardedAdUnitId,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAdInstance = ad
                        isAdLoading = false
                        if (activity != null) {
                            ad.show(activity) { _ ->
                                isSimulatingAd = false
                                adManager.addReward()
                                adManager.incrementImpression()
                                Toast.makeText(context, "Felisitasyon! Rekonpans debloke a siksè!", Toast.LENGTH_SHORT).show()
                                onRewardEarned()
                                onDismiss()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAdInstance = null
                        isAdLoading = false
                    }
                }
            )
        }
    }

    // Direct 15-second countdown starts immediately
    LaunchedEffect(isSimulatingAd) {
        if (isSimulatingAd) {
            simulationSecondsLeft = 15
            while (simulationSecondsLeft > 0) {
                delay(1000L)
                simulationSecondsLeft--
            }
            if (isSimulatingAd) {
                isSimulatingAd = false
                adManager.addReward()
                adManager.incrementImpression()
                Toast.makeText(context, "Felisitasyon! Ou debloke fonksyon avanse a siksè!", Toast.LENGTH_LONG).show()
                onRewardEarned()
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fèmen")
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Videyo Reklam (15s)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Videyo anons lan ap jwe pou w ka jwenn rekonpans epi debloke fonksyon avanse yo!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { (15 - simulationSecondsLeft) / 15f },
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 6.dp,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Ap jwe... $simulationSecondsLeft segonn ki rete",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mèsi paske w ap sipòte travay Levanjil Bib La!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

