package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
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

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    product: Product,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit,
) {

    val dimensions = LocalDim.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    if (screenWidth in dimensions.layoutCompactSizeRange){
        CompactProductDetailScreen(
            modifier = modifier,
            product = product,
            state = state,
            onEvent = onEvent
        )
    } else {
        ExpandedProductDetailScreen(
            modifier = modifier,
            product = product,
            state = state,
            onEvent = onEvent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpandedProductDetailScreen(
    modifier: Modifier,
    product: Product,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit
) {
    val dimensions = LocalDim.current
    val pagerState = rememberPagerState { product.imageUrls.count() }

    val quantityInCart by remember(state, product) {
        derivedStateOf { state.productsCart[product] ?: 0 }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = spacedBy(dimensions.spaceSmall),
        ) {
            Column(
                verticalArrangement = spacedBy(dimensions.spaceSmall),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(.4f)
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxWidth(),
                        state = pagerState
                    ) { page ->
                        SubcomposeAsyncImage(
                            modifier = Modifier
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
                                    contentDescription = stringResource(R.string.loading_image),
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
                }

                HorizontalPagerIndicator(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .align(Alignment.CenterHorizontally),
                    pagerState = pagerState
                )
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = spacedBy(dimensions.spaceSmall)
            ) {
                if (product.discountType != DiscountType.None){
                    Spacer(modifier = Modifier.height(dimensions.spaceMedium))
                    DiscountText(
                        modifier = Modifier.fillMaxWidth(),
                        discountType = product.discountType
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceSmall))
                Text(text = product.name, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = runCatching {
                        product.price.formatPrice(
                            symbol = product.currency.symbol,
                            decimalPartStyle = MaterialTheme.typography.titleLarge.toSpanStyle().copy(fontWeight = FontWeight.Bold)
                        )
                    }.getOrNull() ?: buildAnnotatedString {},
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalArrangement = spacedBy(dimensions.spaceMedium),
        ) {

            if (quantityInCart == 0) {
                Button(
                    modifier = Modifier
                        .testTag(TestTags.ADD_TO_CART_TAG)
                        .fillMaxSize(),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactProductDetailScreen(modifier: Modifier, product: Product, state: ProductsState, onEvent: (ProductsEvent) -> Unit) {

    val dimensions = LocalDim.current
    val pagerState = rememberPagerState { product.imageUrls.count() }

    val quantityInCart by remember(state, product) {
        derivedStateOf { state.productsCart[product] ?: 0 }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
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
                                contentDescription = stringResource(R.string.loading_image)
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
            }

            HorizontalPagerIndicator(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.CenterHorizontally),
                pagerState = pagerState
            )

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
                .height(IntrinsicSize.Max),
            verticalArrangement = spacedBy(dimensions.spaceMedium),
        ) {

            if (quantityInCart == 0) {
                Button(
                    modifier = Modifier
                        .testTag(TestTags.ADD_TO_CART_TAG)
                        .fillMaxSize(),
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
        is DiscountType.Bulk ->
            BulkColor to stringResource(id = R.string.the_more_you_buy_the_more_discount_you_will_receive) + " " + stringResource(
                R.string.purchase_more_than_x_units_to_qualify_for_the_discount, discountType.limitAmount
            )
        DiscountType.None -> Color.Transparent to ""
        DiscountType.TwoForOne -> TwoForOneColor to stringResource(id = R.string.buy_two_and_get_one_for_free)
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
            text = discountText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {

    val dimensions = LocalDim.current

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(dimensions.spaceSmall)
    ){
        repeat(pagerState.pageCount) { page ->

            val isSelected = page == pagerState.targetPage

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
private fun DiscountTextPreview() {
    AppSurface {
        DiscountText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically)
                .padding(LocalDim.current.spaceMedium),
            discountType = DiscountType.TwoForOne
        )
    }
}

@PreviewScreenSizes
@PreviewLightDark
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