package com.kiruhm.cabify_mobile_challenge.domain.models

import androidx.annotation.StringRes

data class Filter<T>(
    @StringRes val name: Int,
    val isSelected: Boolean,
    val predicate: (T) -> Boolean
)