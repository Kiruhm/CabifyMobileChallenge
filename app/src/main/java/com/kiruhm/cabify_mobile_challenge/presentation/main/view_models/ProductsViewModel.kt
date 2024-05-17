package com.kiruhm.cabify_mobile_challenge.presentation.main.view_models

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kiruhm.cabify_mobile_challenge.domain.models.Filter
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.use_cases.GetProductsUseCase
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    companion object {
        private const val PRODUCT_LIST_STATE = "product_list_state"
    }

    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>(PRODUCT_LIST_STATE)?.let { state ->
            _state.update { Gson().fromJson(state, ProductsState::class.java) }
        }
        viewModelScope.launch(Dispatchers.Default){
            _state.collectLatest {
                savedStateHandle[PRODUCT_LIST_STATE] = Gson().toJson(it)
            }
        }

        viewModelScope.launch {
            getProductsUseCase().collectLatest { result ->
                _state.update { it.copy(products = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    fun onEvent(event : ProductsEvent) = viewModelScope.launch(Dispatchers.Main) {
        when(event){
            is ProductsEvent.ProductClicked -> {}
            ProductsEvent.ShoppingCartClicked -> {}
            is ProductsEvent.FilterClicked -> toggleProductFilter(event.filter)
            ProductsEvent.ClearFilters -> clearProductFilters()
        }
    }

    private fun clearProductFilters() {
        _state.update {
            val newFilterList = it.productFilters.map { currentFilter ->
                currentFilter.copy(isSelected = false)
            }
            it.copy(productFilters = newFilterList)
        }
    }

    private fun toggleProductFilter(filter: Filter<Product>) {
        _state.update {
            val newFilterList = it.productFilters.map { currentFilter ->
                if (currentFilter != filter) return@map currentFilter
                currentFilter.copy(isSelected = !currentFilter.isSelected)
            }
            it.copy(productFilters = newFilterList)
        }
    }
}

data class ProductsState(
    val products: List<Product> = emptyList(),
    val productFilters: List<Filter<Product>> = MockData.productFilters.toMutableList()
)

sealed interface ProductsEvent {
    data class ProductClicked(val product: Product) : ProductsEvent
    data object ShoppingCartClicked : ProductsEvent

    data class FilterClicked(val filter: Filter<Product>) : ProductsEvent

    data object ClearFilters : ProductsEvent
}

@Suppress("UNCHECKED_CAST")
class ProductsViewModelFactory(
    private val getProductsUseCase: GetProductsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
        ProductsViewModel(getProductsUseCase, handle) as T
}

