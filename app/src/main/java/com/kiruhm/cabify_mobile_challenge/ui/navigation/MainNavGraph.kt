package com.kiruhm.cabify_mobile_challenge.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Graphs
import com.kiruhm.cabify_mobile_challenge.ui.navigation.models.Screens
import com.kiruhm.cabify_mobile_challenge.ui.screens.ProductDetailScreen
import com.kiruhm.cabify_mobile_challenge.ui.screens.ProductListScreen
import com.kiruhm.cabify_mobile_challenge.ui.screens.PurchaseProductsScreen
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {

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

        composable(
            route = Screens.ProductDetail.route + "/{${Constants.NAME_KEY}}",
            arguments = listOf(
                navArgument(Constants.NAME_KEY){
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->

            val product = backStackEntry.arguments?.getString(Constants.NAME_KEY)?.let { productName ->
                state.products.find { it.name == productName }
            }

            if (product == null){
                navController.navigateUp()
                return@composable
            }

            ProductDetailScreen(
                modifier = Modifier.fillMaxWidth().padding(dimensions.spaceMedium),
                product = product,
                state = state,
                onEvent = onEvent
            )
        }

        composable(route = Screens.Purchase.route){
            PurchaseProductsScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceMedium),
                state = state,
                onEvent = { event ->

                    when(event){
                        is ProductsEvent.PurchaseProductClicked -> navController.navigate(Screens.ProductDetail.route + "/${event.product.name}")
                        else -> {}
                    }

                    onEvent(event)
                }
            )
        }
    }


}