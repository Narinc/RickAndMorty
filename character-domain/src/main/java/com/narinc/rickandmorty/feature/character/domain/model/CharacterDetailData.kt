package com.narinc.rickandmorty.feature.character.domain.model

/**
 * Facade UseCase'in ürettiği, "ekranın gerçekte ihtiyaç duyduğu" birleşik model.
 * Character (API'den) + isFavorite (yerel state) iki farklı kaynaktan geliyor
 * ama UI'nin bunu bilmesine gerek yok, tek bir model olarak alıyor.
 */
data class CharacterDetailData(
    val character: Character,
    val isFavorite: Boolean
)