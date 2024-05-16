package com.kiruhm.cabify_mobile_challenge.domain.repositories

import com.kiruhm.cabify_mobile_challenge.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProducts(): Flow<Result<List<Product>>>
}