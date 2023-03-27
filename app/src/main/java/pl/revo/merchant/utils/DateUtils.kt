package pl.revo.merchant.utils

import java.util.*

val Date.calendar: Calendar
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar
    }

fun now(): Date = Calendar.getInstance().time

fun Date.with(
        year: Int = -1,
        month: Int = -1,
        day: Int = -1,
        hour: Int = -1,
        minute: Int = -1,
        second: Int = -1
): Date {
    val cal = this.calendar
    if (year > -1) cal.set(Calendar.YEAR, year)
    if (month > 0) cal.set(Calendar.MONTH, month - 1)
    if (day > 0) cal.set(Calendar.DATE, day)
    if (hour > -1) cal.set(Calendar.HOUR_OF_DAY, hour)
    if (minute > -1) cal.set(Calendar.MINUTE, minute)
    if (second > -1) cal.set(Calendar.SECOND, second)
    return cal.time
}

fun Date.beginOfMonth() : Date {
    return with(day = 1, hour = 0, minute = 0, second = 0)
}

fun Date.endOfMonth() : Date {
    val lastDay = this.calendar.getActualMaximum(Calendar.DATE)
    return with(day = lastDay, hour = 23, minute = 59, second = 59)
}

fun Date.addMonth(count: Int) : Date {
    val cal = this.calendar
    cal.add(Calendar.MONTH, count)
    return cal.time
}

fun Date.addDay(count: Int) : Date {
    val cal = this.calendar
    cal.add(Calendar.DATE, count)
    return cal.time
}

val Date.age: Int
    get() = this.diffTo(now())

fun Date.diffTo(date: Date): Int {
    val a = this.calendar
    val b = date.calendar

    var diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR)

    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)) {
        diff--
    }

    return diff
}