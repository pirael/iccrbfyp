package com.iccrb_fyp.app.mvp;

import com.iccrb_fyp.app.FypMessageModel;

import java.util.List;

public interface FyPBasePresenter {
    void initViews();
    void requestAIResponse(String requestText);
    void closeSession();
    void clearMessages();
    List<FypMessageModel> getMessageList();
}
