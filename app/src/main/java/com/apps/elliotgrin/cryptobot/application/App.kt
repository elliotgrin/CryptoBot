package com.apps.elliotgrin.cryptobot.application

import android.app.Application
import com.apps.elliotgrin.cryptobot.retrofit.CoinmarketcapApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory





/**
 * Created by elliotgrin on 28.12.2017.
 */
class App: Application() {

    companion object {
        lateinit var coinMarketApi: CoinmarketcapApi

        fun getApi(): CoinmarketcapApi {
            return coinMarketApi
        }
    }

    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()

        retrofit = Retrofit.Builder()
                .baseUrl("https://api.coinmarketcap.com/v1/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build()

        coinMarketApi = retrofit.create(CoinmarketcapApi::class.java) //Создаем объект, при помощи которого будем выполнять запросы
    }
}