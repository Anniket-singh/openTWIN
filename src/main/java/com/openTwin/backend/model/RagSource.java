package com.opentwin.backend.model;

public class RagSource {

    private final Long documentId;
    private final int chunkIndex;
    private final float score;

    public RagSource(
            Long documentId,
            int chunkIndex,
            float score
    ) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.score = score;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public float getScore() {
        return score;
    }
}