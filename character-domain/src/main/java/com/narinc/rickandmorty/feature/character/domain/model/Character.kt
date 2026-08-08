package com.narinc.rickandmorty.feature.character.domain.model

/**
 * ---- KAVRAM: Domain Model vs DTO ----
 * CharacterDto (core-network'te) API'nin JSON şekline birebir uyar.
 * Character (burada) ise UYGULAMANIN ihtiyacına göre şekillenir.
 *
 * Neden ayırıyoruz? İki sebep:
 * 1) API şekli değişirse (örn. Rick and Morty API'si bir alanı yeniden
 *    adlandırırsa), sadece "mapper" fonksiyonunu güncellersin — domain
 *    modelini kullanan ViewModel/Compose kodlarına hiç dokunmazsın.
 * 2) Domain modelini API'nin JSON yapısından tamamen bağımsız,
 *    UI'nin ihtiyaç duyduğu şekilde tasarlayabilirsin (örn. "isAlive: Boolean"
 *    gibi türetilmiş bir alan ekleyebilirsin, DTO'da böyle bir alan yok).
 */
data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val imageUrl: String,
    val originName: String,
    val locationName: String
)

enum class CharacterStatus {
    ALIVE, DEAD, UNKNOWN
}