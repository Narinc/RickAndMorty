package com.narinc.rickandmorty.feature.character.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * ---- KAVRAM: Migration ----
 * "startVersion'dan endVersion'a nasıl geçilir" tarifi. migrate() içinde
 * ELLE, RAW SQL yazıyorsun -- Room burada seni "koruyor" değil, sen ne
 * yazarsan o çalışıyor. RxJava/Java döneminde SQLiteOpenHelper.onUpgrade()
 * içine yazdığın ALTER TABLE komutlarının BİREBİR aynısı, sadece Room'un
 * kendi sınıf yapısına oturtulmuş hali.
 *
 * NOT NULL DEFAULT '' EKLEMEK ZORUNLU: SQLite'ta mevcut satırları olan bir
 * tabloya NOT NULL bir sütun eklerken, o sütun için bir DEFAULT değer
 * vermek ZORUNDASIN -- aksi halde "mevcut 50 satırın bu yeni sütunda ne
 * değeri olacak" sorusu cevapsız kalır, migration başarısız olur.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE characters ADD COLUMN gender TEXT NOT NULL DEFAULT ''"
        )
    }
}