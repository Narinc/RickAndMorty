package com.narinc.rickandmorty.feature.character.domain.usecase

import com.narinc.rickandmorty.core.common.DataResult
import com.narinc.rickandmorty.feature.character.domain.model.Character
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import javax.inject.Inject

/**
 * ---- KAVRAM: UseCase (Interactor) ----
 * Neden ViewModel direkt Repository'yi çağırmıyor da araya bir UseCase
 * katmanı koyuyoruz? Çünkü Single Responsibility Principle:
 * - Repository'nin görevi: veri KAYNAĞINI yönetmek (network mi, cache mi).
 * - UseCase'in görevi: BİR iş kuralını temsil etmek ("karakter detayını getir").
 *
 * Basit bir "getir" işleminde bu ayrım gereksiz gibi görünebilir, ama
 * yarın "karakter detayını getir VE favorilere otomatik ekle" gibi
 * birden fazla repository/iş kuralını birleştiren bir senaryo geldiğinde,
 * bu mantık ViewModel'e değil, UseCase'e yazılır. ViewModel sadece
 * UI state yönetir, iş kuralı bilmez.
 *
 * "operator fun invoke" sayesinde bu sınıfı bir fonksiyon gibi çağırabiliriz:
 * getCharacterDetailUseCase(5) -- getCharacterDetailUseCase.invoke(5) yerine.
 */
class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): DataResult<Character> {
        return repository.getCharacterDetail(id)
    }
}