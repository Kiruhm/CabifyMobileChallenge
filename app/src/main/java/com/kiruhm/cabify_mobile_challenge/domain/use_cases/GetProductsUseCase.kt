package com.kiruhm.cabify_mobile_challenge.domain.use_cases

import com.kiruhm.cabify_mobile_challenge.domain.repositories.ProductsRepository

class GetProductsUseCase(private val repository: ProductsRepository) {
    suspend operator fun invoke() = repository.getProducts()
}