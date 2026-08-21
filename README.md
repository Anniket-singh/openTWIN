# 🚀 Open Twin

### AI-Powered Professional Knowledge Clone Platform

> **Turn personal documents into a private, searchable, conversational AI Twin.**

Open Twin is an AI-powered Digital Twin platform that builds a conversational representation of a person's professional knowledge, projects, skills, experiences, and documents using **Retrieval-Augmented Generation (RAG)**.

Instead of treating an LLM as a generic chatbot, Open Twin grounds its answers in the user's own knowledge base.

---

<p align="center">

**Upload Knowledge → Process → Embed → Retrieve → Generate → Cite**

</p>

---

## ✨ What is Open Twin?

Traditional resumes are static and limited. Important project details, technical decisions, certifications, notes, and experiences are often scattered across multiple documents.

Open Twin turns those documents into a **private semantic knowledge base** that an AI can search and use when answering questions.

For example:

```text
User uploads:
Resume + Project Reports + Technical Documents
                ↓
        Text Extraction
                ↓
        Cleaning + Chunking
                ↓
        Gemini Embeddings
                ↓
             Qdrant
                ↓
      Semantic Retrieval
                ↓
      Grounded Gemini Prompt
                ↓
        AI Twin Response
                ↓
         Source Attribution
```

The goal is not to replace people.

**The goal is to make their knowledge more accessible.**

---

# 🎯 Current Status

### V1 MVP — Backend AI Core

| Phase | Status |
|---|---|
| Phase 1 — Project Foundation | ✅ Complete |
| Phase 2 — Document Processing | ✅ Complete |
| Phase 3 — Vector Search | ✅ Complete |
| Phase 4 — RAG Pipeline | ✅ Complete |
| Phase 5 — Chat Experience | 🚧 Next |
| Phase 6 — Deployment | ⏳ Planned |

The current implementation already supports the core backend knowledge pipeline:

**Authentication → Document Processing → Embeddings → Qdrant → Semantic Retrieval → RAG → Source Attribution**

The React chat experience and public deployment are intentionally planned for the next phases.

---

# 🧠 Core Features

## 🔐 Secure Authentication

- User registration
- Login
- Password hashing
- JWT authentication
- Protected API endpoints
- Authenticated user context
- User ownership enforcement

The authenticated user's ID is derived from the JWT rather than trusting a `userId` supplied by the client.

---

## 📄 Document Processing

Supported document formats:

- PDF
- DOCX
- TXT

Processing pipeline:

```text
File
 ↓
Validation
 ↓
Storage
 ↓
Text Extraction
 ↓
Cleaning
 ↓
Chunking
```

Documents are divided into manageable chunks of approximately **1000 characters** before embedding.

---

## 🧩 Semantic Knowledge Search

Each chunk is converted into a vector embedding using Gemini.

Current embedding configuration:

```text
Embedding dimension: 768
Task type:
  Document → RETRIEVAL_DOCUMENT
  Query    → RETRIEVAL_QUERY
```

This allows Open Twin to search for **meaning**, rather than relying only on exact keyword matches.

---

## 🗄️ Qdrant Vector Database

Open Twin uses **Qdrant** to store and search knowledge embeddings.

Current collection:

```text
open_twin_knowledge
```

Configuration:

```text
Vector dimension: 768
Distance metric: Cosine
```

Each vector point stores metadata such as:

```json
{
  "userId": 3,
  "documentId": 11,
  "chunkIndex": 2,
  "content": "..."
}
```

---

# 🔥 RAG Pipeline

The core of Open Twin is its Retrieval-Augmented Generation pipeline.

A user question does **not** go directly to Gemini.

Instead:

```text
                    USER QUESTION
                         │
                         ▼
                 JWT Authentication
                         │
                         ▼
                  Authenticated User
                         │
                         ▼
                 Query Embedding
                         │
                         ▼
                      Qdrant
                         │
                  userId filter 🔐
                         │
                         ▼
                Relevant Knowledge
                         │
                         ▼
                 Context Construction
                         │
                         ▼
                  Grounding Prompt
                         │
                         ▼
                       Gemini
                         │
                         ▼
                 Grounded Answer
                         │
                         ▼
                 Source Attribution
```

