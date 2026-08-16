import { Navigate, Outlet } from "react-router-dom";
import { tokenService } from "../api/tokenService";

function ProtectedRoute() {

    const authenticated =
        tokenService.isAuthenticated();

    if (!authenticated) {

        return (
            <Navigate
                to="/login"
                replace
            />
        );
    }

    return <Outlet />;
}

export default ProtectedRoute;