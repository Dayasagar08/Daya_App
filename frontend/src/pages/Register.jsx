import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi";

function Register() {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        displayName: "",
        primaryEmail: "",
        phoneNumber: "",
        password: "",
        confirmPassword: ""
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [validationErrors, setValidationErrors] = useState({});

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData(previous => ({
            ...previous,
            [name]: value
        }));

        setError("");

        setValidationErrors(previous => ({
            ...previous,
            [name]: ""
        }));
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        setLoading(true);
        setError("");
        setValidationErrors({});

        try {

    const response = await registerUser(formData);

    console.log(
        "Registration response:",
        response
    );

    navigate("/verify-registration-otp", {
        state: {
            email: formData.primaryEmail
        }
    });

} catch (err) {

    console.error(
        "Registration error:",
        err
    );

    const response =
        err.response?.data;

    setError(
        response?.message ||
        "Registration failed. Please try again."
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
                                Create Account
                            </h2>

                            {error && (
                                <div className="alert alert-danger">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>

                                {/* Display Name */}
                                <div className="mb-3">

                                    <label className="form-label">
                                        Display Name
                                    </label>

                                    <input
                                        type="text"
                                        name="displayName"
                                        className={`form-control ${
                                            validationErrors.displayName
                                                ? "is-invalid"
                                                : ""
                                        }`}
                                        value={formData.displayName}
                                        onChange={handleChange}
                                        required
                                    />

                                    {validationErrors.displayName && (
                                        <div className="invalid-feedback">
                                            {validationErrors.displayName}
                                        </div>
                                    )}

                                </div>

                                {/* Email */}
                                <div className="mb-3">

                                    <label className="form-label">
                                        Primary Email
                                    </label>

                                    <input
                                        type="email"
                                        name="primaryEmail"
                                        className={`form-control ${
                                            validationErrors.primaryEmail
                                                ? "is-invalid"
                                                : ""
                                        }`}
                                        value={formData.primaryEmail}
                                        onChange={handleChange}
                                        required
                                    />

                                    {validationErrors.primaryEmail && (
                                        <div className="invalid-feedback">
                                            {validationErrors.primaryEmail}
                                        </div>
                                    )}

                                </div>

                                {/* Phone */}
                                <div className="mb-3">

                                    <label className="form-label">
                                        Phone Number
                                    </label>

                                    <input
                                        type="tel"
                                        name="phoneNumber"
                                        className={`form-control ${
                                            validationErrors.phoneNumber
                                                ? "is-invalid"
                                                : ""
                                        }`}
                                        value={formData.phoneNumber}
                                        onChange={handleChange}
                                        required
                                    />

                                    {validationErrors.phoneNumber && (
                                        <div className="invalid-feedback">
                                            {validationErrors.phoneNumber}
                                        </div>
                                    )}

                                </div>

                                {/* Password */}
                                <div className="mb-3">

                                    <label className="form-label">
                                        Password
                                    </label>

                                    <input
                                        type="password"
                                        name="password"
                                        className={`form-control ${
                                            validationErrors.password
                                                ? "is-invalid"
                                                : ""
                                        }`}
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                    />

                                    {validationErrors.password && (
                                        <div className="invalid-feedback">
                                            {validationErrors.password}
                                        </div>
                                    )}

                                </div>

                                {/* Confirm Password */}
                                <div className="mb-4">

                                    <label className="form-label">
                                        Confirm Password
                                    </label>

                                    <input
                                        type="password"
                                        name="confirmPassword"
                                        className={`form-control ${
                                            validationErrors.confirmPassword
                                                ? "is-invalid"
                                                : ""
                                        }`}
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        required
                                    />

                                    {validationErrors.confirmPassword && (
                                        <div className="invalid-feedback">
                                            {validationErrors.confirmPassword}
                                        </div>
                                    )}

                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={loading}
                                >
                                    {loading
                                        ? "Creating Account..."
                                        : "Register"
                                    }
                                </button>

                            </form>

                            <div className="text-center mt-3">

                                <span>
                                    Already have an account?
                                </span>

                                <button
                                    type="button"
                                    className="btn btn-link"
                                    onClick={() =>
                                        navigate("/login")
                                    }
                                >
                                    Login
                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Register;