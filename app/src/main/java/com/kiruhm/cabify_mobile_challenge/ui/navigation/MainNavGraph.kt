package com.kiruhm.cabify_mobile_challenge.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastJoinToString
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
import com.kiruhm.cabify_mobile_challenge.ui.screens.PurchaseCartScreen
import com.kiruhm.cabify_mobile_challenge.ui.screens.PurchaseProductsScreen
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.utils.navigateToAndClear

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
                        is ProductsEvent.ProductClicked ->
                            navController.navigate(Screens.ProductDetail.route + "/${event.product.name}" + "?" + "${Constants.TITLE_KEY}=${event.product.name}")
                        ProductsEvent.ShoppingCartClicked -> navController.navigate(Screens.Cart.route)
                        else -> {}
                    }
                    onEvent(event)
                }
            )
        }

        composable(
            route = Screens.ProductDetail.route + "/{${Constants.NAME_KEY}}?${Constants.TITLE_KEY}={${Constants.TITLE_KEY}}",
            arguments = listOf(
                navArgument(Constants.NAME_KEY){
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Constants.TITLE_KEY){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceMedium),
                product = product,
                state = state,
                onEvent = onEvent
            )
        }

        composable(route = Screens.Cart.route){
            PurchaseCartScreen(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onEvent = { event ->

                    when(event){
                        is ProductsEvent.PurchaseProductClicked -> navController.navigate(Screens.ProductDetail.route + "/${event.product.name}" + "?" + "${Constants.TITLE_KEY}=${event.product.name}")
                        is ProductsEvent.ContinuePurchaseButtonClicked -> navController.navigate(
                            Screens.Purchase.route + "/" + event.products.fastJoinToString(
                                separator = "@",
                                transform = { it.name }
                            )
                        )
                        else -> {}
                    }

                    onEvent(event)
                }
            )
        }

        composable(
            route = Screens.Purchase.route + "/{${Constants.PRODUCT_NAMES_KEY}}",
            arguments = listOf(
                navArgument(Constants.PRODUCT_NAMES_KEY){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){

            val productsNames = it.arguments?.getString(Constants.PRODUCT_NAMES_KEY)?.split("@")

            if (productsNames.isNullOrEmpty()){
                navController.navigateUp()
                return@composable
            }

            val productsToBuy by remember {
                derivedStateOf {
                    state.productsCart.filter { (product, _) ->
                        productsNames.contains(product.name)
                    }
                }
            }

            PurchaseProductsScreen(
                modifier = Modifier.fillMaxWidth(),
                productsToBuy = productsToBuy,
                state = state,
                onEvent = { event ->

                    when(event){
                        is ProductsEvent.PurchaseItems -> navController.navigateToAndClear(Graphs.Main.route)
                        else -> {}
                    }

                    onEvent(event)
                }
            )
        }
    }


}