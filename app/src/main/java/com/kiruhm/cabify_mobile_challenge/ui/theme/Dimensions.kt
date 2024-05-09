package com.kiruhm.cabify_mobile_challenge.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalDim = compositionLocalOf { Dimensions() }

data class Dimensions(

    val layoutCompactSizeRange: ClosedRange<Dp> = 0.dp..599.dp,
    val layoutMediumSizeRange: ClosedRange<Dp> = 600.dp..839.dp,
    val layoutExpandedSizeRange: ClosedRange<Dp> = 840.dp..Dp.Infinity,

    val default: Dp = 0.dp,
    val spaceXXSmall: Dp = 2.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 32.dp,
    val spaceExtraLarge: Dp = 64.dp,
    val spaceXXLarge: Dp = 128.dp,
    val spaceXXXLarge: Dp = 256.dp,

    val cornersExtraSmall: Dp = 3.dp,
    val cornersSmall: Dp = 6.dp,
    val cornersMedium: Dp = 12.dp,
    val cornersLarge: Dp = 24.dp,
    val cornersExtraLarge: Dp = 42.dp,

    val iconExtraSmallSize: Dp = 6.dp,
    val iconSmallSize: Dp = 12.dp,
    val iconMediumSize: Dp = 24.dp,
    val iconLargeSize: Dp = 36.dp,
    val iconExtraLargeSize: Dp = 48.dp,

    val cardExtraSmallElevation: Dp = 1.dp,
    val cardSmallElevation: Dp = 3.dp,
    val cardMediumElevation: Dp = 6.dp,
    val cardLongElevation: Dp = 8.dp,
    val cardExtraLongElevation: Dp = 12.dp
)