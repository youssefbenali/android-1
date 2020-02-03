/**
 * ownCloud Android client application
 *
 * @author David A. Velasco
 * @author masensio
 * @author Christian Schabesberger
 * Copyright (C) 2019 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.operations;

import android.content.Context;

import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.common.operations.RemoteOperationResult.ResultCode;
import com.owncloud.android.lib.resources.server.GetRemoteStatusOperation;
import com.owncloud.android.lib.resources.status.OwnCloudVersion;
import timber.log.Timber;

/**
 * Get basic information from an ownCloud server given its URL.
 * <p>
 * Checks the existence of a configured ownCloud server in the URL, gets its version
 * and finds out what authentication method is needed to access files in it.
 *
 * TODO: Remove this operation. Call {@link com.owncloud.android.domain.server.usecases.GetServerInfoUseCase} instead.
 */
@Deprecated
public class GetServerInfoOperation extends RemoteOperation<GetServerInfoOperation.ServerInfo> {

    private String mUrl;
    private ServerInfo mResultData;

    /**
     * Constructor.
     *
     * @param url URL to an ownCloud server.
     */
    public GetServerInfoOperation(String url, Context context) {
        mUrl = trimWebdavSuffix(url);
        mContext = context;

        mResultData = new ServerInfo();
    }

    /**
     * Performs the operation
     *
     * @return Result of the operation. If successful, includes an instance of
     * {@link ServerInfo} with the information retrieved from the server.
     * Call {@link RemoteOperationResult#getData()}.get(0) to get it.
     */
    @Override
    protected RemoteOperationResult<ServerInfo> run(OwnCloudClient client) {
        // first: check the status of the server (including its version)
        GetRemoteStatusOperation getStatusOperation = new GetRemoteStatusOperation();
        final RemoteOperationResult<OwnCloudVersion> remoteStatusResult = getStatusOperation.execute(client);
        RemoteOperationResult<ServerInfo> result = new RemoteOperationResult(remoteStatusResult);

        if (remoteStatusResult.isSuccess()) {
            // second: get authentication method required by the server
            mResultData.mVersion = remoteStatusResult.getData();
            mResultData.mIsSslConn = (remoteStatusResult.getCode() == ResultCode.OK_SSL);
            mResultData.mBaseUrl = normalizeProtocolPrefix(mUrl, mResultData.mIsSslConn);
            final RemoteOperationResult<AuthenticationMethod> detectAuthResult = detectAuthorizationMethod(client);

            // third: merge results
            if (detectAuthResult.isSuccess()) {
                mResultData.mAuthMethod = detectAuthResult.getData();
                result.setData(mResultData);
            } else {
                result = new RemoteOperationResult<>(detectAuthResult);
                mResultData.mAuthMethod = detectAuthResult.getData();
                result.setData(mResultData);
            }
        }
        return result;
    }

    private RemoteOperationResult<AuthenticationMethod> detectAuthorizationMethod(OwnCloudClient client) {
        Timber.d("Trying empty authorization to detect authentication method");
        DetectAuthenticationMethodOperation operation = new DetectAuthenticationMethodOperation();
        return operation.execute(client);
    }

    private String trimWebdavSuffix(String url) {
        if (url == null) {
            url = "";
        } else {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            if (url.toLowerCase().endsWith(AccountUtils.WEBDAV_PATH_4_0_AND_LATER)) {
                url = url.substring(0, url.length() - AccountUtils.WEBDAV_PATH_4_0_AND_LATER.length());
            }
        }
        return url;
    }

    private String normalizeProtocolPrefix(String url, boolean isSslConn) {
        if (!url.toLowerCase().startsWith("http://") &&
                !url.toLowerCase().startsWith("https://")) {
            if (isSslConn) {
                return "https://" + url;
            } else {
                return "http://" + url;
            }
        }
        return url;
    }

    @Deprecated
    /** TODO: Remove this class and use {@link com.owncloud.android.domain.server.model.ServerInfo} */
    public static class ServerInfo {
        public OwnCloudVersion mVersion = null;
        public String mBaseUrl = "";
        public AuthenticationMethod mAuthMethod = null;
        public boolean mIsSslConn = false;
    }
}