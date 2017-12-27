package com.apps.elliotgrin.cryptobot.retrofit

import com.apps.elliotgrin.cryptobot.models.CryptoList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by elliotgrin on 28.12.2017.
 */
interface CoinmarketcapApi {
    @GET("ticker")
    fun getData(@Query("limit") limit: Int): Call<List<CryptoList>>
}