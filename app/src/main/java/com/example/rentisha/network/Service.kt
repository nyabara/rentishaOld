package com.example.rentisha.network


import com.example.rentisha.domain.Renter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch rentHouses.
 */
interface RentishaService {
    @GET("devbytes")
    suspend fun getRentlist(): NetworkHouseContainer
    @GET("devbytes/url")
    suspend fun getRent():NetworkHouseContainer
    @POST("renters")
    fun addRenters(@Body renter: Renter): Call<Renter>
    @GET("renters")
    suspend fun getRenters():List<Renter>
}

/**
 * Main entry point for network access. Call like `RentishaNetwork.houses.getRentlist()`
 */
object RentishaNetwork {

    val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    // Configure retrofit to parse JSON and use coroutines
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://rentisha.herokuapp.com/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val rentishaApi = retrofit.create(RentishaService::class.java)

}
