package com.narinc.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint

/**
 * ---- KAVRAM: @AndroidEntryPoint ----
 * Hilt'in bir Activity/Fragment'a bağımlılık inject edebilmesi için,
 * o sınıfı bu annotation ile işaretlemen gerekir. Bunu unutursan,
 * Hilt o Activity'nin varlığından haberdar olmaz, hiçbir şey inject
 * edemez -- sık yapılan bir hata, aklında olsun.
 *
 * ---- KAVRAM: setContent { } ----
 * XML dünyasında setContentView(R.layout.activity_main) yazardın.
 * Compose'da setContent { } içine DOĞRUDAN Kotlin ile UI ağacını yazarsın.
 * Ayrı bir XML dosyası, ayrı bir "layout inflate" süreci yok.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyApp() // deneme
        }
    }
}

/**
 * ---- KAVRAM: @Composable fun ----
 * Bu bir "fonksiyon" ama normal bir fonksiyon gibi davranmıyor. @Composable
 * annotation'ı, Kotlin compiler'ına "bu fonksiyon UI ağacı tanımlıyor,
 * içindeki state değiştiğinde bu fonksiyonu yeniden çalıştırabilirsin"
 * der.
 */
@Composable
fun RickAndMortyApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RickAndMortyNavHost()
        }
    }
}

/**
 * ---- KAVRAM: @Preview ----
 * Bu annotation, Android Studio'nun "Design" panelinde bu Composable'ı
 * DERLEME YAPMADAN, EMULATOR AÇMADAN canlı önizlemesini sağlar. XML
 * dünyasındaki Layout Editor'ün önizlemesine benzer ama çok daha hızlı --
 * her kod değişikliğinde anlık güncellenir.
 */
@Preview(showBackground = true)
@Composable
fun RickAndMortyAppPreview() {
    RickAndMortyApp()
}