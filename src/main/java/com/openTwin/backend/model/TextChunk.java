package com.opentwin.backend.model;

public class TextChunk {

    private final Long documentId;
    private final int chunkIndex;
    private final String content;

    public TextChunk(
            Long documentId,
            int chunkIndex,
            String content
    ) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
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
}