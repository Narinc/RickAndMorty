package com.narinc.rickandmorty.feature.favorites

import com.narinc.rickandmorty.core.ui.characterlistitem.CharacterUIModel
import com.narinc.rickandmorty.feature.character.domain.model.Character

fun Character.toUiModel(): CharacterUIModel {
    return CharacterUIModel(id = id, title = name, subtitle = species)
}