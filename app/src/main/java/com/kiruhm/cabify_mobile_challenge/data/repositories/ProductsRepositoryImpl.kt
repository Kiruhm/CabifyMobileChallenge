package com.kiruhm.cabify_mobile_challenge.data.repositories

import android.content.res.Resources
import com.kiruhm.cabify_mobile_challenge.data.local.LocalDataRequestClassImpl
import com.kiruhm.cabify_mobile_challenge.domain.repositories.ProductsRepository

class ProductsRepositoryImpl(private val resources: Resources, private val drc: LocalDataRequestClassImpl) : ProductsRepository {
    override suspend fun getProducts() = drc.getProducts(resources)
}