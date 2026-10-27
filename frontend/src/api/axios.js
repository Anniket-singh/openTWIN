import axios from "axios";

const rawBaseUrl = import.meta.env.VITE_API_URL;
const baseURL = (rawBaseUrl && rawBaseUrl.trim())
    ? rawBaseUrl.trim().replace(/\/+$/, "")
    : "http://localhost:8080";

const api = axios.create({
    baseURL,
    timeout: 30000, // 30-second timeout to prevent infinite spinners
    headers: {
        "Content-Type": "application/json",
    },
});

// Automatically inject JWT token into requests if available
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token && !config.headers.Authorization) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Normalize error messages for consistent UI feedback
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.code === "ECONNABORTED" || error.message?.includes("timeout")) {
            error.userMessage = "Request timed out. The server may be waking up or unreachable. Please retry.";
        } else if (!error.response) {
            error.userMessage = "Cannot reach backend server. Please verify the backend is running and reachable.";
        } else if (error.response.status === 503) {
            error.userMessage = error.response.data?.error || "Service temporarily unavailable. Please try again shortly.";
        } else if (error.response.status === 401) {
            error.userMessage = error.response.data?.error || "Invalid email or password.";
        } else {
            error.userMessage = error.response.data?.error || error.response.data?.message || "An unexpected error occurred.";
        }
        return Promise.reject(error);
    }
);

export default api;