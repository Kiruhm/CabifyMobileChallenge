package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData

@Composable
fun PurchaseProductsScreen(
    modifier: Modifier = Modifier,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {

    val dimensions = LocalDim.current

    var showPurchaseDialog by remember {
        mutableStateOf(false)
    }

    var productsToBuy by remember {
        mutableStateOf(state.productsCart.map { it to true }.toMap())
    }

    val totalPrice by remember {
        derivedStateOf {
            state.productsCart.entries.sumOf { (product, quantity) ->
                val pricePerUnitWithDiscount = product.getDiscountPricePerUnit(quantity)
                pricePerUnitWithDiscount * quantity
            }
        }
    }

    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = spacedBy(dimensions.spaceMedium)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {

                item {
                    Text(
                        text = stringResource(R.string.products_in_cart),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(thickness = 1.dp)
                }

                items(items = productsToBuy.toList()){ (productAndQuantity, isSelected) ->
                    val (product, quantity) = productAndQuantity

                    PurchaseProductItem(
                        modifier = Modifier.fillMaxWidth(),
                        product = product,
                        quantity = quantity,
                        isSelected = isSelected,
                        onClicked = { onEvent.invoke(ProductsEvent.PurchaseProductClicked(product)) },
                        onSelectionChange = {
                            productsToBuy = productsToBuy.toMutableMap().apply {
                                this[productAndQuantity] = !isSelected
                            }
                        }
                    )

                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showPurchaseDialog = true }
            ) {
                Text(text = stringResource(R.string.purchase))
            }
        }

    }
}

@Composable
fun PurchaseProductItem(
    modifier: Modifier,
    product: Product,
    quantity: Int,
    isSelected: Boolean,
    onClicked: () -> Unit,
    onSelectionChange: () -> Unit
) {

}

@Preview
@Composable
private fun PurchaseProductItemPreview() {

    var isSelected by remember {
        mutableStateOf(false)
    }

    AppSurface {
        PurchaseProductItem(
            modifier = Modifier.fillMaxWidth(),
            product = MockData.productList.random(),
            quantity = 1,
            isSelected = isSelected,
            onClicked = {},
            onSelectionChange = { isSelected = !isSelected }
        )
    }
}

@Preview
@Composable
private fun PurchaseProductsScreenPreview() {
    AppSurface {
        PurchaseProductsScreen(
            modifier = Modifier.fillMaxWidth(),
            state = ProductsState(
                products = MockData.productList,
                productsCart = MockData.productsCart
            ),
            onEvent = {}
        )
    }
}

