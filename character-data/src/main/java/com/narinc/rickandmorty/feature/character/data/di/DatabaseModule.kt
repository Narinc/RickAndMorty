package com.narinc.rickandmorty.feature.character.data.di

import android.content.Context
import androidx.room.Room
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import com.narinc.rickandmorty.feature.character.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ---- KAVRAM: @ApplicationContext ----
 * Room.databaseBuilder() bir Context istiyor -- Activity Context DEĞİL,
 * uygulama ömrü boyunca yaşayan Application Context. Hilt, bunu
 * @ApplicationContext annotation'ıyla senin için otomatik sağlıyor,
 * elle Application sınıfına erişip cast etmen gerekmiyor.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rick_and_morty.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}