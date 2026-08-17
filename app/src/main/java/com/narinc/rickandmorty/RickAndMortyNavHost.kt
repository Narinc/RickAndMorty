package com.narinc.rickandmorty

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.narinc.rickandmorty.feature.character.detail.CharacterDetailScreen
import com.narinc.rickandmorty.navigation.Screen

/**
 * ---- KAVRAM: NavHost ----
 * Tüm ekranların "birbirine nasıl bağlandığını" TEK BİR YERDE, app
 * modülünde topluyoruz. Bu, "composition root" dediğimiz bir kalıp --
 * bağımlılıkların (burada: ekranların birbirine geçişi) en üst seviyede,
 * tek bir yerde örüldüğü nokta. Feature modülleri birbirini görmüyor,
 * SADECE burası ikisini de görüyor ve bağlıyor.
 *
 * navController.navigate(...) -- RxJava döneminde Fragment
 * transaction'ları ya da startActivity() ile yaptığın geçişlerin
 * Compose'daki karşılığı.
 */
@Composable
fun RickAndMortyNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(Screen.CharacterDetail.createRoute(characterId))
                }
            )
        }

        composable(route = Screen.CharacterDetail.route) {
            // characterId'yi burada elle okumuyoruz -- ViewModel,
            // SavedStateHandle üzerinden Hilt aracılığıyla kendisi okuyor
            // (Adım 15.4'te gördüğün gibi).
            CharacterDetailScreen()
        }
    }
}