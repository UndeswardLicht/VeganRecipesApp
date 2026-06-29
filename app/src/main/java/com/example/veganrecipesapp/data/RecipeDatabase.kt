package com.example.veganrecipesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.veganrecipesapp.data.entity.Ingredient
import com.example.veganrecipesapp.data.entity.Recipe
import com.example.veganrecipesapp.data.entity.RecipeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Recipe::class, Ingredient::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {

    // Room generates the concrete implementation of RecipeDao at compile time
    abstract fun recipeDao(): RecipeDao

    companion object {

        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): RecipeDatabase {
            return Room.databaseBuilder(
                //  applicationContext (and not activity context) to avoid leaking the Activity:
                //  the DB lives as long as the app, which is longer than any single Activity.
                context.applicationContext,
                RecipeDatabase::class.java,
                "recipe_database" //the SQLite db name
            )
                //seed the db with demo recipes on first run
                .addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database.recipeDao())
                            }
                        }
                    }
                })
                .build()
        }

        private suspend fun populateDatabase(dao: RecipeDao) {
            val pastaId = dao.insertRecipe(
                Recipe(title = "Spaghetti Carbonara", description = "Classic Italian pasta with eggs, cheese, and pancetta.",
                    type = RecipeType.DISH.name, servings = 2, prepTimeMinutes = 25)
            )
            dao.insertIngredients(listOf(
                Ingredient(recipeId = pastaId, name = "Spaghetti", quantity = "200", unit = "g"),
                Ingredient(recipeId = pastaId, name = "Pancetta", quantity = "100", unit = "g"),
                Ingredient(recipeId = pastaId, name = "Eggs", quantity = "3", unit = ""),
                Ingredient(recipeId = pastaId, name = "Parmesan", quantity = "50", unit = "g"),
                Ingredient(recipeId = pastaId, name = "Black pepper", quantity = "1", unit = "tsp")
            ))

            val soupId = dao.insertRecipe(
                Recipe(title = "Tomato Soup", description = "Smooth and warming tomato soup with basil.",
                    type = RecipeType.DISH.name, servings = 4, prepTimeMinutes = 30)
            )
            dao.insertIngredients(listOf(
                Ingredient(recipeId = soupId, name = "Canned tomatoes", quantity = "800", unit = "g"),
                Ingredient(recipeId = soupId, name = "Onion", quantity = "1", unit = "large"),
                Ingredient(recipeId = soupId, name = "Garlic cloves", quantity = "3", unit = ""),
                Ingredient(recipeId = soupId, name = "Vegetable stock", quantity = "500", unit = "ml"),
                Ingredient(recipeId = soupId, name = "Fresh basil", quantity = "10", unit = "leaves")
            ))

            val smoothieId = dao.insertRecipe(
                Recipe(title = "Berry Smoothie", description = "Refreshing mixed berry smoothie.",
                    type = RecipeType.DRINK.name, servings = 1, prepTimeMinutes = 5)
            )
            dao.insertIngredients(listOf(
                Ingredient(recipeId = smoothieId, name = "Mixed berries", quantity = "200", unit = "g"),
                Ingredient(recipeId = smoothieId, name = "Banana", quantity = "1", unit = ""),
                Ingredient(recipeId = smoothieId, name = "Greek yoghurt", quantity = "150", unit = "ml"),
                Ingredient(recipeId = smoothieId, name = "Honey", quantity = "1", unit = "tbsp")
            ))

            val coffeeId = dao.insertRecipe(
                Recipe(title = "Dalgona Coffee", description = "Whipped coffee foam over milk.",
                    type = RecipeType.DRINK.name, servings = 1, prepTimeMinutes = 10)
            )
            dao.insertIngredients(listOf(
                Ingredient(recipeId = coffeeId, name = "Instant coffee", quantity = "2", unit = "tbsp"),
                Ingredient(recipeId = coffeeId, name = "Sugar", quantity = "2", unit = "tbsp"),
                Ingredient(recipeId = coffeeId, name = "Hot water", quantity = "2", unit = "tbsp"),
                Ingredient(recipeId = coffeeId, name = "Milk", quantity = "200", unit = "ml")
            ))
        }
    }
}