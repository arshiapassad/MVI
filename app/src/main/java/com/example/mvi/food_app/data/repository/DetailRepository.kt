package com.example.mvi.food_app.data.repository

import academy.nouri.s3_mvi.food_app.data.database.FoodDao
import academy.nouri.s3_mvi.food_app.data.database.FoodEntity
import academy.nouri.s3_mvi.food_app.data.server.ApiServices
import javax.inject.Inject

class DetailRepository @Inject constructor(private val api: ApiServices, private val dao: FoodDao) {
    suspend fun detailFood(id: Int) = api.foodDetail(id)
    suspend fun saveFood(entity: FoodEntity) = dao.saveFood(entity)
    suspend fun deleteFood(entity: FoodEntity) = dao.deleteFood(entity)
    fun existsFood(id: Int) = dao.existsFood(id)
}