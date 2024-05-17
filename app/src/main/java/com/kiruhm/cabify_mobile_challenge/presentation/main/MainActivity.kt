package com.kiruhm.cabify_mobile_challenge.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiruhm.cabify_mobile_challenge.data.local.LocalDataRequestClassImpl
import com.kiruhm.cabify_mobile_challenge.data.repositories.ProductsRepositoryImpl
import com.kiruhm.cabify_mobile_challenge.domain.use_cases.GetProductsUseCase
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModel
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModelFactory
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val productListViewModel by viewModels<ProductsViewModel> {
                val repository = ProductsRepositoryImpl(resources, LocalDataRequestClassImpl())
                val getProductsUseCase = GetProductsUseCase(repository)

                ProductsViewModelFactory(getProductsUseCase)
            }

            val productsState = productListViewModel.state.collectAsStateWithLifecycle().value

            AppSurface {
                MainScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = productsState,
                    onEvent = productListViewModel::onEvent
                )
            }
        }
    }
}