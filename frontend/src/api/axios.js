import axios from "axios";

const rawBaseUrl = import.meta.env.VITE_API_URL;
let baseURL = (rawBaseUrl && rawBaseUrl.trim())
    ? rawBaseUrl.trim().replace(/\/+$/, "")
    : "https://opentwin-app-backend.onrender.com";

// If environment variable still points to the old backend URL, redirect to the new active backend
if (baseURL.includes("opentwin-backend.onrender.com")) {
    baseURL = baseURL.replace("opentwin-backend.onrender.com", "opentwin-app-backend.onrender.com");
}

const api = axios.create({
    baseURL,
    timeout: 30000, // 30-second timeout to prevent infinite spinners
    headers: {
        "Content-Type": "application/json",
    },
});

// Automatically inject JWT token and handle multipart/form-data
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token && !config.headers.Authorization) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        // If uploading FormData, delete Content-Type so browser sets boundary automatically
        if (config.data instanceof FormData) {
            delete config.headers["Content-Type"];
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