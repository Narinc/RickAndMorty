package com.narinc.rickandmorty.feature.character.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.narinc.rickandmorty.feature.character.domain.model.Character

/**
 * ---- KAVRAM: collectAsStateWithLifecycle() ----
 * StateFlow'u Compose'un anlayacağı bir "State" nesnesine çeviriyor.
 * "WithLifecycle" kısmı önemli: ekran arka plana atıldığında (Activity
 * STOPPED durumundayken) otomatik olarak collection'ı DURDURUR, ekran
 * tekrar öne geldiğinde devam eder. Böylece görünmeyen bir ekran için
 * gereksiz network/CPU işi yapılmaz -- RxJava'da bunu manuel
 * onStart/onStop içinde subscribe/dispose ederek yapardın.
 *
 * ---- KAVRAM: Recomposition burada CANLI ----
 * uiState değiştiğinde (Loading -> Success gibi), Compose bu fonksiyonu
 * OTOMATİK yeniden çalıştırır ve ekranı günceller. Sen "TextView.setText(...)"
 * gibi elle bir güncelleme çağırmıyorsun -- state değişti, UI kendini
 * senin tanımına göre yeniden çizdi.
 */
@Composable
fun CharacterDetailScreen(
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is CharacterDetailUiState.Loading -> {
            LoadingContent()
        }
        is CharacterDetailUiState.Success -> {
            CharacterContent(character = state.character)
        }
        is CharacterDetailUiState.Error -> {
            ErrorContent(message = state.message)
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CharacterContent(character: Character) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = character.name, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Tür: ${character.species}")
        Text(text = "Durum: ${character.status}")
        Text(text = "Köken: ${character.originName}")
        Text(text = "Konum: ${character.locationName}")
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hata: $message")
    }
}