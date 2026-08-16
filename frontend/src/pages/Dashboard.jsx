import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
    logoutUser,
    testAuthenticatedEndpoint
} from "../api/authApi";

import {
    tokenService
} from "../api/tokenService";


function Dashboard() {

    const navigate = useNavigate();


    // =========================
    // STATE
    // =========================

    const [error, setError] =
        useState("");

    const [backendMessage, setBackendMessage] =
        useState("");

    const [backendLoading, setBackendLoading] =
        useState(true);

    const [backendError, setBackendError] =
        useState("");


    // =========================
    // USER INFORMATION
    // =========================

    const email =
        tokenService.getUserEmail();


    // =========================
    // TEST PROTECTED BACKEND
    // =========================

    useEffect(() => {

        const testAuthentication = async () => {

            try {

                setBackendLoading(true);
                setBackendError("");

                const response =
                    await testAuthenticatedEndpoint();

                console.log(
                    "Protected endpoint response:",
                    response
                );

                setBackendMessage(
                    response.message
                );

            } catch (error) {

                console.error(
                    "Protected endpoint error:",
                    error.response?.data ||
                    error.message
                );

                setBackendError(
                    error.response?.data?.message ||
                    "Unable to authenticate with backend."
                );

            } finally {

                setBackendLoading(false);
            }
        };


        testAuthentication();

    }, []);


    // =========================
    // LOGOUT
    // =========================

    const handleLogout = async () => {

        setError("");

        const refreshToken =
            tokenService.getRefreshToken();


        try {

            /*
             * Revoke refresh token
             * on backend.
             */
            if (refreshToken) {

                await logoutUser({
                    refreshToken
                });
            }

        } catch (error) {

            console.error(
                "Logout API error:",
                error.response?.data ||
                error.message
            );

        } finally {

            /*
             * Always clear local
             * authentication.
             */
            tokenService.clear();

            navigate(
                "/login",
                {
                    replace: true
                }
            );
        }
    };


    // =========================
    // UI
    // =========================

    return (

        <div className="container mt-5">

            <div className="card shadow">

                <div className="card-body text-center">

                    <h1>
                        Apartment Manager
                    </h1>


                    <h3 className="mt-4">
                        Welcome!
                    </h3>


                    <p>
                        Logged in as:

                        <strong className="ms-2">
                            {email}
                        </strong>
                    </p>


                    <div className="alert alert-success">

                        Login successful.

                        <br />

                        JWT authentication has
                        been completed.

                    </div>


                    {error && (

                        <div className="alert alert-danger">

                            {error}

                        </div>
                    )}


                    {/* =========================
                        PROTECTED BACKEND TEST
                    ========================== */}

                    <div className="mt-4">

                        <h5>
                            Backend Authentication Test
                        </h5>


                        {backendLoading && (

                            <div className="alert alert-info mt-3">

                                Checking JWT authentication...

                            </div>
                        )}


                        {backendMessage && (

                            <div className="alert alert-success mt-3">

                                {backendMessage}

                            </div>
                        )}


                        {backendError && (

                            <div className="alert alert-danger mt-3">

                                {backendError}

                            </div>
                        )}

                    </div>


                    {/* =========================
                        LOGOUT
                    ========================== */}

                    <button
                        type="button"
                        className="btn btn-danger mt-3"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>

                </div>

            </div>

        </div>
    );
}


export default Dashboard;