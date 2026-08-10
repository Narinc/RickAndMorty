package com.narinc.rickandmorty.feature.character.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ---- KAVRAM: @HiltViewModel ----
 * Bu annotation, Hilt'e "bu ViewModel'i sen inşa et" der. Activity/Fragment'ta
 * `hiltViewModel()` çağırdığında, Hilt buradaki constructor'a bakıp
 * GetCharacterDetailUseCase'i otomatik sağlar -- sen elle
 * "CharacterDetailViewModel(GetCharacterDetailUseCase(CharacterRepositoryImpl(...)))"
 * gibi zincir kurmak zorunda kalmazsın. Bu zincirin TAMAMINI (Retrofit'ten
 * UseCase'e kadar) Hilt senin yerine örüyor.
 *
 * ---- KAVRAM: MutableStateFlow + StateFlow (dışa expose) ----
 * MutableStateFlow -- RxJava'daki BehaviorSubject'in tam karşılığı:
 *   - Her zaman "şu anki" bir değeri vardır (ilk değeri constructor'da veriyoruz)
 *   - Yeni bir "collector" (RxJava'daki "subscriber") bağlandığında,
 *     ONA HEMEN son değeri verir, geçmiş yayınları beklemez.
 *   - .value ile senkron okuyabilir/yazabilirsin.
 *
 * Neden private MutableStateFlow + public StateFlow ayrımı yapıyoruz?
 * RxJava'da da benzer bir pratik vardı: dışarıya sadece "gözlemleme"
 * (read-only) yeteneği vermek, "yayınlama" (write) yeteneğini sınıfın
 * içinde saklamak. .asStateFlow() bunun için var -- dışarıdakiler
 * .value = ... diyerek state'i elle değiştiremesin, sadece ViewModel'in
 * kendisi değiştirebilsin. Bu bir Encapsulation (SOLID'in bir parçası) örneği.
 *
 * ---- KAVRAM: viewModelScope ----
 * RxJava döneminde her Observable'ı elle .dispose() etmen gerekirdi
 * (CompositeDisposable ile), unutursan memory leak olurdu. Coroutine'de
 * viewModelScope, ViewModel'in onCleared() çağrıldığı anda İÇİNDEKİ TÜM
 * coroutine'leri OTOMATİK iptal eder. Elle dispose etmek yok.
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        loadCharacter(id = 1) // Şimdilik sabit id=1 (Rick), navigation'a geçince dinamik olacak
    }

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            _uiState.value = CharacterDetailUiState.Loading
            when (val result = getCharacterDetailUseCase(id)) {
                is DataResult.Success -> {
                    _uiState.value = CharacterDetailUiState.Success(result.data)
                }
                is DataResult.Error -> {
                    _uiState.value = CharacterDetailUiState.Error(
                        result.message ?: "Bilinmeyen bir hata oluştu"
                    )
                }
                is DataResult.Loading -> {
                    _uiState.value = CharacterDetailUiState.Loading
                }
            }
        }
    }
}