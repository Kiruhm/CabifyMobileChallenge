package com.kiruhm.cabify_mobile_challenge.domain.models.enums

import androidx.annotation.StringRes
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants
import kotlin.math.roundToInt

sealed class DiscountType(
    @StringRes val name: Int
) {

    open fun getDiscountPricePerUnit(amount: Int, price: Double) : Double = price

    data object None : DiscountType(R.string.discount_none)

    data object TwoForOne : DiscountType(R.string.two_for_one) {
        override fun getDiscountPricePerUnit(amount: Int, price: Double): Double = ((amount/2f).roundToInt() * price) / amount
    }
    class Bulk(
        val limitAmount: Int = Constants.BULK_AMOUNT_LIMIT,
        private val discountFormula: (amount: Int, price: Double) -> Double
    ) : DiscountType(R.string.discount_bulk) {
        override fun getDiscountPricePerUnit(amount: Int, price: Double) : Double =
            if (amount < limitAmount) price
            else discountFormula(amount, price)
    }
}