package com.sangmeebee.searchmovie.domain.usecase

import com.sangmeebee.searchmovie.domain.repository.UserRepository
import javax.inject.Inject


class GetInitUserTokenUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<String> =
        userRepository.getUserToken()
}