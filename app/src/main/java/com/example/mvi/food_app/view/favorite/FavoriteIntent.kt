package com.example.mvi.food_app.view.favorite

sealed class FavoriteIntent {
    object LoadFavorite : FavoriteIntent()
}