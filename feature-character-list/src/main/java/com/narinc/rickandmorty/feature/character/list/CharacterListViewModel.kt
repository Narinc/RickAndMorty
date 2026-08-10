package com.narinc.rickandmorty.feature.character.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ---- KAVRAM: .cachedIn(viewModelScope) ----
 * Bu satır ÇOK ÖNEMLİ. cachedIn olmadan, kullanıcı ekranı döndürdüğünde
 * (configuration change) ya da başka bir Composable recomposition
 * tetiklediğinde, Paging BAŞTAN İNDİRMEYE başlar -- kullanıcı zaten
 * gördüğü sayfaları tekrar network'ten çeker.
 *
 * cachedIn(viewModelScope), bu Flow'u ViewModel'in ömrü boyunca "paylaşımlı"
 * (multicasted) hale getirir -- RxJava'daki .share() / .replay() operatörlerine
 * çok benzer bir kavram. Yani Flow bir kere "canlı" hale gelir, kim
 * collect ederse etsin AYNI, cache'lenmiş veriyi görür, tekrar network
 * isteği atılmaz.
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    val characterPagingData: Flow<PagingData<Character>> =
        getCharactersUseCase()
            .cachedIn(viewModelScope)
}