package com.opentwin.backend.model;

import java.util.List;

public class RagResponse {

    private final String answer;
    private final List<RagSource> sources;

    public RagResponse(
            String answer,
            List<RagSource> sources
    ) {
        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public List<RagSource> getSources() {
        return sources;
    }
}