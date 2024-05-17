package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kiruhm.cabify_mobile_challenge.ui.theme.CabifyMobileTheme

@Composable
fun AppSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    CabifyMobileTheme {
        Surface(modifier = modifier.fillMaxSize(), content = content)
    }
}