package com.narinc.rickandmorty.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Main

@Serializable
data object CharacterList

@Serializable
data object Favorites

@Serializable
data class CharacterDetail(val characterId: Int)