package com.narinc.rickandmorty.core.common

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ---- DÜZELTME: "Detected use of different schedulers" ----
 * runTest { } kendi TestCoroutineScheduler'ını yaratır ve test body'sini
 * onun üzerinde çalıştırır. Eğer biz StandardTestDispatcher() diye
 * PARAMETRESİZ, bağımsız bir dispatcher yaratırsak, bu runTest'in
 * scheduler'ından TAMAMEN FARKLI bir sanal zaman çizelgesine sahip olur.
 * withContext ile birinden diğerine geçmeye çalışınca kütüphane bunu
 * hata olarak görür.
 *
 * Çözüm: test dispatcher'ımızı, runTest'in TestScope'unun kendi
 * `testScheduler`'ı ile oluşturuyoruz -> StandardTestDispatcher(testScheduler).
 * Böylece tek bir sanal zaman çizelgesi olur, ikisi senkron ilerler.
 */
class SafeApiCallTest {

    private fun TestScope.testDispatchers(): DispatcherProvider {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return object : DispatcherProvider {
            override val main = dispatcher
            override val io = dispatcher
            override val default = dispatcher
        }
    }

    @Test
    fun `apiCall basarili olursa Success doner`() = runTest {
        val dispatchers = testDispatchers()
        val expected = "Rick Sanchez"

        val result = safeApiCall(dispatchers) { expected }

        assertTrue(result is DataResult.Success)
        assertEquals(expected, (result as DataResult.Success).data)
    }

    @Test
    fun `apiCall exception firlatirsa Error doner`() = runTest {
        val dispatchers = testDispatchers()
        val exception = IllegalStateException("Network koptu")

        val result = safeApiCall(dispatchers) { throw exception }

        assertTrue(result is DataResult.Error)
        assertEquals("Network koptu", (result as DataResult.Error).message)
    }
}