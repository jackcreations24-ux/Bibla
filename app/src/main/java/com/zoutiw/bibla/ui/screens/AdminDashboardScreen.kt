package com.zoutiw.bibla.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zoutiw.bibla.ads.AdManager
import com.zoutiw.bibla.ads.RewardedAdDialog
import com.zoutiw.bibla.ui.BibleViewModel
import com.zoutiw.bibla.ui.components.NeumorphicButton
import com.zoutiw.bibla.ui.components.NeumorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: BibleViewModel
) {
    val context = LocalContext.current
    val adManager = remember { AdManager.getInstance(context) }

    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkModePreference by viewModel.isDarkMode.collectAsState()
    val isDarkTheme = isDarkModePreference ?: systemInDarkTheme
    val appLanguage by viewModel.appLanguage.collectAsState()

    val adminPin by adManager.adminPin.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var isAuthenticated by remember { mutableStateOf(false) }

    val isPremiumUser by adManager.isPremiumUser.collectAsState()
    val adsEnabled by adManager.adsEnabled.collectAsState()
    val bannerAdsEnabled by adManager.bannerAdsEnabled.collectAsState()
    val rewardedAdsEnabled by adManager.rewardedAdsEnabled.collectAsState()
    val isTestMode by adManager.isTestMode.collectAsState()
    val customBannerId by adManager.customBannerAdUnitId.collectAsState()
    val customRewardedId by adManager.customRewardedAdUnitId.collectAsState()
    val appId by adManager.appId.collectAsState()
    val impressions by adManager.impressionCount.collectAsState()

    val paypalAddress by adManager.paypalAddress.collectAsState()
    val wiseAddress by adManager.wiseAddress.collectAsState()
    val binanceId by adManager.binanceId.collectAsState()
    val customLogoUrl by adManager.customLogoUrl.collectAsState()

    val playStorePackage by adManager.playStorePackage.collectAsState()
    val youtubeUrl by adManager.youtubeUrl.collectAsState()
    val facebookUrl by adManager.facebookUrl.collectAsState()
    val whatsappUrl by adManager.whatsappUrl.collectAsState()
    val privacyPolicyUrl by adManager.privacyPolicyUrl.collectAsState()
    val supportEmail by adManager.supportEmail.collectAsState()
    val developerPhone by adManager.developerPhone.collectAsState()

    var editableAppId by remember(appId) { mutableStateOf(appId) }
    var editableBannerId by remember(customBannerId) { mutableStateOf(customBannerId) }
    var editableRewardedId by remember(customRewardedId) { mutableStateOf(customRewardedId) }
    var editablePaypal by remember(paypalAddress) { mutableStateOf(paypalAddress) }
    var editableWise by remember(wiseAddress) { mutableStateOf(wiseAddress) }
    var editableBinance by remember(binanceId) { mutableStateOf(binanceId) }
    var newPinText by remember(adminPin) { mutableStateOf(adminPin) }

    var editablePlayStorePackage by remember(playStorePackage) { mutableStateOf(playStorePackage) }
    var editableYoutube by remember(youtubeUrl) { mutableStateOf(youtubeUrl) }
    var editableFacebook by remember(facebookUrl) { mutableStateOf(facebookUrl) }
    var editableWhatsapp by remember(whatsappUrl) { mutableStateOf(whatsappUrl) }
    var editablePrivacyUrl by remember(privacyPolicyUrl) { mutableStateOf(privacyPolicyUrl) }
    var editableSupportEmail by remember(supportEmail) { mutableStateOf(supportEmail) }
    var editablePhone by remember(developerPhone) { mutableStateOf(developerPhone) }

    var announcementTitle by remember { mutableStateOf("") }
    var announcementMessage by remember { mutableStateOf("") }

    var showTestRewardedDialog by remember { mutableStateOf(false) }

    if (showTestRewardedDialog) {
        RewardedAdDialog(
            adManager = adManager,
            isDarkTheme = isDarkTheme,
            language = appLanguage,
            onDismiss = { showTestRewardedDialog = false },
            onRewardEarned = {
                val msg = if (appLanguage == "fr") "Test d'annonce avec récompense réussi !" else "Test Rewarded Ad konplete ak siksè!"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (appLanguage == "fr") "Tableau de Bord Admin • AdMob" else "Admin Dashboard • AdMob",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (appLanguage == "fr") "Retour" else "Retounen"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (!isAuthenticated) {
            // PIN Verification Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                NeumorphicCard(
                    isDarkTheme = isDarkTheme,
                    cornerRadius = 24.dp,
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (appLanguage == "fr") "Accès Secret Administrateur" else "Aksè Sekrè Admin",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (appLanguage == "fr") "Entrez le code PIN pour gérer les annonces AdMob." else "Antre kòd PIN pou jere anons AdMob yo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                        )

                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { enteredPin = it },
                            label = { Text(if (appLanguage == "fr") "Code PIN Admin" else "Kòd PIN Admin") },
                            placeholder = { Text("1234") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        NeumorphicButton(
                            onClick = {
                                if (enteredPin == adminPin) {
                                    isAuthenticated = true
                                } else {
                                    val err = if (appLanguage == "fr") "Code PIN incorrect !" else "Kòd PIN enkòrèk!"
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            },
                            cornerRadius = 16.dp,
                            elevation = 6.dp,
                            isDarkTheme = isDarkTheme,
                            backgroundColor = Color(0xFFEF4444),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (appLanguage == "fr") "OUVRIR LE DASHBOARD" else "OUVRI DASHBOARD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Full Admin Management Interface
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Status Card
                item {
                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = if (appLanguage == "fr") "STATUT ADMOB ACTUEL" else "STATUT ADMOB KOUNYE A",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (appLanguage == "fr") {
                                            "Annonces actives : ${if (adsEnabled && !isPremiumUser) "OUI" else "NON"}"
                                        } else {
                                            "Anons Aktif: ${if (adsEnabled && !isPremiumUser) "WI" else "NON"}"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (appLanguage == "fr") {
                                            "Mode : ${if (isTestMode) "Test (ID Test Google)" else "Production"}"
                                        } else {
                                            "Mòd: ${if (isTestMode) "Test (Google Test IDs)" else "Pwodiksyon"}"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    color = if (adsEnabled && !isPremiumUser) Color(0xFF10B981) else Color(0xFFEF4444),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "$impressions ${if (appLanguage == "fr") "Affichages" else "Afichaj"}",
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

                // Global Ad Switches
                item {
                    Text(
                        text = if (appLanguage == "fr") "CONFIGURATION PRINCIPALE" else "KONFIGIRASYON PWENKIPAY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Global Ads Switch
                            AdminSwitchRow(
                                title = if (appLanguage == "fr") "Activer Toutes les Annonces AdMob" else "Aktive Tout Anons AdMob",
                                subtitle = if (appLanguage == "fr") "Activer ou désactiver les annonces sur toute l'application" else "Limen oswa koupe anons sou tout app la",
                                checked = adsEnabled,
                                onCheckedChange = { adManager.setAdsEnabled(it) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Banner Ads Switch
                            AdminSwitchRow(
                                title = if (appLanguage == "fr") "Bannière Publicitaire Inférieure (Bottom Banner)" else "Anons Bò Anba (Bottom Banner)",
                                subtitle = if (appLanguage == "fr") "Afficher une annonce en dessous de la section 'Thème du jour'" else "Afiche reklam anba section 'Tèm jodi a'",
                                checked = bannerAdsEnabled,
                                onCheckedChange = { adManager.setBannerAdsEnabled(it) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Rewarded Ads Switch
                            AdminSwitchRow(
                                title = if (appLanguage == "fr") "Annonces avec Récompense (Rewarded Ads)" else "Anons ak Rekonpans (Rewarded Ads)",
                                subtitle = if (appLanguage == "fr") "Permettre aux utilisateurs de regarder 15s de vidéo pour des récompenses" else "Permèt itilizatè gade videyo 15s pou debloke sipleman",
                                checked = rewardedAdsEnabled,
                                onCheckedChange = { adManager.setRewardedAdsEnabled(it) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Test Mode Toggle
                            AdminSwitchRow(
                                title = if (appLanguage == "fr") "Mode Test AdMob" else "Mòd Test AdMob",
                                subtitle = if (appLanguage == "fr") "Utiliser les identifiants officiels de test Google (sécurisé)" else "Sèvi ak ID test ofisyèl Google yo (sere pou evite blòk)",
                                checked = isTestMode,
                                onCheckedChange = { adManager.setTestMode(it) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Force Premium Mode
                            AdminSwitchRow(
                                title = if (appLanguage == "fr") "Simuler Mode Premium ($2.99)" else "Simule Bib Premium ($2.99)",
                                subtitle = if (appLanguage == "fr") "Supprime toutes les annonces comme un utilisateur ayant payé" else "Retire tout anons tankou yon moun ki fin peye",
                                checked = isPremiumUser,
                                onCheckedChange = { adManager.setPremiumUser(it) }
                            )
                        }
                    }
                }

                // Ad Unit IDs Configuration
                item {
                    Text(
                        text = if (appLanguage == "fr") "IDENTIFIANTS ADMOB PERSONNALISÉS (PRODUCTION)" else "ID ADMOB KOUTIM (PRODUCTION)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = editableAppId,
                                onValueChange = { editableAppId = it },
                                label = { Text("AdMob Application ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableBannerId,
                                onValueChange = { editableBannerId = it },
                                label = { Text("Banner Ad Unit ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableRewardedId,
                                onValueChange = { editableRewardedId = it },
                                label = { Text("Rewarded Ad Unit ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NeumorphicButton(
                                onClick = {
                                    adManager.setAppId(editableAppId)
                                    adManager.setCustomBannerAdUnitId(editableBannerId)
                                    adManager.setCustomRewardedAdUnitId(editableRewardedId)
                                    val toastMsg = if (appLanguage == "fr") "Identifiants AdMob enregistrés !" else "ID AdMob yo anregistre!"
                                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFF2563EB),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (appLanguage == "fr") "ENREGISTRER LES IDENTIFIANTS" else "ANREGISTRE ID YO",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // Donation Addresses Configuration
                item {
                    Text(
                        text = if (appLanguage == "fr") "GESTION DES ADRESSES DE DONS" else "JESYON ADRES DONASYON YO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = editablePaypal,
                                onValueChange = { editablePaypal = it },
                                label = { Text("PayPal Username / ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableWise,
                                onValueChange = { editableWise = it },
                                label = { Text("Wise Tag / Username") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableBinance,
                                onValueChange = { editableBinance = it },
                                label = { Text("Binance Pay ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NeumorphicButton(
                                onClick = {
                                    adManager.setPaypalAddress(editablePaypal)
                                    adManager.setWiseAddress(editableWise)
                                    adManager.setBinanceId(editableBinance)
                                    val toastMsg = if (appLanguage == "fr") "Adresses de dons mises à jour !" else "Adres donasyon yo mete ajou!"
                                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFF10B981),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (appLanguage == "fr") "ENREGISTRER LES ADRESSES DE DONS" else "ANREGISTRE ADRES DONASYON YO",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // Social Media, Play Store & Legal URL Management
                item {
                    Text(
                        text = if (appLanguage == "fr") "GESTION DES RÉSEAUX SOCIAUX, PLAY STORE & SUPPORT" else "JESYON LYEN REZO SOSYO, PLAY STORE & SIPÒ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = editablePlayStorePackage,
                                onValueChange = { editablePlayStorePackage = it },
                                label = { Text(if (appLanguage == "fr") "ID de Package Play Store (pour 'Noter')" else "Package ID Play Store (pou 'Rate Us')") },
                                placeholder = { Text("com.zoutiw.bibla") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableYoutube,
                                onValueChange = { editableYoutube = it },
                                label = { Text(if (appLanguage == "fr") "Lien YouTube Officiel" else "Lyen YouTube Ofisyèl") },
                                placeholder = { Text("https://youtube.com/@bibla") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableFacebook,
                                onValueChange = { editableFacebook = it },
                                label = { Text(if (appLanguage == "fr") "Lien Facebook Officiel" else "Lyen Facebook Ofisyèl") },
                                placeholder = { Text("https://facebook.com/bibla") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableWhatsapp,
                                onValueChange = { editableWhatsapp = it },
                                label = { Text(if (appLanguage == "fr") "Lien WhatsApp Communauté / Support" else "Lyen WhatsApp Kominote / Sipò") },
                                placeholder = { Text("https://chat.whatsapp.com/...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editableSupportEmail,
                                onValueChange = { editableSupportEmail = it },
                                label = { Text(if (appLanguage == "fr") "E-mail de Support Développeur" else "Imel Sipò Devlopè") },
                                placeholder = { Text("jacksonofisyal@gmail.com") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editablePhone,
                                onValueChange = { editablePhone = it },
                                label = { Text(if (appLanguage == "fr") "Numéro de Contact Développeur" else "Nimewo Kontak Devlopè") },
                                placeholder = { Text("+18296211349") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editablePrivacyUrl,
                                onValueChange = { editablePrivacyUrl = it },
                                label = { Text(if (appLanguage == "fr") "Lien Politique de Confidentialité en Ligne" else "Lyen Sit Wèb Politik Konfidansyalite (Online URL)") },
                                placeholder = { Text("https://sites.google.com/...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NeumorphicButton(
                                onClick = {
                                    adManager.setPlayStorePackage(editablePlayStorePackage)
                                    adManager.setYoutubeUrl(editableYoutube)
                                    adManager.setFacebookUrl(editableFacebook)
                                    adManager.setWhatsappUrl(editableWhatsapp)
                                    adManager.setSupportEmail(editableSupportEmail)
                                    adManager.setDeveloperPhone(editablePhone)
                                    adManager.setPrivacyPolicyUrl(editablePrivacyUrl)
                                    val toastMsg = if (appLanguage == "fr") "Tous les liens réseaux et support sont enregistrés !" else "Tout lyen rezo sosyo ak sipò yo anregistre!"
                                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFF3B82F6),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (appLanguage == "fr") "ENREGISTRER TOUS LES LIENS" else "ANREGISTRE TOUT LYEN YO",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // Developer Announcements Section
                item {
                    Text(
                        text = if (appLanguage == "fr") "COMMUNICATION ET ANNONCES POUR LES UTILISATEURS" else "KOMINIKASYON AK ANONS POU ITILIZATÈ YO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = announcementTitle,
                                onValueChange = { announcementTitle = it },
                                label = { Text(if (appLanguage == "fr") "Titre de l'annonce" else "Tòt Anons lan") },
                                placeholder = { Text(if (appLanguage == "fr") "Ex : Annonce Officielle Développeur" else "Eg: Anons Ofisyèl Devlopè") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = announcementMessage,
                                onValueChange = { announcementMessage = it },
                                label = { Text(if (appLanguage == "fr") "Message pour les utilisateurs" else "Mesaj pou itilizatè yo") },
                                placeholder = { Text(if (appLanguage == "fr") "Écrivez le message que vous voulez envoyer à tous les utilisateurs ici..." else "Ekri mesaj ou vle voye bay tout itilizatè yo la a...") },
                                minLines = 3,
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Automatic Direct Cloud Sync Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (appLanguage == "fr") "Synchronisation Cloud Automatique" else "Senkronizasyon Nwaj Otomatik",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (appLanguage == "fr") {
                                                "Les annonces sont envoyées directement sur tous les appareils sans configuration complexe."
                                            } else {
                                                "Anons yo ap voye dirèkteman sou tout aparèy k ap itilize aplikasyon an san w pa bezwen rantre kle."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            NeumorphicButton(
                                onClick = {
                                    if (announcementTitle.isBlank() || announcementMessage.isBlank()) {
                                        val fillMsg = if (appLanguage == "fr") "Veuillez remplir le titre et le message avant d'envoyer !" else "Tanpri ranpli tòt ak mesaj la anvan w voye l!"
                                        Toast.makeText(context, fillMsg, Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.sendDeveloperAnnouncement(announcementTitle, announcementMessage)
                                        val sentMsg = if (appLanguage == "fr") "Annonce envoyée avec succès sur tous les téléphones !" else "Anons lan voye ak siksè sou tout telefòn!"
                                        Toast.makeText(context, sentMsg, Toast.LENGTH_LONG).show()
                                        announcementTitle = ""
                                        announcementMessage = ""
                                    }
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFF6366F1),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (appLanguage == "fr") "ENVOYER L'ANNONCE À TOUS" else "VOYE ANONS SOU TOUT TELEFÒN",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Testing & Security Tools
                item {
                    Text(
                        text = if (appLanguage == "fr") "OUTILS DE TEST ET SÉCURITÉ" else "OUTIL TÈS AK SEKURITE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NeumorphicCard(
                        isDarkTheme = isDarkTheme,
                        cornerRadius = 20.dp,
                        elevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Test Rewarded Ad Button
                            NeumorphicButton(
                                onClick = { showTestRewardedDialog = true },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (appLanguage == "fr") "TESTER REWARDED AD (15s)" else "TESTE REWARDED AD (15s)",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Change Admin PIN
                            OutlinedTextField(
                                value = newPinText,
                                onValueChange = { newPinText = it },
                                label = { Text(if (appLanguage == "fr") "Modifier le Code PIN Admin" else "Chanje Kòd PIN Admin") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            NeumorphicButton(
                                onClick = {
                                    if (newPinText.length >= 4) {
                                        adManager.setAdminPin(newPinText)
                                        val successMsg = if (appLanguage == "fr") "Code PIN modifié avec succès !" else "Kòd PIN chanje avèk siksè!"
                                        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                                    } else {
                                        val minMsg = if (appLanguage == "fr") "Le PIN doit contenir au moins 4 chiffres !" else "PIN dwe gen omwen 4 chif!"
                                        Toast.makeText(context, minMsg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (appLanguage == "fr") "MODIFIER LE CODE PIN" else "CHANJE KÒD PIN",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }

                            // Reset Stats
                            NeumorphicButton(
                                onClick = {
                                    adManager.resetStats()
                                    val resetMsg = if (appLanguage == "fr") "Statistiques réinitialisées !" else "Statistik yo re-inisyalize!"
                                    Toast.makeText(context, resetMsg, Toast.LENGTH_SHORT).show()
                                },
                                cornerRadius = 12.dp,
                                elevation = 4.dp,
                                isDarkTheme = isDarkTheme,
                                backgroundColor = Color(0xFFEF4444),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (appLanguage == "fr") "RÉINITIALISER LES STATISTIQUES" else "RE-INISYALIZE STATISTIK YO",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

