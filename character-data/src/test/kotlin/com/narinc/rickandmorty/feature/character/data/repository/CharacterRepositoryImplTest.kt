package com.narinc.rickandmorty.feature.character.data.repository

import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.core.common.DispatcherProvider
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.core.network.model.CharacterDto
import com.narinc.rickandmorty.core.network.model.LocationRefDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ---- KAVRAM: MockK ile "sahte" (fake) bağımlılık ----
 * RxJava/Mockito döneminden tanıdık bir kavram: gerçek RickAndMortyApiService
 * yerine, "bu metod çağrılırsa şunu dön" diyen sahte bir nesne veriyoruz.
 * Böylece gerçek bir network isteği atmadan, Repository'nin MANTIĞINI
 * (mapping doğru mu, hata yönetimi doğru mu) izole test edebiliyoruz.
 *
 * mockk<T>()      -> sahte nesne oluşturur
 * coEvery { ... }  -> "every" in suspend fonksiyon versiyonu (coroutine-aware)
 */
class CharacterRepositoryImplTest {

    private fun TestScope.testDispatchers(): DispatcherProvider {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return object : DispatcherProvider {
            override val main = dispatcher
            override val io = dispatcher
            override val default = dispatcher
        }
    }

    private val fakeDto = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        gender = "Male",
        image = "https://example.com/rick.png",
        origin = LocationRefDto(name = "Earth (C-137)", url = ""),
        location = LocationRefDto(name = "Citadel of Ricks", url = ""),
        episodeUrls = listOf("https://example.com/episode/1")
    )

    @Test
    fun `getCharacterDetail basarili olursa domain modele dogru map eder`() = runTest {
        // Given
        val apiService = mockk<RickAndMortyApiService>()
        coEvery { apiService.getCharacterDetail(1) } returns fakeDto
        val repository = CharacterRepositoryImpl(apiService, testDispatchers())

        // When
        val result = repository.getCharacterDetail(1)

        // Then
        assertTrue(result is DataResult.Success)
        val character = (result as DataResult.Success).data
        assertEquals("Rick Sanchez", character.name)
        assertEquals("Earth (C-137)", character.originName)
    }

    @Test
    fun `getCharacterDetail api hata firlatirsa Error doner`() = runTest {
        // Given
        val apiService = mockk<RickAndMortyApiService>()
        coEvery { apiService.getCharacterDetail(999) } throws RuntimeException("404 Not Found")
        val repository = CharacterRepositoryImpl(apiService, testDispatchers())

        // When
        val result = repository.getCharacterDetail(999)

        // Then
        assertTrue(result is DataResult.Error)
        assertEquals("404 Not Found", (result as DataResult.Error).message)
    }
}