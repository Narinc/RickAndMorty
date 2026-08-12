package com.narinc.rickandmorty.feature.character.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ---- KAVRAM: @Entity ----
 * Bu annotation, Room'a "bu sınıftan bir SQLite tablosu üret" der. Her
 * property, o tablonun bir sütunu olur. RxJava döneminde SQLite ile
 * çalışırken elle SQL yazıp Cursor'dan elle veri okurdun (ContentValues,
 * Cursor.getString() gibi) -- Room bunların HEPSİNİ senin için, compile-time
 * kod üretimiyle (KSP) hallediyor.
 *
 * @PrimaryKey: bu sütunun benzersiz anahtar olduğunu belirtiyor. Karakter
 * id'sini doğrudan primary key yapıyoruz -- her karakterin en fazla bir
 * favori kaydı olabilir, bu doğal bir eşleşme.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val characterId: Int
)