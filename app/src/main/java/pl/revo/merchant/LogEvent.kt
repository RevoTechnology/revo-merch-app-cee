package pl.revo.merchant

import android.util.Log
import com.exponea.sdk.Exponea
import com.exponea.sdk.models.PropertiesList
import pl.revo.merchant.utils.getDeviceInfo

enum class Event(val api: String) {
    LOGIN("auth_login"),
    PIN_REQUEST("auth_pin_request"),
    STORE_SELECTION("auth_store_selection"),
    DASHBOARD("dashboard"),
    LIMIT_CHECK("limit_check"),
    SELF_REG_MAIN("selfregistration_main"),
    SELF_REG_SUCCESS("selfregistration_success"),
    SELF_REG_ERROR("selfregistration_error"),
    SELF_REG_REPEAT("selfregistration_repeat"),
    CLIENT_PROFILE("client_profile"),
    SMS_CONFIRM("sms_confirm"),
    LIMIT_SCREEN("limit_screen"),
    LIMIT_REFUSE_SCREEN("limit_refuse_screen"),
    LOAN_CALC("loan_calc"),
    LOAN_AGREE("loan_agree"),
    FINALIZE("finalize"),
    RETURN_MAIN("return_main");
}

fun Event.track(properties: HashMap<String, Any>? = null, exponea: Boolean = true) {
    val trackProps = properties ?: hashMapOf()
    Log.e("event_track", api)

    if (exponea) {
        App.instance.getDeviceInfo(api)?.toMap()?.let { trackProps.putAll(it) }
        Exponea.trackEvent(
                eventType = api,
                properties = PropertiesList(trackProps)
        )
    }

}