package com.example.mvi.food_app.view.detail


import academy.nouri.s3_mvi.food_app.data.model.ResponseFoodsList

sealed class DetailState {
    object FinishPage : DetailState()
    object Loading : DetailState()
    data class LoadFood(val data: ResponseFoodsList) : DetailState()
    data class Error(val error: String) : DetailState()
    data class SaveFavorite(val unit: Unit) : DetailState()
    data class DeleteFavorite(val unit: Unit) : DetailState()
    data class ExistsFavorite(val exists: Boolean) : DetailState()
}
