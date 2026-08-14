package com.narinc.rickandmorty.feature.character.data.paging

import androidx.paging.PagingSource

object TestPagerHelper {
    suspend fun <T : Any> loadFirstPage(source: PagingSource<Int, T>): List<T> {
        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 50, placeholdersEnabled = false)
        )
        return (result as PagingSource.LoadResult.Page).data
    }
}