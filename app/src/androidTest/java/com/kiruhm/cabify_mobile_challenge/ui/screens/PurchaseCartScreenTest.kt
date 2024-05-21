package com.kiruhm.cabify_mobile_challenge.ui.screens

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isOff
import androidx.compose.ui.test.isOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
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
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsEvent
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModel
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModelFactory
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PurchaseCartScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {

        context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.setContent {
            AppSurface {

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

                PurchaseCartScreen(
                    state = productsState,
                    onEvent = productsViewModel::onEvent
                )

                LaunchedEffect(Unit) {
                    // Add products to cart
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[0]))
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[0]))
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[1]))
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[1]))
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[2]))
                    productsViewModel.onEvent(ProductsEvent.AddToCart(productsState.products[2]))
                }
            }
        }
    }

    @Test
    fun checkProductSelections_isCorrect() {
        with(composeRule){
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG).assertAll(isOn())
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_AMOUNT_TAG).assertAll(hasTestTag("x2"))

            onNodeWithTag(testTag = TestTags.CART_PRODUCT_CONTINUE_BUTTON_TAG).assertIsEnabled()

            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG)[0].performClick()
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG)[1].performClick()
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG)[2].performClick()

            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG).assertAll(isOff())
            onNodeWithTag(testTag = TestTags.CART_PRODUCT_CONTINUE_BUTTON_TAG).assertIsNotEnabled()
        }
    }

    @Test
    fun checkProductDeletion_isCorrect() {
        with(composeRule){
            // Try to delete but cancel the process
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_TAG).assertCountEquals(3)
            onAllNodesWithContentDescription(label = context.getString(R.string.delete_from_cart))[0].performClick()

            onNodeWithTag(testTag = TestTags.DELETE_PRODUCT_DIALOG_TAG).assertIsDisplayed()
            onNodeWithContentDescription(label = context.getString(R.string.negative_button)).performClick()

            onNodeWithTag(testTag = TestTags.DELETE_PRODUCT_DIALOG_TAG).assertIsNotDisplayed()

            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_TAG).assertCountEquals(3)

            // Try to delete and fulfill the process
            onAllNodesWithContentDescription(label = context.getString(R.string.delete_from_cart))[1].performClick()

            onNodeWithTag(testTag = TestTags.DELETE_PRODUCT_DIALOG_TAG).assertIsDisplayed()
            onNodeWithContentDescription(label = context.getString(R.string.positive_button)).performClick()

            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_TAG).assertCountEquals(2)
        }
    }
}