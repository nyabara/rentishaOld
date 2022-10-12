package com.example.rentisha.api


import com.example.rentisha.domain.House
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch rentHouses.
 */
interface RentishaService {
    @GET("renters/")
    suspend fun getRenters():List<Renter>

    @GET("renters/{id}")
    fun getRenter(@Path("id") id: Int):Call<Renter>

    @GET("house_types")
    suspend fun getHouseTypes():List<NetworkHouseTypes>

    @GET("electricity_types")
    suspend fun getElectricityTypes():List<NetworkElectricityTypes>

    @GET("houses/")
    suspend fun getHouses(@Query("page") page: Int,
                          @Query("per_page") itemsPerPage: Int): List<NetworkHouse>

    @PUT("renters/{id}")
    fun updateRenter(@Path("id")id: Int): Call<Renter>

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("renters/")
    fun addRenters(@Body renter: Renter): Call<Renter>

    @POST("houses/")
    fun addHouse(@Body house: House): Call<House>

    @PUT("houses/")
    fun updateHouse(@Body house: House): Call<House>
}


object RentishaNetwork {

    //create instance of interceptor
       val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    val client: OkHttpClient = OkHttpClient.Builder().apply {
        //added interceptor to OkHttpClient
        this.addInterceptor(interceptor)

            .connectTimeout(1,TimeUnit.MINUTES)
            .writeTimeout(1,TimeUnit.MINUTES)
            .readTimeout(1,TimeUnit.MINUTES)
    }
        .build()

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
