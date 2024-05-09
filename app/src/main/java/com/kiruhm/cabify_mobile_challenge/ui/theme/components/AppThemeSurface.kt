package com.kiruhm.cabify_mobile_challenge.ui.theme.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kiruhm.cabify_mobile_challenge.ui.theme.Cabify_mobileTheme

@Composable
fun AppThemeSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Cabify_mobileTheme {
        Surface(modifier = modifier.fillMaxSize(), content = content)
    }
}