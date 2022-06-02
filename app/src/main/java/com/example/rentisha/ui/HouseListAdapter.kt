package com.example.rentisha.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.rentisha.databinding.ListItemBinding
import com.example.rentisha.domain.House

class HouseListAdapter(val clickListener:HouseListener):
    ListAdapter<House, HouseListAdapter.HouseViewHolder>(DiffCallback) {

    class HouseViewHolder(val binding:ListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: HouseListener,house: House){
            binding.house= house
            binding.clickListener= clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HouseViewHolder(
            ListItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = getItem(position)
        holder.bind(clickListener, house)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<House>() {

        override fun areItemsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem == newItem
        }

    }
}


class HouseListener(val clickListener: (house: House) ->Unit) {
    fun onClick(house: House) = clickListener(house)
}
