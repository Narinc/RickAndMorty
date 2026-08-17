package com.narinc.rickandmorty.feature.favorites

import com.narinc.rickandmorty.core.ui.characterlistitem.CharacterListItemUiModel
import com.narinc.rickandmorty.feature.character.domain.model.Character

fun Character.toUiModel(): CharacterListItemUiModel {
    return CharacterListItemUiModel(id = id, title = name, subtitle = species)
}