package com.narinc.rickandmorty.feature.character.domain.repository

import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.model.Character

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
}