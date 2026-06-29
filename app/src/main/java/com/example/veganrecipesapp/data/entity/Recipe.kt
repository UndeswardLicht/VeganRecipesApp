package com.example.veganrecipesapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="recipes")
data class Recipe(
    @PrimaryKey(autoGenerate=true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String,
    val servings: Int,
    val prepTimeMinutes: Int
)

enum class RecipeType{
    DISH,
    DRINK
}