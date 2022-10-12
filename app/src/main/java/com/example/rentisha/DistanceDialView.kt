package com.example.rentisha

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class DistanceDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var distances: ArrayList<Int> =
        arrayListOf(Color.BLUE, Color.BLACK, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
    private var dialDrawable: Drawable? = null

    private val paint = Paint().also {
        it.color = Color.WHITE
        it.isAntiAlias = true
    }
    private var dialDiameter = toDP(100)
    private var extraPadding = toDP(30)
    private var tickSize = toDP(10).toFloat()
    private var angleBetweenDistances = 0f
    private var scaleToFit = false
    private var scale = 1f
    private var tickSizeScaled = tickSize * scale

    //Pre-computed padding values
    private var totalLeftPadding = 0f
    private var totalTopPadding = 0f
    private var totalRightPadding = 0f
    private var totalBottomPadding = 0f

    //pre-computed helpers
    private var horizontalSize = 0f
    private var verticalSize = 0f

    //pre-computed position values
    private var tickPositionVertical = 0f
    private var centerHorizontal = 0f
    private var centerVertical = 0f


    init {
        // Load attributes
        dialDrawable = context.getDrawable(R.drawable.ic_dial).also {
            it?.bounds = getCenteredBounds(dialDiameter)
            it?.setTint(Color.DKGRAY)
        }
        angleBetweenDistances = 360f / distances.size
        refreshValues()


    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val saveCount: Int = canvas.save()
        distances.forEachIndexed { index, distance ->
            paint.color = distances[index]
            canvas.drawCircle(centerHorizontal,
                tickPositionVertical, tickSize, paint)
            canvas.drawText(distances[index].toString(),centerHorizontal,tickPositionVertical,paint)
            canvas.rotate(angleBetweenDistances,
                centerHorizontal, centerVertical)
        }
        canvas.restoreToCount(saveCount)
        canvas.translate(centerHorizontal, centerVertical)
        dialDrawable?.draw(canvas)
    }

    private fun refreshValues() {

        //Compute padding values
        this.totalLeftPadding = (paddingLeft + extraPadding).toFloat()
        this.totalTopPadding = (paddingTop + extraPadding).toFloat()
        this.totalRightPadding = (paddingRight + extraPadding).toFloat()
        this.totalBottomPadding = (paddingBottom + extraPadding).toFloat()
        //compute helper values
        this.horizontalSize =
            paddingLeft + paddingRight + (extraPadding * 2) + dialDiameter.toFloat()
        this.verticalSize = paddingTop + paddingBottom + (extraPadding * 2) + dialDiameter.toFloat()

        //compute position values
        this.tickPositionVertical = paddingTop + extraPadding / 2f
        this.centerHorizontal =
            totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding) / 2f
        this.centerVertical =
            totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
        //this.tickSizeScaled = tickSize * localScale
    }

    private fun getCenteredBounds(size: Int, scalar: Float = 1f): Rect {

        val half: Int = ((if (size > 0) size / 2 else 1) * scalar).toInt()
        return Rect(-half, -half, half, half)
    }

    private fun toDP(value: Int): Int {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(),
            context.resources.displayMetrics).toInt()

    }
}