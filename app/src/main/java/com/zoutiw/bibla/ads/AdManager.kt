package com.zoutiw.bibla.ads

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        "admob_settings_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdManager(context).also {
                    INSTANCE = it
                    try {
                        MobileAds.initialize(context.applicationContext) {}
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // State Flows
    private val _isPremiumUser = MutableStateFlow(
        prefs.getBoolean("is_premium_user", false)
    )
    val isPremiumUser: StateFlow<Boolean> = _isPremiumUser

    private val _adsEnabled = MutableStateFlow(
        prefs.getBoolean("ads_enabled", true)
    )
    val adsEnabled: StateFlow<Boolean> = _adsEnabled

    private val _bannerAdsEnabled = MutableStateFlow(
        prefs.getBoolean("banner_ads_enabled", true)
    )
    val bannerAdsEnabled: StateFlow<Boolean> = _bannerAdsEnabled

    private val _rewardedAdsEnabled = MutableStateFlow(
        prefs.getBoolean("rewarded_ads_enabled", true)
    )
    val rewardedAdsEnabled: StateFlow<Boolean> = _rewardedAdsEnabled

    private val _isTestMode = MutableStateFlow(
        prefs.getBoolean("is_test_mode", true)
    )
    val isTestMode: StateFlow<Boolean> = _isTestMode

    private val _customBannerAdUnitId = MutableStateFlow(
        prefs.getString("custom_banner_id", TEST_BANNER_AD_UNIT_ID) ?: TEST_BANNER_AD_UNIT_ID
    )
    val customBannerAdUnitId: StateFlow<String> = _customBannerAdUnitId

    private val _customRewardedAdUnitId = MutableStateFlow(
        prefs.getString("custom_rewarded_id", TEST_REWARDED_AD_UNIT_ID) ?: TEST_REWARDED_AD_UNIT_ID
    )
    val customRewardedAdUnitId: StateFlow<String> = _customRewardedAdUnitId

    private val _appId = MutableStateFlow(
        prefs.getString("app_id", TEST_APP_ID) ?: TEST_APP_ID
    )
    val appId: StateFlow<String> = _appId

    private val _adminPin = MutableStateFlow(
        prefs.getString("admin_pin", "1234") ?: "1234"
    )
    val adminPin: StateFlow<String> = _adminPin

    private val _impressionCount = MutableStateFlow(
        prefs.getInt("impression_count", 0)
    )
    val impressionCount: StateFlow<Int> = _impressionCount

    private val _rewardedUnlocked = MutableStateFlow(
        prefs.getBoolean("rewarded_unlocked", false)
    )
    val rewardedUnlocked: StateFlow<Boolean> = _rewardedUnlocked

    private val _rewardCount = MutableStateFlow(
        prefs.getInt("reward_count", 0)
    )
    val rewardCount: StateFlow<Int> = _rewardCount

    private val _paypalAddress = MutableStateFlow(
        prefs.getString("paypal_address", "jackson3186") ?: "jackson3186"
    )
    val paypalAddress: StateFlow<String> = _paypalAddress

    private val _wiseAddress = MutableStateFlow(
        prefs.getString("wise_address", "@jacksonl524") ?: "@jacksonl524"
    )
    val wiseAddress: StateFlow<String> = _wiseAddress

    private val _binanceId = MutableStateFlow(
        prefs.getString("binance_id", "954688069") ?: "954688069"
    )
    val binanceId: StateFlow<String> = _binanceId

    private val _customLogoUrl = MutableStateFlow(
        prefs.getString("custom_logo_url", "") ?: ""
    )
    val customLogoUrl: StateFlow<String> = _customLogoUrl

    private val _logoTheme = MutableStateFlow(
        prefs.getString("logo_theme", "GOLD") ?: "GOLD"
    )
    val logoTheme: StateFlow<String> = _logoTheme

    private val _pushAppId = MutableStateFlow(
        prefs.getString("push_app_id", "") ?: ""
    )
    val pushAppId: StateFlow<String> = _pushAppId

    private val _pushApiKey = MutableStateFlow(
        prefs.getString("push_api_key", null) ?: "BBR3Zb7TLehCpvxYXzI8aCpjnWL-eJBj77fx0DBoVtS-qaBjf-IEDdB1f5aTUSjjQNjmZnTp8b0JPw6TPUkQWbI"
    )
    val pushApiKey: StateFlow<String> = _pushApiKey

    // Play Store, Social Media, Legal & Support links
    private val _playStorePackage = MutableStateFlow(
        prefs.getString("play_store_package", "com.zoutiw.bibla") ?: "com.zoutiw.bibla"
    )
    val playStorePackage: StateFlow<String> = _playStorePackage

    private val _youtubeUrl = MutableStateFlow(
        prefs.getString("youtube_url", "https://www.youtube.com") ?: "https://www.youtube.com"
    )
    val youtubeUrl: StateFlow<String> = _youtubeUrl

    private val _facebookUrl = MutableStateFlow(
        prefs.getString("facebook_url", "https://www.facebook.com") ?: "https://www.facebook.com"
    )
    val facebookUrl: StateFlow<String> = _facebookUrl

    private val _whatsappUrl = MutableStateFlow(
        prefs.getString("whatsapp_url", "https://chat.whatsapp.com") ?: "https://chat.whatsapp.com"
    )
    val whatsappUrl: StateFlow<String> = _whatsappUrl

    private val _privacyPolicyUrl = MutableStateFlow(
        prefs.getString("privacy_policy_url", "https://sites.google.com/view/bib-la-edisyon-enpakt-privacy") ?: "https://sites.google.com/view/bib-la-edisyon-enpakt-privacy"
    )
    val privacyPolicyUrl: StateFlow<String> = _privacyPolicyUrl

    private val _supportEmail = MutableStateFlow(
        prefs.getString("support_email", "jacksonofisyal@gmail.com") ?: "jacksonofisyal@gmail.com"
    )
    val supportEmail: StateFlow<String> = _supportEmail

    private val _developerPhone = MutableStateFlow(
        prefs.getString("developer_phone", "+18296211349") ?: "+18296211349"
    )
    val developerPhone: StateFlow<String> = _developerPhone

    // Effective Ad Unit IDs
    fun getEffectiveBannerAdUnitId(): String {
        return if (_isTestMode.value || _customBannerAdUnitId.value.isBlank()) {
            TEST_BANNER_AD_UNIT_ID
        } else {
            _customBannerAdUnitId.value
        }
    }

    fun getEffectiveRewardedAdUnitId(): String {
        return if (_isTestMode.value || _customRewardedAdUnitId.value.isBlank()) {
            TEST_REWARDED_AD_UNIT_ID
        } else {
            _customRewardedAdUnitId.value
        }
    }

    fun isBannerVisible(): Boolean {
        return !_isPremiumUser.value && _adsEnabled.value && _bannerAdsEnabled.value
    }

    fun setPremiumUser(enabled: Boolean) {
        _isPremiumUser.value = enabled
        prefs.edit().putBoolean("is_premium_user", enabled).apply()
    }

    fun setAdsEnabled(enabled: Boolean) {
        _adsEnabled.value = enabled
        prefs.edit().putBoolean("ads_enabled", enabled).apply()
    }

    fun setBannerAdsEnabled(enabled: Boolean) {
        _bannerAdsEnabled.value = enabled
        prefs.edit().putBoolean("banner_ads_enabled", enabled).apply()
    }

    fun setRewardedAdsEnabled(enabled: Boolean) {
        _rewardedAdsEnabled.value = enabled
        prefs.edit().putBoolean("rewarded_ads_enabled", enabled).apply()
    }

    fun setTestMode(enabled: Boolean) {
        _isTestMode.value = enabled
        prefs.edit().putBoolean("is_test_mode", enabled).apply()
    }

    fun setCustomBannerAdUnitId(id: String) {
        _customBannerAdUnitId.value = id
        prefs.edit().putString("custom_banner_id", id).apply()
    }

    fun setCustomRewardedAdUnitId(id: String) {
        _customRewardedAdUnitId.value = id
        prefs.edit().putString("custom_rewarded_id", id).apply()
    }

    fun setAppId(id: String) {
        _appId.value = id
        prefs.edit().putString("app_id", id).apply()
    }

    fun setAdminPin(pin: String) {
        _adminPin.value = pin
        prefs.edit().putString("admin_pin", pin).apply()
    }

    fun incrementImpression() {
        val newCount = _impressionCount.value + 1
        _impressionCount.value = newCount
        prefs.edit().putInt("impression_count", newCount).apply()
    }

    fun setRewardedUnlocked(unlocked: Boolean) {
        _rewardedUnlocked.value = unlocked
        prefs.edit().putBoolean("rewarded_unlocked", unlocked).apply()
    }

    fun addReward() {
        val newCount = _rewardCount.value + 1
        _rewardCount.value = newCount
        prefs.edit().putInt("reward_count", newCount).apply()
        _rewardedUnlocked.value = true
        prefs.edit().putBoolean("rewarded_unlocked", true).apply()
    }

    fun setPaypalAddress(address: String) {
        _paypalAddress.value = address
        prefs.edit().putString("paypal_address", address).apply()
    }

    fun setWiseAddress(address: String) {
        _wiseAddress.value = address
        prefs.edit().putString("wise_address", address).apply()
    }

    fun setBinanceId(id: String) {
        _binanceId.value = id
        prefs.edit().putString("binance_id", id).apply()
    }

    fun setCustomLogoUrl(url: String) {
        _customLogoUrl.value = url
        prefs.edit().putString("custom_logo_url", url).apply()
    }

    fun setLogoTheme(theme: String) {
        _logoTheme.value = theme
        prefs.edit().putString("logo_theme", theme).apply()
    }

    fun setPushAppId(id: String) {
        _pushAppId.value = id
        prefs.edit().putString("push_app_id", id).apply()
    }

    fun setPushApiKey(key: String) {
        _pushApiKey.value = key
        prefs.edit().putString("push_api_key", key).apply()
    }

    fun setPlayStorePackage(pkg: String) {
        _playStorePackage.value = pkg
        prefs.edit().putString("play_store_package", pkg).apply()
    }

    fun setYoutubeUrl(url: String) {
        _youtubeUrl.value = url
        prefs.edit().putString("youtube_url", url).apply()
    }

    fun setFacebookUrl(url: String) {
        _facebookUrl.value = url
        prefs.edit().putString("facebook_url", url).apply()
    }

    fun setWhatsappUrl(url: String) {
        _whatsappUrl.value = url
        prefs.edit().putString("whatsapp_url", url).apply()
    }

    fun setPrivacyPolicyUrl(url: String) {
        _privacyPolicyUrl.value = url
        prefs.edit().putString("privacy_policy_url", url).apply()
    }

    fun setSupportEmail(email: String) {
        _supportEmail.value = email
        prefs.edit().putString("support_email", email).apply()
    }

    fun setDeveloperPhone(phone: String) {
        _developerPhone.value = phone
        prefs.edit().putString("developer_phone", phone).apply()
    }

    fun resetStats() {
        _impressionCount.value = 0
        prefs.edit().putInt("impression_count", 0).apply()
    }
}