### Why this matters

A generic LLM may know a lot about the world, but it does not automatically know the contents of a user's private documents.

Open Twin therefore follows:

```text
Question + Retrieved User Knowledge
                 ↓
              Gemini
                 ↓
         Grounded Response
```

This helps reduce hallucinations and keeps responses personalized to the user's uploaded knowledge.

---

# 🔒 User Data Isolation

Security is enforced **during retrieval**, not after retrieval.

The important flow is:

```text
JWT
 ↓
Authenticated User ID
 ↓
Qdrant Filter
 ↓
Only that user's vectors
 ↓
Retrieved Context
 ↓
Gemini
```

Conceptually:

```text
Find vectors similar to the query
WHERE userId = authenticatedUserId
```

This is important because filtering after retrieving all users' data would create a serious privacy risk.

Open Twin has been tested with multiple users to verify that one user's documents are not returned in another user's semantic search results.

---

# 📚 Source Attribution

RAG responses include information about the chunks used to generate the answer.

Example:

```json
{
  "answer": "Based on your professional records, you have built...",
  "sources": [
    {
      "documentId": 11,
      "chunkIndex": 2,
      "score": 0.6787966
    },
    {
      "documentId": 11,
      "chunkIndex": 1,
      "score": 0.6671593
    }
  ]
}
```

This provides a trace from:

```text
AI Answer
   ↓
Retrieved Chunk
   ↓
Document
```

Source attribution improves **trust, explainability, and debuggability**.

---

# 🧱 Architecture

## Current Backend Architecture

```text
┌──────────────────────────────────────────┐
│              Spring Boot API             │
├──────────────────────────────────────────┤
│                                          │
│  Authentication / Security               │
│            │                             │
│            ▼                             │
│  Document Processing                     │
│            │                             │
│            ▼                             │
│  Text Extraction + Chunking              │
│            │                             │
│            ▼                             │
│  Gemini Embedding Service                │
│            │                             │
│            ▼                             │
│  Qdrant Vector Storage                   │
│            │                             │
│            ▼                             │
│  Semantic Retrieval                      │
│            │                             │
│            ▼                             │
│  RAG Service                              │
│            │                             │
│            ▼                             │
│  Gemini Response Generation              │
│            │                             │
│            ▼                             │
│  Source Attribution                      │
│                                          │
└──────────────────────────────────────────┘

                 │
                 ▼

             PostgreSQL
                 +
               Qdrant
```

## Planned Full Architecture

```text
React + Tailwind CSS
        │
        ▼
     Axios
        │
        ▼
Spring Boot Backend
        │
   ┌────┴─────────────────────┐
   │                          │
   ▼                          ▼
PostgreSQL                  Qdrant
   │                          │
Users / Documents       Embeddings / Chunks
                              │
                              ▼
                         Gemini API
```

---

# 🛠️ Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| Backend | Java 21 | Application development |
| Backend Framework | Spring Boot | REST APIs and business logic |
| Security | Spring Security | Authentication and authorization |
| Authentication | JWT | Stateless user authentication |
| Database | PostgreSQL | Users, documents and metadata |
| Vector Database | Qdrant | Semantic vector storage and retrieval |
| AI | Gemini API | Embeddings and response generation |
| PDF Processing | Apache PDFBox | PDF text extraction |
| DOCX Processing | Apache POI | DOCX text extraction |
| Frontend | React | Planned user interface |
| Styling | Tailwind CSS | Planned UI styling |
| HTTP Client | Axios | Planned frontend/backend communication |

> **Note:** LangChain4j is part of the planned project stack, but the current RAG milestone was implemented directly with the existing Gemini + Qdrant integration rather than introducing an additional abstraction where it was not necessary.

---

# 🧪 Current API Examples

## Semantic Retrieval

```http
GET /api/retrieval/search?query=What%20projects%20have%20I%20built?&limit=5
Authorization: Bearer <JWT>
```

The backend:

