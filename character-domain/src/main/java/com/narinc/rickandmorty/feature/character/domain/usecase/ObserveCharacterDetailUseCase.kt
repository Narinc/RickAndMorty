package com.narinc.rickandmorty.feature.character.domain.usecase

import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.model.CharacterDetailData
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * ---- KAVRAM: Facade UseCase ----
 * ViewModel artık "karakteri getir" ve "favori mi kontrol et" diye İKİ AYRI
 * çağrı yapıp bunları kendi içinde birleştirmiyor -- bu iş burada, domain
 * katmanında, TEK BİR UseCase'e toplanıyor. ViewModel sadece "bana ekranın
 * ihtiyacı olan veriyi ver" diyor, NASIL birleştirildiğini bilmiyor.
 *
 * ---- KAVRAM: flow { } builder ----
 * getCharacterDetailUseCase suspend bir fonksiyon (tek seferlik sonuç
 * döner). Onu combine() ile birleştirebilmek için önce bir Flow'a
 * "sarmalamamız" gerekiyor -- flow { emit(...) } tam olarak bunu yapıyor:
 * içinde bir kere emit çağrılan, sonra tamamlanan bir Flow üretiyor.
 *
 * ---- KAVRAM: combine() ----
 * RxJava'daki Observable.combineLatest()'in birebir karşılığı. İki Flow'u
 * (characterFlow ve favoriteFlow) alır, İKİSİNDEN de en son değer geldiğinde
 * (ilk ikisi geldiğinde VE her ikisinden biri her güncellendiğinde) verdiğin
 * lambda'yı çalıştırıp YENİ bir değer yayınlar.
 *
 * Pratik sonucu: kullanıcı favori butonuna bastığında, characterFlow hiç
 * yeniden çalışmaz (zaten tamamlanmıştı) ama favoriteFlow yeni bir değer
 * yayınlar, combine() bu ikisini tekrar birleştirip GÜNCEL isFavorite ile
 * yeni bir CharacterDetailData yayınlar. Network'e tekrar gitmeden, UI
 * otomatik güncellenir.
 */
class ObserveCharacterDetailUseCase @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(characterId: Int): Flow<DataResult<CharacterDetailData>> {
        val characterFlow: Flow<DataResult<com.narinc.rickandmorty.feature.character.domain.model.Character>> =
            flow { emit(characterRepository.getCharacterDetail(characterId)) }

        val favoriteFlow: Flow<Boolean> = favoriteRepository.observeIsFavorite(characterId)

        return combine(characterFlow, favoriteFlow) { characterResult, isFavorite ->
            when (characterResult) {
                is DataResult.Success -> DataResult.Success(
                    CharacterDetailData(character = characterResult.data, isFavorite = isFavorite)
                )
                is DataResult.Error -> DataResult.Error(characterResult.exception, characterResult.message)
                is DataResult.Loading -> DataResult.Loading
            }
        }
    }
}