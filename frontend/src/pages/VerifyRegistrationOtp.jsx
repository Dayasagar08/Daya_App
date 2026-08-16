import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
    verifyRegistrationOtp,
    resendRegistrationOtp
} from "../api/authApi";

const MAX_RESENDS = 3;
const OTP_EXPIRY_SECONDS = 5 * 60;

function VerifyRegistrationOtp() {

    const location = useLocation();
    const navigate = useNavigate();

    const email = location.state?.email;

    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);
    const [resending, setResending] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const [resendCount, setResendCount] = useState(0);

    const [remainingSeconds, setRemainingSeconds] =
        useState(OTP_EXPIRY_SECONDS);

    /*
     * If user directly opens /verify-registration
     * without coming from registration, redirect to register.
     */
    useEffect(() => {

        if (!email) {
            navigate("/register", { replace: true });
        }

    }, [email, navigate]);

    /*
     * OTP countdown.
     */
    useEffect(() => {

        if (!email || remainingSeconds <= 0) {
            return;
        }

        const timer = setInterval(() => {

            setRemainingSeconds(previous => {

                if (previous <= 1) {
                    clearInterval(timer);
                    return 0;
                }

                return previous - 1;
            });

        }, 1000);

        return () => clearInterval(timer);

    }, [email, remainingSeconds]);

    const formatTime = () => {

        const minutes =
            Math.floor(remainingSeconds / 60);

        const seconds =
            remainingSeconds % 60;

        return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    };

    const handleOtpChange = (event) => {

        const value =
            event.target.value.replace(/\D/g, "");

        if (value.length <= 6) {
            setOtp(value);
        }

        setError("");
        setSuccess("");
    };

    const handleVerify = async (event) => {

        event.preventDefault();

        if (otp.length !== 6) {

            setError("Please enter the 6-digit OTP.");
            return;
        }

        setLoading(true);
        setError("");
        setSuccess("");

        try {

    const response =
        await verifyRegistrationOtp({
            email: email,
            otp: otp
        });

    console.log(
        "Registration OTP verification response:",
        response
    );

    /*
     * Registration verification successful.
     *
     * Do NOT store JWT here.
     *
     * JWT is generated only after LOGIN OTP
     * verification.
     */

    navigate("/login", {
        replace: true,
        state: {
            email: email
        }
    });

} catch (err) {

    console.error(
        "Registration OTP verification error:",
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

    const handleResend = async () => {

        if (resendCount >= MAX_RESENDS) {

            setError(
                "Maximum resend limit reached. Please contact support."
            );

            return;
        }

        setResending(true);
        setError("");
        setSuccess("");

        try {

            const response =
                await resendRegistrationOtp({
                    email
                });

            console.log(
                "Resend registration OTP response:",
                response
            );

            setResendCount(
                previous => previous + 1
            );

            /*
             * Reset OTP and 5-minute countdown.
             */
            setOtp("");
            setRemainingSeconds(
                OTP_EXPIRY_SECONDS
            );

            setSuccess(
                response?.message ||
                "A new verification code has been sent to your email."
            );

        } catch (err) {

            console.error(
                "Resend registration OTP error:",
                err
            );

            const response =
                err.response?.data;

            setError(
                response?.message ||
                "Unable to resend OTP. Please try again."
            );

        } finally {

            setResending(false);
        }
    };

    if (!email) {
        return null;
    }

    return (
        <div className="container">

            <div className="row justify-content-center mt-5">

                <div className="col-md-6 col-lg-5">

                    <div className="card shadow">

                        <div className="card-body p-4">

                            <h2 className="text-center mb-3">
                                Verify Your Email
                            </h2>

                            <p className="text-center text-muted">
                                Enter the 6-digit verification code
                                sent to:
                            </p>

                            <p className="text-center fw-bold">
                                {email}
                            </p>

                            {error && (
                                <div className="alert alert-danger">
                                    {error}
                                </div>
                            )}

                            {success && (
                                <div className="alert alert-success">
                                    {success}
                                </div>
                            )}

                            <form onSubmit={handleVerify}>

                                <div className="mb-3">

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
                                        onChange={handleOtpChange}
                                        placeholder="Enter 6-digit OTP"
                                        maxLength={6}
                                        required
                                    />

                                </div>

                                <div className="text-center mb-3">

                                    {remainingSeconds > 0 ? (
                                        <span className="text-muted">
                                            OTP expires in{" "}
                                            <strong>
                                                {formatTime()}
                                            </strong>
                                        </span>
                                    ) : (
                                        <span className="text-danger">
                                            OTP has expired.
                                        </span>
                                    )}

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
                                        : "Verify Email"}
                                </button>

                            </form>

                            <div className="text-center mt-4">

                                <p className="mb-2">
                                    Didn't receive the OTP?
                                </p>

                                <button
                                    type="button"
                                    className="btn btn-outline-primary"
                                    onClick={handleResend}
                                    disabled={
                                        resending ||
                                        resendCount >= MAX_RESENDS
                                    }
                                >
                                    {resending
                                        ? "Sending..."
                                        : "Resend OTP"}
                                </button>

                            </div>

                            <div className="text-center mt-3">

                                <small className="text-muted">
                                    Resend attempts:{" "}
                                    {resendCount} / {MAX_RESENDS}
                                </small>

                            </div>

                            <div className="text-center mt-3">

                                <button
                                    type="button"
                                    className="btn btn-link"
                                    onClick={() =>
                                        navigate("/register")
                                    }
                                >
                                    Back to Registration
                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default VerifyRegistrationOtp;