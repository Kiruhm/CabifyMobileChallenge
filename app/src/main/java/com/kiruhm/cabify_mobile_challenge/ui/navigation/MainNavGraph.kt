package com.kiruhm.cabify_mobile_challenge.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Graphs
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Screens
import com.kiruhm.cabify_mobile_challenge.ui.screens.ProductListScreen
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {

    val context = LocalContext.current
    val dimensions = LocalDim.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Graphs.Main.startDestination.route,
        route = Graphs.Main.route
    ) {
        composable(route = Screens.ProductList.route) {
            ProductListScreen(
                modifier = Modifier.padding(
                    top = dimensions.spaceMedium,
                    start = dimensions.spaceMedium,
                    end = dimensions.spaceMedium
                ),
                state = state,
                onEvent = { event ->
                    when(event){
                        is ProductsEvent.ProductClicked -> navController.navigate(Screens.ProductDetail.route + "/${event.product.name}")
                        ProductsEvent.ShoppingCartClicked -> navController.navigate(Screens.Purchase.route)
                        else -> {}
                    }
                    onEvent(event)
                }
            )
        }
        
        
    }


}