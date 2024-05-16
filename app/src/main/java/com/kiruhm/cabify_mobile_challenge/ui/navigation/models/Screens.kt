package com.kiruhm.cabify_mobile_challenge.ui.navigation.models

enum class Screens(
    val route: String,
    val icon: Int? = null,
    val title: Int? = null
) {
    ProductList("product_list"),
    Purchase("purchase"),
    ProductDetail("product_detail");

    companion object {
        fun getScreenByRoute(route: String) = entries.find { route.startsWith(it.route) }
    }

}