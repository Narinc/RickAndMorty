package com.narinc.rickandmorty.core.common

import kotlinx.coroutines.withContext

/**
 * ---- KAVRAM: suspend fun ----
 * "suspend" bir fonksiyonun "askıya alınabilir" olduğunu belirtir: coroutine
 * içinde çalıştığı sürece, thread'i BLOKE ETMEDEN bekleyebilir.
 *
 * RxJava karşılaştırması:
 *   Single<T>.blockingGet()  -> thread'i bloke eder (kötü)
 *   suspend fun getX(): T    -> thread'i bırakır, iş bitince kaldığı yerden
 *                                devam eder (iyi)
 *
 * ---- KAVRAM: withContext ----
 * withContext(dispatcher) bloğu belirtilen dispatcher'a TAŞIR, iş bitince
 * ÇAĞRILDIĞI dispatcher'a otomatik geri döner. RxJava'daki subscribeOn/
 * observeOn'un çok daha az boilerplate'li hali.
 *
 * Bu fonksiyonu repository katmanında her network çağrısını sarmalamak için
 * kullanacağız: try/catch tekrarını burada bir kere yazıp her yerde
 * DataResult<T> döneceğiz.
 */
suspend fun <T> safeApiCall(
    dispatcher: DispatcherProvider,
    apiCall: suspend () -> T
): DataResult<T> {
    return withContext(dispatcher.io) {
        try {
            DataResult.Success(apiCall())
        } catch (e: Exception) {
            DataResult.Error(exception = e, message = e.message)
        }
    }
}