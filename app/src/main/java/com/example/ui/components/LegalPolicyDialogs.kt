package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog konplè pou Politik Konfidansyalite (Privacy Policy) an Kreyòl Ayisyen
 * ki respekte tout direktiv ak egzijans Google Play Store.
 */
@Composable
fun PrivacyPolicyDialog(
    isDarkTheme: Boolean,
    onlineUrl: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PrivacyTip,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Politik Konfidansyalite",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dènye mizajou: Dawout 2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fèmen")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PolicySection(
                            title = "1. Entwodiksyon & Angajman Nou",
                            content = "Aplikasyon \"Bib La • Edisyon Enpakt\" angaje l pou pwoteje vi prive w ak konfidansyalite done w yo. Politik sa a eksplike kijan nou trete enfòmasyon lè w ap itilize aplikasyon an, an konfòmite total ak règleman Google Play Store."
                        )
                    }

                    item {
                        PolicySection(
                            title = "2. Done Nou Kolekte & Depo Lokal",
                            content = "• Pa Gen Done Pèsonèl Idantifikab: Nou pa mande ni kolekte non w, adrès imel ou, oswa kote w ye (GPS).\n" +
                                    "• Depo 100% Lokal: Tout nòt pèsonèl ou ekri, vèsè favori ou make, preferans tèm, ak pwogrè lekti ou yo estoke sèlman andedan telefòn ou gras ak baz done lokal Room.\n" +
                                    "• Pa Gen Vant Done: Nou pa janm vann, lwe, oswa pataje done w yo bay okenn konpayi deyò."
                        )
                    }

                    item {
                        PolicySection(
                            title = "3. Pèmisyon Aplikasyon an Mande",
                            content = "• Notifikasyon (POST_NOTIFICATIONS): Sèvi sèlman pou voye rapèl lekti chak jou ak vèsè jounen an si w aktive yo nan paramèt yo.\n" +
                                    "• Aksè Entènèt (INTERNET): Sèvi pou chaje anons Google AdMob ak resevwa anons mizajou devlopè a si sa nesesè."
                        )
                    }

                    item {
                        PolicySection(
                            title = "4. Piblisite & Google AdMob",
                            content = "Aplikasyon an itilize sèvis Google AdMob pou afiche reklam. Google ka itilize idantifyan piblisite anonim aparèy ou an pou afiche anons ki adapte. Ou ka retire tout anons nèt grasa opsyon Bib Premium nan aplikasyon an."
                        )
                    }

                    item {
                        PolicySection(
                            title = "5. Pwoteksyon Timoun (COPPA)",
                            content = "Aplikasyon Bib La fèt pou tout laj, soti nan timoun rive nan granmoun. Nou pa kolekte okenn enfòmasyon sou timoun ki gen mwens pase 13 lane, epi kontni nou respekte nòm moral ak familyal strik."
                        )
                    }

                    item {
                        PolicySection(
                            title = "6. Sekirite & Kontwòl Itilizatè",
                            content = "Ou gen kontwòl total sou done w yo. Si w dezenstale aplikasyon an oswa efase done aplikasyon an nan paramèt telefòn ou, tout nòt ak preferans yo ap efase nèt san kite okenn tras sou okenn sèvè."
                        )
                    }

                    item {
                        PolicySection(
                            title = "7. Kontak Sipò & Responsab Konfidansyalite",
                            content = "Pou nenpòt kesyon, sijesyon oswa demand konsènan politik konfidansyalite sa a, ou ka kontakte nou pa imel nan: jacksonofisyal@gmail.com oswa nan WhatsApp: +18296211349."
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onlineUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(onlineUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gade sou Sit", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mwen Konprann")
                    }
                }
            }
        }
    }
}

/**
 * Dialog konplè pou Kondisyon pou Sèvi ak Aplikasyon an (Terms of Use) an Kreyòl Ayisyen.
 */
