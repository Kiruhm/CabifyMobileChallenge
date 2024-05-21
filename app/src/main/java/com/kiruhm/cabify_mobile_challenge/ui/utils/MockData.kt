package com.kiruhm.cabify_mobile_challenge.ui.utils

import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.Constants

object MockData {

    private val discountTypes = listOf(
        DiscountType.None,
        DiscountType.TwoForOne,
        DiscountType.Bulk { _, price -> price * 0.5 },
        DiscountType.Bulk { amount, price -> price - amount * 0.25 },
    )

    val productList = mutableListOf<Product>().apply{
        (1..10).forEach {
            add(
                Product(
                    name = "Test Product Name $it",
                    code = "Test Product Code $it",
                    imageUrls = Constants.IMAGE_URLS.entries.random().value.shuffled(),
                    price = (1 ..1000).random() + (0 .. 99).random()/100.0,
                    discountType = discountTypes.random()
                )
            )
        }
    }

    val productsCart = mutableMapOf<Product, Int>().apply {
        productList.shuffled().subList(0, productList.size/2).forEach { product ->
            this[product] = (1 .. 50).random()
        }
    }

}