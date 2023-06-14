package com.smartcut.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.smartcut.databinding.ItemHotNowBinding
import com.smartcut.ui.HairStyleDetailActivity
import com.squareup.picasso.Picasso

class HairStyleAdapter(
    private val listId: ArrayList<Int?>,
    private val listImage: ArrayList<String?>,
    private val listName: ArrayList<String?>,
    private val listStyle: ArrayList<String?>,
    private val description: ArrayList<String>,
    private val context: Context
) : RecyclerView.Adapter<HairStyleAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemHotNowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardView: CardView = binding.cardView
        val imageView: ImageView = binding.ivHotnow
        val nameTextView: TextView = binding.tvHairName
        val styleTextView: TextView = binding.tvHairStyle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHotNowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get().load(listImage[position]).into(holder.imageView)
        holder.nameTextView.text = listName[position]
        holder.styleTextView.text = listStyle[position]
        holder.cardView.setOnClickListener {
            val moveWithDataIntent = Intent(context, HairStyleDetailActivity::class.java)
            moveWithDataIntent.putExtra(HairStyleDetailActivity.EXTRA_ID, listId[position])
            moveWithDataIntent.putExtra(
                HairStyleDetailActivity.EXTRA_PHOTO_URL,
                listImage[position]
            )
            moveWithDataIntent.putExtra(HairStyleDetailActivity.EXTRA_NAME, listName[position])
            moveWithDataIntent.putExtra(HairStyleDetailActivity.EXTRA_STYLE, listStyle[position])
            moveWithDataIntent.putExtra(
                HairStyleDetailActivity.EXTRA_DESCRIPTION,
                description[position]
            )
            context.startActivity(moveWithDataIntent)
        }
    }

    override fun getItemCount(): Int = listId.size

}