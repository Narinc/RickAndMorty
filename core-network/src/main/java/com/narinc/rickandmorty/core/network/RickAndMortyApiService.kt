package com.narinc.rickandmorty.core.network

import com.narinc.rickandmorty.core.network.model.CharacterDto
import com.narinc.rickandmorty.core.network.model.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * ---- KAVRAM: Retrofit + suspend fun ----
 * RxJava'da Retrofit interface'lerinde dönüş tipi genelde Single<T> ya da
 * Observable<T> olurdu (CallAdapter.Factory ile RxJava'ya bağlardık).
 * Coroutine dünyasında buna hiç gerek yok: Retrofit, "suspend fun" dönüş
 * tipini NATIVE olarak destekler. Sen sadece fonksiyonu suspend işaretlersin,
 * Retrofit arka planda senin için coroutine'e uygun şekilde çalıştırır.
 *
 * Yani şu satır tek başına RxJava'daki CallAdapter kurulumunun tamamının
 * yerini tutuyor.
 */
interface RickAndMortyApiService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacterDetail(
        @Path("id") id: Int
    ): CharacterDto
}