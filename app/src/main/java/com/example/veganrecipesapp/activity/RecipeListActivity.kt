package com.example.veganrecipesapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veganrecipesapp.R
import com.example.veganrecipesapp.adapter.RecipeListAdapter
import com.example.veganrecipesapp.adapter.RecipeTableAdapter
import com.example.veganrecipesapp.intent.RecipeListIntent
import com.example.veganrecipesapp.model.RecipeListViewModel
import com.example.veganrecipesapp.state.RecipeListUiState
import kotlinx.coroutines.launch

class RecipeListActivity : AppCompatActivity() {

    //'by viewModels()' is a Kotlin property delegate from activity-ktx
    //it lazily creates the ViewModel first time it's accessed and keeps the same instance across orientation changes
    //this is how screen rotation is handled without losing data
    private val viewModel: RecipeListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    //we create both Adapters and have them around when/if the view mode changes
    private val listAdapter = RecipeListAdapter(
        onItemClick = { id -> viewModel.processIntent(RecipeListIntent.NavigateToDetail(id)) },
        onDelete = { id -> viewModel.processIntent(RecipeListIntent.DeleteRecipe(id)) }
    )

    private val tableAdapter = RecipeTableAdapter(
        onItemClick = { id -> viewModel.processIntent(RecipeListIntent.NavigateToDetail(id)) },
        onDelete = { id -> viewModel.processIntent(RecipeListIntent.DeleteRecipe(id)) }
    )

    //called once when the Activity is first created or re-created
    override fun onCreate(savedInstaneState: Bundle?){
        super.onCreate(savedInstaneState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_list)

        //apply insets so content doesn't hide under the status/nav bars
        val rootView = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        //set up the toolbar as the app's Action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Recipes"

        recyclerView = findViewById(R.id.recycler_view_recipes)
        emptyText = findViewById(R.id.text_empty)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter //start in list mode

        //'Add' button
        findViewById<Button>(R.id.button_add_recipe).setOnClickListener {
            viewModel.processIntent(RecipeListIntent.NavigateToAddRecipe)
        }

        //collect state changes
        // repeatOnLifecycle(STARTED) means: collect while the app is visible, pause when it goes to the background
        //to save battery and avoid updates to UI that isn't on screen.

        /* Every intent emission
         — list updated and loading became false, view mode toggled, delete completed, navigation event set -
          flows through this one collect block, which calls render(state)
         */
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    //It takes a complete snapshot of "everything the screen needs to know" and makes the UI match it
    private fun render(state: RecipeListUiState) {
        //switch adapter when view mode changes
        if (state.isListView && recyclerView.adapter !== listAdapter){
            recyclerView.adapter = listAdapter
        }else if (!state.isListView && recyclerView.adapter !== tableAdapter) {
            recyclerView.adapter = tableAdapter
        }

        listAdapter.submitList(state.recipes)
        tableAdapter.submitList(state.recipes)

        emptyText.visibility = if (state.recipes.isEmpty() && !state.isLoading)
            View.VISIBLE else View.GONE

        state.navigateToDetail?.let { recipeId ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
                .putExtra(EXTRA_RECIPE_ID, recipeId)
            startActivity(intent)
            viewModel.onNavigatedToDetail()
        }

        if(state.navigateToAdd) {
            //open a dialog first to let the user pick DISH or DRINK
            showAddRecipeTypeDialog()
            viewModel.onNavigatedToAdd()
        }

        //update toggle menu item icon/title if menu is inflated
        invalidateOptionsMenu()
    }

    private fun showAddRecipeTypeDialog(){
        val recipeTypes = arrayOf("Dish", "Drink")
        AlertDialog.Builder(this)
            .setTitle("What type of recipe?")
            .setItems(recipeTypes) { _, which ->
                val recipeTypeName = if (which == 0) "DISH" else "DRINK"
                val intent = Intent(this, EditRecipeActivity::class.java)
                    .putExtra(EditRecipeActivity.EXTRA_RECIPE_ID, -1L)
                    .putExtra(EditRecipeActivity.EXTRA_RECIPE_TYPE, recipeTypeName)
                startActivity(intent)
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipe_list, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val toggleItem = menu.findItem(R.id.action_toggle_view)
        val isListView = viewModel.uiState.value.isListView
        toggleItem?.title = if (isListView) "Table View" else "List View"
        toggleItem?.setIcon(if (isListView) R.drawable.ic_table else R.drawable.ic_list)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_view -> {
                viewModel.processIntent(RecipeListIntent.ToggleViewMode)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"
    }
}