package com.kaelesty.audionautica.domain.usecases

import com.kaelesty.audionautica.domain.repos.IAccessRepo
import javax.inject.Inject

class RegisterUseCase @Inject constructor(val repo: IAccessRepo) {

	suspend operator fun invoke(email: String, name: String, password: String) =
		repo.register(email, name, password)
}