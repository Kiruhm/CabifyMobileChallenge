package com.kiruhm.cabify_mobile_challenge.data.local

import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface IDataRequestClass {
    suspend fun getProducts() : Flow<Result<List<Product>>>
}