import { Link } from "react-router-dom";

const steps = [
    ["01", "Upload your knowledge", "Add your resume, project reports, notes, and technical documents."],
    ["02", "Build your knowledge base", "Open Twin extracts and organizes the information that matters."],
    ["03", "Talk to your Twin", "Ask questions and receive grounded answers with source references."],
];

const features = [
    ["Personal knowledge base", "Keep your professional knowledge in one private, searchable place."],
    ["Grounded AI answers", "RAG retrieves relevant context before your Twin responds."],
    ["Source attribution", "See the documents behind each answer and understand why it was given."],
    ["Built for your career", "Explain your skills, projects, and experience with more depth than a static resume."],
];

function Landing() {
    return (
        <main className="min-h-screen bg-slate-950 text-white">
            <nav className="mx-auto flex max-w-6xl items-center justify-between px-6 py-6">
                <Link to="/" className="text-xl font-bold tracking-tight">
                    Open Twin<span className="text-blue-400">.</span>
                </Link>

                <Link
                    to="/login"
                    className="rounded-lg px-4 py-2 text-sm font-semibold text-slate-200 transition hover:bg-white/10"
                >
                    Log in
                </Link>
            </nav>

            <section className="mx-auto max-w-6xl px-6 pb-20 pt-16 text-center sm:pt-24">
                <p className="inline-flex rounded-full border border-blue-400/20 bg-blue-400/10 px-4 py-2 text-sm font-medium text-blue-200">
                    Your professional knowledge, ready to talk.
                </p>

                <h1 className="mx-auto mt-7 max-w-4xl text-5xl font-bold tracking-tight text-white sm:text-7xl">
                    Turn your knowledge into a digital twin.
                </h1>

                <p className="mx-auto mt-6 max-w-2xl text-lg leading-8 text-slate-400">
                    Open Twin uses your documents to create a private, conversational
                    representation of your skills, experience, projects, and expertise.
                </p>

                <div className="mt-10 flex flex-col justify-center gap-3 sm:flex-row">
                    <Link
                        to="/register"
                        className="rounded-xl bg-blue-600 px-6 py-3.5 font-semibold text-white transition hover:bg-blue-500"
                    >
                        Get started
                    </Link>
                    <Link
                        to="/login"
                        className="rounded-xl border border-white/15 px-6 py-3.5 font-semibold text-slate-200 transition hover:bg-white/5"
                    >
                        Log in
                    </Link>
                </div>

                <div className="mx-auto mt-16 max-w-4xl rounded-3xl border border-white/10 bg-gradient-to-br from-slate-900 to-slate-950 p-7 text-left shadow-2xl shadow-blue-950/30 sm:p-10">
                    <p className="text-sm font-semibold text-blue-300">ASK YOUR TWIN</p>
                    <p className="mt-4 text-xl text-slate-200">“What projects has Aniket worked on?”</p>
                    <div className="mt-6 rounded-2xl border border-white/10 bg-slate-900/80 p-5">
                        <p className="text-sm font-semibold text-white">Your Twin</p>
                        <p className="mt-2 leading-7 text-slate-400">
                            I can answer using the projects and experience in your uploaded knowledge base, with the relevant source documents attached.
                        </p>
                    </div>
                </div>
            </section>

            <section className="border-y border-white/10 bg-slate-900/40">
                <div className="mx-auto max-w-6xl px-6 py-20">
                    <div className="max-w-2xl">
                        <p className="text-sm font-semibold text-blue-300">HOW IT WORKS</p>
                        <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
                            From documents to meaningful conversation.
                        </h2>
                    </div>

                    <div className="mt-12 grid gap-5 md:grid-cols-3">
                        {steps.map(([number, title, description]) => (
                            <article key={number} className="rounded-2xl border border-white/10 bg-slate-950 p-6">
                                <p className="text-sm font-bold text-blue-400">{number}</p>
                                <h3 className="mt-6 text-xl font-semibold">{title}</h3>
                                <p className="mt-3 leading-7 text-slate-400">{description}</p>
                            </article>
                        ))}
                    </div>
                </div>
            </section>

            <section className="mx-auto max-w-6xl px-6 py-20">
                <div className="text-center">
                    <p className="text-sm font-semibold text-blue-300">WHY OPEN TWIN</p>
                    <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
                        More useful than a static resume.
                    </h2>
                </div>

                <div className="mt-12 grid gap-5 sm:grid-cols-2">
                    {features.map(([title, description]) => (
                        <article key={title} className="rounded-2xl border border-white/10 bg-slate-900 p-6">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-500/10 text-lg text-blue-300">✦</div>
                            <h3 className="mt-5 text-xl font-semibold">{title}</h3>
                            <p className="mt-2 leading-7 text-slate-400">{description}</p>
                        </article>
                    ))}
                </div>
            </section>

            <section className="mx-6 mb-6 rounded-3xl bg-blue-600 px-6 py-16 text-center sm:mx-auto sm:max-w-6xl">
                <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">Start building your Twin.</h2>
                <p className="mx-auto mt-4 max-w-xl text-blue-100">
                    Add the knowledge behind your work, then let Open Twin make it easier to explore.
                </p>
                <Link to="/register" className="mt-8 inline-block rounded-xl bg-white px-6 py-3.5 font-semibold text-blue-700 transition hover:bg-blue-50">
                    Create your account
                </Link>
            </section>
        </main>
    );
}

export default Landing;
