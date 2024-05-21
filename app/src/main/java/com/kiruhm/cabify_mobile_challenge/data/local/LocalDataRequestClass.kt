package com.kiruhm.cabify_mobile_challenge.data.local

import android.content.res.Resources
import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface LocalDataRequestClass {
    suspend fun getProducts(resources: Resources) : Flow<Result<List<Product>>>
}