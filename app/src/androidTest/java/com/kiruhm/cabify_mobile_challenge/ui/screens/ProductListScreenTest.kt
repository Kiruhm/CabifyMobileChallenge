package com.kiruhm.cabify_mobile_challenge.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.data.local.LocalDataRequestClassImpl
import com.kiruhm.cabify_mobile_challenge.data.repositories.ProductsRepositoryImpl
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
import com.kiruhm.cabify_mobile_challenge.domain.use_cases.GetProductsUseCase
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModel
import com.kiruhm.cabify_mobile_challenge.presentation.main.view_models.ProductsViewModelFactory
import com.kiruhm.cabify_mobile_challenge.ui.components.AppSurface
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

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

                ProductListScreen(
                    state = productsState,
                    onEvent = productsViewModel::onEvent
                )
            }
        }
    }

    @Test
    fun showProducts_isCorrect() {
        composeRule.onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG).assertCountEquals(3)
    }

    @Test
    fun searchBarSearchProcess_isCorrect() {
        with(composeRule){
            val products = onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)
            val searchBar = onNodeWithText(context.getString(R.string.search_by_name))
            val clearQueryButton = onNodeWithTag(testTag = TestTags.SEARCH_BAR_CLEAR_TAG)

            clearQueryButton.assertIsNotDisplayed()

            searchBar.performTextInput("voucher")
            waitForIdle()

            products.assertCountEquals(1)

            clearQueryButton.assertIsDisplayed()
            clearQueryButton.performClick()

            searchBar.performTextInput("efesfsef")
            products.assertCountEquals(0)
        }
    }

    @Test
    fun filtersBehaviour_isCorrect() {
        with(composeRule){

            val filters = onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_FILTER_TAG)
            filters.assertCountEquals(2)

            val twoForOneFilter = onNode(
                hasTestTag(TestTags.PRODUCT_ITEM_FILTER_TAG)
                    .and(
                        SemanticsMatcher("Role is Checkbox") {
                            val roleProperty = it.config.getOrNull(SemanticsProperties.Role) ?: false
                            roleProperty == Role.Checkbox
                        }
                    )
                    .and(hasText(context.getString(DiscountType.TwoForOne.name)))
            )

            twoForOneFilter.assertExists("Node does not exist")
            twoForOneFilter.performClick()
            twoForOneFilter.assertIsSelected()

            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG).assertCountEquals(1)

            val bulkFilter = onNode(
                hasTestTag(TestTags.PRODUCT_ITEM_FILTER_TAG)
                    .and(
                        SemanticsMatcher("Role is Checkbox") {
                            val roleProperty = it.config.getOrNull(SemanticsProperties.Role) ?: false
                            roleProperty == Role.Checkbox
                        }
                    )
                    .and(hasText(context.getString(DiscountType.Bulk { _, _ -> 1.0 }.name)))
            )

            bulkFilter.assertExists("Node does not exist")
            bulkFilter.performClick()
            bulkFilter.assertIsSelected()

            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG).assertCountEquals(2)

            val clearFiltersButton = onNodeWithTag(TestTags.PRODUCT_ITEM_FILTER_CLEAR_TAG)
            clearFiltersButton.assertIsDisplayed()
            clearFiltersButton.performClick()

            twoForOneFilter.assertIsNotSelected()
            bulkFilter.assertIsNotSelected()

            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG).assertCountEquals(3)
        }
    }

    @Test
    fun toggleGridAndList_isCorrect() {
        with(composeRule){
            onNodeWithTag(TestTags.GRID_SELECTOR_LIST_TAG).assertIsSelected()
            onNodeWithTag(TestTags.GRID_SELECTOR_GRID_TAG).assertIsNotSelected()

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_list)))
            ).assertCountEquals(3)

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_grid)))
            ).assertCountEquals(0)

            // Change to grid view
            onNodeWithTag(TestTags.GRID_SELECTOR_GRID_TAG).performClick()

            onNodeWithTag(TestTags.GRID_SELECTOR_LIST_TAG).assertIsNotSelected()
            onNodeWithTag(TestTags.GRID_SELECTOR_GRID_TAG).assertIsSelected()

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_list)))
            ).assertCountEquals(0)

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_grid)))
            ).assertCountEquals(3)

            // Change to list view
            onNodeWithTag(TestTags.GRID_SELECTOR_LIST_TAG).performClick()

            onNodeWithTag(TestTags.GRID_SELECTOR_LIST_TAG).assertIsSelected()
            onNodeWithTag(TestTags.GRID_SELECTOR_GRID_TAG).assertIsNotSelected()

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_list)))
            ).assertCountEquals(3)

            onAllNodes(
                hasTestTag(TestTags.PRODUCT_ITEM_TAG)
                    .and(hasContentDescription(context.getString(R.string.product_item_description_grid)))
            ).assertCountEquals(0)
        }
    }

}