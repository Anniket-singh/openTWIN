import { useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { Link, useLocation, useNavigate } from "react-router-dom";

function Login() {
    const { login } = useAuth();

    const navigate = useNavigate();
    const location = useLocation();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(location.state?.message || "");

    const handleLogin = async (event) => {
        event.preventDefault();
    
        setError("");
        setMessage("");
        setLoading(true);
    
        try {
            const response = await axios.post(
                "http://localhost:8080/api/auth/login",
                {
                    email,
                    password
                }
            );
    
            login(response.data.token);
    
            console.log("Login successful!");
            setMessage("Login successful!");

            navigate("/dashboard");
        } catch (error) {
            console.error("Login failed:", error);
            setError("Invalid email or password.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4">
            <div className="w-full max-w-md rounded-2xl bg-slate-900 p-8 shadow-xl">

                <h1 className="text-3xl font-bold text-white">
                    Welcome Back
                </h1>

                <p className="mt-2 text-slate-400">
                    Sign in to your Open Twin
                </p>

                <form
                    onSubmit={handleLogin}
                    className="mt-8 space-y-5"
                >
                    <div>
                        <label className="block text-sm font-medium text-slate-300">
                            Email
                        </label>

                        <input
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            className="mt-2 w-full rounded-lg bg-slate-800 px-4 py-3 text-white outline-none"
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-300">
                            Password
                        </label>

                        <input
                            type="password"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                            className="mt-2 w-full rounded-lg bg-slate-800 px-4 py-3 text-white outline-none"
                            placeholder="Your password"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                    >
                        {loading ? "Signing in..." : "Sign In"}
                    </button>
                </form>

                {error && (
                    <p className="mt-5 text-center text-red-400">
                        {error}
                    </p>
                )}

                {message && (
                    <p className="mt-5 text-center text-sm text-emerald-400">
                        {message}
                    </p>
                )}

                <p className="mt-6 text-center text-sm text-slate-400">
                    New to Open Twin?{" "}
                    <Link
                        to="/register"
                        className="font-semibold text-blue-400 hover:text-blue-300"
                    >
                        Create an account
                    </Link>
                </p>

            </div>
        </div>
    );
}

export default Login;
