package com.kiruhm.cabify_mobile_challenge.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsState
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.components.GridSelector
import com.kiruhm.cabify_mobile_challenge.ui.components.ScrollDirection
import com.kiruhm.cabify_mobile_challenge.ui.components.rememberDirectionalLazyStaggeredGridState
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit,
) {

    val dimensions = LocalDim.current

    var query by rememberSaveable {
        mutableStateOf("")
    }

    val filters = state.productFilters.sortedByDescending { it.isSelected }

    val activeFilters by remember(filters) {
        derivedStateOf { filters.filter { it.isSelected } }
    }

    val products by remember(query, activeFilters) {
        derivedStateOf {
            state.products.filter { product ->

                val containsQuery = if (query.length >= Constants.QUERY_MIN_LENGTH_TO_SEARCH)
                    product.name.contains(query, ignoreCase = true) || product.code.contains(query, ignoreCase = true)
                else true

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
                FloatingActionButton(
                    onClick = { onEvent.invoke(ProductsEvent.ShoppingCartClicked) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = "Shopping basket"
                    )
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
private fun ColumnScope.ProductListScreenHeader(
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
        modifier = Modifier.fillMaxWidth(),
        query = query,
        onQueryChange = { insertedText -> onQueryChanged(insertedText) },
        onSearch = {},
        active = false,
        onActiveChange = {},
        trailingIcon = {
            if (query.isNotEmpty()){
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear))
                }
            }
        },
        placeholder = { Text(text = stringResource(R.string.search_by_name_or_code)) }
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
                    FilterItem(
                        modifier = Modifier.animateItemPlacement(),
                        name = null,
                        icon = Icons.Default.Clear,
                        isSelected = true,
                        shape = CircleShape,
                        onClicked = {
                            onEvent.invoke(ProductsEvent.ClearFilters)
                        }
                    )
                }
            }

            items(
                items = filters,
                key = { it.hashCode() }
            ){ filter ->
                FilterItem(
                    modifier = Modifier.animateItemPlacement(),
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
    @StringRes name: Int? = null,
    icon: ImageVector? = null,
    isSelected: Boolean,
    shape: Shape =  FilterChipDefaults.shape,
    onClicked: () -> Unit
) {

    if (name == null && icon == null) return

    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = onClicked,
        shape = shape,
        border = BorderStroke(1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground),
        label = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            } else {
                Text(text = stringResource(id = name!!))
            }
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
                    .width(80.dp)
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
                        product.price.formatPrice(product.currency.symbol)
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
                                .padding(horizontal = dimensions.spaceExtraSmall)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
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
                    product.price.formatPrice(product.currency.symbol)
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

@Composable
private fun Double.formatPrice(symbol: String): AnnotatedString {

    val formattedNumber = DecimalFormat.getInstance(Locale.getDefault()).format(this)
    val (wholePart, decimalPart) = formattedNumber.split(DecimalFormatSymbols.getInstance(Locale.getDefault()).decimalSeparator).run {
        when(size){
            1 -> this[0] to "00"
            2 -> this[0] to if (this[1].toInt() < 10) "${this[1]}0" else this[1]
            else -> throw IllegalArgumentException("Invalid number format")
        }
    }

    return buildAnnotatedString {
        withStyle(
            style = MaterialTheme.typography.bodyLarge.toSpanStyle()
        ) {
            append(wholePart)
            append('\'')
        }
        withStyle(style = MaterialTheme.typography.bodySmall.toSpanStyle()) {
            append(decimalPart)
            append(' ')
        }
        withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(symbol)
        }
    }
}