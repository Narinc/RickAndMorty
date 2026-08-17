package com.narinc.rickandmorty.feature.character.data.repository

import com.narinc.rickandmorty.feature.character.data.local.dao.FavoriteDao
import com.narinc.rickandmorty.feature.character.data.local.entity.FavoriteEntity
import com.narinc.rickandmorty.feature.character.data.mapper.toDomain
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ---- DEĞİŞEN: InMemoryFavoriteRepository -> RoomFavoriteRepository ----
 * Domain katmanındaki FavoriteRepository interface'i, ObserveCharacterDetailUseCase,
 * ViewModel, Compose ekranı -- HİÇBİRİNE DOKUNMADIK. Sadece implementasyonu
 * değiştirdik. Adım 9 ve 17'de bahsettiğimiz Dependency Inversion'ın tam
 * olarak kazandırdığı şey bu: implementasyon detayı (bellek içi mi, SQLite
 * mi) değişse bile, onu KULLANAN katmanlar hiç haberdar olmuyor.
 */
class RoomFavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeIsFavorite(characterId: Int): Flow<Boolean> {
        return favoriteDao.observeIsFavorite(characterId)
    }

    override suspend fun toggleFavorite(characterId: Int) {
        val isFavorite = favoriteDao.isFavoriteOnce(characterId)
        if (isFavorite) {
            favoriteDao.delete(characterId)
        } else {
            favoriteDao.insert(FavoriteEntity(characterId))
        }
    }

    override fun observeFavoriteCharacters(): Flow<List<Character>> {
        return favoriteDao.observeFavoriteCharacters().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}