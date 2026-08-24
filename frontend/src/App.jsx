import { Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Landing from "./pages/Landing";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import Chat from "./pages/Chat";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />

      <Route
        path="/login"
        element={<Login />}
      />

      <Route path="/register" element={<Register />} />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/chat"
        element={
          <ProtectedRoute>
            <Chat />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />

    </Routes>
  );
}

export default App;
