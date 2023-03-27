package pl.revo.merchant.api

import android.content.Context
import android.graphics.Bitmap
import io.reactivex.Single
import pl.revo.merchant.api.request.LoanReqData
import pl.revo.merchant.api.response.BillResponse
import pl.revo.merchant.api.response.FinalizationResponse
import pl.revo.merchant.api.response.LoanApplication
import pl.revo.merchant.api.response.ReturnId
import pl.revo.merchant.api.response.ReturnRes
import pl.revo.merchant.api.response.TariffClientData
import pl.revo.merchant.api.response.TariffClientSmsInfoData
import pl.revo.merchant.api.response.TariffRes
import pl.revo.merchant.model.AgentData
import pl.revo.merchant.model.BarcodeDto
import pl.revo.merchant.model.BnplData
import pl.revo.merchant.model.ClientData
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.ReportData
import pl.revo.merchant.model.Schedule
import pl.revo.merchant.model.SearchData
import pl.revo.merchant.model.StoreData
import pl.revo.merchant.model.StoreDto
import pl.revo.merchant.model.TariffData
import pl.revo.merchant.utils.DateFormats
import pl.revo.merchant.utils.addDay
import pl.revo.merchant.utils.addMonth
import pl.revo.merchant.utils.now
import pl.revo.merchant.utils.toDate
import pl.revo.merchant.utils.toText
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

class MockData(val context: Context) {

    companion object {
        private const val CONFIRM_CODE = "1111"
        private const val AUTH_TOKEN = "DEMO"
        private const val REQUEST_TOKEN = "REQUEST_TOKEN"
        private const val BAR_CODE_TEXT = "12345678901234567890"
        private const val BAR_CODE = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMzcwcHgiIGhlaWdodD0iMTIwcHgiIHZpZXdCb3g9IjAgMCAzNzAgMTIwIiB2ZXJzaW9uPSIxLjEiPgo8dGl0bGU+MlpaMDMxMjM1NjAxODAwNDY4NTg2MDc3MDwvdGl0bGU+CjxnIGlkPSJjYW52YXMiID4KPHJlY3QgeD0iMCIgeT0iMCIgd2lkdGg9IjM3MHB4IiBoZWlnaHQ9IjEyMHB4IiBmaWxsPSJ3aGl0ZSIgLz4KPGcgaWQ9ImJhcmNvZGUiIGZpbGw9ImJsYWNrIj4KPHJlY3QgeD0iMTAiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSIxMyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE1IiB5PSIxMCIgd2lkdGg9IjJweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iMTgiIHk9IjEwIiB3aWR0aD0iMnB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSIyMSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIzIiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iMjUiIHk9IjEwIiB3aWR0aD0iMnB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSIyOSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMxIiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iMzMiIHk9IjEwIiB3aWR0aD0iMnB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSIzNiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM5IiB5PSIxMCIgd2lkdGg9IjJweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iNDIiIHk9IjEwIiB3aWR0aD0iMnB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI0NSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjQ3IiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iNDkiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI1MiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjU1IiB5PSIxMCIgd2lkdGg9IjJweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iNTgiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI2MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjYyIiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iNjQiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI2NyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjcwIiB5PSIxMCIgd2lkdGg9IjJweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iNzMiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI3NSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9Ijc4IiB5PSIxMCIgd2lkdGg9IjJweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iODIiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI4NCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9Ijg2IiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iODgiIHk9IjEwIiB3aWR0aD0iMnB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI5MSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9Ijk0IiB5PSIxMCIgd2lkdGg9IjFweCIgaGVpZ2h0PSIxMDBweCIgLz4KPHJlY3QgeD0iOTYiIHk9IjEwIiB3aWR0aD0iMXB4IiBoZWlnaHQ9IjEwMHB4IiAvPgo8cmVjdCB4PSI5OCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEwMSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEwMyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEwNyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEwOSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjExMSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjExNCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjExNyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEyMSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEyMyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEyNSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEyNyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEzMCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEzMyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEzNiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjEzOCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE0MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE0MiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcH" +
                "giIC8+CjxyZWN0IHg9IjE0NiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE0OSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE1MSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE1MyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE1NSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE1OCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE2MSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE2NCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE2NiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE2OSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE3MiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE3NCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE3NiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE3OSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE4MiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE4NSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE4NyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE5MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE5MiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE5NCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjE5NyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIwMCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIwMyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIwNSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIwNyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIxMCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIxMyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIxNiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIxOCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIyMCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIyMyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIyNiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIyOCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIzMSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIzMyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjIzNyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI0MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI0MiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI0NCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI0NyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI1MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI1MiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI1NSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI1NyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI2MCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI2MyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI2NiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI2OCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI3MCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI3MyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI3NiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI3OCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI4MSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI4MyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI4NSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI4OSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI5MiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI5NCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI5NiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaW\n" +
                "dodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjI5OCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMwMSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMwNCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMwNyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMwOSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMxMSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMxNCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMxNiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMxOSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMyMiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMyNCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMyNyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMyOSIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMzMiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMzNSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjMzNyIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM0MCIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM0MyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM0NiIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM0OCIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM1MSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM1MyIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM1NiIgeT0iMTAiIHdpZHRoPSIycHgiIGhlaWdodD0iMTAwcHgiIC8+CjxyZWN0IHg9IjM1OSIgeT0iMTAiIHdpZHRoPSIxcHgiIGhlaWdodD0iMTAwcHgiIC8+Cgo8L2c+PC9nPgo8L3N2Zz4K"

        private const val CLIENT = "Test Test"
        private const val PHONE = "123456789"
    }

