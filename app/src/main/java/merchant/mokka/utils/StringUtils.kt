package merchant.mokka.utils

import android.util.Patterns
import merchant.mokka.BuildConfig
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {
    var sumFormat: DecimalFormat
    var sumWithCentFormat: DecimalFormat
    var serverFormat: DecimalFormat
    val decimalSeparator = ','

    init {
        val textDfs = DecimalFormatSymbols(Locale.getDefault())
        textDfs.decimalSeparator = decimalSeparator
        textDfs.groupingSeparator = ' '
        sumFormat = DecimalFormat("###,###,###.##", textDfs)
        sumFormat.minimumIntegerDigits = 1

        sumWithCentFormat = DecimalFormat("###,###,##0.00", textDfs)

        val serverDfs = DecimalFormatSymbols(Locale.getDefault())
        serverDfs.decimalSeparator = '.'
        serverFormat = DecimalFormat("0.00", serverDfs)
    }
}

enum class DateFormats(val pattern: String) {
    SIMPLE_FORMAT("dd.MM.yyyy"),
    MONTH_FORMAT("d MMMM"),
    TIMER_FORMAT("mm:ss"),
    SERVER_FORMAT("dd-MM-yyyy"),
    LONG_SERVER_FORMAT("yyyy-MM-dd'T'HH:mm:ssXXX")
}

val DateFormats.formatter: SimpleDateFormat
    get() = SimpleDateFormat(this.pattern, Locale.US)

fun Date.toText(format: DateFormats): String {
    return format.formatter.format(this)
}

fun String.toDate(format: DateFormats): Date? {
    return try {
        format.formatter.isLenient = false
        format.formatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun Date.toDayMonth(): String = this.toText(DateFormats.MONTH_FORMAT)

fun Long?.toTimerText(): String {
    return if (this == null) {
        ""
    } else {
        val date = Date(this)
        date.toText(DateFormats.TIMER_FORMAT)
    }
}

fun Double?.toTextWithCent(): String {
    return if (this == null)
        ""
    else {
        val fractional = this.minus(this.toInt())
        val res = if (fractional == 0.0) StringUtils.sumFormat.format(this)
        else StringUtils.sumWithCentFormat.format(this)
        return res + " " + Constants.CURRENCY
    }
}

fun Double?.toTextWithCurrency() = when {
    this == null -> ""
//        else -> StringUtils.sumFormat.format(this) + " " + Constants.CURRENCY
    else -> DecimalFormat("0.00").format(this) + " " + Constants.CURRENCY
}

fun Double.toText(): String {
    return StringUtils.sumFormat.format(this)
}

fun Double.toServerFormat(): String {
    return StringUtils.serverFormat.format(this)
}

fun String?.parse(): Double {
    return if (this.isNullOrEmpty()) 0.0 else {
        val text = this
                .replace(Constants.CURRENCY, "")
                .replace(" ", "")
                .replace(",", StringUtils.decimalSeparator.toString())
                .replace(".", StringUtils.decimalSeparator.toString())
        StringUtils.sumFormat.parse(text).toDouble()
    }
}

fun String.clearPhone() = replace("[^\\d.]".toRegex(), "")
fun String?.isEmail() = this?.let { Patterns.EMAIL_ADDRESS.matcher(this).matches() } ?: false

fun Boolean?.toText(): String {
    return if (this == true) "1" else "0"
}

fun String.polishPeselToBirthDay(): Date? {
    try {
        val birthday = this.substring(0, 6)

        var year = birthday.substring(0, 2).toInt()
        var month = birthday.substring(2, 4).toInt()
        val day = birthday.substring(4, 6).toInt()

        when (month) {
            in (1..12) -> {
                year += 1900
            }
            in (21..32) -> {
                year += 2000
                month -= 20
            }
            in (41..52) -> {
                year += 2100
                month -= 40
            }
            in (61..72) -> {
                year += 2200
                month -= 60
            }
            in (81..92) -> {
                year += 1800
                month -= 80
            }
            else -> {
                return null
            }
        }

        return "$day.$month.$year".toDate(DateFormats.SIMPLE_FORMAT)
    } catch (e: Exception) {
        return null
    }
}

fun String.toFirstUpperCase(): String {
    return this.substring(0, 1).toUpperCase() + this.substring(1)
}

fun String?.toCheckInt(): Int? {
    return when {
        this == null -> null
        this.isEmpty() -> null
        else -> this.toInt()
    }
}

private fun String.toVersionCode(): Int {
    val data = split('.').map { it.trim().toInt() }
    if (data.size != 3) return -1

    return data[0] * 1000 + data[1] * 10 + data[2]
}

fun String.isNewVersion(version: String = BuildConfig.VERSION_NAME) = toVersionCode() > version.toVersionCode()
fun String?.barCodeFormat(): String {
    return if (this == null) ""
    else {
        val barCode = StringBuilder()
        (indices).forEach { i ->
            barCode.append(this[i])
            if ((i + 1) % 4 == 0) barCode.append(" ")
        }
        barCode.toString()
    }
}