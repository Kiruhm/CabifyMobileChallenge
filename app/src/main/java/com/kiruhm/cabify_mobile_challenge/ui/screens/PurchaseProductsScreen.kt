package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import com.kiruhm.cabify_mobile_challenge.ui.utils.formatPrice

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
                        onProductClick = { onEvent.invoke(ProductsEvent.PurchaseProductClicked(product)) },
                        onSelectionChange = {
                            productsToBuy = productsToBuy.toMutableMap().apply {
                                this[productAndQuantity] = it
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
    onProductClick: () -> Unit,
    onSelectionChange: (Boolean) -> Unit
) {

    val costPerUnit = product.getDiscountPricePerUnit(quantity)
    val productCost = costPerUnit * quantity

    val dimensions = LocalDim.current

    Card(
        modifier = modifier,
        onClick = onProductClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            horizontalArrangement = spacedBy(dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ){

            Box(
                modifier = Modifier.width(80.dp)
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .aspectRatio(16 / 9f),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrls[0])
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    loading = {
                        Icon(
                            modifier = Modifier.size(dimensions.iconMediumSize),
                            imageVector = Icons.Default.Image,
                            contentDescription = "Loading"
                        )
                    },
                    error = { rememberVectorPainter(Icons.Default.Image) }
                )

                if (product.discountType != DiscountType.None){
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.CenterEnd)
                            .align(Alignment.TopEnd)
                            .padding(top = dimensions.spaceXXSmall)
                            .background(
                                color = when (product.discountType) {
                                    is DiscountType.Bulk -> Color.Blue
                                    DiscountType.None -> Color.Transparent
                                    DiscountType.TwoForOne -> Color.Red
                                },
                                shape = RoundedCornerShape(
                                    topStart = dimensions.cornersSmall,
                                    topEnd = 0.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = dimensions.cornersSmall
                                )
                            )
                    ) {
                        Text(
                            text = stringResource(id = product.discountType.name),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = dimensions.spaceXXSmall)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = spacedBy(dimensions.spaceSmall)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = product.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text =
                        buildAnnotatedString {
                            append(runCatching {
                                productCost.formatPrice(
                                    symbol = product.currency.symbol,
                                    decimalPartStyle = MaterialTheme.typography.bodyMedium.toSpanStyle()
                                        .copy(fontWeight = FontWeight.Bold)
                                )
                            }.getOrNull() ?: buildAnnotatedString {})

                            withStyle(
                                MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .8f),
                                    fontStyle = FontStyle.Italic
                                )
                            ) {
                                append(" (")
                                append(
                                    runCatching {
                                        costPerUnit.formatPrice(
                                            symbol = product.currency.symbol + "/u",
                                            decimalPartStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy()
                                        )
                                    }.getOrNull() ?: buildAnnotatedString {}
                                )
                                append(")")
                            }
                        },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                modifier = Modifier.wrapContentWidth(),
                text = "x$quantity",
                fontWeight = FontWeight.Bold
            )

            Checkbox(
                modifier = Modifier.wrapContentWidth(Alignment.End),
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
        }
    }
}

@Preview
@Composable
private fun PurchaseProductItemPreview() {

    var isSelected by remember {
        mutableStateOf(false)
    }

    AppSurface {
        PurchaseProductItem(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically),
            product = MockData.productList[5],
            quantity = 12,
            isSelected = isSelected,
            onProductClick = {},
            onSelectionChange = { isSelected = it }
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

