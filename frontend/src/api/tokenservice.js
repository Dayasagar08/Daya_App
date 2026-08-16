const ACCESS_TOKEN_KEY =
    "accessToken";

const REFRESH_TOKEN_KEY =
    "refreshToken";

const USER_EMAIL_KEY =
    "userEmail";

const TOKEN_TYPE_KEY =
    "tokenType";

const EXPIRES_IN_KEY =
    "expiresIn";


export const tokenService = {


    getAccessToken() {

        return localStorage.getItem(
            ACCESS_TOKEN_KEY
        );
    },


    getRefreshToken() {

        return localStorage.getItem(
            REFRESH_TOKEN_KEY
        );
    },


    getUserEmail() {

        return localStorage.getItem(
            USER_EMAIL_KEY
        );
    },


    getTokenType() {

        return localStorage.getItem(
            TOKEN_TYPE_KEY
        ) || "Bearer";
    },


    getExpiresIn() {

        return localStorage.getItem(
            EXPIRES_IN_KEY
        );
    },


    setTokens(
        accessToken,
        refreshToken,
        tokenType = "Bearer",
        expiresIn = null
    ) {

        localStorage.setItem(
            ACCESS_TOKEN_KEY,
            accessToken
        );


        localStorage.setItem(
            REFRESH_TOKEN_KEY,
            refreshToken
        );


        localStorage.setItem(
            TOKEN_TYPE_KEY,
            tokenType
        );


        if (expiresIn !== null) {

            localStorage.setItem(
                EXPIRES_IN_KEY,
                String(expiresIn)
            );
        }
    },


    setUserEmail(email) {

        localStorage.setItem(
            USER_EMAIL_KEY,
            email
        );
    },


    clear() {

        localStorage.removeItem(
            ACCESS_TOKEN_KEY
        );

        localStorage.removeItem(
            REFRESH_TOKEN_KEY
        );

        localStorage.removeItem(
            USER_EMAIL_KEY
        );

        localStorage.removeItem(
            TOKEN_TYPE_KEY
        );

        localStorage.removeItem(
            EXPIRES_IN_KEY
        );
    },


    isAuthenticated() {

        return !!this.getAccessToken();
    }
};