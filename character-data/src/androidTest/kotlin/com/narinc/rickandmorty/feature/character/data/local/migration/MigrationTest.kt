package com.narinc.rickandmorty.feature.character.data.local.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.narinc.rickandmorty.feature.character.data.local.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ---- KAVRAM: MigrationTestHelper ----
 * Bu araç, GERÇEK bir SQLite veritabanını version 2 şemasıyla oluşturuyor,
 * içine test verisi ekliyor, SONRA senin migrate() fonksiyonunu çalıştırıp
 * version 3'e geçiyor -- ve şemanın DOĞRU olduğunu (yeni sütun gerçekten
 * eklendi mi, tip doğru mu) otomatik doğruluyor. Elle "uygulamayı aç, veri
 * gir, güncelle, tekrar aç, kontrol et" diye test etmek yerine, bunu
 * otomatik bir teste dönüştürüyoruz.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate2To3_addsGenderColumn() {
        // Given -- version 2 şemasıyla veritabanı oluştur, ESKİ şemayla
        // (gender sütunu OLMADAN) bir satır ekle.
        migrationTestHelper.createDatabase("test_db", 2).apply {
            execSQL(
                """
                INSERT INTO characters (id, name, status, species, imageUrl, originName, locationName)
                VALUES (1, 'Rick Sanchez', 'Alive', 'Human', 'url', 'Earth', 'Citadel')
                """.trimIndent()
            )
            close()
        }

        // When -- migration'ı çalıştır
        val migratedDb = migrationTestHelper.runMigrationsAndValidate(
            "test_db", 3, true, MIGRATION_2_3
        )

        // Then -- yeni sütun eklenmiş VE eski satır KAYBOLMAMIŞ olmalı
        val cursor = migratedDb.query("SELECT * FROM characters WHERE id = 1")
        cursor.moveToFirst()
        val genderColumnIndex = cursor.getColumnIndex("gender")
        assert(genderColumnIndex != -1) { "gender sütunu eklenmemiş" }
        assert(cursor.getString(genderColumnIndex) == "") { "default değer boş string olmalı" }
        cursor.close()
    }
}