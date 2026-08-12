package com.narinc.rickandmorty.feature.character.domain.usecase

import app.cash.turbine.test
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.model.CharacterStatus
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ---- KAVRAM: Sahte (fake) bir Flow tabanlı repository ile combine() testi ----
 * Burada FavoriteRepository'yi MockK ile değil, GERÇEK bir MutableStateFlow
 * kullanan basit bir "fake" sınıfla test ediyoruz -- çünkü asıl test etmek
 * istediğimiz şey "favoriteFlow yeni bir değer yayınladığında, combine()
 * bunu yakalayıp yeni bir DataResult üretiyor mu?" Bunu gerçekten CANLI
 * bir Flow ile test etmek, mock'lamaktan daha güvenilir -- çünkü combine()
 * kütüphanesinin GERÇEK zamanlama davranışını sınıyoruz.
 */
private class FakeFavoriteRepository : FavoriteRepository {
    private val favorites = MutableStateFlow<Set<Int>>(emptySet())

    override fun observeIsFavorite(characterId: Int) =
        favorites.map { it.contains(characterId) }.distinctUntilChanged()

    override suspend fun toggleFavorite(characterId: Int) {
        favorites.update { if (characterId in it) it - characterId else it + characterId }
    }
}

class ObserveCharacterDetailUseCaseTest {

    private val fakeCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        imageUrl = "https://example.com/rick.png",
        originName = "Earth (C-137)",
        locationName = "Citadel of Ricks"
    )

    @Test
    fun `favori durumu degistiginde combine yeni CharacterDetailData yayinlar`() = runTest {
        // Given
        val characterRepository = mockk<CharacterRepository>()
        coEvery { characterRepository.getCharacterDetail(1) } returns DataResult.Success(fakeCharacter)

        val favoriteRepository = FakeFavoriteRepository()
        val useCase = ObserveCharacterDetailUseCase(characterRepository, favoriteRepository)

        // When / Then
        useCase(characterId = 1).test {
            val first = awaitItem()
            assertTrue(first is DataResult.Success)
            assertEquals(false, (first as DataResult.Success).data.isFavorite)

            // Favori durumunu DIŞARIDAN değiştiriyoruz -- gerçek uygulamada
            // bu, kullanıcının favori butonuna basması olurdu.
            favoriteRepository.toggleFavorite(1)

            val second = awaitItem()
            assertTrue(second is DataResult.Success)
            assertEquals(true, (second as DataResult.Success).data.isFavorite)
            // Character verisi DEĞİŞMEDİ -- network'e tekrar gidilmedi,
            // sadece favori state'i güncellendi.
            assertEquals(fakeCharacter.name, second.data.character.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `karakter getirilemezse Error yayinlar`() = runTest {
        // Given
        val characterRepository = mockk<CharacterRepository>()
        coEvery { characterRepository.getCharacterDetail(999) } returns DataResult.Error(
            exception = RuntimeException("404"),
            message = "Karakter bulunamadı"
        )
        val useCase = ObserveCharacterDetailUseCase(characterRepository, FakeFavoriteRepository())

        // When / Then
        useCase(characterId = 999).test {
            val result = awaitItem()
            assertTrue(result is DataResult.Error)
            assertEquals("Karakter bulunamadı", (result as DataResult.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}