1. Extracts the authenticated user ID from JWT.
2. Generates a query embedding.
3. Searches Qdrant.
4. Applies the authenticated user's `userId` filter.
5. Returns relevant chunks and similarity scores.

---

## RAG Question Answering

```http
GET /api/rag/ask?question=What%20projects%20have%20I%20built?&limit=5
Authorization: Bearer <JWT>
```

Example response:

```json
{
  "answer": "Based on your professional records, you have built...",
  "sources": [
    {
      "documentId": 11,
      "chunkIndex": 2,
      "score": 0.6787966
    }
  ]
}
```

---

# ⚙️ Configuration

Open Twin uses environment variables for secrets.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/opentwin
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=900000

gemini.api-key=${GEMINI_API_KEY}
gemini.embedding.model=gemini-embedding-2
gemini.embedding.dimension=768

qdrant.host=localhost
qdrant.grpc-port=6334
qdrant.collection-name=open_twin_knowledge
qdrant.vector-dimension=768
```

### ⚠️ Never commit secrets

Do **not** commit:

```text
GEMINI_API_KEY
JWT_SECRET
database passwords
real production credentials
```

Use environment variables or a local environment configuration instead.

---

# 🐳 Running Qdrant Locally

Qdrant is currently used as the local vector database.

The project uses a persistent Docker volume so vector data can survive container restarts.

The expected Qdrant configuration is:

```text
Host: localhost
gRPC Port: 6334
Collection: open_twin_knowledge
Dimension: 768
Distance: Cosine
```

---

# 🚦 Getting Started

## Prerequisites

Install:

- Java 21
- Maven
- PostgreSQL
- Docker
- Qdrant
- A Gemini API key

---

## 1. Clone the repository

```bash
git clone <your-repository-url>
cd openTWIN
```

---

## 2. Create the PostgreSQL database

Create:

```text
opentwin
```

Then configure the database connection through environment variables.

---

## 3. Start Qdrant

Run your local Qdrant container with persistent storage.

The application expects Qdrant to be available on:

```text
localhost:6334
```

---

## 4. Configure environment variables

Set:

```text
DB_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

Do not place real credentials directly in the repository.

---

## 5. Run the backend

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

---

# 🧪 Testing Strategy

Open Twin is developed incrementally with real documents rather than relying only on artificial test strings.

Important tests include:

### Authentication

- Registration
- Login
- JWT validation
- Protected endpoints

### Document Processing

- PDF upload
- DOCX upload
- TXT upload
- Invalid file types
- File size limits
- Real document extraction
- Chunk generation

### Vector Search

- Embedding generation
- 768-dimensional vectors
- Qdrant storage
- Semantic retrieval
- Similarity scores
- User isolation

### RAG

Example questions:

```text
What projects have I built?

What technologies do I know?

Explain my Cricket Stats API.

What is Open Twin?

What did I use Gemini for?

Tell me about my technical skills.
```

Also test questions where the knowledge base does not contain the answer.

Expected behavior:

```text
Insufficient context
        ↓
Do not invent an answer
        ↓
Tell the user that the information
is not available in the knowledge base
```

---

# 🧠 Engineering Decisions

## Why RAG instead of a direct Gemini call?

A direct Gemini call:

```text
Question → Gemini → Answer
```

does not automatically have access to private user documents.

Open Twin instead uses:

```text
Question
   ↓
Semantic Retrieval
   ↓
Private User Context
   ↓
Gemini
   ↓
Grounded Answer
```

This makes the response personalized and gives the system a way to trace the answer back to source knowledge.

---

## Why Qdrant?

Open Twin needs semantic search over user knowledge.

Traditional SQL queries are useful for structured metadata, but vector search is better suited for finding chunks based on semantic similarity.

Qdrant provides:

- Vector storage
- Similarity search
- Payload metadata
- Filtering
- User-level retrieval isolation

---

## Why approximately 1000-character chunks?

Large documents are difficult to retrieve effectively as a single block.

Chunking creates smaller units of knowledge that can be embedded and retrieved independently.

The current implementation intentionally uses chunks of approximately 1000 characters as a practical starting point.

Retrieval quality can be tuned later based on real evaluation results.

