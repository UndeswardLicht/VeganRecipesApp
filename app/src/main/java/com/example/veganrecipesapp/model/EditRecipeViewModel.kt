package com.example.veganrecipesapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.veganrecipesapp.data.RecipeRepository
import com.example.veganrecipesapp.data.entity.Recipe
import com.example.veganrecipesapp.intent.EditRecipeIntent
import com.example.veganrecipesapp.state.EditRecipeUiState
import com.example.veganrecipesapp.state.RecipeDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    application: Application,
    private val recipeId: Long,
    private val recipeType: String
) : AndroidViewModel(application) {

    private val repository = RecipeRepository.getInstance(application)
    private val _uiState = MutableStateFlow(EditRecipeUiState())
    val uiState: StateFlow<EditRecipeUiState> = _uiState.asStateFlow()

    // recipeId = -1L means "add new recipe", any other value means "edit existing"
    val isNewRecipe: Boolean get() = recipeId == -1L

    init {
        if(isNewRecipe){
            //provide a blank recipe skeleton with the chosen type
            _uiState.update {
                it.copy(
                    recipe = Recipe(title = "", description = "", type = recipeType,
                        servings = 2, prepTimeMinutes = 30),
                    ingredients = emptyList(),
                    isLoading = false,
                    isNewRecipe = true
                )
            }
        } else {
            viewModelScope.launch {
                repository.getRecipeWithIngredients(recipeId).collect { recipeWithIngredients ->
                    recipeWithIngredients?.let {
                        _uiState.update { state ->
                            state.copy(
                                recipe = it.recipe,
                                ingredients = it.ingredients,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun processIntent(intent: EditRecipeIntent) {
        when (intent) {
            is EditRecipeIntent.Save -> save(intent)
            EditRecipeIntent.Cancel -> _uiState.update { it.copy(isDone = true) }
            is EditRecipeIntent.AddIngredient -> {
                _uiState.update { it.copy(ingredients = it.ingredients + intent.ingredient) }
            }
            is EditRecipeIntent.RemoveIngredient -> {
                _uiState.update { it.copy(ingredients = it.ingredients - intent.ingredient) }
            }
        }
    }

    private fun save(intent: EditRecipeIntent.Save) {
        viewModelScope.launch {
            if (isNewRecipe) {
                // Insert the recipe and get back its id
                val newId = repository.insertRecipe(
                    Recipe(
                        title = intent.title,
                        description = intent.description,
                        type = recipeType,  // type is fixed at creation
                        servings = intent.servings,
                        prepTimeMinutes = intent.prepTimeMinutes
                    )
                )
                // insert ingredients with the correct recipeId
                val ingredientsWithId = intent.ingredients.map { it.copy(recipeId = newId) }
                repository.insertIngredients(ingredientsWithId)
            } else {
                // Update the recipe (keep the same ID and type)
                val existing = _uiState.value.recipe ?: return@launch
                repository.updateRecipe(
                    existing.copy(
                        title = intent.title,
                        description = intent.description,
                        servings = intent.servings,
                        prepTimeMinutes = intent.prepTimeMinutes
                        // type is not updated as it's fixed by that time already
                    )
                )
                // Replace all ingredients: delete the old set, insert the new set
                repository.deleteIngredientsForRecipe(existing.id)
                val ingredientsWithId = intent.ingredients.map { it.copy(recipeId = existing.id) }
                repository.insertIngredients(ingredientsWithId)
            }
            _uiState.update { it.copy(isDone = true) }
        }
    }

    // The Factory pattern is the standard way to pass constructor arguments (recipeId, recipeType) to a ViewModel
    // that are created by the framework (and we can't call the constructor directly)
    class Factory(
        private val application: Application,
        private val recipeId: Long,
        private val recipeType: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditRecipeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditRecipeViewModel(application, recipeId, recipeType) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}