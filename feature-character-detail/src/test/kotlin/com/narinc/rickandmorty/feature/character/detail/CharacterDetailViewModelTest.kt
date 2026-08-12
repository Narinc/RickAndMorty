package com.narinc.rickandmorty.feature.character.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.core.testing.MainDispatcherRule
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.model.CharacterDetailData
import com.narinc.rickandmorty.feature.character.domain.model.CharacterStatus
import com.narinc.rickandmorty.feature.character.domain.usecase.ObserveCharacterDetailUseCase
import com.narinc.rickandmorty.feature.character.domain.usecase.ToggleFavoriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * ---- KAVRAM: Turbine ile StateFlow test etme ----
 * RxJava'da `TestObserver` ile ".assertValues(...)" yapardın. Turbine'in
 * karşılığı: `flow.test { ... }` bloğu açar, içinde `awaitItem()` ile
 * flow'un yayınladığı DEĞERLERİ SIRAYLA bekleyip alırsın. Bu, "flow şu
 * anda ne yayınlıyor" diye elle .value okumaktan çok daha güvenilir --
 * ARADAKİ TÜM emisyonları (örn. önce Loading, sonra Success) sırayla
 * doğrulamanı sağlar, hiçbirini atlamana izin vermez.
 *
 * Dikkat çekmek istediğim önemli nokta: cancelAndIgnoreRemainingEvents() — Turbine,
 * test { } bloğu bitmeden tüm emisyonların tüketildiğinden emin olmanı ister
 * (aksi halde "beklenmeyen emisyon var, testin eksik" diye hata verir).
 * StateFlow sonsuz bir akış olduğu için (asla "tamamlanmaz"),
 * test bloğunu bitirmeden önce ya tüm emisyonları awaitItem() ile tüketmen ya da
 * cancelAndIgnoreRemainingEvents() ile "geri kalanını umursamıyorum, testimi bitiriyorum" demen gerekiyor.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var observeCharacterDetailUseCase: ObserveCharacterDetailUseCase

    @MockK
    lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @SpyK
    var savedStateHandle: SavedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))

    @InjectMockKs
    lateinit var viewModel: CharacterDetailViewModel

    private val rick = Character(
        id = 1, name = "Rick Sanchez", status = CharacterStatus.ALIVE,
        species = "Human", imageUrl = "", originName = "Earth (C-137)",
        locationName = "Citadel of Ricks"
    )
    private val morty = rick.copy(id = 2, name = "Morty Smith")

    @Before
    fun setup() {
        // Bu noktada henüz hiçbir stub tanımlı değil, ama ViewModel şimdiden
        // inşa ediliyor. Eskiden bu MockKException fırlatırdı -- artık
        // fırlatmıyor, çünkü observeCharacterDetailUseCase(id) çağrısı
        // flatMapLatest sayesinde HENÜZ TETİKLENMEDİ.
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `selectCharacter cagirildiginda yeni id ile uiState guncellenir`() = runTest {
        // Given -- stub'lar burada, @Test gövdesinde, güvenle tanımlanabiliyor
        every { observeCharacterDetailUseCase(1) } returns flowOf(
            DataResult.Success(CharacterDetailData(character = rick, isFavorite = false))
        )
        every { observeCharacterDetailUseCase(2) } returns flowOf(
            DataResult.Success(CharacterDetailData(character = morty, isFavorite = false))
        )

        // When / Then
        viewModel.uiState.test {
            // StateFlow'un initialValue'su Loading, ilk emisyon her zaman bu.
            assertTrue(awaitItem() is CharacterDetailUiState.Loading)

            val first = awaitItem()
            assertTrue(first is CharacterDetailUiState.Success)
            assertEquals("Rick Sanchez", (first as CharacterDetailUiState.Success).character.name)

            // id'yi değiştiriyoruz -- flatMapLatest eskisini iptal edip
            // yeniden tetikleniyor.
            viewModel.selectCharacter(2)

            val second = awaitItem()
            assertTrue(second is CharacterDetailUiState.Success)
            assertEquals("Morty Smith", (second as CharacterDetailUiState.Success).character.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClick guncel secili id ile toggleFavoriteUseCase'i cagirir`() = runTest {
        // Given
        every { observeCharacterDetailUseCase(1) } returns flowOf(
            DataResult.Success(CharacterDetailData(character = rick, isFavorite = false))
        )
        every { observeCharacterDetailUseCase(2) } returns flowOf(
            DataResult.Success(CharacterDetailData(character = morty, isFavorite = false))
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            awaitItem() // Rick

            // id'yi değiştiriyoruz
            viewModel.selectCharacter(2)
            awaitItem() // Morty

            cancelAndIgnoreRemainingEvents()
        }

        // When
        viewModel.onFavoriteClick()
        advanceUntilIdle() // <-- YENİ: kuyruktaki launch{} bloğunu şimdi çalıştır

        // Then -- toggleFavoriteUseCase'in artık 1 DEĞİL, GÜNCEL id olan 2 ile
        // çağrıldığını doğruluyoruz. Bu, selectedCharacterId.value kullanmanın
        // (sabit bir constructor parametresi yerine) doğruluğunu kanıtlıyor.
        coVerify { toggleFavoriteUseCase(2) }
    }

    @Test
    fun `observeCharacterDetailUseCase hata donerse uiState Error olur`() = runTest {
        // Given
        every { observeCharacterDetailUseCase(1) } returns flowOf(
            DataResult.Error(exception = RuntimeException("404"), message = "Karakter bulunamadı")
        )

        // When / Then
        viewModel.uiState.test {
            assertTrue(awaitItem() is CharacterDetailUiState.Loading)

            val errorState = awaitItem()
            assertTrue(errorState is CharacterDetailUiState.Error)
            assertEquals("Karakter bulunamadı", (errorState as CharacterDetailUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}