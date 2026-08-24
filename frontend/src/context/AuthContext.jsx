import { createContext, useContext, useEffect, useState } from "react";


const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const login = (newToken) => {
        localStorage.setItem("token", newToken);
        setToken(newToken);
    };

    const logout = () => {
        localStorage.removeItem("token");
        setToken(null);
    };

    const getTokenExpiration = (token) => {
        try {
            const payload = JSON.parse(
                atob(token.split(".")[1])
            );
    
            return payload.exp * 1000;
        } catch (error) {
            console.error("Invalid JWT:", error);
            return null;
        }
    };

    const isAuthenticated = !!token;

    useEffect(() => {
        if (!token) {
            return;
        }
    
        const expirationTime = getTokenExpiration(token);
    
        if (!expirationTime) {
            logout();
            return;
        }
    
        const remainingTime = expirationTime - Date.now();
    
        if (remainingTime <= 0) {
            logout();
            return;
        }
    
        const timer = setTimeout(() => {
            logout();
        }, remainingTime);
    
        return () => clearTimeout(timer);
    }, [token]);

    return (
        <AuthContext.Provider
            value={{
                token,
                login,
                logout,
                isAuthenticated
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}