package com.narinc.rickandmorty.core.common

/**
 * ---- KAVRAM: sealed class ile hata yönetimi ----
 * RxJava'da hata yönetimini genelde onError callback'i ya da exception fırlatma
 * ile yapıyorduk. Coroutine'de de suspend fonksiyonlar exception fırlatabilir,
 * ama katmanlar arası (repository -> viewmodel) net bir sözleşme kurmak için
 * sealed class kullanmak çok daha temiz: caller, `when` bloğunda TÜM durumları
 * ele almaya derleyici tarafından ZORLANIR -> exception'ı unutup geçmek yok.
 */
sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()
}

inline fun <T> DataResult<T>.onSuccess(action: (T) -> Unit): DataResult<T> {
    if (this is DataResult.Success) action(data)
    return this
}

inline fun <T> DataResult<T>.onError(action: (Throwable, String?) -> Unit): DataResult<T> {
    if (this is DataResult.Error) action(exception, message)
    return this
}