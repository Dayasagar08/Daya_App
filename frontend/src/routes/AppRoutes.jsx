import {
    BrowserRouter,
    Routes,
    Route,
    Navigate
} from "react-router-dom";

import Register from "../pages/Register";
import VerifyRegistrationOtp from "../pages/VerifyRegistrationOtp";
import Login from "../pages/Login";
import VerifyLoginOtp from "../pages/VerifyLoginOtp";
import Dashboard from "../pages/Dashboard";
import ProtectedRoute from "./ProtectedRoute";


function AppRoutes() {

    return (
        <BrowserRouter>

            <Routes>

                {/* =========================
                    PUBLIC HOME
                ========================== */}
                <Route
                    path="/"
                    element={
                        <div className="container mt-5">

                            <h1>
                                Apartment Manager
                            </h1>

                            <p>
                                Welcome to Apartment Manager.
                            </p>

                            <a
                                href="/register"
                                className="btn btn-primary me-2"
                            >
                                Create Account
                            </a>

                            <a
                                href="/login"
                                className="btn btn-outline-primary"
                            >
                                Login
                            </a>

                        </div>
                    }
                />


                {/* =========================
                    REGISTRATION
                ========================== */}

                <Route
                    path="/register"
                    element={<Register />}
                />

                <Route
                    path="/verify-registration-otp"
                    element={<VerifyRegistrationOtp />}
                />


                {/* =========================
                    LOGIN
                ========================== */}

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/verify-login-otp"
                    element={<VerifyLoginOtp />}
                />


                {/* =========================
                    PROTECTED APPLICATION
                ========================== */}

                <Route element={<ProtectedRoute />}>

                    <Route
                        path="/dashboard"
                        element={<Dashboard />}
                    />

                </Route>


                {/* =========================
                    UNKNOWN ROUTE
                ========================== */}

                <Route
                    path="*"
                    element={
                        <Navigate
                            to="/"
                            replace
                        />
                    }
                />

            </Routes>

        </BrowserRouter>
    );
}

export default AppRoutes;