package com.opentwin.backend.model;

public class RetrievedChunk {

    private final Long documentId;
    private final int chunkIndex;
    private final String content;
    private final float score;

    public RetrievedChunk(
            Long documentId,
            int chunkIndex,
            String content,
            float score
    ) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.score = score;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public float getScore() {
        return score;
    }
}