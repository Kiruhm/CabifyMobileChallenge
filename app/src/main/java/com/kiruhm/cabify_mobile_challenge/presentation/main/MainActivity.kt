package com.kiruhm.cabify_mobile_challenge.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kiruhm.cabify_mobile_challenge.data.local.DataRequestClass
import com.kiruhm.cabify_mobile_challenge.ui.theme.Cabify_mobileTheme
import com.kiruhm.cabify_mobile_challenge.ui.theme.components.AppThemeSurface
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeSurface {

                runBlocking {
                    DataRequestClass(this@MainActivity.resources).getProducts().collectLatest { result ->
                        val r = result.getOrNull()
                    }
                }


            }
        }
    }
}