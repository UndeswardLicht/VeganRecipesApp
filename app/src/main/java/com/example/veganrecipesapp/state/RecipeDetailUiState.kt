package com.example.veganrecipesapp.state

import com.example.veganrecipesapp.data.entity.Ingredient
import com.example.veganrecipesapp.data.entity.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val ingredients: List<Ingredient> = emptyList()

)