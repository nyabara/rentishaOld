package com.example.rentisha.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.example.rentisha.R
import kotlinx.android.synthetic.main.image_selector.view.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ImageSelector @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null,
defStyle: Int = 0, defRes: Int = 0): LinearLayout(context, attributeSet,defStyle,defRes){

    private var listOfColors = listOf(Color.BLUE,Color.RED,Color.GREEN)
    private var selectedColorIndex = 0
    init {
        orientation = LinearLayout.HORIZONTAL
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.image_selector,this)
        selectedImage.setBackgroundColor(listOfColors[selectedColorIndex])
        imageSelectorArrowLeft.setOnClickListener {
            selectPreviousImage()
        }
        imageSelectorArrowRight.setOnClickListener {
            selectNextImage()
        }
    }

    private fun selectNextImage() {
        if (selectedColorIndex == listOfColors.lastIndex){
            selectedColorIndex = 0
        }else{
            selectedColorIndex++
        }
        selectedImage.setBackgroundColor(listOfColors[selectedColorIndex])
    }

    private fun selectPreviousImage() {
        if (selectedColorIndex == 0){
            selectedColorIndex = listOfColors.lastIndex
        }else{
            selectedColorIndex--
        }
        selectedImage.setBackgroundColor(listOfColors[selectedColorIndex])
    }
}