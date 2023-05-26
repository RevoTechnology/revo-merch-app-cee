package merchant.mokka.api

import android.content.Context
import android.net.Uri
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sentry.Sentry
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import merchant.mokka.api.apis.ApiAddress
import merchant.mokka.api.apis.ApiAgent
import merchant.mokka.api.apis.ApiClient
import merchant.mokka.api.apis.ApiDocuments
import merchant.mokka.api.apis.ApiLoan
import merchant.mokka.api.apis.ApiReturn
import merchant.mokka.api.apis.ApiUpdate
import merchant.mokka.api.apis.ApiUserAgent
import merchant.mokka.api.request.ApprovedClientReq
import merchant.mokka.api.request.ApprovedClientSmsInfoReq
import merchant.mokka.api.request.ApprovedReq
import merchant.mokka.api.request.BillRequest
import merchant.mokka.api.request.CodeReq
import merchant.mokka.api.request.DeviceInfoReq
import merchant.mokka.api.request.FinalizeData
import merchant.mokka.api.request.FinalizeReq
import merchant.mokka.api.request.LoanReq
import merchant.mokka.api.request.LoanReqData
import merchant.mokka.api.request.SaveClientData
import merchant.mokka.api.request.SaveClientReq
import merchant.mokka.api.request.UserData
import merchant.mokka.api.request.UserReq
import merchant.mokka.api.response.BillResponse
import merchant.mokka.api.response.DeviceSpecificRes
import merchant.mokka.api.response.FinalizationResponse
import merchant.mokka.api.response.ReturnRes
import merchant.mokka.api.response.TariffRes
import merchant.mokka.api.response.UpdateRes
import merchant.mokka.model.AddressData
import merchant.mokka.model.AgentData
import merchant.mokka.model.ClientData
import merchant.mokka.model.ClientIdPhoto
import merchant.mokka.model.GdprAcceptance
import merchant.mokka.model.IdPhoto
import merchant.mokka.model.LoanData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.model.PurchaseData
import merchant.mokka.model.ReportData
import merchant.mokka.model.SearchData
import merchant.mokka.pref.Prefs
import merchant.mokka.utils.DateFormats
import merchant.mokka.utils.createPart
import merchant.mokka.utils.orZero
import merchant.mokka.utils.photoDir
import merchant.mokka.utils.toText
import merchant.mokka.utils.writeResponseBodyToDisk
import retrofit2.Response
import java.io.File