@Composable
fun TermsOfUseDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Kondisyon Itilizasyon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Terms of Use • Bib La",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fèmen")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PolicySection(
                            title = "1. Akseptasyon Kondisyon yo",
                            content = "Lè w telechaje, enstale oswa itilize aplikasyon \"Bib La • Edisyon Enpakt\", ou rekonèt epi ou aksepte kondisyon itilizasyon sa yo. Si w pa dakò ak kèk pwen, ou ka retire aplikasyon an sou telefòn ou nenpòt kilè."
                        )
                    }

                    item {
                        PolicySection(
                            title = "2. Dwa Itilizasyon & Tèks Sakre a",
                            content = "• Tèks Bib la disponib gratis pou lekti pèsonèl, etid biblik, meditasyon, kilt legliz ak evanjelizasyon.\n" +
                                    "• Ou lib pou kopye vèsè ak pataje yo ak fanmi w, zanmi w, oswa sou rezo sosyo yo pou beni lavi lòt moun.\n" +
                                    "• Li entèdi pou modifye tèks sakre a oswa vann aplikasyon an san otorizasyon ekri."
                        )
                    }

                    item {
                        PolicySection(
                            title = "3. Pwopriyete Entèlektyèl & Dwa Otè",
                            content = "Tout konsepsyon koòdone grafik (UI), kòd sous, logo ofisyèl, animasyon, ak karakteristik espesyal aplikasyon an se pwopriyete eksklizif devlopè a ak ekip Bib La anba lwa entènasyonal sou dwa otè."
                        )
                    }

                    item {
                        PolicySection(
                            title = "4. Piblisite & Sèvis Bib Premium",
                            content = "Vèsyon gratis la gen piblisite AdMob pou sipòte devlopman ak jesyon sèvè yo. Itilizatè yo ka chwazi achte \"Bib Premium\" pou retire tout anons pou tout tan oswa gade videyo rekonpans."
                        )
                    }

                    item {
                        PolicySection(
                            title = "5. Garanti & Limit Responsablite",
                            content = "Aplikasyon an bay \"jan li ye a\" (AS IS). Nou fè tout posib nou pou asire presizyon tèks la ak yon eksperyans san fot, men nou pa responsab okenn domaj endirèk ki ta ka soti nan itilizasyon aplikasyon an."
                        )
                    }

                    item {
                        PolicySection(
                            title = "6. Mizajou & Modifikasyon",
                            content = "Nou rezève dwa pou mete ajou aplikasyon an, ajoute nouvo fonksyonalite oswa modifye kondisyon sa yo pou amelyore sèvis la selon nòm Google Play Store."
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mwen Aksepte Kondisyon yo")
                }
            }
        }
    }
}

/**
 * Dialog pou Enfòmasyon Legal & Dwa Otè (Legal & Copyright)
 */
@Composable
fun LegalInfoDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Enfòmasyon Legal & Dwa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Legal & Open Source Notices",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fèmen")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PolicySection(
                            title = "📖 Tradiksyon Bib Kreyòl Ayisyen",
                            content = "Tèks Bib la ki nan aplikasyon sa a se tradiksyon Sent Bib la an Kreyòl Ayisyen (Edisyon Enpakt). Tradiksyon sa a itilize pou glorifye non Bondye epi gaye Levanjil Jezi Kris la nan mitan tout kominote ayisyen ak kreyolofòn atravè lemond."
                        )
                    }

                    item {
                        PolicySection(
                            title = "© Copyright & Dwa Otè",
                            content = "Copyright © 2026 Bib La • Edisyon Enpakt.\n" +
                                    "Tout dwa sou konsepsyon aplikasyon, koòdone, ak mak grafik yo rezève.\n" +
                                    "Devlope avèk pasyon ak devouman pa Jackson Charles pou kominote kretyen an."
                        )
                    }

                    item {
                        PolicySection(
                            title = "🛡️ Lisans Bibliyotèk Sous Ouvè (Open Source)",
                            content = "Aplikasyon sa a bati gras ak teknoloji avanse Google Android ak bibliyotèk sous ouvè anba lisans Apache 2.0 ak MIT:\n" +
                                    "• Jetpack Compose & Material Design 3 (Google)\n" +
                                    "• AndroidX Room Database & SQLite Engine\n" +
                                    "• Kotlin Coroutines & StateFlow (JetBrains)\n" +
                                    "• Google Play Services & AdMob SDK"
                        )
                    }

                    item {
                        PolicySection(
                            title = "✅ Konfòmite Google Play Store",
                            content = "Aplikasyon an konfòm ak tout règleman devlopè Google Play: pa gen okenn kòd malveyan, pa gen tracking san otorizasyon, epi li respekte tout estanda sekirite ak konfidansyalite modèn."
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Fèmen")
                }
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}
