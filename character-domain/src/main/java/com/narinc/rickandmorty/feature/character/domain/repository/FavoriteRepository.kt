package com.narinc.rickandmorty.feature.character.domain.repository

import com.narinc.rickandmorty.feature.character.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    /**
     * ---- KAVRAM: neden suspend fun değil, Flow<Boolean>? ----
     * getCharacterDetail gibi "bir kere sor, cevabı al" değil, bu "SÜREKLİ
     * GÜNCEL DURUMU izle" demek. Kullanıcı favori butonuna bastığında,
     * bu Flow'u dinleyen HERKES (örn. hem detay ekranı hem ileride liste
     * ekranındaki kalp ikonu) otomatik güncellensin istiyoruz.
     */
    fun observeIsFavorite(characterId: Int): Flow<Boolean>

    suspend fun toggleFavorite(characterId: Int)

    fun observeFavoriteCharacters(): Flow<List<Character>>
}