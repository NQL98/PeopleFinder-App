package com.example.peoplefinderapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat>{

    private Context context;
    private ArrayList<ModelChat> modelChatList;

    private FirebaseAuth firebaseAuth;

    public AdapterChat(Context context, ArrayList<ModelChat> modelChatList) {
        this.context = context;
        this.modelChatList = modelChatList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        return new HolderChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {

        //get data
        ModelChat model = modelChatList.get(position);
        String timestmap = model.getTimestamp();
        String message = model.getMessage();
        String senderUid = model.getSender();
        //Long time = new Date().getTime();

        //convert timestamp to normal format
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((Long.parseLong(timestmap)));
        String dateTime = DateFormat.format("dd.MM.yyyy hh:mm", cal).toString();

        //set data
        holder.messageTv_text.setText(message);
        holder.messageTv_time.setText(dateTime);

        setUserName(model, holder);
    }

    private void setUserName(ModelChat model, final HolderChat holder) {
        //get sender info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();

                            holder.messageTv_user.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelChatList.size();
    }

    class HolderChat extends RecyclerView.ViewHolder{

        private TextView messageTv_user;
        private TextView messageTv_time;
        private TextView messageTv_text;

        public HolderChat(@NonNull View itemView) {
            super(itemView);

            messageTv_user = itemView.findViewById(R.id.messageTv_user);
            messageTv_time = itemView.findViewById(R.id.messageTv_time);
            messageTv_text = itemView.findViewById(R.id.messageTv_text);
        }
    }
}
