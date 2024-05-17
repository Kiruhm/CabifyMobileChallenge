package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.components.AppTopBar
import com.kiruhm.cabify_mobile_challenge.ui.navigation.MainNavGraph
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Graphs
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Screens
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData

@Composable
fun MainScreen(
    modifier: Modifier,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = try {
        Screens.getScreenByRoute(
            backStackEntry?.destination?.route ?: Graphs.Main.route
        ) ?: throw IllegalArgumentException("Invalid route ${backStackEntry?.destination?.route}")
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        Graphs.Main.startDestination
    }

    val currentScreenTitle = backStackEntry?.arguments?.getString(Constants.TITLE_KEY) ?: currentScreen.title?.let { stringResource(id = it) }

    Scaffold(
        modifier = modifier,
        topBar = {

            if (currentScreenTitle == null) return@Scaffold

            AppTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = currentScreenTitle,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        MainNavGraph(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            state = state,
            onEvent = onEvent
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun MainScreenPreview() {
    AppSurface {
        MainScreen(
            modifier = Modifier.fillMaxSize(),
            state = ProductsState(
                products = MockData.productList
            ),
            onEvent = {}
        )
    }
}