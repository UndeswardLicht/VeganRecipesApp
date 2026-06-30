package com.example.veganrecipesapp.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.veganrecipesapp.data.entity.RecipeWithIngredients

class RecipeListAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onDelete: (Long) -> Unit
) : ListAdapter<RecipeWithIngredients, RecipeListAdapter.RecipeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): RecipeViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        p0: RecipeViewHolder,
        p1: Int
    ) {
        TODO("Not yet implemented")
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    class DiffCallback : DiffUtil.ItemCallback<RecipeWithIngredients>() {
        override fun areItemsTheSame(
            p0: RecipeWithIngredients,
            p1: RecipeWithIngredients
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(
            p0: RecipeWithIngredients,
            p1: RecipeWithIngredients
        ): Boolean {
            TODO("Not yet implemented")
        }
    }
}