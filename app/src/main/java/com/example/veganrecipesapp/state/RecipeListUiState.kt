package com.example.veganrecipesapp.state

import com.example.veganrecipesapp.data.entity.RecipeWithIngredients

data class RecipeListUiState(
    val recipes: List<RecipeWithIngredients> = emptyList(),
    val isLoading: Boolean = true,

    // true = card list, false = compact table
    val isListView: Boolean = true,

    // If non-null, the Activity navigates to detail for this recipeID,
    // then clears this field so it doesn't re-navigate on re-render
    val navigateToDetail: Long? = null,

    // If true, navigate to the Add Recipe screen
    val navigateToAdd: Boolean = false
)