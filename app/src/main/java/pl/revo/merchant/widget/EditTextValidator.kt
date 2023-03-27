package pl.revo.merchant.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pl.revo.merchant.R

class EditTextValidator(
        private val onChangedState: ((isValid: Boolean) -> Unit),
        private val validator: ((text: String) -> Boolean),
        private val errorText: String? = null,
        private val errorRequired: String? = null
) : TextWatcher {

    private var validState = true
    private var inputLayout: TextInputLayout? = null
    private var editText: EditText? = null

    override fun afterTextChanged(s: Editable?) { }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        validate(s.toString())
        if (errorRequired != null && s.toString().isEmpty()) {
            inputLayout?.error = errorRequired
        } else {
            inputLayout?.error = if (isValid()) "" else errorText
        }
        editText?.let {
            it.background = ContextCompat.getDrawable(it.context,
                    if (isValid()) R.drawable.edit_bg_selector else R.drawable.edit_error_selector
            )
        }
    }

    private fun isValid(text: String) : Boolean {
        return validator.invoke(text)
    }

    fun setInputLayout(inputLayout: TextInputLayout?) {
        this.inputLayout = inputLayout
    }

    fun setEditText(editText: EditText?) {
        this.editText = editText
    }

    fun isValid() : Boolean {
        return validState
    }

    fun validate(value: String) {
        val valid = isValid(value)

        if (validState != valid) {
            validState = valid
            onChangedState.invoke(valid)
        }
    }

    fun check() {
        val text = editText?.text.toString()
        validate(text)
    }
}

fun TextInputEditText.attachValidator(validator: EditTextValidator, inputLayout: TextInputLayout?) {
    validator.setInputLayout(inputLayout)
    validator.setEditText(this)
    validator.validate(this.text.toString())
    this.addTextChangedListener(validator)
}

fun TextInputEditText.detachValidator(validator: EditTextValidator) {
    validator.setInputLayout(null)
    validator.setEditText(null)
    this.removeTextChangedListener(validator)
}

fun AutoCompleteTextView.attachValidator(validator: EditTextValidator, inputLayout: TextInputLayout?) {
    validator.setInputLayout(inputLayout)
    validator.setEditText(this)
    validator.validate(this.text.toString())
    this.addTextChangedListener(validator)
}

fun AutoCompleteTextView.detachValidator(validator: EditTextValidator) {
    validator.setInputLayout(null)
    validator.setEditText(null)
    this.removeTextChangedListener(validator)
}