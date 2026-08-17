package com.narinc.rickandmorty

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.narinc.rickandmorty.feature.character.list.CharacterListScreen
import com.narinc.rickandmorty.feature.favorites.FavoritesScreen
import com.narinc.rickandmorty.navigation.Screen

/**
 * ---- KAVRAM: Nested Navigation Graph ----
 * Bu composable KENDİ navController'ını tutuyor -- dıştaki
 * RickAndMortyNavHost'un navController'ından TAMAMEN AYRI. Bottom nav
 * sekmeleri arası geçiş burada, İÇ navController ile yönetiliyor.
 * "Detay ekranına git" gibi dış geçişler ise, dışarıdan lambda olarak
 * gelen onCharacterClick üzerinden DIŞ navController'a devrediliyor.
 */
@Composable
fun MainScreen(onCharacterClick: (characterId: Int) -> Unit) {
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route

                NavigationBarItem(
                    selected = currentRoute == Screen.CharacterList.route,
                    onClick = {
                        innerNavController.navigate(Screen.CharacterList.route) {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                    label = { Text("Ana Sayfa") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Favorites.route,
                    onClick = {
                        innerNavController.navigate(Screen.Favorites.route) {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoriler") },
                    label = { Text("Favoriler") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.CharacterList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.CharacterList.route) {
                CharacterListScreen(onCharacterClick = onCharacterClick)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onCharacterClick = onCharacterClick)
            }
        }
    }
}