package com.narinc.rickandmorty.feature.character.detail

import com.narinc.rickandmorty.feature.character.domain.model.Character

/**
 * ---- KAVRAM: UI State sealed class ----
 * Bir ekranın alabileceği TÜM durumları burada modelliyoruz. RxJava'da
 * bunu genelde ayrı ayrı LiveData/Subject'ler ile yapardık (isLoading,
 * errorMessage, data gibi üç ayrı stream) -- bu YANLIŞ KOMBİNASYONLARA
 * açık bir yaklaşımdır (örn. isLoading=true VE data dolu olabilir gibi
 * anlamsız bir state'e düşebilirsin).
 *
 * Tek bir sealed class ile, ekran HER ZAMAN bu üç durumdan TAM OLARAK
 * birinde olur, asla ikisi birden değil. Compose tarafında "when" ile
 * tüketeceğiz, derleyici tüm dalları ele almanı zorunlu kılacak.
 */
sealed class CharacterDetailUiState {
    data object Loading : CharacterDetailUiState()
    data class Success(val character: Character) : CharacterDetailUiState()
    data class Error(val message: String) : CharacterDetailUiState()
}