    fun getToken() = AUTH_TOKEN

    fun getSigleTrue() = Single.just(true)

    fun checkConfirmCode(confirmCode: String) = confirmCode == CONFIRM_CODE

    fun getAgentData(): AgentData {
        return AgentData(
                "DEMO",
                "DEMO",
                "",
                mutableListOf(StoreDto(StoreData(
                        1, "", "", "",
                        0.0, 10000.0, false, 0
                )))
        )
    }

    fun getReportData(dateFrom: String, dateTo: String) = Single.just(ReportData(
            dateFrom.toDate(DateFormats.SERVER_FORMAT),
            dateTo.toDate(DateFormats.SERVER_FORMAT),
            0,
            null
    ))

    fun getRequestToken(): Single<LoanReqData> = Single.just(LoanReqData(token = REQUEST_TOKEN))

    fun getClientInfo() = Single.error<ClientData>(Throwable())

    fun createClient(clientData: ClientData): Single<ClientData> {
        return Single.just(
                ClientData(
                        id = 1,
                        firstName = clientData.firstName.orEmpty(),
                        lastName = clientData.lastName.orEmpty(),
                        birthDate = clientData.birthDate,
                        email = clientData.email.orEmpty(),
                        area = clientData.settlement.orEmpty(),
                        settlement = clientData.settlement.orEmpty(),
                        street = clientData.street.orEmpty(),
                        house = clientData.house.orEmpty(),
                        apartment = clientData.apartment,
                        postalCode = clientData.postalCode.orEmpty(),
                        blackMark = clientData.blackMark.orEmpty(),
                        idDocuments = clientData.idDocuments,
                        creditDecision = "approved",
                        creditLimit = 6000.0,
                        missingDocuments = mutableListOf("name", "client_with_passport"),
                        _isRepeated = true,
                        _rclAccepted = true
                )
        )
    }

