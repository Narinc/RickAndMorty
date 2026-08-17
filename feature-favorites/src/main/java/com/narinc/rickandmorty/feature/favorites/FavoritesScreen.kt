package com.narinc.rickandmorty.feature.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.narinc.rickandmorty.core.ui.characterlistitem.CharacterListItem

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onCharacterClick: (characterId: Int) -> Unit
) {

    val favorites by viewModel.favotites.collectAsStateWithLifecycle()

    if (favorites.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Henüz favori karakter yok")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favorites, key = { it.id }) { character ->
                CharacterListItem(
                    item = character.toUiModel(),
                    onClick = { onCharacterClick(character.id) }
                )
                HorizontalDivider()
            }
        }
    }
}