package pl.revo.merchant.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.widget_pin.view.*
import pl.revo.merchant.R

class PinWidget : RelativeLayout {
    private var state: PinState = PinState.EMPTY

    private var pinEmpty: Drawable? = null
    private var pinFilled: Drawable? = null
    private var pinError: Drawable? = null
    private var pinValid: Drawable? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context?) {
        LayoutInflater.from(context).inflate(R.layout.widget_pin, this, true)
        context?.let {
            pinEmpty = ContextCompat.getDrawable(it, R.drawable.pin_empty)
            pinFilled = ContextCompat.getDrawable(it, R.drawable.pin_filled)
            pinError = ContextCompat.getDrawable(it, R.drawable.pin_error)
            pinValid = ContextCompat.getDrawable(it, R.drawable.pin_valid)
        }
    }

    fun setState(value: PinState) {
        state = value
        pinControl.setImageDrawable(
                when(state) {
                    PinState.EMPTY -> pinEmpty
                    PinState.FILLED -> pinFilled
                    PinState.ERROR -> pinError
                    PinState.VALID -> pinValid
                }
        )
    }

    fun isFilled() = state == PinState.FILLED
}

enum class PinState{
    EMPTY, FILLED, ERROR, VALID
}