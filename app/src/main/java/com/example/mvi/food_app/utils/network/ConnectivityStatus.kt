package com.example.mvi.food_app.utils.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityStatus {

    enum class Status{ Available, Unavailable, Losing, Lost}

    fun observe(): Flow<Status>
}