package com.sangmeebee.searchmovie.domain.repository

import com.sangmeebee.searchmovie.domain.model.Movie

interface MovieRepository {
    suspend fun getMovies(
        query: String,
        display: Int,
        start: Int,
    ): Result<Movie>
}