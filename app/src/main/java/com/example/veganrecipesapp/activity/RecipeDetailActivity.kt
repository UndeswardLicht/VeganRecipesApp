package com.example.veganrecipesapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veganrecipesapp.R
import com.example.veganrecipesapp.adapter.IngredientEditAdapter
import com.example.veganrecipesapp.model.RecipeDetailViewModel
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {
    private val recipeId: Long by lazy {
        intent.getLongExtra(RecipeListActivity.EXTRA_RECIPE_ID, -1L)
    }
    private val viewModel: RecipeDetailViewModel by viewModels {
        RecipeDetailViewModel.Factory(application, recipeId)
    }

    // The ingredient adapter is reused here in read-only:
    // passing an empty lambda for 'onRemove' since this is a view-only screen
    private val ingredientAdapter = IngredientEditAdapter(onRemove = {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this screen doesn't call EdgeToEdge()
        setContentView(R.layout.activity_recipe_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //show back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_ingredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ingredientAdapter

        val editButton = findViewById<Button>(R.id.button_edit_recipe)
        editButton.setOnClickListener {
            val intent = Intent(this, EditRecipeActivity::class.java)
                .putExtra(EditRecipeActivity.EXTRA_RECIPE_ID, recipeId)
                .putExtra(EditRecipeActivity.EXTRA_RECIPE_TYPE, viewModel.uiState.value.recipe?.type ?: "DISH")
            startActivity(intent)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.recipe?.let { recipe ->
                        supportActionBar?.title = recipe.title
                        findViewById<TextView>(R.id.text_detail_title).text = recipe.title
                        findViewById<TextView>(R.id.text_detail_type).text = recipe.type
                        findViewById<TextView>(R.id.text_detail_desc).text = recipe.description
                        findViewById<TextView>(R.id.text_detail_meta).text =
                            "Serves ${recipe.servings} · ${recipe.prepTimeMinutes} min"
                    }
                    ingredientAdapter.submitList(state.ingredients)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}