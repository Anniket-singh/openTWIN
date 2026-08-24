import { useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import ReactMarkdown from "react-markdown";

function Chat() {
    const { token } = useAuth();

    const [question, setQuestion] = useState("");
    const [answer, setAnswer] = useState("");
    const [sources, setSources] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!question.trim()) {
            return;
        }

        setLoading(true);
        setError("");
        setAnswer("");
        setSources([]);

        try {
            const response = await axios.get(
                "http://localhost:8080/api/rag/ask",
                {
                    params: {
                        question: question
                    },
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            setAnswer(response.data.answer);
            setSources(response.data.sources || []);

        } catch (error) {
            console.error("Chat request failed:", error);

            if (error.response?.status === 401) {
                setError("Your session has expired. Please log in again.");
            } else {
                setError("Something went wrong. Please try again.");
            }

        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-950 text-white px-6 py-10">

            <div className="mx-auto max-w-3xl">

                <div className="mb-10 text-center">
                    <h1 className="text-4xl font-bold">
                        Talk to Your Twin
                    </h1>

                    <p className="mt-3 text-slate-400">
                        Ask questions about your knowledge, projects, and experience.
                    </p>
                </div>

                <div className="min-h-[300px] rounded-2xl border border-white/10 bg-slate-900 p-6">

                    {!answer && !loading && !error && (
                        <div className="flex min-h-[250px] items-center justify-center text-center">
                            <p className="max-w-md text-slate-500">
                                Ask your Twin something about your uploaded knowledge.
                            </p>
                        </div>
                    )}

                    {loading && (
                        <div className="flex min-h-[250px] items-center justify-center">
                            <p className="text-slate-400">
                                Your Twin is thinking...
                            </p>
                        </div>
                    )}

                    {answer && (
                        <div>
                            <p className="mb-3 text-sm font-semibold text-blue-400">
                                Your Twin
                            </p>

                            <div className="rounded-2xl border border-white/10 bg-slate-800 p-6">
                                <div className="max-w-none text-slate-200">

                                    <ReactMarkdown
                                        components={{
                                            h1: ({ children }) => (
                                                <h1 className="mb-4 mt-2 text-2xl font-bold text-white">
                                                    {children}
                                                </h1>
                                            ),

                                            h2: ({ children }) => (
                                                <h2 className="mb-3 mt-6 text-xl font-bold text-white">
                                                    {children}
                                                </h2>
                                            ),

                                            h3: ({ children }) => (
                                                <h3 className="mb-2 mt-5 text-lg font-semibold text-blue-300">
                                                    {children}
                                                </h3>
                                            ),

                                            p: ({ children }) => (
                                                <p className="mb-4 leading-7 text-slate-300">
                                                    {children}
                                                </p>
                                            ),

                                            ul: ({ children }) => (
                                                <ul className="mb-4 ml-6 list-disc space-y-2 text-slate-300">
                                                    {children}
                                                </ul>
                                            ),

                                            ol: ({ children }) => (
                                                <ol className="mb-4 ml-6 list-decimal space-y-2 text-slate-300">
                                                    {children}
                                                </ol>
                                            ),

                                            li: ({ children }) => (
                                                <li className="leading-7">
                                                    {children}
                                                </li>
                                            ),

                                            strong: ({ children }) => (
                                                <strong className="font-semibold text-white">
                                                    {children}
                                                </strong>
                                            ),

                                            code: ({ children }) => (
                                                <code className="rounded-md bg-slate-950 px-2 py-1 text-sm text-blue-300">
                                                    {children}
                                                </code>
                                            ),

                                            blockquote: ({ children }) => (
                                                <blockquote className="my-4 border-l-4 border-blue-500 pl-4 italic text-slate-400">
                                                    {children}
                                                </blockquote>
                                            ),
                                        }}
                                    >
                                        {answer}
                                    </ReactMarkdown>

                                </div>
                            </div>
                        </div>
                    )}

                    {sources.length > 0 && (
                        <div className="mt-6 border-t border-white/10 pt-5">

                            <h3 className="mb-3 text-sm font-semibold text-slate-300">
                                📚 Sources
                            </h3>

                            <div className="space-y-3">

                                {sources.map((source, index) => (
                                    <div
                                        key={`${source.documentId}-${source.chunkIndex}-${index}`}
                                        className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
                                    >
                                        <div className="flex items-center justify-between gap-4">

                                            <div>
                                                <p className="font-medium text-slate-200">
                                                    📄 {source.documentName}
                                                </p>

                                                <p className="mt-1 text-xs text-slate-500">
                                                    Chunk {source.chunkIndex}
                                                </p>
                                            </div>

                                            <span className="rounded-full bg-blue-500/10 px-3 py-1 text-xs font-medium text-blue-300">
                                                {Math.round(source.score * 100)}% relevant
                                            </span>

                                        </div>
                                    </div>
                                ))}

                            </div>
                        </div>
                    )}

                    {error && (
                        <div className="rounded-xl border border-red-500/20 bg-red-500/10 p-4 text-red-400">
                            {error}
                        </div>
                    )}

                </div>

                <form
                    onSubmit={handleSubmit}
                    className="mt-5 flex gap-3"
                >
                    <input
                        type="text"
                        value={question}
                        onChange={(event) => setQuestion(event.target.value)}
                        placeholder="Ask your Twin something..."
                        className="flex-1 rounded-xl border border-white/10 bg-slate-900 px-5 py-4 text-white outline-none placeholder:text-slate-500 focus:border-blue-500"
                    />

                    <button
                        type="submit"
                        disabled={loading || !question.trim()}
                        className="rounded-xl bg-blue-600 px-6 py-4 font-semibold transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {loading ? "Thinking..." : "Ask"}
                    </button>
                </form>

            </div>
        </div>
    );
}

export default Chat;