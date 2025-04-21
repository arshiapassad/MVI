package academy.nouri.s3_mvi.food_app.data.database

import academy.nouri.s3_mvi.food_app.utils.FOOD_DB_TABLE
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFood(entity: FoodEntity)

    @Delete
    suspend fun deleteFood(entity: FoodEntity)

    @Query("SELECT * FROM $FOOD_DB_TABLE")
    fun getAllFoods(): Flow<MutableList<FoodEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM $FOOD_DB_TABLE WHERE id = :id)")
    fun existsFood(id: Int): Flow<Boolean>
}