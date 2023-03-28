package merchant.mokka.api.apis

import io.reactivex.Single
import merchant.mokka.model.AddressData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiAddress {
    @GET
    fun getAddressByPostalCode(@Url url: String): Single<Response<List<AddressData>>>
}