package com.zoutiw.bibla.ads

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoutiw.bibla.ui.components.NeumorphicButton

@Composable
fun PremiumUpgradeDialog(
    adManager: AdManager,
    isDarkTheme: Boolean,
    language: String = "ht",
    onDismiss: () -> Unit,
    onUpgraded: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = if (language == "fr") "Fermer" else "Fèmen")
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (language == "fr") "La Sainte Bible • Mode Premium" else "Bib La • Mòd Premium",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = Color(0xFF2563EB).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = if (language == "fr") "$2.99 • À vie, un seul paiement" else "$2.99 • Yon sèl fwa pou tout tan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (language == "fr") {
                        PremiumFeatureRow("Suppression totale des publicités dans toute l'application")
                        PremiumFeatureRow("Débloquez tous les thèmes et couvertures personnalisés")
                        PremiumFeatureRow("Lecture rapide et fonctionnalités avancées illimitées")
                        PremiumFeatureRow("Soutenez le développement et le ministère de La Sainte Bible")
                    } else {
                        PremiumFeatureRow("Retire tout reklam yo nèt sou tout aplikasyon an")
                        PremiumFeatureRow("Debloke tout tèm ak kouvèti nòt pèsonalize yo")
                        PremiumFeatureRow("Lekti rapid ak tout fonksyon avanse san limit")
                        PremiumFeatureRow("Sipòte devlopman ak ministè Bib La")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NeumorphicButton(
                    onClick = {
                        adManager.setPremiumUser(true)
                        val toastMsg = if (language == "fr") {
                            "Félicitations ! Vous êtes désormais membre Premium !"
                        } else {
                            "Felisitasyon! Ou se yon manm Bib Premium kounye a!"
                        }
                        Toast.makeText(
                            context,
                            toastMsg,
                            Toast.LENGTH_LONG
                        ).show()
                        onUpgraded()
                        onDismiss()
                    },
                    cornerRadius = 20.dp,
                    elevation = 8.dp,
                    isDarkTheme = isDarkTheme,
                    backgroundColor = Color(0xFF2563EB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "fr") "DÉBLOQUER PREMIUM ($2.99)" else "DEBLOKE PREMIUM ($2.99)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
private fun PremiumFeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

