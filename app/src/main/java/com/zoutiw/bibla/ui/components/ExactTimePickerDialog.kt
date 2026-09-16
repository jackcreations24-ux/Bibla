package com.zoutiw.bibla.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zoutiw.bibla.ui.theme.PrimaryColor

@Composable
fun ExactTimePickerDialog(
    currentEnabled: Boolean,
    currentHour: Int,
    currentMinute: Int,
    onSave: (Boolean, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isEnabled by remember { mutableStateOf(currentEnabled) }
    var selectedHour24 by remember { mutableIntStateOf(currentHour) }
    var selectedMinute by remember { mutableIntStateOf(currentMinute) }

    // Derive 12h display
    val isPm = selectedHour24 >= 12
    val hour12 = when {
        selectedHour24 == 0 -> 12
        selectedHour24 > 12 -> selectedHour24 - 12
        else -> selectedHour24
    }

    var activeField by remember { mutableStateOf("minute") } // "hour" or "minute"

    val formattedTime = remember(selectedHour24, selectedMinute) {
        val h = if (selectedHour24 == 0) 12 else if (selectedHour24 > 12) selectedHour24 - 12 else selectedHour24
        val amPm = if (selectedHour24 >= 12) "PM" else "AM"
        String.format("%02d:%02d %s", h, selectedMinute, amPm)
    }

    val presetTimes = listOf(
        Triple("06:00 AM", "Bonè", 6 to 0),
        Triple("07:30 AM", "Maten", 7 to 30),
        Triple("12:00 PM", "Midi", 12 to 0),
        Triple("06:30 PM", "Aswè", 18 to 30),
        Triple("08:00 PM", "Nwit", 20 to 0),
        Triple("09:30 PM", "Dòmi", 21 to 30)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = PrimaryColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Alarm,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Lè Egzak Pou Rapèl Lekti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Konfigirasyon Rapèl Chak Jou",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Switch activation
                Surface(
                    color = if (isEnabled) PrimaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aktive Rapèl Otomatik",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isEnabled) "Notifikasyon ap voye chak jou" else "Rapèl la dezaktive",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryColor)
                        )
                    }
                }

                if (isEnabled) {
                    // Digital Time Display & Tuner
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LÈ W CHWAZI A",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Large interactive clock
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Hour Block
                                Surface(
                                    color = if (activeField == "hour") PrimaryColor else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .clickable { activeField = "hour" }
                                        .border(
                                            width = if (activeField == "hour") 2.dp else 1.dp,
                                            color = if (activeField == "hour") PrimaryColor else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Text(
                                        text = String.format("%02d", hour12),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (activeField == "hour") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }

                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                // Minute Block
                                Surface(
                                    color = if (activeField == "minute") PrimaryColor else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .clickable { activeField = "minute" }
                                        .border(
                                            width = if (activeField == "minute") 2.dp else 1.dp,
                                            color = if (activeField == "minute") PrimaryColor else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Text(
                                        text = String.format("%02d", selectedMinute),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (activeField == "minute") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // AM/PM Segment
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        color = if (!isPm) PrimaryColor else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .clickable {
                                                if (isPm) {
                                                    selectedHour24 = (selectedHour24 - 12).coerceAtLeast(0)
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = "AM",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (!isPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        color = if (isPm) PrimaryColor else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .clickable {
                                                if (!isPm) {
                                                    selectedHour24 = (selectedHour24 + 12).coerceAtMost(23)
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = "PM",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPm) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Precision Stepper Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // -1 Hour
                                OutlinedButton(
                                    onClick = {
                                        selectedHour24 = if (selectedHour24 == 0) 23 else selectedHour24 - 1
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("-1h", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }

                                // -5 Min
                                OutlinedButton(
                                    onClick = {
                                        selectedMinute = (selectedMinute - 5 + 60) % 60
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("-5m", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }

                                // +5 Min
                                OutlinedButton(
                                    onClick = {
                                        selectedMinute = (selectedMinute + 5) % 60
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+5m", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }

                                // +1 Hour
                                OutlinedButton(
                                    onClick = {
                                        selectedHour24 = (selectedHour24 + 1) % 24
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+1h", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Minute slider for ultra precise adjustment
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Ajiste minit yo:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$selectedMinute min", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                }
                                Slider(
                                    value = selectedMinute.toFloat(),
                                    onValueChange = { selectedMinute = it.toInt() },
                                    valueRange = 0f..59f,
                                    steps = 58,
                                    colors = SliderDefaults.colors(
                                        thumbColor = PrimaryColor,
                                        activeTrackColor = PrimaryColor
                                    )
                                )
                            }
                        }
                    }

                    // Fast Presets Section
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "LÈ KI POPILÈ (PRESETS RAPID)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            presetTimes.chunked(3).forEach { rowPresets ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowPresets.forEach { (timeStr, label, pair) ->
                                        val (h, m) = pair
                                        val isSelected = selectedHour24 == h && selectedMinute == m
                                        Surface(
                                            color = if (isSelected) PrimaryColor else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    selectedHour24 = h
                                                    selectedMinute = m
                                                }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = timeStr,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 9.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Confirmation description preview
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notifikasyon an ap vini chak jou a $formattedTime.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(isEnabled, selectedHour24, selectedMinute) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Anrejistre Lè a", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anile")
            }
        }
    )
}

