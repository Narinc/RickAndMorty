package com.narinc.rickandmorty.feature.character.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.core.network.model.CharacterDto
import com.narinc.rickandmorty.core.network.model.CharacterResponseDto
import com.narinc.rickandmorty.core.network.model.LocationRefDto
import com.narinc.rickandmorty.core.network.model.PageInfoDto
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import com.narinc.rickandmorty.feature.character.data.local.entity.RemoteKeysEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ---- KAVRAM: RemoteMediator.load()'ı DOĞRUDAN çağırmak ----
 * Gerçek bir Pager/UI zinciri kurmuyoruz -- CharacterRemoteMediator'ı
 * elle inşa edip load(loadType, state) fonksiyonunu KENDİMİZ çağırıyoruz.
 * Bu, RemoteMediator'ı izole test etmenin en direkt yolu.
 *
 * ---- KAVRAM: PagingState'i elle inşa etmek ----
 * PagingState, normalde Paging kütüphanesinin kendisi tarafından, gerçek
 * bir kullanıcı scroll ederken oluşturuluyor. Test için, "kullanıcı şu an
 * bu sayfayı görüyormuş gibi davran" diye BİZ elle bir PagingState
 * inşa ediyoruz -- APPEND senaryosunda mediator'ın "en son görünen öğe"yi
 * (lastItemOrNull) doğru okuyup okumadığını bu şekilde test edebiliyoruz.
 */
@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class CharacterRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private lateinit var apiService: RickAndMortyApiService

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        apiService = mockk()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun fakeDto(id: Int) = CharacterDto(
        id = id,
        name = "Character $id",
        status = "Alive",
        species = "Human",
        gender = "Male",
        image = "",
        origin = LocationRefDto("Earth", ""),
        location = LocationRefDto("Earth", ""),
        episodeUrls = emptyList()
    )

    @Test
    fun refresh_basarili_olursa_room_a_yazar_ve_eski_veriyi_temizler() = runTest {
        // Given -- Room'da ESKİ bir kayıt var, REFRESH bunu silip yenisini yazmalı
        database.characterDao().insertAll(listOf(
            CharacterEntity(999, "Eski Karakter", "Dead", "Alien", "unknown", "", "?", "?")
        ))

        coEvery { apiService.getCharacters(page = 1) } returns CharacterResponseDto(
            info = PageInfoDto(count = 2, pages = 1, next = null, prev = null),
            results = listOf(fakeDto(1), fakeDto(2))
        )

        val mediator = CharacterRemoteMediator(apiService, database)
        val state = PagingState<Int, CharacterEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.REFRESH, state)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(true, (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val allCharacters = database.characterDao().pagingSource()
        // Eski kayıt (999) silinmiş, yeni ikisi (1, 2) eklenmiş olmalı
        val page = TestPagerHelper.loadFirstPage(allCharacters)
        assertEquals(listOf(1, 2), page.map { it.id })
    }

    @Test
    fun append_dogru_sayfa_key_ile_network_cagirir_ve_ekler() = runTest {
        // Given -- Room'da 1. sayfadan gelmiş bir karakter ve onun remote key'i var
        val existingCharacter = CharacterEntity(1, "Rick Sanchez", "Alive", "Human", "Male", "", "Earth", "Earth")
        database.characterDao().insertAll(listOf(existingCharacter))
        database.remoteKeysDao().insertAll(listOf(
            RemoteKeysEntity(characterId = 1, prevKey = null, nextKey = 2)
        ))

        coEvery { apiService.getCharacters(page = 2) } returns CharacterResponseDto(
            info = PageInfoDto(count = 40, pages = 2, next = null, prev = "page1"),
            results = listOf(fakeDto(3), fakeDto(4))
        )

        val mediator = CharacterRemoteMediator(apiService, database)
        val page = PagingSource.LoadResult.Page<Int, CharacterEntity>(
            data = listOf(existingCharacter),
            prevKey = null,
            nextKey = null
        )
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.APPEND, state)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(true, (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val allIds = TestPagerHelper.loadFirstPage(database.characterDao().pagingSource()).map { it.id }
        assertEquals(listOf(1, 3, 4), allIds) // eski karakter KORUNDU, yenileri EKLENDİ (REFRESH değil, APPEND)
    }

    @Test
    fun network_hata_verirse_MediatorResult_Error_doner() = runTest {
        // Given
        coEvery { apiService.getCharacters(page = 1) } throws RuntimeException("Network koptu")

        val mediator = CharacterRemoteMediator(apiService, database)
        val state = PagingState<Int, CharacterEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // When
        val result = mediator.load(LoadType.REFRESH, state)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}