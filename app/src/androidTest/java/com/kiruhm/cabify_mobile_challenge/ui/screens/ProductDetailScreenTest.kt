package com.kiruhm.cabify_mobile_challenge.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.data.local.LocalDataRequestClassImpl
import com.kiruhm.cabify_mobile_challenge.data.repositories.ProductsRepositoryImpl
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
import com.kiruhm.cabify_mobile_challenge.domain.use_cases.GetProductsUseCase
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModel
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModelFactory
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {

        context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.setContent {
            AppSurface {

                val dimensions = LocalDim.current

                val productsViewModel = viewModel<ProductsViewModel>(
                    factory = ProductsViewModelFactory(
                        GetProductsUseCase(
                            ProductsRepositoryImpl(
                                context.resources,
                                LocalDataRequestClassImpl()
                            )
                        )
                    )
                )

                val productsState by productsViewModel.state.collectAsStateWithLifecycle()

                ProductDetailScreen(
                    modifier = Modifier.fillMaxWidth().padding(dimensions.spaceMedium),
                    product = productsState.products[0],
                    state = productsState,
                    onEvent = productsViewModel::onEvent
                )
            }
        }
    }

    @Test
    fun testAddAndRemoveProductsFromCart_isCorrect() {
        with(composeRule){

            // Add 2 products to cart

            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_PRODUCT_TAG).performClick()

            onNodeWithContentDescription(context.getString(R.string.quantity_description)).assertTextContains("2", substring = true)

            // Remove 2 products from cart
            onNodeWithTag(testTag = TestTags.REMOVE_PRODUCT_TAG).performClick()
            onNodeWithContentDescription(context.getString(R.string.quantity_description)).assertTextContains("1", substring = true)
            onNodeWithTag(testTag = TestTags.REMOVE_PRODUCT_TAG).performClick()

            onNodeWithContentDescription(context.getString(R.string.quantity_description)).assertIsNotDisplayed()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()

        }
    }



}