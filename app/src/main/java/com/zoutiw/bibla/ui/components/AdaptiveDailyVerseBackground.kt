package com.zoutiw.bibla.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.zoutiw.bibla.data.Verse

/**
 * Spiritual Theme categorization extracted mathematically and semantically
 * without requiring any external AI.
 */
enum class VerseSpiritualTheme(
    val title: String,
    val lightGradient: List<Color>,
    val darkGradient: List<Color>,
    val accentColor: Color,
    val badgeLabel: String,
    val iconEmoji: String
) {
    PEACE_AND_COMFORT(
        title = "Lapè & Konsolasyon",
        lightGradient = listOf(Color(0xFFE0F2FE), Color(0xFFF0F9FF), Color(0xFFE0E7FF)),
        darkGradient = listOf(Color(0xFF0C243C), Color(0xFF0F172A), Color(0xFF1E1B4B)),
        accentColor = Color(0xFF0284C7),
        badgeLabel = "Lapè & Trankilite",
        iconEmoji = "🕊️"
    ),
    FAITH_AND_STRENGTH(
        title = "Lafwa & Viktwa",
        lightGradient = listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB), Color(0xFFFED7AA)),
        darkGradient = listOf(Color(0xFF451A03), Color(0xFF1F1206), Color(0xFF2E1065)),
        accentColor = Color(0xFFD97706),
        badgeLabel = "Lafwa & Fòs",
        iconEmoji = "🛡️"
    ),
    PRAISE_AND_JOY(
        title = "Lwanj & Rekonesans",
        lightGradient = listOf(Color(0xFFFDF4FF), Color(0xFFFAF5FF), Color(0xFFFCE7F3)),
        darkGradient = listOf(Color(0xFF3B0764), Color(0xFF1A0636), Color(0xFF4C0519)),
        accentColor = Color(0xFF9333EA),
        badgeLabel = "Lwanj & Glwa",
        iconEmoji = "🙌"
    ),
    LOVE_AND_GRACE(
        title = "Lanmou & Gras Bondye",
        lightGradient = listOf(Color(0xFFFFE4E6), Color(0xFFFFF1F2), Color(0xFFFEE2E2)),
        darkGradient = listOf(Color(0xFF4C0519), Color(0xFF2A0812), Color(0xFF1F1206)),
        accentColor = Color(0xFFE11D48),
        badgeLabel = "Lanmou & Gras",
        iconEmoji = "❤️"
    ),
    CREATION_AND_WISDOM(
        title = "Sajès & Limyè",
        lightGradient = listOf(Color(0xFFECFDF5), Color(0xFFF0FDF4), Color(0xFFCCFBF1)),
        darkGradient = listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF042F2E)),
        accentColor = Color(0xFF059669),
        badgeLabel = "Sajès & Limyè",
        iconEmoji = "🌿"
    ),
    HOPE_AND_DAWN(
        title = "Espwa & Nouvo Jou",
        lightGradient = listOf(Color(0xFFEFF6FF), Color(0xFFFAF5FF), Color(0xFFFEF9C3)),
        darkGradient = listOf(Color(0xFF172554), Color(0xFF0F172A), Color(0xFF312E81)),
        accentColor = Color(0xFF3B82F6),
        badgeLabel = "Espwa & Pwomès",
        iconEmoji = "🌅"
    );

    fun getTitle(language: String): String = if (language == "fr") {
        when (this) {
            PEACE_AND_COMFORT -> "Paix & Consolation"
            FAITH_AND_STRENGTH -> "Foi & Victoire"
            PRAISE_AND_JOY -> "Louange & Reconnaissance"
            LOVE_AND_GRACE -> "Amour & Grâce Divine"
            CREATION_AND_WISDOM -> "Sagesse & Lumière"
            HOPE_AND_DAWN -> "Espérance & Aube Nouvelle"
        }
    } else title

    fun getBadgeLabel(language: String): String = if (language == "fr") {
        when (this) {
            PEACE_AND_COMFORT -> "Paix & Sérénité"
            FAITH_AND_STRENGTH -> "Foi & Force"
            PRAISE_AND_JOY -> "Louange & Gloire"
            LOVE_AND_GRACE -> "Amour & Grâce"
            CREATION_AND_WISDOM -> "Sagesse & Lumière"
            HOPE_AND_DAWN -> "Espérance & Promesse"
        }
    } else badgeLabel

    companion object {
        fun detectTheme(verse: Verse?): VerseSpiritualTheme {
            if (verse == null) return HOPE_AND_DAWN
            val text = verse.text.lowercase()
            val book = verse.book.lowercase()

            return when {
                // Love & Grace (Kreyòl & Français)
                text.contains("renmen") || text.contains("lanmou") || text.contains("gras") ||
                text.contains("kè sansib") || text.contains("pitye") || text.contains("renmen nou") ||
                text.contains("amour") || text.contains("grâce") || text.contains("miséricorde") ||
                text.contains("compassion") || text.contains("charité") ||
                book.contains("jan") || book.contains("jean") || text.contains("pitit li a") || text.contains("son fils") -> LOVE_AND_GRACE

                // Praise & Joy (Kreyòl & Français)
                text.contains("louwe") || text.contains("chante") || text.contains("lwanj") ||
                text.contains("beni") || text.contains("kè kontan") || text.contains("glwa") ||
                text.contains("loue") || text.contains("louange") || text.contains("célébre") ||
                text.contains("béni") || text.contains("joie") || text.contains("gloire") || text.contains("alléluia") ||
                book.contains("sòm") || book.contains("psaume") -> PRAISE_AND_JOY

                // Faith & Victory & Strength (Kreyòl & Français)
                text.contains("fòs") || text.contains("pouvwa") || text.contains("viktwa") ||
                text.contains("konba") || text.contains("boukliye") || text.contains("lafwa") ||
                text.contains("pa pè") || text.contains("vanyan") || text.contains("konfyans") ||
                text.contains("force") || text.contains("puissance") || text.contains("victoire") ||
                text.contains("combat") || text.contains("bouclier") || text.contains("foi") ||
                text.contains("courage") || text.contains("confiance") || text.contains("vaillant") -> FAITH_AND_STRENGTH

                // Peace & Comfort (Kreyòl & Français)
                text.contains("lapè") || text.contains("repo") || text.contains("trankil") ||
                text.contains("konsolasyon") || text.contains("kè poze") || text.contains("gadò") ||
                text.contains("dlo fre") || text.contains("pa gen krentif") ||
                text.contains("paix") || text.contains("repos") || text.contains("tranquille") ||
                text.contains("tranquillité") || text.contains("consolation") || text.contains("berger") ||
                text.contains("eaux paisibles") || text.contains("ne crains") -> PEACE_AND_COMFORT

                // Wisdom & Guidance (Kreyòl & Français)
                text.contains("sajès") || text.contains("limyè") || text.contains("chemen") ||
                text.contains("pawòl") || text.contains("konesans") || text.contains("kreyatè") ||
                text.contains("sagesse") || text.contains("lumière") || text.contains("chemin") ||
                text.contains("parole") || text.contains("connaissance") || text.contains("créateur") ||
                book.contains("pwovèb") || book.contains("proverbe") || book.contains("jak") || book.contains("jacques") -> CREATION_AND_WISDOM

                // Default Hope
                else -> HOPE_AND_DAWN
            }
        }
    }
}

