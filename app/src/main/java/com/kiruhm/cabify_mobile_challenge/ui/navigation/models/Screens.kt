package com.kiruhm.cabify_mobile_challenge.ui.navigation.models

import com.kiruhm.cabify_mobile_challenge.R

enum class Screens(
    val route: String,
    val icon: Int? = null,
    val title: Int? = null
) {
    ProductList("product_list"),
    Cart("cart_purchase", title = R.string.cart_title),
    Purchase("purchase", title = R.string.purchase_title),
    ProductDetail("product_detail");

    companion object {
        fun getScreenByRoute(route: String) = entries.find { route.startsWith(it.route) }
    }

}