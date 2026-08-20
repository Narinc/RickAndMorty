package com.narinc.rickandmorty.feature.character.detail

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    observeCharacterDetailUseCase: ObserveCharacterDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {


    private val selectedCharacterId = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CharacterDetailUiState> = selectedCharacterId
        .filterNotNull()
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
            selectedCharacterId.value?.let { toggleFavoriteUseCase(it) }
        }
    }

    fun selectCharacter(id: Int) {
        selectedCharacterId.value = id
    }
}