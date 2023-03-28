package merchant.mokka.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TableLayout
import android.widget.TextView
import merchant.mokka.R

class StepButtonWidget : FrameLayout {

    private lateinit var captionView: TextView
    private lateinit var progressView: View
    private lateinit var digressView: View

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
        setAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
        setAttributes(attrs)
    }

    private fun initView(context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_step_button, this, true)
        with(view) {
            captionView = view.findViewById(R.id.button_caption)
            progressView = view.findViewById(R.id.button_progress)
            digressView = view.findViewById(R.id.button_digress)
        }
    }

    private fun setAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepButtonWidget)
        try {

            val step = typedArray.getInt(R.styleable.StepButtonWidget_step, 0)
            val steps = typedArray.getInt(R.styleable.StepButtonWidget_steps, step)
            val progress = if (steps > 0) step.toFloat() / steps.toFloat() else 0.0f
            val digress = 1.0f - progress

            progressView.layoutParams = TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, digress
            )

            digressView.layoutParams = TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, progress
            )

            captionView.text = typedArray.getString(R.styleable.StepButtonWidget_android_text)
        } finally {
            typedArray.recycle()
        }
    }
}