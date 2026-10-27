import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";


function getErrorMessage(error) {
    const responseData = error.response?.data;

    if (typeof responseData === "string") {
        return responseData;
    }

    if (responseData?.error) {
        return responseData.error;
    }

    if (responseData?.message) {
        return responseData.message;
    }

    return "Unable to create your account. Please try again.";
}

function Register() {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegister = async (event) => {
        event.preventDefault();
        setError("");

        if (password.length < 8) {
            setError("Password must contain at least 8 characters.");
            return;
        }

        setLoading(true);

        try {
            await api.post("/api/auth/register", {
                name: name.trim(),
                email: email.trim(),
                password,
            });

            navigate("/login", {
                replace: true,
                state: { message: "Account created. Please sign in." },
            });
        } catch (error) {
            console.error("Registration failed:", error);
            setError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-10 text-white sm:flex sm:items-center sm:justify-center">
            <div className="w-full max-w-md rounded-2xl border border-white/10 bg-slate-900 p-8 shadow-xl shadow-black/20">
                <Link to="/" className="text-lg font-bold tracking-tight">
                    Open Twin<span className="text-blue-400">.</span>
                </Link>

                <h1 className="mt-8 text-3xl font-bold">Create your account</h1>
                <p className="mt-2 text-slate-400">Start building your private professional knowledge base.</p>

                <form onSubmit={handleRegister} className="mt-8 space-y-5" noValidate>
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium text-slate-300">Name</label>
                        <input
                            id="name"
                            type="text"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-800 px-4 py-3 text-white outline-none transition placeholder:text-slate-500 focus:border-blue-500"
                            placeholder="Your name"
                            autoComplete="name"
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-slate-300">Email</label>
                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-800 px-4 py-3 text-white outline-none transition placeholder:text-slate-500 focus:border-blue-500"
                            placeholder="you@example.com"
                            autoComplete="email"
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-slate-300">Password</label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-800 px-4 py-3 text-white outline-none transition placeholder:text-slate-500 focus:border-blue-500"
                            placeholder="At least 8 characters"
                            autoComplete="new-password"
                            minLength="8"
                            required
                        />
                    </div>

                    {error && (
                        <p className="rounded-lg border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-300" role="alert">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {loading ? "Creating account..." : "Create account"}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-400">
                    Already have an account?{" "}
                    <Link to="/login" className="font-semibold text-blue-400 hover:text-blue-300">Log in</Link>
                </p>
            </div>
        </main>
    );
}

export default Register;
