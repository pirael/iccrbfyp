package com.iccrb_fyp.app.mvp;

import com.iccrb_fyp.app.FypMessageModel;

import java.util.List;

import ai.api.model.Result;

public interface FypBaseView {
    void setToolbar();
    void refreshMessages();
    void clearText();
    void hideKeyboard();
    void onLoadingIndicator(boolean isLoaded);
}
