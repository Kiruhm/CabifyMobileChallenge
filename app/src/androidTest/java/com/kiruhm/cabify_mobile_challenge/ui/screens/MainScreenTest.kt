package com.kiruhm.cabify_mobile_challenge.ui.screens

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
import com.kiruhm.cabify_mobile_challenge.presentation.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun addTwoItemsToCartAndThemDeleteFirstOne_isCorrect() {
        with(composeRule){

            // Select first product
            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)[0].performClick()
            waitForIdle()
            // Add 2 items to cart
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_PRODUCT_TAG).performClick()

            // Go back to list screen
            onNodeWithContentDescription(context.getString(R.string.back_button_desc)).performClick()

            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("1")

            // Select second product
            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)[1].performClick()
            waitForIdle()
            // Add 1 item to cart
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).performClick()

            // Go back to list screen
            onNodeWithContentDescription(context.getString(R.string.back_button_desc)).performClick()

            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("2")


            // Remove second product from cart
            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)[0].performClick()
            waitForIdle()
            onNodeWithTag(testTag = TestTags.REMOVE_PRODUCT_TAG).performClick()
            onNodeWithTag(testTag = TestTags.REMOVE_PRODUCT_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()

            // Go back to list screen
            onNodeWithContentDescription(context.getString(R.string.back_button_desc)).performClick()

            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("1")
        }
    }

    @Test
    fun addTwoItemsToCartAndPurchaseFirstOne_isCorrect() {
        with(composeRule){
            // Select first product
            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)[0].performClick()
            waitForIdle()
            // Add 2 items to cart
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_PRODUCT_TAG).performClick()

            // Go back to list screen
            onNodeWithContentDescription(context.getString(R.string.back_button_desc)).performClick()

            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("1")

            // Select second product
            onAllNodesWithTag(testTag = TestTags.PRODUCT_ITEM_TAG)[1].performClick()
            waitForIdle()
            // Add 1 item to cart
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.ADD_TO_CART_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_PRODUCT_TAG).performClick()
            onNodeWithTag(testTag = TestTags.ADD_PRODUCT_TAG).performClick()

            // Go back to list screen
            onNodeWithContentDescription(context.getString(R.string.back_button_desc)).performClick()

            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("2")

            // Click on cart icon in order to navigate to cart screen
            onNodeWithTag(testTag = TestTags.CAST_ICON_TAG).performClick()
            waitForIdle()

            // Check there is 2 different items in cart
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_TAG).assertCountEquals(2)

            // Deselect second product and continue with the purchase process
            onAllNodesWithTag(testTag = TestTags.CART_PRODUCT_SELECT_BOX_TAG)[1].performClick()
            onNodeWithTag(testTag = TestTags.CART_PRODUCT_CONTINUE_BUTTON_TAG).performClick()
            waitForIdle()

            // Press buy button and accept dialog prompt
            onNodeWithTag(testTag = TestTags.BUY_PRODUCT_PURCHASE_BUTTON_TAG).performClick()
            onNodeWithTag(testTag = TestTags.BUY_PRODUCTS_DIALOG_TAG).assertIsDisplayed()
            onNodeWithContentDescription(label = context.getString(R.string.positive_button)).performClick()
            waitForIdle()

            // Check if FAB badge is displayed and has correct value
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertIsDisplayed()
            onNodeWithTag(testTag = TestTags.CAST_ICON_BADGE_TAG).assertTextEquals("1")
        }
    }
}