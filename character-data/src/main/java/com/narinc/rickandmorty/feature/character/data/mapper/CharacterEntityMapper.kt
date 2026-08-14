package com.narinc.rickandmorty.feature.character.data.mapper

import com.narinc.rickandmorty.core.network.model.CharacterDto
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.model.CharacterStatus

fun CharacterDto.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        imageUrl = image,
        originName = origin.name,
        locationName = location.name
    )
}

fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = when (status.lowercase()) {
            "alive" -> CharacterStatus.ALIVE
            "dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = species,
        imageUrl = imageUrl,
        originName = originName,
        locationName = locationName
    )
}