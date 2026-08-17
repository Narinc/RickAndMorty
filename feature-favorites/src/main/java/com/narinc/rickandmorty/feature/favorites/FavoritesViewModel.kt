package com.narinc.rickandmorty.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.usecase.ObserveFavoriteCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase
) : ViewModel() {

    val favotites: StateFlow<List<Character>> = observeFavoriteCharactersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}