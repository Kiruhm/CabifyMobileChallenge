package com.kiruhm.cabify_mobile_challenge.data.mappers

import com.kiruhm.cabify_mobile_challenge.data.local.entities.ProductEntity
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.ui.utils.MockData

fun ProductEntity.toProduct() = Product(
    code = code,
    name = name,
    price = price,
    // Image urls for the product that should be retrieved from back
    imageUrls = MockData.IMAGE_URLS[code] ?: emptyList(),
    // This should be changed and replace with discount type from back
    discountType = when(code){
        "VOUCHER" -> DiscountType.TwoForOne
        "TSHIRT" -> DiscountType.Bulk{ _, price -> (price - 1).coerceAtLeast(0.0) }
        else -> DiscountType. None
    }
)

