package com.dicoding.storiin.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.storiin.databinding.ItemDetailsBinding
import com.dicoding.storiin.response.Story
import com.dicoding.storiin.ui.detail.DetailActivity

class StoryAdapter: PagingDataAdapter<Story, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

    class MyViewHolder(private val binding: ItemDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Story) {
            binding.apply {
                Glide.with((itemView))
                    .load(user.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(imgDetailPhoto)
                tvItemName.text = user.name
                deskripsi.text = user.description
            }
            itemView.setOnClickListener{
                val intentDetail = Intent(itemView.context, DetailActivity:: class.java)
                intentDetail.putExtra("id_user", user.id)
                itemView.context.startActivity(intentDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder((view))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = getItem(position)
        if (list != null) {
            holder.bind(list)
        }
    }


    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>(){
            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}