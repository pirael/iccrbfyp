package com.iccrb_fyp.app.network;

import android.os.AsyncTask;

import com.iccrb_fyp.app.listeners.OnAIRequestListener;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class AIRequestTask extends AsyncTask<AIRequest, Integer, AIResponse> {
    private AIDataService aiDataService;
    private OnAIRequestListener requestListener;
    private AIError aiError;
    public AIRequestTask(AIDataService aiDataService,OnAIRequestListener requestListener){
        this.aiDataService=aiDataService;
        this.requestListener = requestListener;
    }

    @Override
    protected AIResponse doInBackground(AIRequest... aiRequests) {
        final AIRequest request = aiRequests[0];
        try {
            return aiDataService.request(request);
        } catch (final AIServiceException e) {
            aiError=new AIError(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(AIResponse aiResponse) {
        super.onPostExecute(aiResponse);
        if (aiResponse != null) {
            requestListener.OnRequestSuccess(aiResponse);
        } else {
            requestListener.OnRequestFailed(aiError);
        }
    }
}
