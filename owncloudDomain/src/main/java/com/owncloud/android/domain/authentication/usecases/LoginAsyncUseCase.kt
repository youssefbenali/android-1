/**
 * ownCloud Android client application
 *
 * @author Abel García de Prada
 * Copyright (C) 2020 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.domain.authentication.usecases

import com.owncloud.android.domain.BaseUseCaseWithResult
import com.owncloud.android.domain.authentication.AuthenticationRepository

class LoginAsyncUseCase(
    private val authenticationRepository: AuthenticationRepository
) : BaseUseCaseWithResult<Unit, LoginAsyncUseCase.Params>() {

    override fun run(params: Params) {
        require(params.serverPath.isNotEmpty()) { "Invalid server url" }
        require(params.username.isNotEmpty()) { "Invalid username" }
        require(params.password.isNotEmpty()) { "Invalid password" }

        authenticationRepository.login(
            params.serverPath,
            params.username,
            params.password
        )
    }

    data class Params(
        val serverPath: String,
        val username: String,
        val password: String
    )
}
