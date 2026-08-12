package com.narinc.rickandmorty.feature.character.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.usecase.ObserveCharacterDetailUseCase
import com.narinc.rickandmorty.feature.character.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ---- KAVRAM: stateIn() -- Adım 13'teki MutableStateFlow'a ALTERNATİF ----
 * Adım 13'te _uiState'i ELLE MutableStateFlow olarak tutup, her durumda
 * "_uiState.value = ..." diye kendimiz güncelliyorduk. Burada FARKLI bir
 * (ve çoğu zaman daha idiomatic kabul edilen) yaklaşım var: elimizde zaten
 * bir Flow (ObserveCharacterDetailUseCase'den geliyor), biz sadece bunu
 * .map() ile UI State'e çeviriyoruz ve .stateIn() ile "StateFlow'a çevir,
 * son değeri cache'le" diyoruz. State'i ELLE hiç set etmiyoruz.
 *
 * SharingStarted.WhileSubscribed(5000): "en az bir collector (Compose
 * ekranı) dinlerken bu Flow'u AKTİF tut, hiç dinleyen kalmazsa 5 saniye
 * bekle sonra durdur" demek. 5 saniyelik tolerans, örn. ekran döndürme
 * sırasında collector'ın anlık kaybolup hemen geri gelmesi durumunda
 * gereksiz yere yeniden başlamayı önlüyor. RxJava'da tam karşılığı yok,
 * en yakın benzetme .replay(1).refCount() kombinasyonu.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    observeCharacterDetailUseCase: ObserveCharacterDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialCharacterId: Int =
        savedStateHandle.get<String>("characterId")?.toIntOrNull() ?: 1

    /**
     * ---- KAVRAM: characterId artık bir MutableStateFlow ----
     * selectCharacter(id) çağrıldığında bu akış yeni bir değer yayınlıyor,
     * flatMapLatest bunu yakalayıp observeCharacterDetailUseCase'i YENİ
     * id ile baştan tetikliyor.
     */
    private val selectedCharacterId = MutableStateFlow(initialCharacterId)


    /**
     * ---- KAVRAM: flatMapLatest ----
     * RxJava'daki switchMap()'in birebir karşılığı. selectedCharacterId her
     * değiştiğinde: (1) önceki observeCharacterDetailUseCase(eskiId) akışı
     * İPTAL EDİLİR, (2) yeni id ile baştan başlanır. "Kullanıcı hızlı hızlı
     * farklı karakterlere geçerse, sadece en son istediğinin sonucunu göster"
     * davranışını tam karşılıyor.
     *
     * ---- ÖNEMLİ YAN FAYDA ----
     * observeCharacterDetailUseCase(id) çağrısı artık property initializer'da
     * DEĞİL, flatMapLatest'in lambda'sının İÇİNDE. Flow operatörleri "cold"dur
     * -- bu lambda, gerçek bir collector bağlanana kadar HİÇ ÇALIŞMAZ. Yani
     * bu çağrı artık constructor anında değil, testte turbine ilk
     * awaitItem()'ı çağırdığında (yani @Test gövdesinde, stub'lar zaten
     * hazırken) tetikleniyor. Bu da @InjectMockKs'i yeniden kullanılabilir
     * hale getiriyor -- birazdan testte göreceksin.
     */
    val uiState: StateFlow<CharacterDetailUiState> = selectedCharacterId
        .flatMapLatest { id -> observeCharacterDetailUseCase(id) }
        .map { result ->
            when (result) {
                is DataResult.Success -> CharacterDetailUiState.Success(
                    character = result.data.character,
                    isFavorite = result.data.isFavorite
                )
                is DataResult.Error -> CharacterDetailUiState.Error(
                    result.message ?: "Bilinmeyen bir hata oluştu"
                )
                is DataResult.Loading -> CharacterDetailUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CharacterDetailUiState.Loading
        )

    fun onFavoriteClick() {
        viewModelScope.launch {
            toggleFavoriteUseCase(selectedCharacterId.value)
        }
    }

    /** Aynı ekranda kalıp id değiştirmek için (örn. "sonraki karakter" butonu). */
    fun selectCharacter(id: Int) {
        selectedCharacterId.value = id
    }
}