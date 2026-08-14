package com.narinc.rickandmorty.feature.character.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.core.common.DispatcherProvider
import com.narinc.rickandmorty.core.common.safeApiCall
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import com.narinc.rickandmorty.feature.character.data.mapper.toDomain
import com.narinc.rickandmorty.feature.character.data.paging.CharacterRemoteMediator
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val dispatcherProvider: DispatcherProvider,
    private val database: AppDatabase
) : CharacterRepository {

    override suspend fun getCharacterDetail(id: Int): DataResult<Character> {
        return safeApiCall(dispatcherProvider) {
            apiService.getCharacterDetail(id).toDomain()
        }
    }

    /**
     * ---- KAVRAM: Pager ----
     * Pager, PagingSource'u sarmalayıp gerçek bir Flow<PagingData<T>>
     * üreten fabrika. PagingConfig ile "kaç öğe geldiğinde bir sayfa daha
     * iste", "ilk yüklemede kaç öğe getir" gibi davranışları ayarlıyoruz.
     *
     * pagingSourceFactory bir LAMBDA -- her yeni "generation" (örn. refresh
     * sonrası) için TAZE bir PagingSource örneği üretmesi gerekiyor, bu
     * yüzden var olan bir örneği değil, "nasıl üretileceğini" veriyoruz.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = CharacterRemoteMediator(apiService, database),
            pagingSourceFactory = { database.characterDao().pagingSource() }
        ).flow.map { pagingData -> pagingData.map { entity -> entity.toDomain() } }
    }
}