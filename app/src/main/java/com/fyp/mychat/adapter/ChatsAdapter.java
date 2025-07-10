package com.fyp.mychat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.mychat.R;
import com.fyp.mychat.model.ChatsModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    List<ChatsModel> chatList ;
    Context context;

    public ChatsAdapter(List<ChatsModel> chatList,Context context){
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.conversation_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatsModel currentChat = chatList.get(position);

        if (!currentChat.getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            setVisibilty(true, holder);
            holder.firstUM.setText(currentChat.getMessage());
            holder.sentTimeM.setText(formatTimeStamp(currentChat.getTimeStamp()));
        } else {
            setVisibilty(false, holder);
            holder.secondUM.setText(currentChat.getMessage());
            holder.receivedTimeM.setText(formatTimeStamp(currentChat.getTimeStamp()));
        }
    }

    private String formatTimeStamp(Long timestamp){
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return format.format(new Date(timestamp));
    }
    private void setVisibilty(Boolean isSender,ViewHolder holder){
        if (isSender){
            holder.firstUS.setVisibility(View.VISIBLE);
            holder.secondUS.setVisibility(View.GONE);
        }else {
            holder.firstUS.setVisibility(View.GONE);
            holder.secondUS.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void update(Boolean chatCleared) {
        if (chatCleared) {
            this.chatList.clear();
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView firstUM,secondUM,sentTimeM,receivedTimeM;
        LinearLayout secondUS,firstUS;

        ViewHolder(View itemView){
            super(itemView);
        firstUM = itemView.findViewById(R.id.firstUserMessage);
        secondUM = itemView.findViewById(R.id.secondUserMessage);
        sentTimeM = itemView.findViewById(R.id.message_sent_time);
        receivedTimeM = itemView.findViewById(R.id.message_recieved_time);
        firstUS = itemView.findViewById(R.id.senderId);
        secondUS = itemView.findViewById(R.id.recieverId);
        }
    }
}
