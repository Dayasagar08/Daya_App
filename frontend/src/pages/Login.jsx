import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi";

function Login() {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData(previous => ({
            ...previous,
            [name]: value
        }));

        setError("");
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        setLoading(true);
        setError("");

        try {

    const response =
        await loginUser(formData);

    console.log(
        "Login response:",
        response
    );

    /*
     * Login credentials are correct.
     * Backend has sent LOGIN OTP.
     */
    navigate("/verify-login-otp", {
        state: {
            email: formData.email
        }
    });

} catch (err) {

    console.error(
        "Login error:",
        err
    );

    const response =
        err.response?.data;

    setError(
        response?.message ||
        "Login failed. Please check your credentials."
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

                            <h2 className="text-center mb-4">
                                Login
                            </h2>

                            {error && (
                                <div className="alert alert-danger">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>

                                <div className="mb-3">

                                    <label
                                        htmlFor="email"
                                        className="form-label"
                                    >
                                        Email Address
                                    </label>

                                    <input
                                        id="email"
                                        type="email"
                                        name="email"
                                        className="form-control"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                        autoComplete="email"
                                    />

                                </div>

                                <div className="mb-4">

                                    <label
                                        htmlFor="password"
                                        className="form-label"
                                    >
                                        Password
                                    </label>

                                    <input
                                        id="password"
                                        type="password"
                                        name="password"
                                        className="form-control"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                        autoComplete="current-password"
                                    />

                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={loading}
                                >
                                    {loading
                                        ? "Signing in..."
                                        : "Login"}
                                </button>

                            </form>

                            <div className="text-center mt-3">

                                <button
                                    type="button"
                                    className="btn btn-link"
                                    onClick={() =>
                                        navigate("/register")
                                    }
                                >
                                    Create an account
                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Login;