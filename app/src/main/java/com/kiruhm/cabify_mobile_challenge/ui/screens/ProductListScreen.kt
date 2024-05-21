package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Filter
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.components.DiscountTag
import com.kiruhm.cabify_mobile_challenge.ui.components.GridSelector
import com.kiruhm.cabify_mobile_challenge.ui.components.ScrollDirection
import com.kiruhm.cabify_mobile_challenge.ui.components.rememberDirectionalLazyStaggeredGridState
import com.kiruhm.cabify_mobile_challenge.ui.theme.BulkColor
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.theme.TwoForOneColor
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import com.kiruhm.cabify_mobile_challenge.ui.utils.formatPrice

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit,
) {

    val dimensions = LocalDim.current
    val context = LocalContext.current

    var query by rememberSaveable {
        mutableStateOf("")
    }

    val filters = state.productFilters.sortedByDescending { it.isSelected }

    val activeFilters by remember(filters) {
        derivedStateOf { filters.filter { it.isSelected } }
    }

    val productsCartCount by remember {
        derivedStateOf { state.productsCart.size.coerceAtMost(99) }
    }

    val products by remember(query, activeFilters) {
        derivedStateOf {
            state.products.filter { product ->

                val containsQuery = query.length < Constants.QUERY_MIN_LENGTH_TO_SEARCH
                    || (query.length >= Constants.QUERY_MIN_LENGTH_TO_SEARCH && product.name.contains(query, ignoreCase = true))

                // Filter by query and then by selected filters
                containsQuery && activeFilters
                    .takeIf { it.isNotEmpty() }
                    ?.any { it.predicate(product) } ?: true
            }
        }
    }

    var displayGrid by rememberSaveable {
        mutableStateOf(false)
    }

    val staggeredGridState = rememberLazyStaggeredGridState()
    val directionalLazyListState = rememberDirectionalLazyStaggeredGridState(
        staggeredGridState
    )
    var currentScrollDirection by remember {
        mutableStateOf(ScrollDirection.None)
    }

    var showFab by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(directionalLazyListState.scrollDirection) {
        showFab = currentScrollDirection != ScrollDirection.Down && directionalLazyListState.scrollDirection != ScrollDirection.Down
        currentScrollDirection = directionalLazyListState.scrollDirection
    }

    Scaffold(
        modifier = modifier,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(),
                exit = scaleOut()
            ) {

                Box {
                    FloatingActionButton(
                        modifier = Modifier
                            .testTag(TestTags.CAST_ICON_TAG)
                            .align(Alignment.Center),
                        onClick = { onEvent.invoke(ProductsEvent.ShoppingCartClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = stringResource(R.string.shopping_basket)
                        )
                    }

                    if (state.productsCart.isNotEmpty()){
                        Badge(
                            modifier = Modifier
                                .padding(
                                    end = dimensions.spaceExtraSmall + dimensions.spaceXXSmall,
                                    top = dimensions.spaceExtraSmall + dimensions.spaceXXSmall
                                )
                                .align(Alignment.TopEnd),
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current
                        ) {
                            Text(
                                modifier = Modifier
                                    .testTag(TestTags.CAST_ICON_BADGE_TAG),
                                text = productsCartCount.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = spacedBy(dimensions.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            ProductListScreenHeader(
                query = query,
                onEvent = onEvent,
                filters = filters,
                activeFilters = activeFilters,
                displayGrid = displayGrid,
                toggleDisplayGrid = { displayGrid = !displayGrid },
                onQueryChanged = { query = it }
            )

            HorizontalDivider(thickness = 1.dp)

            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                state = staggeredGridState,
                contentPadding = PaddingValues(
                    bottom = dimensions.spaceMedium,
                    top = dimensions.spaceExtraSmall,
                    start = dimensions.spaceExtraSmall,
                    end = dimensions.spaceExtraSmall
                ),
                verticalItemSpacing = dimensions.spaceSmall,
                horizontalArrangement = Arrangement.SpaceEvenly,
                columns = if (displayGrid) StaggeredGridCells.FixedSize(160.dp) else StaggeredGridCells.Fixed(1)
            ){
                items(
                    items = products,
                    key = { it.name }
                ){ product ->
                    ProductItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.PRODUCT_ITEM_TAG)
                            .semantics {
                                contentDescription =
                                    context.getString(if (displayGrid) R.string.product_item_description_grid else R.string.product_item_description_list)
                            }
                            .animateItemPlacement(),
                        product = product,
                        displayGrid = displayGrid,
                        onProductClick = {
                            onEvent.invoke(ProductsEvent.ProductClicked(product))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ProductListScreenHeader(
    query: String,
    onEvent: (ProductsEvent) -> Unit,
    filters: List<Filter<Product>> = emptyList(),
    activeFilters: List<Filter<Product>> = emptyList(),
    displayGrid: Boolean,
    toggleDisplayGrid: () -> Unit,
    onQueryChanged: (String) -> Unit
) {

    val dimensions = LocalDim.current

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.SEARCH_BAR_TAG),
        query = query,
        onQueryChange = { insertedText -> onQueryChanged(insertedText) },
        onSearch = {},
        active = false,
        onActiveChange = {},
        trailingIcon = {
            if (query.isNotEmpty()){
                IconButton(
                    modifier = Modifier.testTag(TestTags.SEARCH_BAR_CLEAR_TAG),
                    onClick = { onQueryChanged("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear))
                }
            }
        },
        placeholder = { Text(text = stringResource(R.string.search_by_name)) }
    ) {}

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(dimensions.spaceExtraSmall)
    ) {
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(dimensions.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(dimensions.spaceSmall)
        ) {

            if (activeFilters.isNotEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .testTag(TestTags.PRODUCT_ITEM_FILTER_CLEAR_TAG)
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                            .border(1.dp, color = MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(dimensions.spaceExtraSmall)
                            .clickable {
                                onEvent.invoke(ProductsEvent.ClearFilters)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear_filters))
                    }
                }
            }

            items(
                items = filters,
                key = { it.hashCode() }
            ){ filter ->
                FilterItem(
                    modifier = Modifier
                        .testTag(TestTags.PRODUCT_ITEM_FILTER_TAG)
                        .animateItemPlacement(),
                    name = filter.name,
                    isSelected = filter.isSelected,
                    onClicked = {
                        onEvent.invoke(ProductsEvent.FilterClicked(filter))
                    }
                )
            }
        }

        GridSelector(
            modifier = Modifier
                .wrapContentSize(Alignment.CenterEnd)
                .fillMaxWidth(.25f),
            displayGrid = displayGrid,
            onToggleGrid = { toggleDisplayGrid() }
        )
    }
}

@Composable
private fun FilterItem(
    modifier: Modifier = Modifier,
    @StringRes name: Int,
    isSelected: Boolean,
    shape: Shape =  FilterChipDefaults.shape,
    onClicked: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = onClicked,
        shape = shape,
        border = BorderStroke(1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground),
        label = {
            Text(text = stringResource(id = name))
        }
    )
}

@Composable
private fun ProductItem(
    modifier: Modifier,
    product: Product,
    displayGrid: Boolean,
    onProductClick: () -> Unit
) {
    if (displayGrid){
        ProductItemGrid(
            modifier = modifier,
            product = product,
            onProductClick = onProductClick
        )
    } else {
        ProductItemList(
            modifier = modifier,
            product = product,
            onProductClick = onProductClick
        )
    }
}

@Composable
private fun ProductItemList(
    modifier: Modifier,
    product: Product,
    onProductClick: () -> Unit
) {
    val dimensions = LocalDim.current

    ElevatedCard(
        onClick = onProductClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            horizontalArrangement = spacedBy(dimensions.spaceSmall),
        ){

            Box(
                modifier = Modifier
                    .width(100.dp),
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
                            contentDescription = stringResource(id = R.string.loading_image)
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
                                    is DiscountType.Bulk -> BulkColor
                                    DiscountType.None -> Color.Transparent
                                    DiscountType.TwoForOne -> TwoForOneColor
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
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    text = product.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = kotlin.runCatching {
                        product.price.formatPrice(
                            symbol = product.currency.symbol,
                            decimalPartStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(fontWeight = FontWeight.Bold)
                        )
                    }.getOrNull() ?: buildAnnotatedString {},
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProductItemGrid(
    modifier: Modifier,
    product: Product,
    onProductClick: () -> Unit
) {

    val dimensions = LocalDim.current

    ElevatedCard(
        onClick = onProductClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceSmall),
            verticalArrangement = spacedBy(dimensions.spaceSmall),
        ){

            Box(
                modifier = Modifier.fillMaxWidth()
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
                            contentDescription = stringResource(id = R.string.loading_image)
                        )
                    },
                    error = { rememberVectorPainter(Icons.Default.Image) }
                )

                if (product.discountType != DiscountType.None){
                    DiscountTag(
                        modifier = Modifier.align(Alignment.TopEnd),
                        discountType = product.discountType
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                text = product.name,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = kotlin.runCatching {
                    product.price.formatPrice(
                        symbol = product.currency.symbol,
                        decimalPartStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(fontWeight = FontWeight.Bold)
                    )
                }.getOrNull() ?: buildAnnotatedString {},
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun FilterItemPreview() {

    var isSelected by rememberSaveable {
        mutableStateOf(false)
    }

    AppSurface {
        FilterItem(name = R.string.app_name, isSelected = isSelected, onClicked = { isSelected = !isSelected })
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    AppSurface {
        ProductItem(
            modifier = Modifier,
            product = MockData.productList.random(),
            displayGrid = (0..1).random() == 1,
            onProductClick = {}
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun ProductListScreenPreview() {

    val dimensions = LocalDim.current

    AppSurface {
        ProductListScreen(
            modifier = Modifier.padding(
                top = dimensions.spaceMedium,
                start = dimensions.spaceMedium,
                end = dimensions.spaceMedium
            ),
            state = ProductsState(
                products = MockData.productList
            ),
            onEvent = {}
        )
    }
}