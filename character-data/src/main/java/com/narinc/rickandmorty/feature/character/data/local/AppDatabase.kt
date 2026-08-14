package com.narinc.rickandmorty.feature.character.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.narinc.rickandmorty.feature.character.data.local.dao.CharacterDao
import com.narinc.rickandmorty.feature.character.data.local.dao.FavoriteDao
import com.narinc.rickandmorty.feature.character.data.local.dao.RemoteKeysDao
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import com.narinc.rickandmorty.feature.character.data.local.entity.FavoriteEntity
import com.narinc.rickandmorty.feature.character.data.local.entity.RemoteKeysEntity

/**
 * ---- KAVRAM: @Database ----
 * Bu, SQLite veritabanının kendisini temsil eden abstract sınıf. entities
 * listesinde HANGİ tabloların bu veritabanında olacağını, version'da ise
 * şema versiyonunu belirtiyorsun -- ileride tabloya yeni bir sütun
 * eklersen, version'ı artırıp bir "migration" yazman gerekecek (şimdilik
 * bunu görmeyeceğiz, tek entity ile başlıyoruz).
 *
 * abstract fun favoriteDao(): FavoriteDao -- Room, bu fonksiyonun
 * gövdesini de compile-time'da kendisi üretiyor.
 */
@Database(
    entities = [FavoriteEntity::class, CharacterEntity::class, RemoteKeysEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}