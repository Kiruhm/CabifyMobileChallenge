package com.kiruhm.cabify_mobile_challenge.domain.models.utils

object Constants {
    const val PRODUCT_NAMES_KEY = "product_name"
    const val NAME_KEY = "name"
    const val TITLE_KEY = "title"
    const val QUERY_MIN_LENGTH_TO_SEARCH = 3
    const val BULK_AMOUNT_LIMIT = 3
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
}