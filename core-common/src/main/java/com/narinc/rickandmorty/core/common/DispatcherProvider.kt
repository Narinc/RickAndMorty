package com.narinc.rickandmorty.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * ---- KAVRAM: Dispatcher ----
 * RxJava'da Schedulers.io() / Schedulers.computation() / AndroidSchedulers.mainThread()
 * neyse, Coroutine'de de Dispatchers.IO / Dispatchers.Default / Dispatchers.Main odur.
 * Bir coroutine'in HANGİ thread havuzunda çalışacağını belirler.
 *
 *  - Dispatchers.IO      -> Network isteği, disk okuma/yazma. Büyük thread havuzu.
 *  - Dispatchers.Default  -> Liste sıralama, JSON parse gibi CPU-yoğun işler.
 *  - Dispatchers.Main     -> UI güncellemesi, tek thread.
 *
 * ---- NEDEN INTERFACE ARKASINA SAKLIYORUZ? ----
 * Kodun içine direkt Dispatchers.IO yazabilirdik ama bu UNIT TEST'i zorlaştırır.
 * Bu interface'i Hilt ile inject edeceğiz: production'da gerçek Dispatchers'ı,
 * testte tek bir TestDispatcher'ı vereceğiz. Böylece testler anlık ve
 * deterministik çalışır (RxJava'daki TestScheduler mantığının aynısı).
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}