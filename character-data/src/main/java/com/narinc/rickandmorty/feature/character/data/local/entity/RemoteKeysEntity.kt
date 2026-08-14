package com.narinc.rickandmorty.feature.character.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ---- KAVRAM: Neden ayrı bir "remote keys" tablosu gerekiyor? ----
 * PagingSource'un (Adım 14'teki CharacterPagingSource) kendi içinde
 * "sıradaki sayfa key'i" tutma mantığı vardı (prevKey/nextKey döndürüyordu).
 * Ama RemoteMediator + Room kombinasyonunda, PagingSource ARTIK ROOM'UN
 * KENDİSİ (bir sonraki adımda göreceksin) -- Room, "sıradaki API sayfası
 * kaçtı" bilgisini DOĞAL OLARAK bilemez, bu API'ye özel bir bilgi.
 *
 * Bu yüzden HER KARAKTER İÇİN, "bu karakter hangi API sayfasından geldi,
 * bir önceki/sonraki sayfa key'i ne" bilgisini AYRI bir tabloda saklıyoruz.
 * RemoteMediator, yeni bir sayfa isteyeceği zaman buraya bakıp "en son
 * hangi sayfadaydık" sorusuna cevap buluyor.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val characterId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)