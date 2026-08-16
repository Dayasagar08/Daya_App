import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { verifyLoginOtp } from "../api/authApi";
import { tokenService } from "../api/tokenService";

function VerifyLoginOtp() {

    const navigate = useNavigate();
    const location = useLocation();

    const email = location.state?.email || "";

    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (event) => {

        const value = event.target.value
            .replace(/\D/g, "")
            .slice(0, 6);

        setOtp(value);
        setError("");
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        if (otp.length !== 6) {
            setError("Please enter the 6-digit OTP.");
            return;
        }

        if (!email) {
            setError("Email information is missing. Please login again.");
            return;
        }

        setLoading(true);
        setError("");

        try {

    const response =
        await verifyLoginOtp({
            email: email,
            otp: otp
        });

    console.log(
        "Login OTP verification response:",
        response
    );

    /*
     * Store JWT tokens centrally.
     */
    tokenService.setTokens(
        response.accessToken,
        response.refreshToken,
        response.tokenType,
        response.expiresIn
    );

    /*
     * Store logged-in user's email.
     */
    tokenService.setUserEmail(email);

    /*
     * Authentication completed.
     */
    navigate("/dashboard", {
        replace: true
    });

} catch (err) {

    console.error(
        "OTP verification error:",
        err
    );

    const response =
        err.response?.data;

    setError(
        response?.message ||
        "Invalid OTP. Please try again."
    );

} finally {

    setLoading(false);
}

    };

    return (
        <div className="container">

            <div className="row justify-content-center mt-5">

                <div className="col-md-6 col-lg-5">

                    <div className="card shadow">

                        <div className="card-body p-4">

                            <h2 className="text-center mb-3">
                                Verify Login
                            </h2>

                            <p className="text-center text-muted">
                                Enter the 6-digit OTP sent to
                            </p>

                            <p className="text-center fw-bold">
                                {email}
                            </p>

                            {error && (
                                <div className="alert alert-danger">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>

                                <div className="mb-4">

                                    <label
                                        htmlFor="otp"
                                        className="form-label"
                                    >
                                        Verification Code
                                    </label>

                                    <input
                                        id="otp"
                                        type="text"
                                        inputMode="numeric"
                                        autoComplete="one-time-code"
                                        className="form-control text-center"
                                        value={otp}
                                        onChange={handleChange}
                                        maxLength={6}
                                        placeholder="Enter 6-digit OTP"
                                        required
                                    />

                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={
                                        loading ||
                                        otp.length !== 6
                                    }
                                >
                                    {loading
                                        ? "Verifying..."
                                        : "Verify OTP"}
                                </button>

                            </form>

                            <div className="text-center mt-3">

                                <button
                                    type="button"
                                    className="btn btn-link"
                                    onClick={() =>
                                        navigate("/login")
                                    }
                                >
                                    Back to Login
                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default VerifyLoginOtp;