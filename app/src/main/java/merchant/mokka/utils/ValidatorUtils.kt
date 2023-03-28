package merchant.mokka.utils

import android.util.Patterns
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*

fun String?.isValidAsEmail(isDemo: Boolean = false) =
        if (isDemo) this?.isNotEmpty() == true
        else Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.getValidPhone(): String? {
    val phoneUtil = PhoneNumberUtil.getInstance()
    val acceptableSymbols = "+0123456789() -"

    this.forEach {
        if (it !in acceptableSymbols)
            return null
    }

    val phone = if (this.contains('+')) this else '+' + this

    return try {
        val numberProto = phoneUtil.parse(phone, null)
        if (phoneUtil.isValidNumber(numberProto)) {
            '+' + numberProto.countryCode.toString() + numberProto.nationalNumber.toString()
        } else {
            null
        }
    } catch (e: NumberParseException) {
        null
    }
}

fun String.isValidAsPhone(isDemo: Boolean = false) = if (isDemo) this.isNotEmpty() else this.getValidPhone() != null

fun String.isValidAsPhoneOrLogin(isDemo: Boolean = false) = this.isValidAsPhone(isDemo) || this.isValidAsEmail(isDemo)

fun String?.isValidSmsCode() = if (this == null) false else this.length == Constants.SMS_CODE_LENGTH

fun String?.isValidPinCode() = if (this == null) false else this.length == Constants.PIN_CODE_LENGTH

fun String.isValidAsPolishPESEL(isDemo: Boolean = false) : Boolean {
    if (isDemo) {
        return this.isNotEmpty()
    }

    val dig = this.trim().toCharArray()
    if (dig.size != 11)
        return false

    val weights = arrayOf(1, 3, 7, 9, 1, 3, 7, 9, 1, 3)
    val sum = (0..9).sumBy { weights[it] * dig[it].toString().toInt() }
    var control = 10 - (sum % 10)
    if (control == 10) {
        control = 0
    }

    var valid = dig[10].toString().toInt() == control

    if (valid) {
        val birthday = this.trim().substring(0, 6)

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
            else -> valid = false
        }

        if (valid) {
            val date = "$day.$month.$year".toDate(DateFormats.SIMPLE_FORMAT)
            valid = date != null && date.age >= Constants.MIN_AGE && date.age <= Constants.MAX_AGE
        }
    }

    return valid
}

fun String.isValidAsPolishID(isDemo: Boolean = false) : Boolean {
    if (isDemo) {
        return this.isNotEmpty()
    }

    try {
        val dig = this.trim().toCharArray()
        if (dig.size != 9)
            return false

        val weights = arrayOf(7, 3, 1, 0, 7, 3, 1, 7, 3)
        val values: HashMap<String, Int> = hashMapOf(
                "A" to 10, "B" to 11, "C" to 12, "D" to 13, "E" to 14, "F" to 15,
                "G" to 16, "H" to 17, "I" to 18, "J" to 19, "K" to 20, "L" to 21, "M" to 22,
                "N" to 23, "O" to 24, "P" to 25, "Q" to 26, "R" to 27, "S" to 28, "T" to 29,
                "U" to 30, "V" to 31, "W" to 32, "X" to 33, "Y" to 34, "Z" to 35
        )

        var sum = (0..2).sumBy { weights[it] * values[dig[it].toString()].orZero() } +
                (4..8).sumBy { weights[it] * dig[it].toString().toInt() }
        while (sum > 10) sum = sum.rem(10)

        return dig[3].toString().toInt() == sum
    } catch (e: NumberFormatException) {
        return false
    }
}

fun String.isValidAsPostalCode(isDemo: Boolean = false) : Boolean {
    if (isDemo) {
        return this.isNotEmpty()
    }

    if (this.length != 6)
        return false

    val acceptableSymbols = "0123456789-"

    this.forEach {
        if (it !in acceptableSymbols)
            return false
    }

    return true
}

fun String.isValidAsDate() : Boolean {
    val date = this.toDate(DateFormats.SIMPLE_FORMAT)
    val equal = date?.toText(DateFormats.SIMPLE_FORMAT)
    return date != null && date <= now() && this == equal
}

fun String.isValidAsBirth() : Boolean {
    val date = this.toDate(DateFormats.SIMPLE_FORMAT)
    val equal = date?.toText(DateFormats.SIMPLE_FORMAT)
    val now = now()
    return date != null && date <= now && date.age >= 18 && date.age < 100 && this == equal
}

fun String.isValidAsFutureDate() : Boolean {
    val date = this.toDate(DateFormats.SIMPLE_FORMAT)
    val equal = date?.toText(DateFormats.SIMPLE_FORMAT)
    return date != null && date > now() && this == equal
}

