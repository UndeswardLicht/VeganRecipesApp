package com.example.veganrecipesapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.veganrecipesapp.data.RecipeRepository
import com.example.veganrecipesapp.state.RecipeDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    application: Application,
    private val recipeId: Long
) : AndroidViewModel(application) {

    private val repository = RecipeRepository.getInstance(application)
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getRecipeWithIngredients(recipeId).collect { item ->
                item?.let {
                    _uiState.update { state ->
                        state.copy(recipe = it.recipe, ingredients = it.ingredients)
                    }
                }
            }
        }
    }

    class Factory(
        private val application: Application,
        private val recipeId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(application, recipeId) as T
        }
    }
}