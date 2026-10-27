import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";
import api from "../api/axios";

function Dashboard() {
    const { token, logout } = useAuth();

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [uploadMessage, setUploadMessage] = useState("");

    const [documents, setDocuments] = useState([]);
    const [documentsLoading, setDocumentsLoading] = useState(true);
    const [documentsError, setDocumentsError] = useState("");

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get("/api/auth/me", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                    }
                );

                setUser(response.data);
            } catch (error) {
                console.error("Failed to load user:", error);
                setError("Unable to load your profile.");
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, [token]);

    useEffect(() => {
        const fetchDocuments = async () => {
            try {
                const response = await api.get("/api/documents", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                    }
                );

                setDocuments(response.data);
            } catch (error) {
                console.error("Failed to load documents:", error);

                if (error.response?.status === 401) {
                    logout();
                    return;
                }

                setDocumentsError("Unable to load your documents.");
            } finally {
                setDocumentsLoading(false);
            }
        };

        if (token) {
            fetchDocuments();
        }
    }, [token, logout]);

    const fetchDocuments = async () => {
        try {
            const response = await api.get("/api/documents", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
                }
            );

            setDocuments(response.data);
        } catch (error) {
            console.error("Failed to load documents:", error);
            setDocumentsError("Unable to load your documents.");
        }
    };


    // Runs when the user selects a file
    const handleFileChange = (event) => {
        const file = event.target.files[0];

        if (!file) {
            return;
        }

        setSelectedFile(file);
        setUploadMessage("");
    };


    // Sends the selected file to Spring Boot
    const handleUpload = async () => {
        if (!selectedFile) {
            setUploadMessage("Please choose a document first.");
            return;
        }

        setUploading(true);
        setUploadMessage("");

        try {
            const formData = new FormData();

            formData.append("file", selectedFile);

            console.log("Upload token:", token);
            console.log("Selected file:", selectedFile);

            const response = await api.post(
                "/api/documents/upload",
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            setUploadMessage(response.data);
            setSelectedFile(null);

            await fetchDocuments();

        } catch (error) {
            console.error("Upload failed:", error);

            if (error.response?.data?.error) {
                setUploadMessage(error.response.data.error);
            } else if (typeof error.response?.data === "string") {
                setUploadMessage(error.response.data);
            } else {
                setUploadMessage(error.userMessage || "Upload failed. Please try again.");
            }

        } finally {
            setUploading(false);
        }
    };


    return (
        <div className="min-h-screen bg-slate-950 text-white flex items-center justify-center">

            <div className="text-center">

                <h2 className="mt-2 text-4xl font-bold tracking-tight">
                    {loading
                        ? "Loading your Twin..."
                        : user
                            ? `Welcome back, ${user.name}.`
                            : "Welcome to your Twin."
                    }
                </h2>


                <p className="mt-3 max-w-xl text-slate-400">
                    {error
                        ? error
                        : user
                            ? `Your personal knowledge workspace, ${user.email}.`
                            : "Build your personal knowledge base and give your AI Twin the information it needs to understand you."
                    }
                </p>


                {/* File selection and upload */}
                <div className="mt-6 flex flex-col items-center gap-3">

                    <label
                        htmlFor="document-upload"
                        className="cursor-pointer rounded-xl bg-blue-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-blue-500"
                    >
                        Choose document
                    </label>


                    <input
                        id="document-upload"
                        type="file"
                        accept=".pdf,.docx,.txt"
                        onChange={handleFileChange}
                        className="hidden"
                    />


                    {/* Show selected file */}
                    {selectedFile && (
                        <>
                            <p className="text-sm text-slate-300">
                                Selected:{" "}
                                <span className="font-medium">
                                    {selectedFile.name}
                                </span>
                            </p>

                            <button
                                onClick={handleUpload}
                                disabled={uploading}
                                className="rounded-xl border border-white/10 px-6 py-3 text-sm font-semibold text-slate-200 transition hover:bg-white/5 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                                {uploading
                                    ? "Uploading..."
                                    : "Upload document"
                                }
                            </button>
                        </>
                    )}

                </div>


                {/* Upload result */}
                {uploadMessage && (
                    <p className="mt-4 text-sm text-slate-300">
                        {uploadMessage}
                    </p>
                )}

                <div className="mt-8">
                    <Link
                        to="/chat"
                        className="inline-flex items-center rounded-xl bg-blue-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-blue-500"
                    >
                        Talk to Your Twin →
                    </Link>
                </div>

                <div className="mt-12 w-full max-w-3xl">
                    <div className="mb-4">
                        <h2 className="text-2xl font-bold text-white">
                            Your Knowledge
                        </h2>

                        <p className="mt-1 text-sm text-slate-400">
                            Documents currently connected to your Twin.
                        </p>
                    </div>

                    {documentsLoading ? (
                        <p className="text-slate-400">
                            Loading your documents...
                        </p>
                    ) : documentsError ? (
                        <p className="text-red-400">
                            {documentsError}
                        </p>
                    ) : documents.length === 0 ? (
                        <div className="rounded-2xl border border-white/10 bg-slate-900 p-8 text-center">
                            <p className="text-slate-300">
                                No documents uploaded yet.
                            </p>

                            <p className="mt-2 text-sm text-slate-500">
                                Upload your resume, project reports, or notes to start building your Twin.
                            </p>
                        </div>
                    ) : (
                        <div className="space-y-3">
                            {documents.map((document) => (
                                <div
                                    key={document.id}
                                    className="flex items-center justify-between rounded-2xl border border-white/10 bg-slate-900 px-5 py-4"
                                >
                                    <div>
                                        <p className="font-semibold text-white">
                                            {document.originalFilename}
                                        </p>

                                        <p className="mt-1 text-sm text-slate-400">
                                            {document.contentType}
                                        </p>
                                    </div>

                                    <span className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-400">
                                        Processed
                                    </span>
                                </div>
                            ))}
                        </div>
                    )}
                </div>



                <button
                    onClick={logout}
                    className="mt-6 rounded-lg bg-red-500 px-5 py-3 font-semibold hover:bg-red-600"
                >
                    Logout
                </button>

            </div>

        </div>
    );
}

export default Dashboard;