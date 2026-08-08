package com.narinc.rickandmorty.feature.character.data.repository

import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.core.common.DispatcherProvider
import com.narinc.rickandmorty.core.common.safeApiCall
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.feature.character.data.mapper.toDomain
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * ---- KAVRAM: Interface implementasyonu ----
 * "CharacterRepository" interface'ini burada, data katmanında, somutlaştırıyoruz.
 * Adım 9'daki yorumu hatırlarsan: domain katmanı bu sınıfın VARLIĞINDAN
 * HABERSİZ. ViewModel de aslında bu sınıfı değil, interface'i bilecek --
 * Hilt runtime'da "CharacterRepository isteyen birine CharacterRepositoryImpl
 * ver" diye bağlayacak (Adım 11'de @Binds ile).
 *
 * Burada Adım 5'te yazdığımız safeApiCall'ı GERÇEK bir senaryoda kullanıyoruz:
 * try/catch'i, dispatcher yönetimini, DataResult sarmalamasını tekrar
 * yazmıyoruz -- hepsi safeApiCall'da hazır.
 */
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: RickAndMortyApiService,
    private val dispatcherProvider: DispatcherProvider
) : CharacterRepository {

    override suspend fun getCharacterDetail(id: Int): DataResult<Character> {
        return safeApiCall(dispatcherProvider) {
            apiService.getCharacterDetail(id).toDomain()
        }
    }
}