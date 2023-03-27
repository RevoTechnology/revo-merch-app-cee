package pl.revo.merchant.api

import android.net.Uri
import io.reactivex.Single
import okhttp3.ResponseBody
import pl.revo.merchant.api.request.DeviceInfoReq
import pl.revo.merchant.api.request.LoanReqData
import pl.revo.merchant.api.response.BillResponse
import pl.revo.merchant.api.response.DeviceSpecificRes
import pl.revo.merchant.api.response.FinalizationResponse
import pl.revo.merchant.api.response.ReturnRes
import pl.revo.merchant.api.response.TariffRes
import pl.revo.merchant.api.response.UpdateRes
import pl.revo.merchant.model.AddressData
import pl.revo.merchant.model.AgentData
import pl.revo.merchant.model.ClientData
import pl.revo.merchant.model.ClientIdPhoto
import pl.revo.merchant.model.GdprAcceptance
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.PurchaseData
import pl.revo.merchant.model.ReportData
import pl.revo.merchant.model.SearchData
import retrofit2.Response

interface ApiService {

    //region ================= user_agent =================

    var demo : Boolean

    fun signIn(login: String, pin: String) : Single<out Boolean>
    fun signOut() : Single<out Boolean>
    fun requestSmsCode(login: String) : Single<out Boolean>
    fun checkSmsCode(login: String, confirmCode: String) : Single<out Boolean>
    fun signUpNewPin(login: String, confirmCode: String, password: String) : Single<out Boolean>
    fun unlock(pin: String) : Single<out Boolean>

    //endregion

    //region ================= agent =================

    fun getAgentInfo() : Single<out AgentData>
    fun getAgentReport(dateFrom: String, dateTo: String) : Single<out ReportData>

    //endregion

    //region ================= loans =================

    fun createLoanRequest() : Single<LoanReqData>
    fun updateLoanRequest(loanToken: String, phone: String, amount: String?, agreeInsurance: Boolean?) : Single<out Boolean>

    fun getClientInfo(loanToken: String) : Single<out ClientData>

    fun createClient(loanToken: String, mobilePhone: String, clientData: ClientData, agrees: GdprAcceptance, confirmCode: String) : Single<out ClientData>
    fun updateClientDocuments(loanToken: String, frontPhotoPath: String, backPhotoPath: String, thirdPhotoPath: String? = null) : Single<out Boolean>
    fun updateClientDocuments(loanToken: String, files: ClientIdPhoto) : Single<out Boolean>
    fun sendConfirmClientCode(loanToken: String) : Single<out Boolean>
    fun confirmClient(loanToken: String, code: String) : Single<out PurchaseData>

    fun getTariffInfo(loan: LoanData) : Single<out TariffRes>
    fun createApprovedLoan(loanToken: String, termId: Int, smsInfoAgree: Boolean) : Single<out Boolean>
    fun finalizeLoan(loanToken: String, agreeSmsInfo: String, agreeProcessing: String, code: String) : Single<out FinalizationResponse>

    fun getDocuments(loanToken: String, kind: String) : Single<out String>

    fun selfRegistration(loanToken: String, phone: String) : Single<out Response<ResponseBody>>
    fun bill(loanToken: String, code: String?): Single<out BillResponse>

    //endregion

    //region ================= address =================

    fun getAddressByPostalCode(postalCode: String) : Single<out List<AddressData>>

    //endregion

    //region ================= return =================

    fun getOrderList(
        phone: String?,
        documentId: String?,
        orderId: Int?,
        guid: String?
    ): Single<out List<SearchData>>

    fun sendReturnConfirmationCode(orderId: Int): Single<out Boolean>
    fun createReturn(orderId: Int, confirmCode: String, amount: String) : Single<out ReturnRes>
    fun confirmReturn(returnId: Int) : Single<out Boolean>
    fun cancelReturn(returnId: Int) : Single<out Boolean>

    //endregion

    //region ================= Update APK =================

    fun loadApkVersion() : Single<out String>
    fun loadApkFile() : Single<out Uri>

    fun getDevice(deviceId: String) : Single<out DeviceSpecificRes>
    fun deviceLogs(deviceId: String, deviceInfo: DeviceInfoReq) : Single<out Response<ResponseBody>>
    fun getDeviceUpdate(deviceId: String) : Single<out UpdateRes>
    fun loadNatashaApkFile(apkUri: String) : Single<out Uri>

    //endregion
}