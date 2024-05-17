package com.kiruhm.cabify_mobile_challenge.ui.navigation.models

enum class Graphs(
    val route: String,
    val startDestination: Screens
) {
    Main("main_graph", Screens.ProductList)
}