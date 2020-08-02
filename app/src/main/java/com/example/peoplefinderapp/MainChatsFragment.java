package com.example.peoplefinderapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainChatsFragment extends Fragment {

    private RecyclerView mainRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelMain> mainChatLists;
    private AdapterMain adapterMain;

    public MainChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_chats, container, false);

        mainRv = view.findViewById(R.id.mainRv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadMainChatsList();

        return view;
    }

    private void loadMainChatsList() {
        mainChatLists = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mainChatLists.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //if current user's uid exists in participants list of group then show that group
                    //if (ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        ModelMain model = ds.getValue(ModelMain.class);
                        mainChatLists.add(model);
                    //}
                }
                adapterMain = new AdapterMain(getActivity(), mainChatLists);
                mainRv.setAdapter(adapterMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void searchMainChatsList(final String query) {
        mainChatLists = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mainChatLists.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //if current user's uid exists in participants list of group then show that group
                    //if (ds.child("Participants").child(firebaseAuth.getUid()).exists()){

                        //search by fio
                        if (ds.child("fio").toString().toLowerCase().contains(query.toLowerCase())){
                            ModelMain model = ds.getValue(ModelMain.class);
                            mainChatLists.add(model);
                        }
                }
                adapterMain = new AdapterMain(getActivity(), mainChatLists);
                mainRv.setAdapter(adapterMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
