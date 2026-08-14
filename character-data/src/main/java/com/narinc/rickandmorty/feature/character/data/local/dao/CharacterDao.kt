package com.narinc.rickandmorty.feature.character.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity

@Dao
interface CharacterDao {

    /**
     * ---- KAVRAM: DAO'dan PagingSource döndürmek ----
     * Bu fonksiyonun dönüş tipi Flow DEĞİL, PagingSource<Int, CharacterEntity>.
     * Room, bunu görünce ARKA PLANDA senin için tablo değişikliklerini
     * izleyen, invalidate edilebilen bir PagingSource üretiyor -- Adım 14'te
     * elle yazdığımız CharacterPagingSource'un YERİNİ bu alıyor, artık
     * network'e değil, Room'a bakıyor.
     */
    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}