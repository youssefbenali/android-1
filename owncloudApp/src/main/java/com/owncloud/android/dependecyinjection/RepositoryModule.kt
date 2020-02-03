/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
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

package com.owncloud.android.dependecyinjection

import com.owncloud.android.data.authentication.repository.OCAuthenticationRepository
import com.owncloud.android.data.capabilities.repository.OCCapabilityRepository
import com.owncloud.android.data.server.repository.OCAnonymousServerRepository
import com.owncloud.android.data.server.repository.OCServerRepository
import com.owncloud.android.data.sharing.sharees.repository.OCShareeRepository
import com.owncloud.android.data.sharing.shares.repository.OCShareRepository
import com.owncloud.android.data.user.repository.OCUserRepository
import com.owncloud.android.domain.authentication.AuthenticationRepository
import com.owncloud.android.domain.capabilities.CapabilityRepository
import com.owncloud.android.domain.server.AnonymousServerRepository
import com.owncloud.android.domain.server.ServerRepository
import com.owncloud.android.domain.sharing.sharees.ShareeRepository
import com.owncloud.android.domain.sharing.shares.ShareRepository
import com.owncloud.android.domain.user.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<CapabilityRepository> {
        OCCapabilityRepository(
            get(),
            get()
        )
    }
    factory<ShareRepository> { OCShareRepository(get(), get()) }
    factory<ShareeRepository> { OCShareeRepository(get()) }
    factory<UserRepository> { OCUserRepository(get()) }
    factory<ServerRepository> { OCServerRepository(get()) }
    factory<AnonymousServerRepository> { OCAnonymousServerRepository(get()) }
    factory<AuthenticationRepository> { OCAuthenticationRepository(get()) }
}
