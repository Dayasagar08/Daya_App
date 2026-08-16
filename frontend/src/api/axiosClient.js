import axios from "axios";
import { tokenService } from "./tokenService";


const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
});


/*
 * Prevent multiple refresh requests
 * when several API calls receive 401
 * at the same time.
 */
let refreshPromise = null;


/*
 * REQUEST INTERCEPTOR
 *
 * Automatically attach the current
 * access token to protected requests.
 */
axiosClient.interceptors.request.use(
    (config) => {

        const accessToken =
            tokenService.getAccessToken();

        const publicAuthEndpoints = [
            "/auth/register",
            "/auth/verify-registration-otp",
            "/auth/resend-registration-otp",
            "/auth/login",
            "/auth/verify-login-otp",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/refresh",
            "/auth/logout"
        ];

        const isPublicAuthEndpoint =
            publicAuthEndpoints.some(
                endpoint =>
                    config.url?.includes(endpoint)
            );

        if (
            accessToken &&
            !isPublicAuthEndpoint
        ) {
            config.headers.Authorization =
                `Bearer ${accessToken}`;
        }

        return config;
    },

    (error) => {
        return Promise.reject(error);
    }
);


/*
 * RESPONSE INTERCEPTOR
 *
 * If backend returns 401:
 *
 * 1. Get refresh token
 * 2. Call /auth/refresh
 * 3. Store new access token
 * 4. Retry original request
 */
axiosClient.interceptors.response.use(

    (response) => {
        return response;
    },

    async (error) => {

        const originalRequest =
            error.config;

        /*
         * Only handle 401 responses.
         */
        if (
            error.response?.status !== 401 ||
            !originalRequest
        ) {
            return Promise.reject(error);
        }


        /*
         * Never refresh the refresh request itself.
         */
        if (
            originalRequest.url?.includes(
                "/auth/refresh"
            )
        ) {
            tokenService.clear();

            window.location.href = "/login";

            return Promise.reject(error);
        }


        /*
         * Prevent infinite retry loops.
         */
        if (originalRequest._retry) {

            tokenService.clear();

            window.location.href = "/login";

            return Promise.reject(error);
        }

        originalRequest._retry = true;


        const refreshToken =
            tokenService.getRefreshToken();


        /*
         * No refresh token means the
         * session cannot be renewed.
         */
        if (!refreshToken) {

            tokenService.clear();

            window.location.href = "/login";

            return Promise.reject(error);
        }


        try {

            /*
             * If another request is already
             * refreshing the token, wait for
             * that same request.
             */
            if (!refreshPromise) {

                refreshPromise =
                    axios.post(
                        `${import.meta.env.VITE_API_BASE_URL}/auth/refresh`,
                        {
                            refreshToken:
                                refreshToken
                        },
                        {
                            headers: {
                                "Content-Type":
                                    "application/json",
                                "Accept":
                                    "application/json"
                            }
                        }
                    )
                    .then((response) => {

                        const data =
                            response.data;

                        /*
                         * Backend returns:
                         *
                         * accessToken
                         * refreshToken
                         * tokenType
                         * expiresIn
                         */

                        tokenService.setTokens(
                            data.accessToken,
                            data.refreshToken,
                            data.tokenType,
                            data.expiresIn
                        );

                        return data;
                    })
                    .finally(() => {

                        refreshPromise = null;
                    });
            }


            const refreshResponse =
                await refreshPromise;


            /*
             * Get the newly generated
             * access token.
             */
            const newAccessToken =
                refreshResponse.accessToken;


            /*
             * Retry the original request
             * with the new JWT.
             */
            originalRequest.headers =
                originalRequest.headers || {};

            originalRequest.headers.Authorization =
                `Bearer ${newAccessToken}`;


            return axiosClient(
                originalRequest
            );

        } catch (refreshError) {

            console.error(
                "Automatic token refresh failed:",
                refreshError.response?.data ||
                refreshError.message
            );

            /*
             * Refresh token is no longer
             * valid, so completely log out.
             */
            tokenService.clear();

            window.location.href = "/login";

            return Promise.reject(
                refreshError
            );
        }
    }
);


export default axiosClient;