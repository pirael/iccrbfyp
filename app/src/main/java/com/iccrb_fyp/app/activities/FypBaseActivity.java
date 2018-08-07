package com.iccrb_fyp.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iccrb_fyp.app.FypMessageModel;
import com.iccrb_fyp.app.R;
import com.iccrb_fyp.app.adapter.FypMessageAdapter;
import com.iccrb_fyp.app.mvp.FyPBasePresenter;
import com.iccrb_fyp.app.mvp.FypBasePresenterImpl;
import com.iccrb_fyp.app.mvp.FypBaseView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FypBaseActivity extends AppCompatActivity implements FypBaseView{

    @BindView(R.id.toolBar) Toolbar toolBar;
    @BindView(R.id.toolBarTitle)TextView toolBarTitle;
    @BindView(R.id.sendButton)ImageView sendButton;
    @BindView(R.id.chatText)EditText chatText;
    @BindView(R.id.chatConversation)RecyclerView mRecyclerView;
    @BindView(R.id.loadingIndicator)AVLoadingIndicatorView loadingIndicatorView;
    @BindString(R.string.app_name) String appName;

    private FyPBasePresenter presenter;
    private FypMessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_layout);
        ButterKnife.bind(this);
        presenter=new FypBasePresenterImpl(this,this);
        presenter.initViews();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String request=chatText.getText().toString();
                if(!request.isEmpty()){
                    presenter.requestAIResponse(request);
                }
            }
        });

        adapter=new FypMessageAdapter(new ArrayList<FypMessageModel>());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        manager.setStackFromEnd(true);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        presenter.clearMessages();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        presenter.closeSession();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setToolbar() {
        setSupportActionBar(toolBar);
        if(toolBar!=null){
            ActionBar actionBar = getSupportActionBar();
            if(actionBar !=null){
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
            toolBar.setTitle("");
            toolBarTitle.setText(appName);
        }
    }

    @Override
    public void refreshMessages() {
       adapter.setMessageList(presenter.getMessageList());
       adapter.notifyDataSetChanged();
       mRecyclerView.invalidate();
        mRecyclerView.scrollToPosition(presenter.getMessageList().size() - 1);
    }

    @Override
    public void clearText() {
        chatText.setText(null);
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onLoadingIndicator(boolean isLoaded) {
        loadingIndicatorView.setVisibility(isLoaded ? View.GONE:View.VISIBLE);
    }


}
