package com.example.mvi.food_app.view.list

import academy.nouri.s3_mvi.food_app.data.model.ResponseCategoriesList
import academy.nouri.s3_mvi.food_app.data.model.ResponseFoodsList

sealed class ListState {
    object Idle : ListState()
    object LoadingCategory : ListState()
    object LoadingFoods : ListState()
    object Empty : ListState()
    data class FilterLetters(val letters: MutableList<Char>) : ListState()
    data class RandomFood(val food: ResponseFoodsList.Meal?) : ListState()
    data class CategoriesList(val categories: MutableList<ResponseCategoriesList.Category>) : ListState()
    data class FoodsList(val foods: MutableList<ResponseFoodsList.Meal>) : ListState()
    data class Error(val error: String) : ListState()
}