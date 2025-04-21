package com.example.mvi.food_app.view.favorite

import academy.nouri.s3_mvi.food_app.data.database.FoodEntity

sealed class FavoriteState {
    object Empty : FavoriteState()
    data class LoadFavorites(val data: MutableList<FoodEntity>) : FavoriteState()
}