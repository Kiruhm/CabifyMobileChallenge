package com.kiruhm.cabify_mobile_challenge.data.local

import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.data.local.entities.ProductEntity
import com.kiruhm.cabify_mobile_challenge.data.mappers.toProduct
import kotlinx.coroutines.flow.flow

class DataRequestClass(private val resources: Resources) : IDataRequestClass {
    override suspend fun getProducts() = flow {
        try {
            val productsJsonArray = resources
                .openRawResource(R.raw.products)
                .bufferedReader()
                .use { it.readText() }
                .let { productsJsonString ->
                    Gson().fromJson(productsJsonString, JsonObject::class.java).get("products").asJsonArray
                }
            val products = Gson().fromJson(productsJsonArray, Array<ProductEntity>::class.java).map { it.toProduct() }
            emit(Result.success(products))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}