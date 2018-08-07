package com.iccrb_fyp.app.listeners;

public interface OnTextTranslation {
    void OnTranslated(String translatedText);
    void onFailed(String error);
}
