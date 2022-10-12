package com.example.rentisha.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.rentisha.databinding.ListItemBinding
import com.example.rentisha.database.DatabaseHouse
import com.example.rentisha.database.DatabaseImage
import com.example.rentisha.viewmodels.RentishaViewModel
import com.example.rentisha.viewmodels.UiModel


class HouseListAdapter(val clickListener: HouseListener, val viewmodel:RentishaViewModel):
    PagingDataAdapter<UiModel, HouseListAdapter.HouseViewHolder>(DiffCallback) {

    class HouseViewHolder(val binding:ListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: HouseListener, house: DatabaseHouse, imageList:List<DatabaseImage>){
            binding.house= house
            Log.d("huse",house.toString())
            binding.clickListener= clickListener
            val childRecyclerViewAdapter = ChildRecyclerViewAdapter()
            binding.childRv.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)
            binding.childRv.adapter = childRecyclerViewAdapter
            childRecyclerViewAdapter.submitList(imageList)
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
        val uiModel = getItem(position)


        uiModel.let {
            when(uiModel){

                is UiModel.HouseItem -> {
                    holder
                        .bind(clickListener,uiModel.house,viewmodel.getHouseImages(uiModel.house.houseId))
                    Log.d("uimodel",uiModel.house.toString())

                }

                else -> {
                    Log.d("EmptyData","emptydata")
                }
            }
        }


//
//        if (house != null) {
//            val imagelist =viewmodel.getHouseImages(house.houseId)
//            Log.d("imagelistrec",imagelist.toString())
//            holder.bind(clickListener, house,imagelist)
//
//        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<UiModel>() {

        override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
            return oldItem == newItem
        }

    }
}


class HouseListener(val clickListener: (house: DatabaseHouse) ->Unit) {
    fun onClick(house: DatabaseHouse) = clickListener(house)
}
