package com.narinc.rickandmorty.feature.character.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.narinc.rickandmorty.core.ui.characterlistitem.CharacterListItem
import com.narinc.rickandmorty.feature.character.domain.model.Character

/**
 * ---- KAVRAM: collectAsLazyPagingItems() ----
 * Flow<PagingData<Character>>'ı, LazyColumn'un doğrudan kullanabileceği
 * bir "LazyPagingItems" nesnesine çeviriyor. Bu nesne üzerinden hem
 * tek tek öğelere (items[index]) hem de yükleme durumlarına
 * (loadState.refresh, loadState.append) erişebiliyoruz.
 */
@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = hiltViewModel(),
    onCharacterClick: (characterId: Int) -> Unit
) {
    val lazyPagingItems = viewModel.characterPagingData.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        CharacterList(
            lazyPagingItems = lazyPagingItems,
            onCharacterClick = onCharacterClick
        )
    }
}

@Composable
private fun CharacterList(
    lazyPagingItems: LazyPagingItems<Character>,
    onCharacterClick: (characterId: Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // ---- KAVRAM: itemKey ----
        // Recomposition sırasında Compose'un öğeleri doğru eşleştirmesi
        // (örn. scroll pozisyonunu korumak) için stabil bir key veriyoruz.
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.id }
        ) { index ->
            val character = lazyPagingItems[index]
            if (character != null) {
                CharacterListItem(
                    item = character.toCharacterListItemUiModel(),
                    onClick = { onCharacterClick(character.id) }
                )
                HorizontalDivider()
            }
        }

        // ---- KAVRAM: loadState.append ----
        // Kullanıcı listenin sonuna yaklaştığında Paging otomatik olarak
        // yeni sayfa ister -- bu sırada "append" durumu Loading olur.
        // Burada listenin ALTINA küçük bir loading indicator koyuyoruz.
        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item { LoadingRow() }
            }

            is LoadState.Error -> {
                item { Text("Daha fazla yüklenemedi", modifier = Modifier.padding(16.dp)) }
            }

            else -> Unit
        }

        // ---- KAVRAM: loadState.refresh ----
        // İLK yükleme (ya da pull-to-refresh) durumunu temsil eder.
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                item { LoadingRow() }
            }

            is LoadState.Error -> {
                item { Text("Liste yüklenemedi", modifier = Modifier.padding(16.dp)) }
            }

            else -> Unit
        }
    }
}

@Composable
private fun LoadingRow() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}