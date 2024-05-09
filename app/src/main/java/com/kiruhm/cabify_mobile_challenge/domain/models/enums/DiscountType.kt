package com.kiruhm.cabify_mobile_challenge.domain.models.enums

import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants
import kotlin.math.roundToInt

sealed interface DiscountType {

    fun getDiscountPricePerUnit(amount: Int, price: Double) : Double = price

    data object None : DiscountType

    data object TwoForOne : DiscountType {
        override fun getDiscountPricePerUnit(amount: Int, price: Double): Double = (amount/2f).roundToInt() * price
    }
    class Bulk(
        private val limitAmount: Int = Constants.BULK_AMOUNT_LIMIT,
        private val transformation: (amount: Int, price: Double) -> Double
    ) : DiscountType {
        override fun getDiscountPricePerUnit(amount: Int, price: Double) : Double =
            if (amount < limitAmount) price
            else transformation(amount, price)
    }
}