package com.kiruhm.cabify_mobile_challenge.domain.use_cases

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.kiruhm.cabify_mobile_challenge.data.local.LocalDataRequestClassImpl
import com.kiruhm.cabify_mobile_challenge.data.repositories.ProductsRepositoryImpl
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.repositories.ProductsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetProductsTest {

    private lateinit var fakeRepository: ProductsRepository
    private lateinit var context: Context

    private lateinit var getProductsUseCase : GetProductsUseCase

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        fakeRepository = ProductsRepositoryImpl(context.resources, LocalDataRequestClassImpl())

        getProductsUseCase = GetProductsUseCase(fakeRepository)
    }

    @Test
    fun getProductsFromJSON_isCorrect() {
        var products : List<Product> = emptyList()
        runBlocking {
            getProductsUseCase().collectLatest {
                products = it.getOrNull() ?: emptyList()
            }
        }
        assert(products.isNotEmpty()) { "Empty products list" }
    }
}