package com.haooz.softglass

import androidx.compose.runtime.Immutable
import com.haooz.softglass.glass.SoftGlassStyle

/** 材质质量档。库默认 [SOFT]。 */
enum class GlassMaterialLevel { SOFT, LIQUID, FROSTED, NONE }

/** 档位导出的全部降级口径，组件只读这一份。 */
@Immutable
data class GlassTuning(
    val blurDp: Float = 6f,
    val panelBlurDp: Float = 12f,
    val dialogBlurDp: Float = 12f,
    val surfaceAlphaDropLight: Float = 0.2f,
    val surfaceAlphaDropDark: Float = 0.32f,
    val refractionScale: Float = 1f,
    val edgeHighlight: Boolean = true,
    val glassEffectsEnabled: Boolean = true,
)

fun GlassMaterialLevel.tuning(): GlassTuning = when (this) {
    GlassMaterialLevel.SOFT -> GlassTuning()
    // 液态玻璃：折射换位移场 + 调色走 liquidClear()，模糊单独压低一档
    GlassMaterialLevel.LIQUID -> GlassTuning(
        blurDp = 4f,
        panelBlurDp = 8f,
        dialogBlurDp = 8f,
    )
    GlassMaterialLevel.FROSTED -> GlassTuning(
        surfaceAlphaDropLight = 0f,
        surfaceAlphaDropDark = 0f,
        refractionScale = 0f,
        edgeHighlight = false,
    )
    GlassMaterialLevel.NONE -> GlassTuning(
        blurDp = 0f,
        panelBlurDp = 0f,
        dialogBlurDp = 0f,
        surfaceAlphaDropLight = 0f,
        surfaceAlphaDropDark = 0f,
        refractionScale = 0f,
        edgeHighlight = false,
        glassEffectsEnabled = false,
    )
}

/** 无材质档把半透底改实色，其余档按比例降低柔光档的底色 alpha。 */
fun GlassMaterialLevel.surfaceAlpha(base: Float, isLight: Boolean): Float {
    if (this == GlassMaterialLevel.NONE) return 1f
    val drop = if (isLight) tuning().surfaceAlphaDropLight else tuning().surfaceAlphaDropDark
    return (base - drop).coerceAtLeast(0f)
}

/** 液态档清透口径：玻璃体只留 40%（柔光档 0.85），调色不再叠色膜。 */
fun SoftGlassStyle.liquidClear(): SoftGlassStyle = copy(
    bodyDensity = 0.4f,
    saturation = 1.2f,
    brightness = 0f,
    innerColorMix = 0.1f,
    luminanceAmount = 0.05f,
    colorPow = 1f,
)
