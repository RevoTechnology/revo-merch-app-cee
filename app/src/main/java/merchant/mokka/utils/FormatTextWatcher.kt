package merchant.mokka.utils

import android.widget.EditText
import merchant.mokka.utils.decoro.FormattedTextChangeListener
import merchant.mokka.utils.decoro.Mask
import merchant.mokka.utils.decoro.MaskImpl
import merchant.mokka.utils.decoro.parser.UnderscoreDigitSlotsParser
import merchant.mokka.utils.decoro.slots.Slot
import merchant.mokka.utils.decoro.watchers.FormatWatcher
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher

class FormatTextWatcher(
        private val textChanged: (text: String) -> Unit
) : FormattedTextChangeListener {

    override fun beforeFormatting(oldValue: String?, newValue: String?) = false

    override fun onTextFormatted(formatter: FormatWatcher?, newFormattedText: String?) {
        textChanged.invoke(newFormattedText.orEmpty())
    }

    override fun onTextChanged(count: Int) {}
}

fun addMask(slots: Array<Slot>, editor: EditText, showEmpty: Boolean) : MaskFormatWatcher {
    val mask = MaskImpl.createTerminated(slots)
    mask.isHideHardcodedHead = true
    mask.isShowingEmptySlots = showEmpty
    val formatWatcher = MaskFormatWatcher(mask)
    formatWatcher.installOn(editor)
    return formatWatcher
}

fun createPhoneMaskFormatWatcher(editor: EditText, phoneMask: String) : MaskFormatWatcher {
    val slots = UnderscoreDigitSlotsParser().parseSlots(phoneMask)
    return addMask(slots, editor, false)
}

fun Mask.isValid() : Boolean {
    val acceptableSymbols = "0123456789"
    val text = toUnformattedString()
    forEachIndexed { index, slot ->
        val textChar = text.getOrNull(index)
        when (slot.value) {
            null -> if (textChar == null || textChar !in acceptableSymbols) return false
            else -> if (slot.value != textChar) return false
        }
    }
    return true
}
