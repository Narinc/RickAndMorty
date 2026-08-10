package com.narinc.rickandmorty.feature.character.domain.repository

import androidx.paging.PagingData
import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.model.Character
import kotlinx.coroutines.flow.Flow

/**
 * ---- KAVRAM: Repository Pattern + Dependency Inversion ----
 * Bu bir interface, implementasyon değil. "character-data" modülü bunu
 * implement edecek (Retrofit çağıracak, Room'a yazacak vs.) ama BURADA,
 * domain katmanında, HİÇBİR implementasyon detayı yok.
 *
 * ViewModel (feature katmanında) bu interface'e bağımlı olacak, somut
 * sınıfa değil. Hilt, runtime'da hangi implementasyonu vereceğine karar
 * verecek (Adım ~11'de @Binds ile göreceğiz). Bu sayede:
 * - Test yazarken gerçek Retrofit yerine sahte (fake/mock) bir
 *   CharacterRepository verebiliriz.
 * - Yarın "cache stratejisini değiştir" dersen, sadece implementasyonu
 *   değiştirirsin, ViewModel'e dokunmazsın.
 */
interface CharacterRepository {
    suspend fun getCharacterDetail(id: Int): DataResult<Character>

    /**
     * ---- KAVRAM: Flow<PagingData<T>> ----
     * StateFlow'dan (Adım 13) farklı bir akış türü. StateFlow "tek bir son
     * değeri tutan" bir akıştı. Bu ise "sayfalanmış listenin GÜNCEL halini
     * temsil eden" özel bir Flow türü. PagingData<T>, içinde henüz
     * yüklenmemiş sayfaları da "placeholder" olarak tutan akıllı bir
     * koleksiyon -- kullanıcı listeyi aşağı kaydırdıkça, Paging kütüphanesi
     * arka planda yeni sayfaları otomatik ister ve bu Flow yeni bir
     * PagingData yayınlar.
     */
    fun getCharacters(): Flow<PagingData<Character>>
}