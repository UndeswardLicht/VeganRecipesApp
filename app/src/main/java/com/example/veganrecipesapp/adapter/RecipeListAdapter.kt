package com.example.veganrecipesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.veganrecipesapp.R
import com.example.veganrecipesapp.data.entity.RecipeType
import com.example.veganrecipesapp.data.entity.RecipeWithIngredients

class RecipeListAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onDelete: (Long) -> Unit
) : ListAdapter<RecipeWithIngredients, RecipeListAdapter.RecipeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.text_recipe_title)
        private val typeChip: TextView = itemView.findViewById(R.id.text_recipe_type_chip)
        private val descText: TextView = itemView.findViewById(R.id.text_recipe_desc)
        private val metaText: TextView = itemView.findViewById(R.id.text_recipe_meta)
        private val deleteBtn: Button = itemView.findViewById(R.id.button_delete_recipe)

        fun bind(item: RecipeWithIngredients) {
            val recipe = item.recipe
            titleText.text = recipe.title
            descText.text = recipe.description
            metaText.text = "${item.ingredients.size} ingredients · ${recipe.prepTimeMinutes} min · serves ${recipe.servings}"

            // Show a colored chip for DISH vs DRINK
            typeChip.text = recipe.type
            val chipColor = if (recipe.type == RecipeType.DISH.name)
                itemView.context.getColor(R.color.chip_dish)
            else
                itemView.context.getColor(R.color.chip_drink)
            typeChip.setBackgroundColor(chipColor)

            itemView.setOnClickListener { onItemClick(recipe.id) }
            deleteBtn.setOnClickListener { onDelete(recipe.id) }
        }
    }

    // DiffCallback tells ListAdapter how to compare items. It needs two checks:
    // 1. areItemsTheSame: do these two items represent the same entity? (compare IDs)
    // 2. areContentsTheSame: is everything about them identical? (compare full data)
    class DiffCallback : DiffUtil.ItemCallback<RecipeWithIngredients>() {
        override fun areItemsTheSame(
            oldItem: RecipeWithIngredients, newItem: RecipeWithIngredients
        ) = oldItem.recipe.id == newItem.recipe.id

        override fun areContentsTheSame(
            oldItem: RecipeWithIngredients, newItem: RecipeWithIngredients
        ) = oldItem == newItem
    }
}
