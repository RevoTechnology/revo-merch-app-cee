package pl.revo.merchant.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.widget_keyboard.view.*
import pl.revo.merchant.R
import pl.revo.merchant.utils.decoro.watchers.FormatWatcher

class KeyboardWidget : LinearLayout {
    private lateinit var view: View
    private lateinit var inputView: TextView
    private lateinit var watcher: FormatWatcher
    private lateinit var pinsView: LinearLayout
    private var isPinAttached: Boolean = false
    private lateinit var nextListener: OnNextListener
    private var backListener: OnBackListener? = null
    private var pinAppendListener: OnPinAppendListener? = null

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
        view = LayoutInflater.from(context).inflate(R.layout.widget_keyboard, this, true)

        with(view) {
            keyboardOne.setOnClickListener { append("1") }
            keyboardTwo.setOnClickListener { append("2") }
            keyboardThree.setOnClickListener { append("3") }
            keyboardFour.setOnClickListener { append("4") }
            keyboardFive.setOnClickListener { append("5") }
            keyboardSix.setOnClickListener { append("6") }
            keyboardSeven.setOnClickListener { append("7") }
            keyboardEight.setOnClickListener { append("8") }
            keyboardNine.setOnClickListener { append("9") }
            keyboardZero.setOnClickListener { append("0") }
            keyboardClear.setOnClickListener { clear() }
            keyboardDelete.setOnClickListener { delete() }
        }

    }

    fun setInput(inputView: TextView) {
        this.inputView = inputView
    }

    fun setWatcher(watcher: FormatWatcher) {
        this.watcher = watcher
    }

    fun setPins(pinsView: LinearLayout) {
        this.pinsView = pinsView
        this.isPinAttached = true
    }

    private fun append(number: String) {
        if (isPinAttached) {
            pinAppendListener?.onAppend()
            appendPin(number)
        } else {
            inputView.append(number)
        }
    }

    private fun appendPin(number: String) {
        for (i in 0 until pinsView.childCount) {
            val pinControl: PinWidget = pinsView.getChildAt(i) as PinWidget
            if (!pinControl.isFilled()) {
                pinControl.setState(PinState.FILLED)
                pinControl.tag = number

                break
            }
        }
        if ((pinsView.getChildAt(3) as PinWidget).isFilled()) {
            nextListener.next()
        }
    }

    fun clear() {
        if (isPinAttached) {
            clearPin()
        } else {
            inputView.text = ""
        }
    }

    private fun clearPin() {
        (pinsView.childCount - 1 downTo 0)
                .map { pinsView.getChildAt(it) as PinWidget }
                .forEach { it.setState(PinState.EMPTY) }
        return
    }

    private fun delete() {
        if (isPinAttached) {
            deletePin()
        } else if (getValue().isNotEmpty()) {
            deleteNumber()
        }
    }

    private fun deletePin() {
        if (!(pinsView.getChildAt(0) as PinWidget).isFilled()) {
            backListener?.back()
            return
        }

        for (i in pinsView.childCount - 1 downTo 0) {
            val pinControl: PinWidget = pinsView.getChildAt(i) as PinWidget
            if (pinControl.isFilled()) {
                pinControl.setState(PinState.EMPTY)
                pinControl.tag = ""
                break
            }
        }
    }

    private fun deleteNumber() {
        var phone: String = getValue()
        phone = phone.substring(0, getValue().length - 1)
        inputView.text = phone
    }

    fun getValue(): String {
        if (isPinAttached) {
            var pin = ""
            for (i in 0 until pinsView.childCount) {
                pin += pinsView.getChildAt(i).tag ?: ""
            }
            return pin
        } else if (watcher.isInstalled) {
            return getUnformattedNumber()
        }
        return inputView.text.toString()
    }

    private fun getUnformattedNumber(): String {
        return watcher.mask.toUnformattedString()
                .replace(" ", "")
                .replace("-", "")
    }

    fun setNextListener(listener: OnNextListener) {
        this.nextListener = listener
    }

    fun setBackListener(listener: OnBackListener) {
        this.backListener = listener
    }

    fun setPinAppendListener(listener: OnPinAppendListener) {
        pinAppendListener = listener
    }

    interface OnNextListener {
        fun next()
    }

    interface OnBackListener {
        fun back()
    }

    interface OnPinAppendListener {
        fun onAppend()
    }
}