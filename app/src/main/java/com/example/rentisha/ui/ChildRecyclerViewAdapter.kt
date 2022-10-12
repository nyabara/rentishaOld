package com.example.rentisha.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rentisha.databinding.ListImageBinding
import com.example.rentisha.database.DatabaseImage

class ChildRecyclerViewAdapter : ListAdapter<DatabaseImage, ChildRecyclerViewAdapter.ChildViewHolder>(
    DiffCallback) {

    class ChildViewHolder(val binding:ListImageBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(databaseImage: DatabaseImage){
            binding.image = databaseImage
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChildViewHolder(ListImageBinding.inflate(layoutInflater,parent,false))
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val image = getItem(position)
        if (image != null) {
            holder.bind(image)
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<DatabaseImage>() {
        override fun areItemsTheSame(oldItem: DatabaseImage, newItem: DatabaseImage): Boolean {
            return oldItem.house_id == newItem.house_id
        }

        override fun areContentsTheSame(oldItem: DatabaseImage, newItem: DatabaseImage): Boolean {
            return oldItem.house_id == newItem.house_id
        }
    }
}