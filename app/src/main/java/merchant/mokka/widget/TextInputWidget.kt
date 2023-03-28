package merchant.mokka.widget

import android.content.Context
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import merchant.mokka.R
import merchant.mokka.utils.SimpleTextWatcher

class TextInputWidget : TextInputLayout {

    private lateinit var editText: TextInputEditText
    private lateinit var textChangeListener: TextWatcher
    private lateinit var errorText: String

    private var validState = false
    private var onChangedState: ((isValid: Boolean) -> Unit)? = null
    private var validator: ((text: String) -> Boolean)? = null

    var text : String
        get() {
            return editText.text.toString()
        }
        set(value) {
            editText.setText(value)
        }

    val editor : TextInputEditText
        get() {
            return editText
        }

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

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_text_input, this, true)
        editText = view.findViewById(R.id.editText)

        textChangeListener = SimpleTextWatcher {
            val valid = isValid(it.toString())
            error = if (valid) "" else errorText
            if (validState != valid) {
                validState = valid
                onChangedState?.invoke(valid)
            }
        }
    }

    private fun isValid(text: String) : Boolean {
        return if (validator != null) validator?.invoke(text) == true else true
    }

    private fun setAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextInputWidget)
        try {
            isPasswordVisibilityToggleEnabled =
                    typedArray.getBoolean(R.styleable.TextInputWidget_passwordToggleEnabled, false)

            val hintResId = typedArray.getResourceId(R.styleable.TextInputWidget_android_hint, 0)
            if (hintResId > 0)
                editText.setHint(hintResId)
            else
                editText.hint = ""

            editText.inputType =
                    typedArray.getInt(R.styleable.TextInputWidget_android_inputType, EditorInfo.TYPE_NULL)

            editText.imeOptions =
                    typedArray.getInt(R.styleable.TextInputWidget_android_imeOptions, 0)

            editText.setTextColor(typedArray.getInt(R.styleable.TextInputWidget_android_textColor, 0))

            val maxLength = typedArray.getInt(R.styleable.TextInputWidget_android_maxLength, 0)
            if (maxLength > 0)
                editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            else
                editText.filters = arrayOf()

        } finally {
            typedArray.recycle()
        }
    }

    fun isValid() : Boolean {
        return validState
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        editText.addTextChangedListener(textChangeListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        editText.removeTextChangedListener(textChangeListener)
    }

    fun setOnChangedState(onChangedState: ((isValid: Boolean) -> Unit)? = null) {
        this.onChangedState = onChangedState
    }

    fun setValidator(validator: ((text: String) -> Boolean)? = null, errorTextRes: Int?) {
        this.validator = validator
        errorText = if (errorTextRes != null && errorTextRes > 0) context.getString(errorTextRes) else ""
    }
}