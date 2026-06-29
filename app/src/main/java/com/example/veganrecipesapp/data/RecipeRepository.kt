package com.example.veganrecipesapp.data

import android.content.Context
import com.example.veganrecipesapp.data.entity.Ingredient
import com.example.veganrecipesapp.data.entity.Recipe
import com.example.veganrecipesapp.data.entity.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val dao: RecipeDao) {

    val getAllRecipesWithIngredients: Flow<List<RecipeWithIngredients>> = dao.getAllRecipesWithIngredients()

    fun getRecipeWithIngredients(recipeId: Long): Flow<RecipeWithIngredients?> = dao.getRecipeWithIngredients(recipeId)

    //all WRITE operations are suspend functions: they must be called from a coroutine
    // The ViewModel will launch these in viewModelScope, which automatically cancels
    // when the ViewModel is cleared (i.e., when the screen is gone).
    suspend fun insertRecipe(recipe: Recipe): Long = dao.insertRecipe(recipe)

    suspend fun updateRecipe(recipe: Recipe) = dao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipe: Recipe) = dao.deleteRecipe(recipe)

    suspend fun insertIngredients(ingredients: List<Ingredient>) = dao.insertIngredients(ingredients)

    suspend fun deleteIngredientsForRecipe(recipeId: Long) = dao.deleteIngredientsForRecipe(recipeId)

    companion object {
        @Volatile
        private var INSTANCE: RecipeRepository? = null

        fun getInstance(context: Context): RecipeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RecipeRepository(
                    RecipeDatabase.getDatabase(context).recipeDao()
                ).also { INSTANCE = it }
            }
        }
    }
}