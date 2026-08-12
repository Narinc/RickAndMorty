package com.narinc.rickandmorty.feature.character.domain.usecase

import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(characterId: Int) {
        favoriteRepository.toggleFavorite(characterId)
    }
}