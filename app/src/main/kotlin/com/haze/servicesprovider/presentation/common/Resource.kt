package com.haze.servicesprovider.presentation.common

sealed interface Resource<out T> {
    data object Idle : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
}
