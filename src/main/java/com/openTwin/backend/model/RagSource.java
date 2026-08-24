package com.opentwin.backend.model;

public class RagSource {

    private final Long documentId;
    private final String documentName;
    private final int chunkIndex;
    private final float score;

    public RagSource(
            Long documentId,
            String documentName,
            int chunkIndex,
            float score
    ) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.chunkIndex = chunkIndex;
        this.score = score;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public float getScore() {
        return score;
    }
}