package com.iccrb_fyp.app.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iccrb_fyp.app.FypMessageModel;
import com.iccrb_fyp.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.iccrb_fyp.app.utils.FypBaseConstants.MessageSender.SENDER_CLIENT;
import static com.iccrb_fyp.app.utils.FypBaseConstants.MessageSender.SENDER_DFLOW;

public class FypMessageAdapter extends RecyclerView.Adapter<FypMessageAdapter.MessageItem> {

    private List<FypMessageModel> messageList;
    private Context mContext;
    public FypMessageAdapter(List<FypMessageModel> messageList){
        this.messageList=messageList;
    }

    public void setMessageList(List<FypMessageModel> messageList){
        this.messageList=messageList;
    }

    @Override
    public MessageItem onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext=parent.getContext();
        return new MessageItem(LayoutInflater.from(mContext).inflate(R.layout.chat_single_view,parent,false));
    }

    @Override
    public void onBindViewHolder(MessageItem holder, final int position) {
        final FypMessageModel message=messageList.get(position);

        if(message.getMessageSender().equals(SENDER_CLIENT)){
            holder.chatBotHolder.setVisibility(GONE);
        }else{
            holder.chatBotHolder.setVisibility(VISIBLE);
        }

        if(message.getMessageSender().equals(SENDER_DFLOW)){
            holder.clientHolder.setVisibility(GONE);
        }else {
            holder.clientHolder.setVisibility(VISIBLE);
        }

        holder.clientMessage.setText(Html.fromHtml(message.getMessageContent()));
        holder.botMessage.setText(Html.fromHtml(message.getMessageContent()));
        holder.contentHolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label"+position, message.getMessageContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext,"Copied to clipboard",Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageItem extends RecyclerView.ViewHolder{
        @BindView(R.id.contentHolder) LinearLayout contentHolder;
        @BindView(R.id.chatBotHolder) LinearLayout chatBotHolder;
        @BindView(R.id.clientHolder) LinearLayout clientHolder;
        @BindView(R.id.clientMessage) TextView clientMessage;
        @BindView(R.id.botMessage) TextView botMessage;
        MessageItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
