package merchant.mokka.widget

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import merchant.mokka.utils.*


class EditSumValidator(
        private val onChangedState: ((isValid: Boolean) -> Unit),
        private val validator: ((value: Double) -> Boolean),
        private val errorText: String?
) : TextWatcher {

    private var validState = false
    private var editor: EditText? = null
    private var inputLayout: TextInputLayout? = null
    private var sumView: TextView? = null

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var text = s.toString()
                .replace(",", StringUtils.decimalSeparator.toString())
                .replace(".", StringUtils.decimalSeparator.toString())

        if (text.isEmpty()) {
            inputLayout?.hint = ""
        } else {
            inputLayout?.hint = editor?.hint
        }

        val length = text.length
        if (text.isNotEmpty() && text[text.length - 1] == StringUtils.decimalSeparator) {
            sumView?.let {
                text = text + " " + Constants.CURRENCY
                it.text = text
            }
        } else {
            val sum = text.parse()
            sumView?.let { it.text = sum.toTextWithCurrency() }
            editor?.let {
                it.removeTextChangedListener(this)
                // due to https://revoplus.atlassian.net/browse/BB-1054
                // it.setText(sum.toText())
                it.setSelection(length)
                it.addTextChangedListener(this)
            }
            validate(sum)
            if (errorText != null) inputLayout?.error = if (isValid()) "" else errorText
        }
        sumView?.isVisible = text.isNotBlank()
    }

    private fun isValid(sum: Double): Boolean {
        return validator.invoke(sum)
    }

    fun setEditor(editText: EditText?) {
        this.editor = editText
        editor?.text?.let {
            if(it.toString().parse() == 0.0) editor?.setText("")
        }

    }

    fun setInputLayout(inputLayout: TextInputLayout?) {
        this.inputLayout = inputLayout
    }

    fun setSumView(sumView: TextView?) {
        this.sumView = sumView
    }

    fun isValid(): Boolean {
        return validState
    }

    private fun validate(sum: Double) {
        val valid = isValid(sum)

        if (validState != valid) {
            validState = valid
            onChangedState.invoke(valid)
        }
    }

    fun check() {
        val sum = editor?.text.toString().parse()
        validate(sum)
    }
}

fun EditText.attachSumValidator(
    validator: EditSumValidator,
    inputLayout: TextInputLayout?,
    sumView: TextView
) {
    val maxLengthFilter = InputFilter.LengthFilter(10)
    this.filters = arrayOf(DecimalDigitsInputFilter(2), maxLengthFilter)
    validator.setInputLayout(inputLayout)
    validator.setSumView(sumView)
    validator.setEditor(this)
    this.addTextChangedListener(validator)
}

fun EditText.detachSumValidator(validator: EditSumValidator) {
    validator.setEditor(null)
    validator.setInputLayout(null)
    validator.setSumView(null)
    this.removeTextChangedListener(validator)
}