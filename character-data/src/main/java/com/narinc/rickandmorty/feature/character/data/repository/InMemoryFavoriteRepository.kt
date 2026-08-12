package com.narinc.rickandmorty.feature.character.data.repository

import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ---- GEÇİCİ İMPLEMENTASYON (Room'a geçince değişecek) ----
 * @Singleton olmasına DİKKAT ET: uygulama boyunca TEK bir favoriteIds
 * seti olsun istiyoruz, her inject edildiğinde sıfırdan boş bir set
 * oluşmasın. Room'a geçtiğimizde bu implementasyonu SİLİP yerine
 * RoomFavoriteRepository yazacağız -- FavoriteRepository interface'i,
 * ObserveCharacterDetailUseCase, ViewModel, Compose ekranı HİÇBİRİNE
 * dokunmayacağız. Bu, Adım 9'da bahsettiğimiz Dependency Inversion'ın
 * gerçek hayatta ne kazandırdığının somut kanıtı olacak.
 */
@Singleton
class InMemoryFavoriteRepository @Inject constructor() : FavoriteRepository {

    private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

    override fun observeIsFavorite(characterId: Int): Flow<Boolean> {
        return favoriteIds
            .map { it.contains(characterId) }
            .distinctUntilChanged() // aynı değer art arda gelirse tekrar emit etme
    }

    override suspend fun toggleFavorite(characterId: Int) {
        favoriteIds.update { current ->
            if (characterId in current) current - characterId else current + characterId
        }
    }
}