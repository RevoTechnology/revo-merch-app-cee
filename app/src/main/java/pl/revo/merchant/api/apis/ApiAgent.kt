package pl.revo.merchant.api.apis

import io.reactivex.Single
import pl.revo.merchant.api.response.AgentRes
import pl.revo.merchant.api.response.ReportRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiAgent {
    @GET("api/loans/v1/agent")
    fun agentGetInfo(   ) : Single<Response<AgentRes>>

    @GET("api/loans/v1/stores/{id}/reports")
    fun agentGetReport(
            @Path("id") storeId: Int,
            @Query("from") dateFrom: String,
            @Query("to") dateTo: String
    ) : Single<Response<ReportRes>>
}