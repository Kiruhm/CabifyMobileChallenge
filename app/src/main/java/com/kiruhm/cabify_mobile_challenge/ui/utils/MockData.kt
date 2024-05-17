package com.kiruhm.cabify_mobile_challenge.ui.utils

import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.Filter
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType

object MockData {

    val IMAGE_URLS = mapOf(
        "TSHIRT" to listOf(
            "https://static.vecteezy.com/system/resources/previews/012/628/161/non_2x/isolated-regular-plain-black-back-t-shirt-free-png.png",
            "https://i.pinimg.com/originals/bd/ef/cb/bdefcbc72735f64db17f3250b1e64245.png",
            "https://jersix.com/wp-content/uploads/2020/07/x-shirt-wild-magliette-personalizzate.png.png"
        ),
        "MUG" to listOf(
            "https://www.pngall.com/wp-content/uploads/2/Mug-PNG-Pic.png",
            "https://www.pngall.com/wp-content/uploads/2/Mug.png",
            "https://www.pngall.com/wp-content/uploads/2/Mug-PNG-Image-HD.png"
        ),
        "VOUCHER" to listOf(
            "https://www.greenback.com/assets/f/blogs/how-to-configure-quickbooks-online-for-gift-cards/hdr.png",
            "https://www.becajun.com/wp-content/uploads/2020/06/25-amazon-gift-card-png-1.png",
        )
    )

    val productFilters = listOf<Filter<Product>>(
        Filter(
            name = R.string.filter_discount_two_for_one,
            isSelected = false,
            predicate = { product -> product.discountType is DiscountType.TwoForOne }
        ),
        Filter(
            name = R.string.filter_discount_bulk,
            isSelected = false,
            predicate = { product -> product.discountType is DiscountType.Bulk }
        )
    )

    private val discoutTypes = listOf(
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
                    imageUrls = IMAGE_URLS.entries.random().value.shuffled(),
                    price = (1 ..1000).random() + (0 .. 99).random()/100.0,
                    discountType = discoutTypes.random()
                )
            )
        }
    }

}