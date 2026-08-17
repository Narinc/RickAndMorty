package com.narinc.rickandmorty.navigation

/**
 * ---- KAVRAM: Sealed class ile type-safe rota tanımı ----
 * Navigation Compose'da eski yöntem, rotaları düz String olarak yazmaktı
 * ("character_detail/{id}" gibi) -- yazım hatasına çok açık, derleyici
 * seni korumaz. Burada sealed class + route property kombinasyonuyla
 * hem "hangi ekranlar var" listesini TEK YERDE topluyoruz hem de
 * argüman geçişini daha güvenli hale getiriyoruz.
 *
 * Bu sınıf, "feature-character-list" ve "feature-character-detail"
 * modüllerinin İKİSİ TARAFINDAN da bilinen TEK ortak nokta olacak.
 * Liste ekranı "detaya git" derken CharacterDetailViewModel'i ya da
 * CharacterDetailScreen'i hiç bilmeyecek, sadece bu route'u bilecek.
 */
sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object CharacterList : Screen("character_list")
    data object Favorites : Screen("favorites")
    data object CharacterDetail : Screen("character_detail/{characterId}") {
        const val ARG_CHARACTER_ID = "characterId"

        fun createRoute(characterId: Int) = "character_detail/$characterId"
    }
}