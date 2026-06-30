package com.example.veganrecipesapp.intent

/* Intents represent "something the user wants to do", they like event fired from View layer toward ViewModel layer
sealed classes are used because:
1. they list every possible action - compiler will warn if something is missing from 'when' expression
2. they carry data (data class variant) or they are standalone (object variant)
 */
sealed class RecipeListIntent {
    data class DeleteRecipe(val recipeId: Long) : RecipeListIntent()
    data class NavigateToDetail(val recipeId: Long) : RecipeListIntent()
    object NavigateToAddRecipe : RecipeListIntent()
    object ToggleViewMode : RecipeListIntent() //switch between List and table view
}