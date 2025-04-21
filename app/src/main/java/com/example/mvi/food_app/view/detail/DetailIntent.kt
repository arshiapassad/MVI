package com.example.mvi.food_app.view.detail

import academy.nouri.s3_mvi.food_app.data.database.FoodEntity

sealed class DetailIntent {
    object FinishPage : DetailIntent()
    data class FoodDetail(val id: Int) : DetailIntent()
    data class ExistsFavorite(val id: Int) : DetailIntent()
    data class SaveFavorite(val entity: FoodEntity) : DetailIntent()
    data class DeleteFavorite(val entity: FoodEntity) : DetailIntent()
}