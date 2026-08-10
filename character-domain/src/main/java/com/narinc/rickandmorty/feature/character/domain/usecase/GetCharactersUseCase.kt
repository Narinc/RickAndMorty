package com.narinc.rickandmorty.feature.character.domain.usecase

import androidx.paging.PagingData
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<PagingData<Character>> {
        return repository.getCharacters()
    }
}