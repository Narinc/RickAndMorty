package com.narinc.rickandmorty.feature.character.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import com.narinc.rickandmorty.feature.character.data.local.entity.RemoteKeysEntity
import com.narinc.rickandmorty.feature.character.data.mapper.toEntity

/**
 * ---- KAVRAM: RemoteMediator ----
 * PagingSource'un (Adım 14) "TEK bir veri kaynağından oku" mantığından
 * farklı olarak, RemoteMediator "İKİ kaynağı KOORDİNE ET" görevi görüyor:
 * Room (local) ve network (remote). Room'daki veri yetmediğinde devreye
 * girip network'ten çekip Room'a YAZIYOR -- kendisi asla doğrudan UI'ye
 * veri döndürmüyor, sadece Room'u besliyor.
 *
 * ---- KAVRAM: LoadType ----
 * REFRESH: ilk yükleme ya da pull-to-refresh (baştan başla)
 * PREPEND: kullanıcı listenin BAŞINA doğru kaydırdı (bizim senaryomuzda
 *          kullanılmıyor çünkü sayfa 1'den başlıyoruz, geriye gidecek yer yok)
 * APPEND: kullanıcı listenin SONUNA yaklaştı, sıradaki sayfayı getir
 */
@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val apiService: RickAndMortyApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    private val characterDao = database.characterDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    1
                }

                LoadType.PREPEND -> {
                    // API'de "baştan öncesi" yok, PREPEND'i her zaman
                    // "yapacak bir şey yok" olarak kapatıyoruz.
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // ---- KAVRAM: anchorPosition ----
                    // Kullanıcının şu an listede EN SON gördüğü öğenin
                    // pozisyonu. Buradan o öğenin remote key'ine ulaşıp,
                    // "bu öğeden sonraki sayfa neydi" bilgisini okuyoruz.
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    val remoteKeys = remoteKeysDao.remoteKeysByCharacterId(lastItem.id)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextKey
                }
            }

            val response = apiService.getCharacters(page = page)
            val characters = response.results.map { it.toEntity() }
            val endOfPaginationReached = response.info.next == null

            // ---- KAVRAM: withTransaction ----
            // "Eski veriyi sil + yeni veriyi yaz + remote key'leri güncelle"
            // işlemlerinin TÜMÜNÜN BİRLİKTE başarılı olmasını (ya da hiçbirinin
            // olmamasını) garanti ediyoruz. Yarıda kesilirse (örn. uygulama
            // çökerse), veritabanı YARIM GÜNCELLENMİŞ bir durumda kalmaz.
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    characterDao.clearAll()
                    remoteKeysDao.clearAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val remoteKeys = characters.map { character ->
                    RemoteKeysEntity(
                        characterId = character.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                remoteKeysDao.insertAll(remoteKeys)
                characterDao.insertAll(characters)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}