package pl.revo.merchant.api.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import pl.revo.merchant.utils.DateFormats
import pl.revo.merchant.utils.toDate
import pl.revo.merchant.utils.toText
import java.util.*

class DateAdapter {
    @FromJson
    fun dateFromJson(value: String): Date? {
        var date = try{
            value.toDate(DateFormats.LONG_SERVER_FORMAT)
        } catch (e: Exception) {
            null
        }
        if (date == null)
            date = try {
                value.toDate(DateFormats.SERVER_FORMAT)
            } catch (e:Exception) {
                null
            }
        return date
    }

    @ToJson
    fun dateToJson(date: Date): String = date.toText(DateFormats.SERVER_FORMAT)
}