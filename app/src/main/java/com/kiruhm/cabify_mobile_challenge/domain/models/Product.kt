package com.kiruhm.cabify_mobile_challenge.domain.models

import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import java.util.Currency

data class Product(
    val code: String,
    val name: String,
    val price: Double,
    val imageUrls: List<String>,
    val discountType: DiscountType,
    val currency: Currency = Currency.getInstance("EUR")
){
    fun getDiscountPrice(amount: Int) : Double = discountType.getDiscountPricePerUnit(amount, price)
}