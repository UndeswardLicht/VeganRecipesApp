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

// This adapter uses the same data type as RecipeListAdapter but renders
// a compact row layout suitable for a table-style view. Having two adapters
// lets the Activity swap them without any conditional logic in either.

class RecipeTableAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onDelete: (Long) -> Unit
) : ListAdapter<RecipeWithIngredients, RecipeTableAdapter.TableRowViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableRowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_row, parent, false)
        return TableRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableRowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TableRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.text_row_title)
        private val typeText: TextView = itemView.findViewById(R.id.text_row_type)
        private val timeText: TextView = itemView.findViewById(R.id.text_row_time)
        private val servingsText: TextView = itemView.findViewById(R.id.text_row_servings)
        private val deleteBtn: Button = itemView.findViewById(R.id.button_row_delete)

        fun bind(item: RecipeWithIngredients) {
            val r = item.recipe
            titleText.text = r.title
            typeText.text = if (r.type == RecipeType.DISH.name) "🍽" else "🥤"
            timeText.text = "${r.prepTimeMinutes}m"
            servingsText.text = "×${r.servings}"
            itemView.setOnClickListener { onItemClick(r.id) }
            deleteBtn.setOnClickListener { onDelete(r.id) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RecipeWithIngredients>() {
        override fun areItemsTheSame(old: RecipeWithIngredients, new: RecipeWithIngredients) =
            old.recipe.id == new.recipe.id
        override fun areContentsTheSame(old: RecipeWithIngredients, new: RecipeWithIngredients) =
            old == new
    }
}
