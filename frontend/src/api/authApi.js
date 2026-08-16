import axiosClient from "./axiosClient";


export const registerUser = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/register",
            data
        );

    return response.data;
};


export const verifyRegistrationOtp = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/verify-registration-otp",
            data
        );

    return response.data;
};


export const resendRegistrationOtp = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/resend-registration-otp",
            data
        );

    return response.data;
};


export const loginUser = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/login",
            data
        );

    return response.data;
};


export const verifyLoginOtp = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/verify-otp",
            data
        );

    return response.data;
};


export const refreshToken = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/refresh",
            data
        );

    return response.data;
};


export const logoutUser = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/logout",
            data
        );

    return response.data;
};


export const forgotPassword = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/forgot-password",
            data
        );

    return response.data;
};


export const resetPassword = async (data) => {

    const response =
        await axiosClient.post(
            "/auth/reset-password",
            data
        );

    return response.data;
};

export const testAuthenticatedEndpoint = async () => {

    const response =
        await axiosClient.get(
            "/test/authenticated"
        );

    return response.data;
};