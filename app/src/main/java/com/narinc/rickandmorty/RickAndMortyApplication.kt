package com.narinc.rickandmorty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * ---- KAVRAM: @HiltAndroidApp ----
 * Bu annotation, Hilt'in tüm DI graph'ını (bağımlılık ağacını) burada
 * kök (root) olarak inşa etmesini tetikler. Uygulama açıldığı an,
 * Hilt burada bir "component" oluşturur ve bu component'ten türeyen
 * alt component'ler (Activity, ViewModel, Fragment seviyesinde) zincirleme
 * oluşur.
 *
 * RxJava + Dagger 2 manuel kurulum döneminde bunu elle yazdığın
 * DaggerAppComponent.builder()... gibi kodların YERİNE geçiyor -- Hilt
 * bunu senin için otomatik üretiyor (annotation processing ile).
 */
@HiltAndroidApp
class RickAndMortyApplication : Application()