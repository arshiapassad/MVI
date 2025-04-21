package com.example.mvi.food_app.data.repository

import academy.nouri.s3_mvi.food_app.data.database.FoodDao
import javax.inject.Inject

class FavoriteRepository @Inject constructor(private val dao: FoodDao){
    fun foodsList() = dao.getAllFoods()
}