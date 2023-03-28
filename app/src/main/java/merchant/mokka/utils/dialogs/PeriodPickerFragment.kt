package merchant.mokka.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import merchant.mokka.R

class PeriodPickerFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var sublimePicker: SublimePicker
    private var callback: ((selectedDate: SelectedDate?) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sublimePicker = requireActivity().layoutInflater
                .inflate(R.layout.dialog_period_picker, container) as SublimePicker

        val options = SublimeOptions()
        options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER)
        options.setCanPickDateRange(true)

        val listener: SublimeListenerAdapter = object : SublimeListenerAdapter() {
            override fun onDateTimeRecurrenceSet(
                    sublimeMaterialPicker: SublimePicker?,
                    selectedDate: SelectedDate?,
                    hourOfDay: Int,
                    minute: Int,
                    recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?,
                    recurrenceRule: String?
            ) {
                callback?.invoke(selectedDate)
                dismiss()
            }

            override fun onCancelled() {
                dismiss()
            }
        }

        sublimePicker.initializePicker(options, listener)
        return sublimePicker
    }

    fun setCallback(onSelect: (selectedDate: SelectedDate?) -> Unit) {
        this.callback = onSelect
    }
}