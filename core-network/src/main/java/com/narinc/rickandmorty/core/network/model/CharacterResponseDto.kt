package com.narinc.rickandmorty.core.network.model

import com.squareup.moshi.JsonClass

/**
 * API sayfalama bilgisini "info" objesinde, karakter listesini "results"
 * içinde döner. Paging 3'e geçtiğimizde (ileriki adım) bu "info.next" alanı
 * bize "sıradaki sayfa var mı" bilgisini verecek.
 */
@JsonClass(generateAdapter = true)
data class CharacterResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

@JsonClass(generateAdapter = true)
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)