# OpenTwin Frontend

React frontend for **OpenTwin — AI-Powered Professional Knowledge Clone Platform**.

OpenTwin allows users to build a private AI-powered digital twin from their own professional knowledge, documents, projects, and experience.

The frontend provides the user interface for authentication, document management, knowledge interaction, and RAG-powered conversations with the user's Digital Twin.

---

## 🚀 Features

- User registration and login
- JWT-based authentication
- Automatic logout when the JWT expires
- Protected application routes
- Public landing page
- User dashboard
- Personal document upload
- Uploaded document listing
- RAG-powered chat with the Digital Twin
- Markdown-rendered AI responses
- Source attribution for retrieved knowledge
- Responsive dark-themed interface
- Navigation between application pages

---

## 🛠️ Technology Stack

- React
- Vite
- Tailwind CSS
- Axios
- React Router
- React Markdown
- JavaScript

---

## 🏗️ Application Structure

```text
frontend/
│
├── public/
│
├── src/
│   ├── components/
│   ├── context/
│   ├── pages/
│   ├── services/
│   ├── App.jsx
│   └── main.jsx
│
├── .gitignore
├── index.html
├── package.json
├── package-lock.json
└── vite.config.js