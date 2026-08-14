package com.narinc.rickandmorty.feature.character.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.narinc.rickandmorty.feature.character.data.local.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys WHERE characterId = :characterId")
    suspend fun remoteKeysByCharacterId(characterId: Int): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}