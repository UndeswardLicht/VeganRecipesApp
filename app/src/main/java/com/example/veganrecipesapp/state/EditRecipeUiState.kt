package com.example.veganrecipesapp.state

import com.example.veganrecipesapp.data.entity.Ingredient
import com.example.veganrecipesapp.data.entity.Recipe

data class EditRecipeUiState(
    // null while loading (for edit mode) but pre-filled for new recipes
    val recipe: Recipe? = null,
    val ingredients: List<Ingredient> = emptyList(),

    val isLoading: Boolean = true,
    val isNewRecipe: Boolean = false,

    // When true, the Activity should call finish()
    val isDone: Boolean = false
)