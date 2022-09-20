package com.sangmeebee.searchmovie.domain.usecase

import com.sangmeebee.searchmovie.domain.model.MovieBookmark
import com.sangmeebee.searchmovie.domain.repository.MovieRepository
import javax.inject.Inject

class GetAllBookmarkedMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(): Result<List<MovieBookmark>> =
        movieRepository.getAllBookmarked()
}