    fun getTariffData(loan: LoanData): Single<TariffRes> {
        val list = mutableListOf<TariffData>()
        val periods = if (loan.sum == 0.0) 1 else 2

        // add bnpl

        (1..periods).forEach { i ->
            val schedulers = mutableListOf<Schedule>()
            val monthAmount = (loan.sum * 100 / (3 * i)).roundToInt().toDouble() / 100
            var sum = loan.sum
            (1..3 * i).forEach { s ->
                schedulers.add(Schedule(
                        date = now().addMonth(s),
                        amount = if (s == 3 * i) sum else monthAmount
                ))
                sum -= monthAmount
            }

            val tariff = TariffData(
                    term = 3 * i,
                    term_id = i,
                    monthlyPayment = monthAmount,
                    totalOfPayments = loan.sum,
                    sumWithDiscount = loan.sum,
                    totalOverpayment = 0.0,
                    minAmount = 0.0,
                    maxAmount = 6000.0,
                    schedule = schedulers,
                    smsInfo = 69.0,
                    tariffProductKind = "rcl" //rcl or factoring; only for Romania
            )

            if (i == 1) {
                // add bnpl
                tariff.bnpl = BnplData(term = 10, dateFirstPayment = now().addDay(10), commission = 0.0)
            }

            list.add(tariff)
        }
        return Single.just(
            TariffRes(
                tariffs = list,
                client = TariffClientData(
                    TariffClientSmsInfoData(
                        available = true,
                        subscribed = true,
                        price = 5.0
                    )
                )
            )
        )
    }

    fun finalizeLoan(code: String): Single<FinalizationResponse> {
        return if (code == CONFIRM_CODE)
            Single.just(
                    FinalizationResponse(
                            "532451801",
                            LoanApplication(
                                    listOf(BarcodeDto(image = BAR_CODE, text = BAR_CODE_TEXT))
                            )
                    )
            )
        else Single.error(Throwable())
    }

    fun bill(): Single<BillResponse> = Single.just(BillResponse())

    fun transformImage(bitmap: Bitmap): Bitmap {
        val width = min(context.resources?.displayMetrics?.widthPixels
                ?: bitmap.width, bitmap.width)
        val height = (width.toFloat() / 1.7).toInt()
        return Bitmap.createBitmap(
                bitmap,
                (bitmap.width - width) / 2,
                (bitmap.height - height) / 2,
                width,
                height
        )
    }

    fun getTemplate(kind: String, loan: LoanData): String {
        return try {
            val stream = context.assets.open("templates/$kind")
            val buffer = ByteArray(stream.available())
            stream.read(buffer)
            var data = String(buffer)
            when (kind) {
                "individual" -> {
                    data = data.replace("{{loan_guid}}", loan.token)
                            .replace("{{loan_date}}", now().toText(DateFormats.SIMPLE_FORMAT))
                            .replace("{{client_full_name}}", loan.client?.fullName.orEmpty())
                            .replace("{{client_address}}", loan.client?.address.orEmpty())
                }
            }
            data
        } catch (e: Exception) {
            ""
        }
    }

    fun getOrders(phone: String?, guid: String?): Single<List<SearchData>> {
        return Single.just(
            listOf(
                when {
                    phone != null -> SearchData(
                        id = 1,
                        client = CLIENT,
                        phone = phone,
                        barcode = BAR_CODE_TEXT,
                        date = now().addMonth(-1),
                        amount = 100.0,
                        remainingAmount = 100.0,
                            guid = UUID.randomUUID().toString()
                    )
                    guid != null -> SearchData(
                            id = 1,
                            client = CLIENT,
                            phone = PHONE,
                            barcode = BAR_CODE_TEXT,
                            date = now().addMonth(-1),
                            amount = 100.0,
                            remainingAmount = 100.0,
                            guid = UUID.randomUUID().toString()
                    )
                    else -> SearchData(
                            id = 1,
                            client = CLIENT,
                            phone = PHONE,
                            barcode = BAR_CODE_TEXT,
                            date = now().addMonth(-1),
                            amount = 100.0,
                            remainingAmount = 100.0,
                            guid = UUID.randomUUID().toString()
                    )
                }
        ))
    }

    fun createReturn(): Single<ReturnRes> {
        return Single.just(
                ReturnRes(
                        returnId = ReturnId(
                                id = 382,
                                barcode = BarcodeDto(
                                        image = BAR_CODE,
                                        text = "2ZZ0312356018004685860770"
                                )
                        )
                )
        )
    }
}