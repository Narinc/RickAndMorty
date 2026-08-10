package com.narinc.rickandmorty.feature.character.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.narinc.rickandmorty.core.network.RickAndMortyApiService
import com.narinc.rickandmorty.feature.character.data.mapper.toDomain
import com.narinc.rickandmorty.feature.character.domain.model.Character

/**
 * ---- KAVRAM: PagingSource ----
 * Bu sınıf, Paging kütüphanesinin "bana bir sayfa ver" dediği her seferinde
 * çağrılan tek bir fonksiyonu (load) implement eder. RxJava döneminde
 * sayfalama mantığını genelde elle (currentPage değişkeni, hasNextPage flag'i,
 * RecyclerView scroll listener ile "sona yaklaştın mı" kontrolü) yazardın --
 * hepsi burada kütüphaneye devrediliyor.
 *
 * Key = sayfa numarası (Int). API "page" query parametresi aldığı için
 * doğal bir eşleşme.
 */
class CharacterPagingSource(
    private val apiService: RickAndMortyApiService
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1 // ilk yüklemede key null gelir, 1. sayfadan başla

        return try {
            val response = apiService.getCharacters(page = page)
            val characters = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = characters,
                prevKey = if (page == 1) null else page - 1,
                // API'nin kendi "info.next" alanına bakarak sonraki sayfa var mı
                // kararını veriyoruz -- API null dönerse son sayfadayız demektir.
                nextKey = if (response.info.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * ---- KAVRAM: getRefreshKey ----
     * Kullanıcı "pull to refresh" yaptığında ya da PagingSource "invalidate"
     * edildiğinde, Paging kütüphanesi "hangi sayfadan yeniden başlamalıyım"
     * diye bunu sorar. Genelde kullanıcının o an gördüğü konuma en yakın
     * sayfayı hesaplarız.
     */
    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}