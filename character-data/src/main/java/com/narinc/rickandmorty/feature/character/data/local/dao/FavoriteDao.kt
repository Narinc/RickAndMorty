package com.narinc.rickandmorty.feature.character.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import com.narinc.rickandmorty.feature.character.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * ---- KAVRAM: @Dao + Flow dönüşü ----
 * DAO (Data Access Object), SQL sorgularının Kotlin fonksiyonlarına
 * eşlendiği interface. @Query içindeki SQL'i SEN yazıyorsun ama Room,
 * bunu çağırmak/sonucu map etmek için gereken TÜM kodu compile-time'da
 * üretiyor.
 *
 * En önemli kısım: bir @Query fonksiyonu Flow<T> DÖNERSE, Room bu sorguyu
 * OTOMATİK OLARAK "reaktif" hale getirir -- yani tablo her DEĞİŞTİĞİNDE
 * (insert/delete/update olduğunda), bu Flow OTOMATİK olarak yeni bir değer
 * yayınlar. Sen hiçbir "yayınla" kodu yazmıyorsun, Room tabloyu izleyip
 * senin yerine yapıyor. Bu, bizim Adım 17'de InMemoryFavoriteRepository'de
 * ELLE MutableStateFlow ile yaptığımız işin YERİNİ alıyor -- Room bunu
 * bedava veriyor.
 */
@Dao
interface FavoriteDao {

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE characterId = :characterId)")
    fun observeIsFavorite(characterId: Int): Flow<Boolean>

    @Query(
        """
    SELECT characters.* FROM characters
    INNER JOIN favorites ON characters.id = favorites.characterId
    ORDER BY characters.name ASC
    """ // TODO daha sonra eklenme sirasina gore siralayacak sekilde gelistirme yapariz. Migration test yapariz.
    )
    fun observeFavoriteCharacters(): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE characterId = :characterId")
    suspend fun delete(characterId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE characterId = :characterId)")
    suspend fun isFavoriteOnce(characterId: Int): Boolean
}