---

## Why filter by user ID inside Qdrant?

Because privacy must be enforced before the retrieved context reaches the LLM.

The safe flow is:

```text
Authenticated User
       ↓
Qdrant user filter
       ↓
Relevant private chunks
       ↓
Gemini
```

Not:

```text
Retrieve everyone
       ↓
Filter later
       ↓
Gemini
```

---

# 🗺️ Roadmap

## Phase 1 — Project Foundation

- [x] Spring Boot backend
- [x] PostgreSQL
- [x] User registration
- [x] Password hashing
- [x] JWT authentication
- [x] Protected endpoints
- [x] User ownership

## Phase 2 — Document Processing

- [x] Document management
- [x] PDF extraction
- [x] DOCX extraction
- [x] TXT processing
- [x] File validation
- [x] Text cleaning
- [x] Chunking

## Phase 3 — Vector Search

- [x] Gemini embeddings
- [x] Qdrant setup
- [x] Vector storage
- [x] Semantic retrieval
- [x] User-isolated retrieval

## Phase 4 — RAG Pipeline

- [x] Retrieval service
- [x] Gemini integration
- [x] Grounding prompt
- [x] Context construction
- [x] Insufficient-context handling
- [x] Source attribution

## Phase 5 — Chat Experience

- [ ] React foundation
- [ ] Tailwind CSS
- [ ] Axios integration
- [ ] Login and registration UI
- [ ] Protected frontend routes
- [ ] Dashboard
- [ ] Document upload UI
- [ ] Chat interface
- [ ] Source display

## Phase 6 — Deployment

- [ ] Production configuration
- [ ] Secret management
- [ ] Backend deployment
- [ ] Production PostgreSQL
- [ ] Production Qdrant
- [ ] Frontend deployment
- [ ] End-to-end verification
- [ ] Deployment documentation

---

# 💡 Why Open Twin?

Most student AI projects stop at:

```text
User → Chatbot → LLM
```

Open Twin focuses on the engineering behind a personalized AI knowledge system:

```text
Private Documents
      ↓
Knowledge Processing
      ↓
Semantic Representation
      ↓
Vector Retrieval
      ↓
Grounded Generation
      ↓
Source Attribution
```

The project combines:

- Backend engineering
- Database design
- Authentication
- Document processing
- Vector databases
- Embeddings
- Semantic search
- RAG
- LLM integration
- Security and data isolation

---

# 📈 Current Achievement

At the end of Phase 4, the backend can already perform the most important AI workflow:

> **A user can ask a question and receive an answer grounded in their own uploaded knowledge, with the retrieved source chunks attached to the response.**

The remaining work focuses primarily on turning this backend AI core into a polished, user-facing product and deploying it publicly.

---

# 🔭 Future Versions

The V1 scope intentionally stays focused.

Potential future directions include:

- Voice integration
- Speech-to-text
- Text-to-speech
- Avatar experiences
- Long-term memory
- Multi-Twin platform
- Interview mode

These are **not part of the current V1 implementation**.

---

# 🎓 Learning Philosophy

Open Twin is being developed using:

```text
Understand
    ↓
Build Small
    ↓
Integrate
    ↓
Test
    ↓
Debug
    ↓
Understand Again
    ↓
Continue
```

The goal is not only to finish the project, but to understand the architecture deeply enough to:

- Debug it
- Explain it
- Make design decisions
- Discuss trade-offs
- Defend it in interviews
- Extend it later

---

# 👨‍💻 Author

**Aniket Singh**

B.Tech Student | Java Backend | AI/GenAI | Full Stack

---

# ⭐ Project Status

**Open Twin is actively under development.**

Current milestone:

```text
████████████████░░░░░░░░  Phase 4 / 6
```

**Backend AI Core:** ✅ Working  
**RAG Pipeline:** ✅ Working  
**Source Attribution:** ✅ Working  
**React Experience:** 🚧 Next  
**Deployment:** ⏳ Planned

---

<p align="center">

### 🚀 Building a Digital Twin, one engineering milestone at a time.

**Open Twin — Your knowledge, conversationally accessible.**

</p>
