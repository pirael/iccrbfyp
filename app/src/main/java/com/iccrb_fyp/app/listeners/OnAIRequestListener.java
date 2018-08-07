package com.iccrb_fyp.app.listeners;

import ai.api.model.AIError;
import ai.api.model.AIResponse;

public interface OnAIRequestListener {
    void OnRequestFailed(AIError aiError);
    void OnRequestSuccess(AIResponse aiResponse);
}
