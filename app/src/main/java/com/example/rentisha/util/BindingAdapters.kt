package com.example.rentisha.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.rentisha.R
import com.example.rentisha.database.DatabaseImage
import com.example.rentisha.ui.ChildRecyclerViewAdapter
import com.example.rentisha.viewmodels.RentishaApiStatus


/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "houselist")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, playlist: Any?) {
    view.visibility = if (playlist != null) View.GONE else View.VISIBLE

    if(isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).into(imageView)
}

@BindingAdapter("listImage")
fun bindRecyclerViewChild(recyclerView: RecyclerView,
                     data: List<DatabaseImage>?) {

    val adapter = recyclerView.adapter as ChildRecyclerViewAdapter
    adapter.submitList(data)
    Log.d("dataiamges",data.toString())
}

//@BindingAdapter("imageUrl")
//fun bindImage(imgView: ImageView, imgUrl: String?) {
//    imgUrl?.let {
//        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
//        imgView.load(imgUri) {
//            placeholder(R.drawable.loading_animation)
//            error(R.drawable.ic_broken_image)
//
//        }
//    }
//
//}

//@BindingAdapter("listData")
//suspend fun bindRecyclerView(recyclerView: RecyclerView,
//                             data: Flow<PagingData<DatabaseHouse>>?) {
//    val adapter = recyclerView.adapter as HouseListAdapter
//    if (data != null) {
//        data.collect{
//            adapter.submitData(it)
//        }
//    }
//
//}


//use a Binding adapter for an ImageView, to display icons for the loading and error states
@BindingAdapter("rentishaApiStatus")
fun bindStatus(statusImageView: ImageView,
               status: RentishaApiStatus?) {
    when (status) {
        RentishaApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        RentishaApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        RentishaApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

