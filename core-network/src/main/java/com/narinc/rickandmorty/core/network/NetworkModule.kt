package com.narinc.rickandmorty.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * ---- GEÇİCİ ÇÖZÜM ----
 * Bu object, Hilt'e geçene kadar (Adım ~10) manuel bir "servis lokator"ı
 * gibi davranacak. Hilt'e geçtiğimizde bu kod bir @Module @InstallIn(...)
 * sınıfına dönüşecek ve Retrofit/OkHttp örnekleri constructor injection
 * ile ViewModel/Repository'lere otomatik verilecek. Şimdilik elle
 * çağıracağız ki farkı gözünle görebilesin.
 */
object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: RickAndMortyApiService by lazy {
        retrofit.create(RickAndMortyApiService::class.java)
    }
}