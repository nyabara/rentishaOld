package com.example.rentisha.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

import com.bumptech.glide.Glide
import com.example.rentisha.R



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

/*@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)

        }
    }

}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<HouseImages>?) {
    val adapter = recyclerView.adapter as HouseListAdapter
    adapter.submitList(data)
}

class BindingAdapters {

}

//use a Binding adapter for an ImageView, to display icons for the loading and error states
@BindingAdapter("houseApiStatus")
fun bindStatus(statusImageView: ImageView,
               status: HouseApiStatus?) {
    when (status) {
        HouseApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        HouseApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        HouseApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
*/
