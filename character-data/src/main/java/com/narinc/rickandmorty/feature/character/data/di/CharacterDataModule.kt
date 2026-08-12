package com.narinc.rickandmorty.feature.character.data.di

import com.narinc.rickandmorty.core.common.DefaultDispatcherProvider
import com.narinc.rickandmorty.core.common.DispatcherProvider
import com.narinc.rickandmorty.feature.character.data.repository.CharacterRepositoryImpl
import com.narinc.rickandmorty.feature.character.data.repository.RoomFavoriteRepository
import com.narinc.rickandmorty.feature.character.domain.repository.CharacterRepository
import com.narinc.rickandmorty.feature.character.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ---- KAVRAM: @Module + @InstallIn ----
 * @Module: "bu sınıf Hilt'e bağımlılık nasıl sağlanır bilgisini veriyor" der.
 * @InstallIn(SingletonComponent::class): "bu bağımlılıklar uygulama ömrü
 * boyunca (Application scope'unda) tek bir örnek olarak yaşasın" der.
 * RxJava+Dagger2 döneminde elle yazdığın @Component arayüzlerinin yerini
 * Hilt'te bu InstallIn hedefleri alıyor -- çok daha az boilerplate.
 *
 * ---- KAVRAM: @Binds vs @Provides ----
 * @Binds: "interface X istenirse, Y implementasyonunu ver" -- SADECE
 * interface -> implementation eşlemesi için kullanılır, abstract fun olmalı,
 * gövdesi olmaz (Hilt derleme zamanında kod üretir).
 *
 * @Provides: Constructor injection ile oluşturulamayan (örn. üçüncü parti
 * bir sınıf, senin sahip olmadığın bir class) nesneler için kullanılır --
 * gövdeli, gerçek bir fonksiyon yazarsın.
 *
 * DefaultDispatcherProvider için @Provides kullanıyoruz çünkü onun
 * constructor'ında @Inject yok (core-common saf Kotlin modülü, Hilt'e
 * hiç bağımlı değil -- bilinçli tercih, hatırlarsan Adım 9'da bahsetmiştik).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CharacterDataModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: RoomFavoriteRepository
    ): FavoriteRepository

    companion object {
        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider {
            return DefaultDispatcherProvider()
        }
    }
}