package com.example.veganrecipesapp.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import com.example.veganrecipesapp.data.entity.Ingredient
import com.example.veganrecipesapp.intent.EditRecipeIntent
import com.example.veganrecipesapp.model.EditRecipeViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class EditRecipeActivity : AppCompatActivity() {
    private val recipeId: Long by lazy {
        intent.getLongExtra(EXTRA_RECIPE_ID, -1L)
    }
    private val recipeType: String by lazy {
        intent.getStringExtra(EXTRA_RECIPE_TYPE) ?: "DISH"
    }

    private val viewModel: EditRecipeViewModel by viewModels {
        EditRecipeViewModel.Factory(application, recipeId, recipeType)
    }

    private val ingredientAdapter = IngredientEditAdapter(
        onRemove = { ingredient ->
            viewModel.processIntent(EditRecipeIntent.RemoveIngredient(ingredient))
        }
    )
    private lateinit var titleEdit: EditText
    private lateinit var descEdit: EditText
    private lateinit var servingsEdit: EditText
    private lateinit var prepTimeEdit: EditText
    private lateinit var typeLabel: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEdit = findViewById(R.id.edit_title)
        descEdit = findViewById(R.id.edit_description)
        servingsEdit = findViewById(R.id.edit_servings)
        prepTimeEdit = findViewById(R.id.edit_prep_time)
        typeLabel = findViewById(R.id.text_type_value)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_ingredients_edit)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ingredientAdapter

        // Add-ingredient mini-form
        val nameInput  = findViewById<EditText>(R.id.edit_ingredient_name)
        val qtyInput   = findViewById<EditText>(R.id.edit_ingredient_quantity)
        val unitInput  = findViewById<EditText>(R.id.edit_ingredient_unit)

        findViewById<Button>(R.id.button_add_ingredient).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val qty  = qtyInput.text.toString().trim()
            val unit = unitInput.text.toString().trim()
            if (name.isBlank() || qty.isBlank()) {
                Toast.makeText(this, getString(R.string.ingredient_name_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.processIntent(
                EditRecipeIntent.AddIngredient(
                    // id=0 because Room will auto-assign when we save
                    Ingredient(
                        id = 0,
                        recipeId = recipeId.coerceAtLeast(0),
                        name = name,
                        quantity = qty,
                        unit = unit
                    )
                )
            )
            nameInput.text.clear()
            qtyInput.text.clear()
            unitInput.text.clear()
        }

        findViewById<Button>(R.id.button_save_recipe).setOnClickListener {
            val title = titleEdit.text.toString().trim()
            val desc  = descEdit.text.toString().trim()
            val servings = servingsEdit.text.toString().toIntOrNull() ?: 1
            val prepTime = prepTimeEdit.text.toString().toIntOrNull() ?: 0

            if (title.isBlank()) {
                Toast.makeText(this, getString(R.string.title_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.processIntent(
                EditRecipeIntent.Save(
                    title = title,
                    description = desc,
                    type = viewModel.uiState.value.recipe?.type ?: recipeType,
                    servings = servings,
                    prepTimeMinutes = prepTime,
                    ingredients = viewModel.uiState.value.ingredients
                )
            )
        }

        findViewById<Button>(R.id.button_cancel_recipe).setOnClickListener {
            viewModel.processIntent(EditRecipeIntent.Cancel)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isDone) {
                        finish()
                        return@collect
                    }

                    // Update screen title
                    supportActionBar?.title = if (state.isNewRecipe)
                        getString(R.string.add_recipe) else getString(R.string.edit_recipe)

                    // Show the type (fixed, not editable)
                    state.recipe?.let { recipe ->
                        typeLabel.text = recipe.type

                        // Only pre-fill text fields on first load (when they're empty)
                        // to avoid overwriting the user's in-progress edits
                        if (titleEdit.text.isEmpty()) titleEdit.setText(recipe.title)
                        if (descEdit.text.isEmpty())  descEdit.setText(recipe.description)
                        if (servingsEdit.text.isEmpty()) servingsEdit.setText(recipe.servings.toString())
                        if (prepTimeEdit.text.isEmpty()) prepTimeEdit.setText(recipe.prepTimeMinutes.toString())
                    }

                    ingredientAdapter.submitList(state.ingredients)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.processIntent(EditRecipeIntent.Cancel)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"
        const val EXTRA_RECIPE_TYPE = "extra_recipe_type"
    }
}