class HttpService(
    val client: HttpClient,
    val context: Context,
    private val memoryCashedData: MemoryCashedData,
    private val mockData: MockData
) : ApiService {

    private val serviceLoan by lazy { client.createService(ApiLoan::class.java) }
    private val serviceUserAgent by lazy { client.createService(ApiUserAgent::class.java) }
    private val serviceAgent by lazy { client.createService(ApiAgent::class.java) }
    private val serviceAddress by lazy { client.createService(ApiAddress::class.java) }
    private val serviceReturn by lazy { client.createService(ApiReturn::class.java) }
    private val serviceUpdate by lazy { client.createService(ApiUpdate::class.java) }
    private val serviceClient by lazy { client.createService(ApiClient::class.java) }

    private val serviceDocuments by lazy {
        client.createService(
                serviceClass = ApiDocuments::class.java,
                logLevel = HttpLoggingInterceptor.Level.HEADERS)
    }

    //region ================= user_agent =================

    override var demo: Boolean
        get() = memoryCashedData.demo
        set(value) {
            memoryCashedData.demo = value
        }

    override fun signIn(login: String, pin: String): Single<out Boolean> {
        return if (demo) {
            Prefs.token = mockData.getToken()
            memoryCashedData.agentLogin = login
            memoryCashedData.agentPin = pin
            mockData.getSigleTrue()
        } else {
            val request = UserReq(UserData(login = login, password = pin))
            val single = serviceUserAgent.userAgentSession(user = request).transform()
            client.compose(single, context)
                    .doOnSuccess { context.photoDir.deleteRecursively() }
                    .map {
                        Prefs.token = it.user.token
                        Prefs.phone = login
                        Sentry.setExtra("agent_id ", login)
                        memoryCashedData.agentLogin = login
                        memoryCashedData.agentPin = pin
                        true
                    }

        }
    }

    override fun signOut(): Single<out Boolean> {
        val onComplete: () -> Unit = {
            Sentry.removeExtra("agent_id")
            Sentry.removeExtra("store_id")
            Sentry.removeExtra("trader_id")

            Prefs.token = ""
            memoryCashedData.agentLogin = ""
            memoryCashedData.agentPin = ""
            memoryCashedData.agentData = null
        }

        return if (demo) {
            Prefs.token = ""
            memoryCashedData.agentLogin = ""
            memoryCashedData.agentPin = ""
            memoryCashedData.agentData = null
            memoryCashedData.demo = false
            mockData.getSigleTrue()
        } else {
            val single = serviceUserAgent.userAgentDelete().transform()

            client.compose(single, context)
                    .doOnError { onComplete() }
                    .map {
                        onComplete()
                        true
                    }
        }
    }

    override fun requestSmsCode(login: String): Single<out Boolean> {
        return if (demo) {
            mockData.getSigleTrue()
        } else {
            val request = UserReq(UserData(login = login))
            val single = serviceUserAgent.userAgentPassword(user = request).transform()
            client.compose(single, context)
                    .map { true }
        }
    }

    override fun checkSmsCode(login: String, confirmCode: String): Single<out Boolean> {
        return if (demo) {
            val check = mockData.checkConfirmCode(confirmCode)
            if (check) memoryCashedData.agentLogin = login
            Single.just(check)
        } else {
            val request = UserReq(
                UserData(
                    login = login,
                    smsCode = confirmCode
            )
            )
            val single = serviceUserAgent.userAgentNewPassword(user = request).transform()
            client.compose(single, context)
                    .map {
                        Prefs.token = it.user.token
                        memoryCashedData.agentLogin = login
                        true
                    }
        }
    }

    override fun signUpNewPin(login: String, confirmCode: String, password: String): Single<out Boolean> {
        return if (demo) {
            val check = mockData.checkConfirmCode(confirmCode)
            if (check) {
                memoryCashedData.agentLogin = login
                memoryCashedData.agentPin = password
            }
            Single.just(check)
        } else {
            val request = UserReq(
                UserData(
                    login = login,
                    smsCode = confirmCode,
                    password = password,
                    confirmation = password
            )
            )
            val single = serviceUserAgent.userAgentNewPassword(user = request).transform()
            client.compose(single, context)
                    .map {
                        Prefs.token = it.user.token
                        memoryCashedData.agentLogin = login
                        memoryCashedData.agentPin = password
                        true
                    }
        }
    }

    override fun unlock(pin: String): Single<out Boolean> {
        return when {
            memoryCashedData.agentLogin == null -> Single.just(false)
            demo -> mockData.getSigleTrue()
            else -> {
                val request = UserReq(UserData(login = memoryCashedData.agentLogin!!, password = pin))
                val single = serviceUserAgent.userAgentSession(user = request).transform()
                client.compose(single, context)
                        .map {
                            Prefs.token = it.user.token
                            true
                        }
            }
        }
    }

    //endregion

    //region ================= agent =================

    override fun getAgentInfo(): Single<out AgentData> {
        return if (demo) {
            val agentData = mockData.getAgentData()
            memoryCashedData.agentData = agentData
            Single.just(agentData)
        } else {
            val single = serviceAgent.agentGetInfo().transform()
            return client.compose(single, context)
                    .map {
                        memoryCashedData.agentData = it.agent
                        it.agent
                    }
        }
    }

    override fun getAgentReport(dateFrom: String, dateTo: String): Single<out ReportData> {
        return if (demo) {
            mockData.getReportData(dateFrom, dateTo)
        } else {
            val single = serviceAgent.agentGetReport(
                    storeId = Prefs.currentStoreId,
                    dateFrom = dateFrom,
                    dateTo = dateTo
            ).transform()
            client.compose(single, context)
                    .map { it.report }
        }
    }

    //endregion

    //region ================= loans =================

    override fun createLoanRequest(phone: String): Single<LoanReqData> {
        return if (demo) {
            mockData.getRequestToken()
        } else {
            val request = LoanReq(
                LoanReqData(
                    storeId = Prefs.currentStoreId.orZero(),
                    insuranceAgree = true,
                    phone = phone
                )
            )
            val single = serviceLoan.loanCreateRequest(
                    loanRequest = request
            )
                    .transform()
                    .doOnSuccess { context.photoDir.deleteRecursively() }

            client.compose(single, context)
                    .map { it.loanRequest }
        }
    }

    override fun updateLoanRequest(loanToken: String, phone: String, amount: String?, agreeInsurance: Boolean?): Single<out Boolean> {
        return if (demo) {
            mockData.getSigleTrue()
        } else {
            val request = LoanReq(LoanReqData(phone = phone, amount = amount, insuranceAgree = agreeInsurance))
            val single = serviceLoan.loanUpdateRequest(
                    loanToken = loanToken,
                    loanRequest = request
            ).transform()
            client.compose(single, context).map { true }
        }
    }

    override fun getClientInfo(loanToken: String): Single<out ClientData> {
        return if (demo) {
            mockData.getClientInfo()
        } else {
            val single = serviceClient.loanClientInfo(loanToken = loanToken).transform()
            client.compose(single, context)
                    .flatMap {
                        when {
                            it.client == null -> Single.error(Throwable())
                            it.client.isEmpty() -> Single.error(Throwable())
                            else -> {
                                it.client.technicalMessage = it.meta?.text
                                Single.just(it.client)
                            }
                        }
                    }
        }
    }

    override fun createClient(
        loanToken: String,
        mobilePhone: String,
        clientData: ClientData,
        agrees: GdprAcceptance,
        confirmCode: String
    ): Single<out ClientData> {
        return if (demo) {
            mockData.createClient(clientData)
        } else {
            val saveClient = SaveClientReq(
                SaveClientData(
                    mobilePhone = mobilePhone,
                    firstName = clientData.firstName.orEmpty(),
                    lastName = clientData.lastName.orEmpty(),
                    middleName = clientData.middleName,
                    birthDate = clientData.birthDate?.toText(DateFormats.SERVER_FORMAT).orEmpty(),
                    email = clientData.email.orEmpty(),
                    area = clientData.settlement.orEmpty(),
                    settlement = clientData.settlement.orEmpty(),
                    street = clientData.street.orEmpty(),
                    house = clientData.house.orEmpty(),
                    apartment = clientData.apartment,
                    postalCode = clientData.postalCode.orEmpty(),
                    blackMark = clientData.blackMark.orEmpty(),
                    confirmationCode = confirmCode,
                    idDocuments = clientData.idDocuments,
                    gdprAcceptance = agrees
            )
            )
            val single = serviceClient.loanCreateClient(
                    loanToken = loanToken,
                    client = saveClient
            ).transform()
            return client.compose(single, context)
                    .flatMap {
                        when {
                            it.client == null || it.client.isEmpty() -> Single.error(Throwable())
                            else -> {
                                it.client.technicalMessage = it.meta?.text
                                Single.just(it.client)
                            }
                        }
                    }
        }
    }

    override fun updateClientDocuments(
            loanToken: String,
            frontPhotoPath: String,
            backPhotoPath: String,
            thirdPhotoPath: String?
    ): Single<out Boolean> {
        return if (demo) {
            mockData.getSigleTrue()
        } else {
            val frontFile = File(frontPhotoPath)
            val backFile = File(backPhotoPath)
            val thirdPhotoFile = File(thirdPhotoPath.orEmpty())

            val single = serviceDocuments.loanUpdateClientDocuments(
                    loanToken = loanToken,
                    phoneName = createPart("client[documents][${IdPhoto.PHOTO_NAME.photoName}]", frontFile),
                    photoClientWithPassport = createPart("client[documents][${IdPhoto.PHOTO_CLIENT_WITH_PASSPORT.photoName}]", backFile),
                    photoLivingAddress = if (!thirdPhotoFile.exists()) null else
                        createPart("client[documents][${IdPhoto.PHOTO_LIVING_ADDRESS.photoName}]", thirdPhotoFile)
            ).transform()

            client
                    .compose(single, context)
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { true }
        }
    }

    override fun updateClientDocuments(loanToken: String, files: ClientIdPhoto): Single<out Boolean> {
        return if (demo) mockData.getSigleTrue()
        else {
            val phoneName = createPart(
                    "client[documents][${IdPhoto.PHOTO_NAME.photoName}]",
                    files.nameImage
            )
            val photoClientWithPassport = createPart(
                    "client[documents][${IdPhoto.PHOTO_CLIENT_WITH_PASSPORT.photoName}]",
                    files.clientWithPassportImage
            )
            val photoLivingAddress = createPart(
                    "client[documents][${IdPhoto.PHOTO_LIVING_ADDRESS.photoName}]",
                    files.livingAddressImage
            )

            val single = serviceDocuments.loanUpdateClientDocuments(
                    loanToken = loanToken,
                    phoneName = phoneName,
                    photoClientWithPassport = photoClientWithPassport,
                    photoLivingAddress = photoLivingAddress
            ).transform()
            client.compose(single, context)
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { true }
        }
    }

    override fun sendConfirmClientCode(loanToken: String): Single<out Boolean> {
        return if (demo) {
            mockData.getSigleTrue()
        } else {
            val single = serviceClient.loanConfirm(loanToken = loanToken).transform()
            client.compose(single, context).map { true }
        }
    }

    override fun confirmClient(loanToken: String, code: String): Single<out PurchaseData> {
        val single = serviceClient.loanConfirmClient(
                loanToken = loanToken,
                code = CodeReq(code)
        ).transform()
        return client.compose(single, context).map {
            it.client
        }
    }

    override fun getTariffInfo(loan: LoanData): Single<out TariffRes> {
        return if (demo) {
            mockData.getTariffData(loan)
        } else {
            val single = serviceLoan.getTariffInformation(loanToken = loan.token).transform()
            client.compose(single, context)
        }
    }

    override fun createApprovedLoan(loanToken: String, termId: Int, smsInfoAgree: Boolean): Single<out Boolean> {
        return if (demo) {
            mockData.getSigleTrue()
        } else {
            val single = serviceLoan.createApprovedLoan(
                    loanToken = loanToken,
                    term = ApprovedReq(
                        termId,
                        ApprovedClientReq(
                            ApprovedClientSmsInfoReq(
                                subscribed = smsInfoAgree
                            )
                        )
                    )
            ).transform()
            client.compose(single, context).map { true }
        }
    }

    override fun finalizeLoan(loanToken: String, agreeSmsInfo: String, agreeProcessing: String, code: String): Single<out FinalizationResponse> {
        return if (demo) {
            mockData.finalizeLoan(code)
        } else {
            val single = serviceLoan.finalizeLoan(
                    loanToken = loanToken,
                    data = FinalizeReq(loan = FinalizeData(
                            agreeProcessing = agreeProcessing,
                            agreeSmsInfo = agreeSmsInfo,
                            confirmationCode = code
                    )
                    )
            ).transformToBarcode()
                    .doOnSuccess { context.photoDir.deleteRecursively() }
            client.compose(single, context)
        }
    }

    override fun bill(loanToken: String, code: String?): Single<out BillResponse> {
        return if (demo) {
            mockData.bill()
        } else {
            val single = serviceLoan.bill(
                    loanToken = loanToken,
                    data = BillRequest(code = code.orEmpty()).loanRequest()
            )
                    .transform()

            client.compose(single, context).map { BillResponse() }
        }
    }

    override fun getDocuments(loanToken: String, kind: String): Single<out String> {
        val single = serviceLoan.getDocuments(
                loanToken = loanToken,
                kind = kind
        ).transform()
        return client.compose(single, context).map { it.string() }
    }

    override fun selfRegistration(loanToken: String, phone: String): Single<out Response<ResponseBody>> {
        val single = serviceLoan.selfRegistration(
                loanToken = loanToken,
                phone = phone
        )
        return client.compose(single, context)
    }

    //endregion

    //region ================= address =================

    override fun getAddressByPostalCode(postalCode: String): Single<out List<AddressData>> {
        val single = serviceAddress.getAddressByPostalCode(
                HttpConfig.ADDRESS_URL + postalCode
        ).transform()
        return client.compose(single, context)
    }

    //endregion

    //region ================= return =================

    override fun getOrderList(
        phone: String?,
        documentId: String?,
        orderId: Int?,
        guid: String?
    ): Single<out List<SearchData>> {
        return if (demo) {
            mockData.getOrders(phone, guid)
        } else {
            val single = serviceReturn.getOrderList(
                storeId = Prefs.currentStoreId,
                phone = phone,
                documentId = documentId,
                orderId = orderId,
                guid = guid
            ).transform()
            client.compose(single, context).map { it.orders }
        }
    }

    override fun sendReturnConfirmationCode(orderId: Int): Single<out Boolean> {
        return if (demo) mockData.getSigleTrue()
        else {
            val single = serviceReturn.sendReturnConfirmationCode(orderId = orderId).transform()
            client.compose(single, context).map { true }
        }
    }

    override fun createReturn(orderId: Int, confirmCode: String, amount: String): Single<out ReturnRes> {
        return if (demo) mockData.createReturn()
        else {
            val single = serviceReturn.createReturn(
                    orderId = orderId,
                    confirmCode = confirmCode,
                    amount = amount,
                    storeId = Prefs.currentStoreId
            ).transformToReturnBarcode()
            client.compose(single, context)
        }
    }

    override fun confirmReturn(returnId: Int): Single<out Boolean> {
        return if (demo) mockData.getSigleTrue()
        else {
            val single = serviceReturn.confirmReturn(returnId = returnId).transform()
            client.compose(single, context).map { true }
        }
    }

    override fun cancelReturn(returnId: Int): Single<out Boolean> {
        return if (demo) mockData.getSigleTrue()
        else {
            val single = serviceReturn.cancelReturn(returnId = returnId).transform()
            client.compose(single, context).map { true }
        }
    }

    //endregion

    //region ================= Update APK =================

    override fun loadApkVersion(): Single<out String> {
        val single = serviceUpdate.downloadVersionFile(
            HttpConfig.VERSION_FILE_URL,
            HttpConfig.UPDATE_APK_TOKEN
        )
        return client.compose(single, context)
                .map { response ->
                    val line = response.body()?.string().orEmpty()
                    line.substring(line.indexOf("version=") + 8)
                }
    }

    override fun loadApkFile(): Single<out Uri> {
        val single = serviceUpdate.downloadApkFile(
            HttpConfig.UPDATE_APK_URL,
            HttpConfig.UPDATE_APK_TOKEN
        )
        return client.compose(single, context)
                .map { writeResponseBodyToDisk(context, it.body()) }
    }

    override fun getDevice(deviceId: String): Single<out DeviceSpecificRes> {
        val single = serviceUpdate.getDevice(
                devicePath = String.format(HttpConfig.CHECK_UPDATE_ENDPOINT, deviceId)
        ).transform()
        return client.compose(single, context)
    }

    override fun deviceLogs(deviceId: String, deviceInfo: DeviceInfoReq): Single<out Response<ResponseBody>> {
        val single = serviceUpdate.deviceLogs(
                updatePath = String.format(HttpConfig.CHECK_UPDATE_ENDPOINT, "$deviceId/logs"),
                event = deviceInfo.event,
                osVersion = deviceInfo.device_os_version,
                deviceModel = deviceInfo.device_model,
                appVersion = deviceInfo.current_app_version,
                macAddress = null,
                phoneNumber = deviceInfo.phone_number,
                storeId = deviceInfo.store_id
        )
        return client.compose(single, context)
    }

    override fun getDeviceUpdate(deviceId: String): Single<out UpdateRes> {
        val single = serviceUpdate.getDeviceUpdate(
                devicePath = String.format(HttpConfig.CHECK_UPDATE_ENDPOINT, "$deviceId/update")
        ).transform()
        return client.compose(single, context)
    }

    override fun loadNatashaApkFile(apkUri: String): Single<out Uri> {
        val single = serviceUpdate.downloadNatashaApkFile(apkUri)
        return client.compose(single, context)
                .map { writeResponseBodyToDisk(context, it.body()) }
    }

    //endregion
}