/**
 * Adaptive custom background canvas that draws subtle spiritual motifs
 * (sunbeams, morning glow, calm water ripples, faith shields, golden stars)
 * according to the detected theme of the Daily Verse.
 */
@Composable
fun AdaptiveDailyVerseBackground(
    theme: VerseSpiritualTheme,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isDarkTheme) theme.darkGradient else theme.lightGradient
    val accent = theme.accentColor

    Box(modifier = modifier) {
        // Dynamic smooth gradient canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Base Gradient Fill
            drawRect(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(width, height)
                )
            )

            // 2. Artistic decorative motifs according to spiritual theme
            when (theme) {
                VerseSpiritualTheme.PEACE_AND_COMFORT -> {
                    // Soft harmonic wave ripples at the bottom (still waters / psalm 23)
                    val path = Path().apply {
                        moveTo(0f, height * 0.75f)
                        cubicTo(
                            width * 0.25f, height * 0.65f,
                            width * 0.75f, height * 0.85f,
                            width, height * 0.7f
                        )
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = accent.copy(alpha = if (isDarkTheme) 0.15f else 0.10f)
                    )

                    // Secondary subtle crest
                    val path2 = Path().apply {
                        moveTo(0f, height * 0.82f)
                        cubicTo(
                            width * 0.35f, height * 0.92f,
                            width * 0.65f, height * 0.75f,
                            width, height * 0.88f
                        )
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }
                    drawPath(
                        path = path2,
                        color = accent.copy(alpha = if (isDarkTheme) 0.12f else 0.08f)
                    )
                }

                VerseSpiritualTheme.FAITH_AND_STRENGTH -> {
                    // Geometric golden shield & corner radiance
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF59E0B).copy(alpha = if (isDarkTheme) 0.25f else 0.20f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.9f, height * 0.2f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.9f, height * 0.2f),
                        radius = width * 0.6f
                    )

                    // Subtle mountain contour (fòs sou mòn yo)
                    val mountainPath = Path().apply {
                        moveTo(0f, height)
                        lineTo(width * 0.35f, height * 0.72f)
                        lineTo(width * 0.7f, height * 0.88f)
                        lineTo(width, height * 0.65f)
                        lineTo(width, height)
                        close()
                    }
                    drawPath(
                        path = mountainPath,
                        color = accent.copy(alpha = if (isDarkTheme) 0.14f else 0.08f)
                    )
                }

                VerseSpiritualTheme.PRAISE_AND_JOY -> {
                    // Radiant celebratory halo & sparkles
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = if (isDarkTheme) 0.28f else 0.18f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.85f, height * 0.15f),
                            radius = width * 0.55f
                        ),
                        center = Offset(width * 0.85f, height * 0.15f),
                        radius = width * 0.55f
                    )
                    // Bottom light curve
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEC4899).copy(alpha = if (isDarkTheme) 0.18f else 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.1f, height * 0.9f),
                            radius = width * 0.5f
                        ),
                        center = Offset(width * 0.1f, height * 0.9f),
                        radius = width * 0.5f
                    )
                }

                VerseSpiritualTheme.LOVE_AND_GRACE -> {
                    // Warm glowing heart warmth aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF43F5E).copy(alpha = if (isDarkTheme) 0.24f else 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.8f, height * 0.85f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.85f),
                        radius = width * 0.7f
                    )
                }

                VerseSpiritualTheme.CREATION_AND_WISDOM -> {
                    // Fresh dew glow & subtle botanical canopy arcs
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF10B981).copy(alpha = if (isDarkTheme) 0.22f else 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.15f, height * 0.2f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.15f, height * 0.2f),
                        radius = width * 0.6f
                    )
                    val leafArch = Path().apply {
                        moveTo(width * 0.6f, height)
                        cubicTo(
                            width * 0.8f, height * 0.7f,
                            width * 0.95f, height * 0.85f,
                            width, height * 0.75f
                        )
                        lineTo(width, height)
                        close()
                    }
                    drawPath(
                        path = leafArch,
                        color = accent.copy(alpha = if (isDarkTheme) 0.15f else 0.08f)
                    )
                }

                VerseSpiritualTheme.HOPE_AND_DAWN -> {
                    // Sunrise dawn ray flare
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFBBF24).copy(alpha = if (isDarkTheme) 0.22f else 0.20f),
                                Color(0xFF38BDF8).copy(alpha = if (isDarkTheme) 0.12f else 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.85f, height * 0.15f),
                            radius = width * 0.75f
                        ),
                        center = Offset(width * 0.85f, height * 0.15f),
                        radius = width * 0.75f
                    )
                }
            }
        }
    }
}

