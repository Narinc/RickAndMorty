package com.narinc.rickandmorty.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * ---- KAVRAM: JUnit Rule ----
 * RxJava döneminde "InstantTaskExecutorRule" ya da benzeri kurallara aşina
 * olabilirsin (LiveData'yı senkron çalıştırmak için). Bu da aynı mantıkla
 * çalışıyor: her testten ÖNCE ve SONRA otomatik tetiklenen bir "sarmalayıcı".
 *
 * ---- KAVRAM: Dispatchers.setMain / resetMain ----
 * ViewModel içindeki viewModelScope, varsayılan olarak Dispatchers.Main'i
 * kullanır. JVM unit testlerinde gerçek bir Android Main thread'i YOKTUR --
 * bu yüzden Dispatchers.setMain(testDispatcher) diyerek "Main dispatcher
 * istendiğinde, bunun yerine benim test dispatcher'ımı kullan" diyoruz.
 * Test bitince resetMain() ile bunu geri alıyoruz, aksi halde bir testteki
 * ayar başka bir teste sızabilir (test izolasyonu bozulur).
 *
 * Bu Rule'u yazdığımız TEK dosya, `@get:Rule val mainDispatcherRule =
 * MainDispatcherRule()` şeklinde HER ViewModel testinde tekrar kullanılacak.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}