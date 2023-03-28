package merchant.mokka.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AspectRatioImageView : AppCompatImageView {

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 4.37f
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newWidth = measuredWidth
        val newHeight = (newWidth / DEFAULT_ASPECT_RATIO).toInt()
        setMeasuredDimension(newWidth, newHeight)
    }
}