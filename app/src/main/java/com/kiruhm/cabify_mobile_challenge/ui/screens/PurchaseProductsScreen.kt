package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.components.GeneralDialog
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import com.kiruhm.cabify_mobile_challenge.ui.utils.formatPrice

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PurchaseProductsScreen(
    modifier: Modifier = Modifier,
    productsToBuy: Map<Product, Int>,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {
    val dimensions = LocalDim.current

    var showPurchaseDialog by remember {
        mutableStateOf(false)
    }

    val totalPrice by remember {
        derivedStateOf {
            state.productsCart.entries.sumOf { (product, quantity) ->
                val pricePerUnitWithDiscount = product.getDiscountPricePerUnit(quantity)
                pricePerUnitWithDiscount * quantity
            }
        }
    }

    if (showPurchaseDialog){
        GeneralDialog(
            description = stringResource(R.string.do_you_really_want_to_buy_the_selected_product),
            positiveButtonText = stringResource(R.string.buy),
            negativeButtonText = stringResource(R.string.cancel),
            onDismiss = { showPurchaseDialog = false },
            onCancel = { showPurchaseDialog = false },
            onAccept = {
                onEvent.invoke(ProductsEvent.PurchaseItems(productsToBuy))
                showPurchaseDialog = false
            }
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Absolute.spacedBy(dimensions.spaceMedium)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Absolute.spacedBy(dimensions.spaceSmall),
                contentPadding = PaddingValues(dimensions.spaceMedium)
            ) {

                if (productsToBuy.isNotEmpty()){
                    item {
                        Text(
                            text = stringResource(R.string.purchase_summary),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(thickness = 1.dp)
                        Spacer(modifier = Modifier.height(dimensions.spaceMedium))
                    }

                    items(
                        items = productsToBuy.toList(),
                        key = { it.first.name },
                    ){ (product, quantity) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentWidth(Alignment.Start),
                                text = product.name + " x$quantity",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                modifier = Modifier.wrapContentWidth(Alignment.End),
                                text = (product.getDiscountPricePerUnit(quantity) * quantity).formatPrice(product.currency.symbol),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    item {
                        HorizontalDivider(thickness = 1.dp)
                    }

                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End),
                            fontWeight = FontWeight.Bold,
                            text = "${totalPrice.formatPrice("€")}")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = productsToBuy.isNotEmpty(),
                onClick = { showPurchaseDialog = true }
            ) {
                Text(text = stringResource(R.string.purchase))
            }
        }
    }
}

@Preview
@Composable
private fun PurchaseProductsScreenPreview() {
    AppSurface {
        PurchaseProductsScreen(
            modifier = Modifier.fillMaxWidth(),
            productsToBuy = MockData.productsCart,
            state = ProductsState(
                products = MockData.productList,
                productsCart = MockData.productsCart
            ),
            onEvent = {}
        )
    }
}