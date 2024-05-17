package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.kiruhm.cabify_mobile_challenge.ui.components.DiscountTag
import com.kiruhm.cabify_mobile_challenge.ui.components.QuantitySelector
import com.kiruhm.cabify_mobile_challenge.ui.theme.BulkColor
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.theme.TwoForOneColor
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import com.kiruhm.cabify_mobile_challenge.ui.utils.formatPrice

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    product: Product,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit,
) {

    val dimensions = LocalDim.current

    val quantityInCart by remember(state, product) {
        derivedStateOf { state.productsCart[product] ?: 0 }
    }

    val pagerState = rememberPagerState { product.imageUrls.count() }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = spacedBy(dimensions.spaceSmall),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState
                ) { page ->
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrls[page])
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
                }

                if (product.discountType != DiscountType.None){
                    DiscountTag(
                        modifier = Modifier.align(Alignment.TopStart),
                        discountType = product.discountType
                    )
                }

                HorizontalPagerIndicator(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .align(BottomCenter),
                    totalPages = pagerState.pageCount,
                    currentPage = pagerState.currentPage + 1
                )
            }

            if (product.discountType != DiscountType.None){
                Spacer(modifier = Modifier.height(dimensions.spaceMedium))
                DiscountText(
                    modifier = Modifier.fillMaxWidth(),
                    discountType = product.discountType
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(text = product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = runCatching {
                    product.price.formatPrice(
                        symbol = product.currency.symbol,
                        decimalPartStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(fontWeight = FontWeight.Bold)
                    )
                }.getOrNull() ?: buildAnnotatedString {},
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .align(BottomCenter),
            verticalArrangement = spacedBy(dimensions.spaceMedium),
        ) {

            if (quantityInCart == 0) {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { onEvent.invoke(ProductsEvent.AddToCart(product)) }
                ) {
                    Text(
                        text = stringResource(R.string.add_to_cart),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                QuantitySelector(
                    modifier = Modifier.fillMaxWidth(),
                    quantity = quantityInCart,
                    onAddClicked = { onEvent.invoke(ProductsEvent.AddToCart(product)) },
                    onSubtractClicked = { onEvent.invoke(ProductsEvent.SubtractFromCart(product)) }
                )
            }
        }
    }
}

@Composable
private fun DiscountText(
    modifier: Modifier,
    discountType: DiscountType
) {

    val dimensions = LocalDim.current

    val (color, discountText) = when(discountType){
        is DiscountType.Bulk -> BulkColor to R.string.the_more_you_buy_the_more_discount_you_will_receive
        DiscountType.None -> Color.Transparent to null
        DiscountType.TwoForOne -> TwoForOneColor to R.string.buy_two_and_get_one_for_free
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
            .border(2.dp, color, MaterialTheme.shapes.small)
    ) {

        DiscountTag(
            modifier = Modifier,
            discountType = discountType
        )

        Text(
            modifier = Modifier.padding(
                start = dimensions.spaceMedium,
                end = dimensions.spaceMedium,
                bottom = dimensions.spaceMedium,
                top = dimensions.spaceSmall
            ),
            text = discountText?.let { stringResource(id = it) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun HorizontalPagerIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPages: Int
) {

    val dimensions = LocalDim.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(dimensions.spaceSmall)
    ){
        (1 .. totalPages).forEach { page ->

            val isSelected = page == currentPage

            Box(
                modifier = Modifier
                    .size(if (isSelected) dimensions.spaceSmall + dimensions.spaceExtraSmall else dimensions.spaceSmall)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
private fun ProductDetailScreenPreview() {

    val dimensions = LocalDim.current

    var state by remember {
        mutableStateOf(
            ProductsState(
                products = MockData.productList
            )
        )
    }

    AppSurface {
        ProductDetailScreen(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            product = state.products[0],
            state = state,
            onEvent = { event ->
                  when(event){
                      is ProductsEvent.AddToCart -> {
                          val newCart = state.productsCart.toMutableMap()
                          newCart[event.product] = (newCart[event.product] ?: 0) + 1
                          state = state.copy(productsCart = newCart)
                      }
                      is ProductsEvent.SubtractFromCart -> {
                          val newCart = state.productsCart.toMutableMap()
                          newCart[event.product] = ((newCart[event.product] ?: 0) - 1).coerceAtLeast(0)
                          state = state.copy(productsCart = newCart)
                      }
                      else -> {}
                  }
            }
        )
    }
}