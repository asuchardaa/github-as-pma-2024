package com.example.myapp015amynotehub.database

import androidx.room.*
import com.example.myapp015amynotehub.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM category_table ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE name = :categoryName LIMIT 1")
    fun getCategoryByName(categoryName: String): Category?
}