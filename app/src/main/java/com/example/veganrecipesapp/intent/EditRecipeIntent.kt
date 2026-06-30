package com.example.veganrecipesapp.intent

import com.example.veganrecipesapp.data.entity.Ingredient

sealed class EditRecipeIntent {
    // Save with all current form field values
    data class Save(
        val title: String,
        val description: String,
        // RecipeType.name — fixed at creation, read-only on edit
        val type: String,
        val servings: Int,
        val prepTimeMinutes: Int,
        val ingredients: List<Ingredient>
    ) : EditRecipeIntent()

    object Cancel : EditRecipeIntent()

    // Ingredient management intents (fired from within the edit screen)
    data class AddIngredient(val ingredient: Ingredient) : EditRecipeIntent()
    data class RemoveIngredient(val ingredient: Ingredient) : EditRecipeIntent()
}
