package com.iccrb_fyp.app.mvp;

import android.app.Activity;
import android.util.Log;

import com.iccrb_fyp.app.FypMessageModel;
import com.iccrb_fyp.app.database.DatabaseManager;
import com.iccrb_fyp.app.listeners.OnAIRequestListener;
import com.iccrb_fyp.app.listeners.OnMessageListener;
import com.iccrb_fyp.app.listeners.OnTextTranslation;
import com.iccrb_fyp.app.network.AIRequestTask;

import java.util.List;
import java.util.logging.Logger;

import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import static com.iccrb_fyp.app.utils.FypBaseConstants.DEVELOPER_KEY;
import static com.iccrb_fyp.app.utils.FypBaseConstants.MessageSender.SENDER_CLIENT;
import static com.iccrb_fyp.app.utils.FypBaseConstants.MessageSender.SENDER_DFLOW;
import static com.iccrb_fyp.app.utils.FypBaseUtils.getEllapedTime;
import static com.iccrb_fyp.app.utils.FypBaseUtils.getMessageId;

public class FypBasePresenterImpl implements FyPBasePresenter, OnAIRequestListener,OnMessageListener,OnTextTranslation {

    private FypBaseView baseView;
    private AIDataService aiDataService;
    private AIRequestTask aiRequestTask;
    private Activity mActivity;
    private DatabaseManager operationDatabase,listDatabase;

    public FypBasePresenterImpl(Activity activity,FypBaseView baseView){
        this.baseView=baseView;
        this.mActivity=activity;
        AIConfiguration configuration = new AIConfiguration(
                DEVELOPER_KEY,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System
        );
        aiDataService=new AIDataService(activity, configuration);
        operationDatabase =DatabaseManager.getInstance(mActivity,true);
        listDatabase =DatabaseManager.getInstance(mActivity,false);

    }

    @Override
    public void initViews() {
        if(baseView!=null){
            baseView.setToolbar();
        }
    }

    @Override
    public void requestAIResponse(String requestText) {
        if(baseView!=null){
            baseView.clearText();
            baseView.hideKeyboard();
            baseView.onLoadingIndicator(false);
            //Client's message
            FypMessageModel message=new FypMessageModel();
            message.setMessageId(getMessageId());
            message.setMessageContent(requestText);
            message.setMessageTime(getEllapedTime());
            message.setMessageSender(SENDER_CLIENT);
            operationDatabase.insertMessage(message,this);

            //request response from DialogFlow
            AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(requestText);
            aiRequestTask=new AIRequestTask(aiDataService,this);
            aiRequestTask.execute(aiRequest);
        }
    }

    @Override
    public void closeSession() {
        if(baseView!=null){
            mActivity.moveTaskToBack(true);
        }
    }

    @Override
    public void clearMessages() {
       if(baseView!=null){
           DatabaseManager.getInstance(mActivity,true).clearConversation();
       }
    }

    @Override
    public List<FypMessageModel> getMessageList() {
        if(baseView!=null){
            return listDatabase.getConversation();
        }else{
            return null;
        }
    }

    @Override
    public void OnRequestFailed(AIError aiError) {
       if(baseView!=null){
           aiRequestTask.cancel(true);
       }
    }

    @Override
    public void OnRequestSuccess(AIResponse aiResponse) {
       if(baseView!=null){
           baseView.onLoadingIndicator(true);
           aiRequestTask.cancel(true);
           Result result = aiResponse.getResult();
           FypMessageModel message=new FypMessageModel();
           message.setMessageId(aiResponse.getId());
           message.setMessageSender(SENDER_DFLOW);
           message.setMessageTime(String.valueOf(aiResponse.getTimestamp().getTime()));
           message.setMessageContent(result.getFulfillment().getSpeech());
           operationDatabase.insertMessage(message,this);
           Log.d("Response",message.toString());
       }


    }

    @Override
    public void OnMessageChange(boolean isReceived) {
        if(baseView!=null){
            baseView.refreshMessages();
        }
    }

    @Override
    public void OnTranslated(String translatedText) {
        if(baseView!=null){
            requestAIResponse(translatedText);
        }
    }

    @Override
    public void onFailed(String error) {

    }
}
