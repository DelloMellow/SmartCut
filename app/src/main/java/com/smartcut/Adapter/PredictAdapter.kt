package com.smartcut.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.smartcut.Response.PredictData
import com.smartcut.databinding.ItemRecommendationBinding
import com.squareup.picasso.Picasso

class PredictAdapter(
    private val listData: PredictData?
) : RecyclerView.Adapter<PredictAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.ivResult
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(listData?.reccomendations!![position]).into(holder.imageView)
    }

    override fun getItemCount(): Int = listData?.reccomendations?.size!!
}