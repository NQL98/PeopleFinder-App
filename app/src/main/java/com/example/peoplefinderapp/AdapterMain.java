package com.example.peoplefinderapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.HolderMainChatList>{

    private Context context;
    private ArrayList<ModelMain> mainChatLists;

    public AdapterMain(Context context, ArrayList<ModelMain> mainChatLists) {
        this.context = context;
        this.mainChatLists = mainChatLists;
    }

    @NonNull
    @Override
    public HolderMainChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);

        return new HolderMainChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMainChatList holder, int position) {

        //get data
        ModelMain model = mainChatLists.get(position);
        final String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        final String fio = model.getFio();

        //set data
        holder.nodeName.setText(fio);
        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.no_image).into(holder.chat_image);
        }
        catch (Exception e) {
            holder.chat_image.setImageResource(R.drawable.no_image);
        }

        //handle group click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open group chat
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mainChatLists.size();
    }

    //view holder class
    class HolderMainChatList extends RecyclerView.ViewHolder {

        //ui views
        private ImageView chat_image;
        private TextView nodeName;

        public HolderMainChatList(@NonNull View itemView) {
            super(itemView);

            chat_image = itemView.findViewById(R.id.chat_image);
            nodeName = itemView.findViewById(R.id.nodeName);
        }
    }
}
