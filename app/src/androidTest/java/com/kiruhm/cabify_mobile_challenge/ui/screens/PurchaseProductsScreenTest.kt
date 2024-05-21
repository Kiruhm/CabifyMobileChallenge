package com.kiruhm.cabify_mobile_challenge.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModel
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModelFactory
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PurchaseProductsScreenTest {

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

                PurchaseProductsScreen(
                    productsToBuy = mapOf(
                        productsState.products[0] to 3,
                        productsState.products[1] to 2,
                        productsState.products[2] to 5
                    ),
                    onEvent = {}
                )
            }
        }
    }

    @Test
    fun checkProductSections_isCorrect() {
        composeRule.onAllNodesWithTag(TestTags.BUY_PRODUCT_ROW_TAG).assertCountEquals(3)
    }

    @Test
    fun checkPurchaseProducts_isCorrect() {
        with(composeRule){
            onNodeWithTag(TestTags.BUY_PRODUCT_PURCHASE_BUTTON_TAG).performClick()
            onNodeWithTag(TestTags.BUY_PRODUCTS_DIALOG_TAG).assertIsDisplayed()

            onNodeWithContentDescription(context.getString(R.string.negative_button)).performClick()
            onNodeWithTag(TestTags.BUY_PRODUCTS_DIALOG_TAG).assertIsNotDisplayed()

            onNodeWithTag(TestTags.BUY_PRODUCT_PURCHASE_BUTTON_TAG).performClick()
            onNodeWithTag(TestTags.BUY_PRODUCTS_DIALOG_TAG).assertIsDisplayed()
            onNodeWithContentDescription(context.getString(R.string.positive_button)).performClick()
        }
    }

}