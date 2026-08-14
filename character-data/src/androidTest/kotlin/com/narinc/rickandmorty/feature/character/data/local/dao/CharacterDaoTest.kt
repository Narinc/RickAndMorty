package com.narinc.rickandmorty.feature.character.data.local.dao

import androidx.paging.PagingConfig
import androidx.paging.testing.TestPager
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import com.narinc.rickandmorty.feature.character.data.local.entity.CharacterEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ---- KAVRAM: In-memory Room veritabanı ----
 * Room.inMemoryDatabaseBuilder(), diski hiç kullanmayan, sadece RAM'de
 * yaşayan bir SQLite veritabanı oluşturuyor. Testler arası hiçbir kalıntı
 * kalmıyor, her test @Before'da SIFIRDAN başlıyor -- test izolasyonu
 * için ideal.
 *
 * ---- KAVRAM: TestPager ----
 * Gerçek bir Pager/ViewModel/Compose zinciri kurmadan, DOĞRUDAN bir
 * PagingSource'u test etmemizi sağlıyor. .refresh() çağırınca, PagingSource'un
 * ilk sayfayı doğru yüklediğini kontrol edebiliyoruz.
 */
@RunWith(AndroidJUnit4::class)
class CharacterDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var characterDao: CharacterDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // sadece test kolaylığı için, production'da ASLA kullanma
            .build()
        characterDao = database.characterDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_pagingSource_verileri_id_sirasina_gore_dondurur() = runTest {
        // Given
        val entities = listOf(
            CharacterEntity(3, "Summer Smith", "Alive", "Human", "Female", "", "Earth", "Earth"),
            CharacterEntity(1, "Rick Sanchez", "Alive", "Human", "Male", "", "Earth", "Earth"),
            CharacterEntity(2, "Morty Smith", "Alive", "Human", "Male", "", "Earth", "Earth")
        )
        characterDao.insertAll(entities)

        // When
        val pager = TestPager(PagingConfig(pageSize = 20), characterDao.pagingSource())
        val result = pager.refresh()

        // Then
        val page = result as androidx.paging.PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2, 3), page.data.map { it.id }) // ID'ye göre sıralı geldi mi?
    }

    @Test
    fun clearAll_tum_karakterleri_siler() = runTest {
        // Given
        characterDao.insertAll(listOf(
            CharacterEntity(1, "Rick Sanchez", "Alive", "Human", "Male", "", "Earth", "Earth")
        ))

        // When
        characterDao.clearAll()

        // Then
        val pager = TestPager(PagingConfig(pageSize = 20), characterDao.pagingSource())
        val result = pager.refresh() as androidx.paging.PagingSource.LoadResult.Page
        assertEquals(emptyList<CharacterEntity>(), result.data)
    }
}