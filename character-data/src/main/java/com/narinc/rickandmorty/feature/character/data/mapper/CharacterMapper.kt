package com.narinc.rickandmorty.feature.character.data.mapper

import com.narinc.rickandmorty.core.network.model.CharacterDto
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.model.CharacterStatus

/**
 * ---- KAVRAM: Extension function ile mapper ----
 * "CharacterDto.toDomain()" şeklinde çağıracağız. Bunu bir class içine de
 * yazabilirdik (örn. CharacterMapper sınıfı, map() metodu), ama basit,
 * tek yönlü, state tutmayan dönüşümler için extension function Kotlin'de
 * daha idiomatic. RxJava döneminde böyle bir kavram yoktu, bu tamamen
 * Kotlin'in sana verdiği bir güç.
 *
 * DİKKAT: DTO'daki "status" alanı API'de "Alive" / "Dead" / "unknown" gibi
 * SERBEST STRING olarak geliyor (enum değil, çünkü JSON'da enum kavramı yok).
 * Biz burada bunu KENDİ enum'umuza çeviriyoruz. Bu satır tam olarak neden
 * DTO ile Domain modeli ayırdığımızın kanıtı: API'nin "gevşek" string
 * yapısı, domain katmanına hiç sızmıyor.
 */
fun CharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status.toCharacterStatus(),
        species = species,
        imageUrl = image,
        originName = origin.name,
        locationName = location.name
    )
}

private fun String.toCharacterStatus(): CharacterStatus {
    return when (this.lowercase()) {
        "alive" -> CharacterStatus.ALIVE
        "dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    }
}