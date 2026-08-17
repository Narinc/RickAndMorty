package com.narinc.rickandmorty.feature.character.list

import com.narinc.rickandmorty.core.ui.characterlistitem.CharacterListItemUiModel
import com.narinc.rickandmorty.feature.character.domain.model.Character

fun Character.toCharacterListItemUiModel(): CharacterListItemUiModel {
    return CharacterListItemUiModel(id = id, title = name, subtitle = species)
}