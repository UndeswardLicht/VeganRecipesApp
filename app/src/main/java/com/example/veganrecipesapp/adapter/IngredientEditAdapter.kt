package com.example.veganrecipesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.veganrecipesapp.R
import com.example.veganrecipesapp.data.entity.Ingredient

class IngredientEditAdapter(
    private val onRemove: (Ingredient) -> Unit
) : ListAdapter<Ingredient, IngredientEditAdapter.IngredientViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_edit, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.text_ingredient_name)
        private val quantityText: TextView = itemView.findViewById(R.id.text_ingredient_quantity)
        private val removeBtn: ImageButton = itemView.findViewById(R.id.button_remove_ingredient)

        fun bind(ingredient: Ingredient) {
            nameText.text = ingredient.name
            val qty = buildString {
                append(ingredient.quantity)
                if (ingredient.unit.isNotBlank()) append(" ${ingredient.unit}")
            }
            quantityText.text = qty
            removeBtn.setOnClickListener { onRemove(ingredient) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(old: Ingredient, new: Ingredient) = old.id == new.id
        override fun areContentsTheSame(old: Ingredient, new: Ingredient) = old == new
    }
}