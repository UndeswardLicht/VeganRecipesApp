package com.example.veganrecipesapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.veganrecipesapp.data.RecipeRepository
import com.example.veganrecipesapp.intent.RecipeListIntent
import com.example.veganrecipesapp.state.RecipeListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeListViewModel(application: Application) : AndroidViewModel(application){
    private val repository = RecipeRepository.getInstance(application)

    //MutableStateFlow in the writable version, only ViewModel writes to it
    //the View gets the read-only version below
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        //Start collecting the Flow from Room immediately
        // viewModelScope is a coroutine scope tied to this ViewModel's lifecycle:
        //it's automatically canceled when viewModel is destroyed - so no coroutines will leak
        viewModelScope.launch {
            repository.allRecipesWithIngredients.collect { recipes ->
                _uiState.update { it.copy(recipes = recipes, isLoading = false) }
            }
        }
    }

    fun processIntent(intent: RecipeListIntent) {
        when(intent){
            is RecipeListIntent.DeleteRecipe -> deleteRecipe(intent.recipeId)
            is RecipeListIntent.NavigateToDetail -> {
                _uiState.update { it.copy(navigateToDetail = intent.recipeId) }
            }
            is RecipeListIntent.NavigateToAddRecipe -> {
                _uiState.update { it.copy(navigateToAdd = true) }
            }
            is RecipeListIntent.ToggleViewMode -> {
                _uiState.update { it.copy(isListView = !it.isListView) }
            }
        }
    }

    fun onNavigatedToDetail() {
        _uiState.update { it.copy(navigateToDetail = null) }
    }

    fun onNavigatedToAdd() {
        _uiState.update { it.copy(navigateToAdd = false) }
    }

    private fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            val recipe = _uiState.value.recipes.find { it.recipe.id == recipeId }?.recipe
                ?: return@launch
            repository.deleteRecipe(recipe)
           // no manual list updates: the Room Flow will automatically re-emit the updated lsit
           //and our collect {} above updates _uiState
        }
    }
}