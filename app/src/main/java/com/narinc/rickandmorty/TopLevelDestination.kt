package com.narinc.rickandmorty

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.narinc.rickandmorty.navigation.CharacterList
import com.narinc.rickandmorty.navigation.Favorites
import kotlin.reflect.KClass

internal data class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val icon: ImageVector,
    val label: String
)

internal val topLevelDestinations = listOf(
    TopLevelDestination(CharacterList, CharacterList::class, Icons.Default.Home, "Ana Sayfa"),
    TopLevelDestination(Favorites, Favorites::class, Icons.Default.Favorite, "Favoriler")
)