package com.narinc.rickandmorty

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.narinc.rickandmorty.feature.character.detail.CharacterDetailScreen
import com.narinc.rickandmorty.feature.character.list.CharacterListScreen
import com.narinc.rickandmorty.feature.favorites.FavoritesScreen
import com.narinc.rickandmorty.navigation.CharacterDetail
import com.narinc.rickandmorty.navigation.CharacterList
import com.narinc.rickandmorty.navigation.Favorites
import com.narinc.rickandmorty.navigation.Main

@Composable
fun RickAndMortyNavHost(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = topLevelDestinations.any { destination ->
        currentDestination?.hasRoute(destination.routeClass) == true
    }

    val onCharacterClick: (Int) -> Unit = { characterId ->
        navController.navigate(CharacterDetail(characterId))
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any {
                                it.hasRoute(destination.routeClass)
                            } == true,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Main,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation<Main>(startDestination = CharacterList) {
                composable<CharacterList> {
                    CharacterListScreen(onCharacterClick = onCharacterClick)
                }
                composable<Favorites> {
                    FavoritesScreen(onCharacterClick = onCharacterClick)
                }
            }

            composable<CharacterDetail> { entry ->
                val route: CharacterDetail = entry.toRoute()
                CharacterDetailScreen(characterId = route.characterId)
            }
        }
    }
}