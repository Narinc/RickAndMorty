package com.narinc.rickandmorty.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * ---- KAVRAM: Moshi @JsonClass(generateAdapter = true) ----
 * Bu annotation, KSP'ye (Adım 7'de eklediğimiz ksp(libs.moshi.codegen))
 * "bu sınıf için compile-time'da bir JSON<->Kotlin dönüştürücü üret" der.
 * Reflection kullanmaz (Gson'un aksine), bu yüzden hem daha hızlıdır
 * hem de ProGuard/R8 ile obfuscate edilmiş release build'lerde sorun çıkarmaz.
 *
 * ---- @Json(name = "...") ----
 * API'deki JSON alan adı ile Kotlin property adın farklıysa (örn. snake_case
 * vs camelCase) burada eşleştirirsin. Bu API'de field isimleri zaten
 * camelCase'e yakın olduğu için çoğu yerde gerekmeyecek, ama örnek olsun
 * diye bir tanesinde göstereceğim.
 */
@JsonClass(generateAdapter = true)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val origin: LocationRefDto,
    val location: LocationRefDto,
    @Json(name = "episode")
    val episodeUrls: List<String>
)

@JsonClass(generateAdapter = true)
data class LocationRefDto(
    val name: String,
    val url: String
)