package com.example.earthunited_v001.ui.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.earthunited_v001.R;
import com.example.earthunited_v001.comMessages;
import com.example.earthunited_v001.comunicationListAdaptor;
import com.example.earthunited_v001.listAdaptor;
import com.example.earthunited_v001.ui.donationFrag;
import com.example.earthunited_v001.ui.globaltarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements comunicationListAdaptor.ItemClickListener {

    private HomeViewModel homeViewModel;
    private ListView lstMessages;
    private HashMap<String, Integer> lstCountries =  new HashMap<>();

    private List<comMessages> lstMapMessages = new ArrayList<>();

    //DB
    private DatabaseReference mDatabase;
    private Context ThisContextc;


    private void loadFragment(Fragment fragment) {
        //FragmentTransaction t = this.getFragmentManager().beginTransaction();
        //t.replace(R.id.content, fragment);
        //t.commit();
        //mapView.setVisibility(View.GONE);
        //LinearLayout LLayBTNS = root.findViewById(R.id.LLayBTNS);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        NestedScrollView idScroll = root.findViewById(R.id.idScroll);
        idScroll.setVisibility(View.GONE);
    }

    public static void showAgain(){
        NestedScrollView idScroll = root.findViewById(R.id.idScroll);
        idScroll.setVisibility(View.VISIBLE);
    }

    private static View root;
    private comunicationListAdaptor adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.dashboard, container, false);

        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        final VideoView videoView = root.findViewById(R.id.videoView2);
        videoView.setVideoURI(Uri.parse("https://video.wixstatic.com/video/f56734_9691baa86b2e4ce8b3a586db660656e7/1080p/mp4/file.mp4"));
//        videoView.setVideoPath("https://youtu.be/DH71CWEpN8g");
        videoView.start();
        videoView.pause();

        // create an object of media controller

// initiate a video view
        //VideoView simpleVideoView = (VideoView) root.findViewById(R.id.videoView);
// set media controller object for a video view


        ImageButton imgbtnPause = root.findViewById(R.id.imgbtnPause);
        imgbtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.pause();
            }
        });

        ImageButton imgbtnPlay = root.findViewById(R.id.imgbtnPlay);
        imgbtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
            }
        });
        imgbtnPlay.requestFocus();




        ThisContextc = root.getContext();

        //Sort out all the FireBase posts
        //lstMessages= (ListView) root.findViewById(R.id.messagesview);
        //arrayAdapter= new ArrayAdapter<String>( ThisContextc, R.layout.message_listview, R.id.testmessage);
        //lstMessages.setAdapter(arrayAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //GetAllCountires and amounts
        db.collection("Country").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> dsdsa = task.getResult().getDocuments();

                    for (DocumentSnapshot anArray : dsdsa) {
                        Map<String, Object> dq = anArray.getData();
                        String sAmount = dq.values().toArray()[0].toString();
                        String sPlace = anArray.getId();
                        lstCountries.put(sPlace, Integer.valueOf(sAmount.toString()));
                    }

                    Map<String, Integer> sortedMapAsc = sortByComparator(lstCountries, false);


                    int icount = 0;
                    int iTotal = 0;
                    for (Map.Entry<String, Integer> entry : sortedMapAsc.entrySet())
                    {
                        icount++;

                        if(icount == 1){
                            TextView txttop1 = root.findViewById(R.id.txttop1);
                            txttop1.setText(entry.getKey());

                            ProgressBar bartop1 = root.findViewById(R.id.bartop1);
                            bartop1.setMax(100000);
                            bartop1.setProgress(entry.getValue());

                            TextView counttop1 = root.findViewById(R.id.counttop1);
                            counttop1.setText(String.valueOf(entry.getValue()));
                            iTotal+= entry.getValue();

                        }else if(icount == 2){

                            TextView txttop2 = root.findViewById(R.id.txttop2);
                            txttop2.setText(entry.getKey());

                            ProgressBar bartop2 = root.findViewById(R.id.bartop2);
                            bartop2.setMax(100000);
                            bartop2.setProgress(entry.getValue());

                            TextView counttop2 = root.findViewById(R.id.counttop2);
                            counttop2.setText(String.valueOf(entry.getValue()));
                            iTotal+= entry.getValue();
                        }else if(icount == 3){
                            TextView txttop3 = root.findViewById(R.id.txttop3);
                            txttop3.setText(entry.getKey());

                            ProgressBar bartop3 = root.findViewById(R.id.bartop3);
                            bartop3.setMax(100000);
                            bartop3.setProgress(entry.getValue());

                            TextView counttop3 = root.findViewById(R.id.counttop3);
                            counttop3.setText(String.valueOf(entry.getValue()));
                            iTotal+= entry.getValue();
                            break;
                        }
                    }
                    TextView textView5 = root.findViewById(R.id.textView5);
                    textView5.setText(String.valueOf(iTotal*100/300000) + "%");

                    ProgressBar progressBar = root.findViewById(R.id.progressBar);
                    progressBar.setMax(300000);
                    progressBar.setProgress(iTotal);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Button btnGlobalTargeSeeAll = root.findViewById(R.id.btnGlobalTargeSeeAll);

        btnGlobalTargeSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new globaltarget());
            }

        });



        try{
            mDatabase.child("MainThread/Messages").addValueEventListener(postListener);
        }catch (Exception es){

        }


        // set up the RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        adapter = new comunicationListAdaptor(root.getContext(), lstMapMessages);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);



        return root;
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            lstMapMessages.clear();
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                if(postSnapshot.hasChild("Message")){


                   // String key = ;
                    //arrayAdapter.add(key);

                    //comMessages cn = new comMessages();
                    //cn.sMessage = "sMessage";
                    //cn.sTitle = "sTitle";
                    //cn.sLink = "https://firebasestorage.googleapis.com/v0/b/earthunited-17491.appspot.com/o/XBCAD_AppBrief_24072020.mp4?alt=media&token=a7baa1a6-c108-4178-8c61-2bfa313a457a";
                    //  https://firebasestorage.googleapis.com/v0/b/earthunited-17491.appspot.com/o/APDS_Description_Video.mp4?alt=media&token=56b3b2df-5f6a-4e60-aff6-87f0d4863340
                    //https://firebasestorage.googleapis.com/v0/b/earthunited-17491.appspot.com/o/XBCAD_AppBrief_24072020.mp4?alt=media

                    comMessages cn = new comMessages();
                    cn.sMessage = postSnapshot.child("Message").getValue(String.class);
                    cn.sTitle = postSnapshot.child("Title").getValue(String.class);
                    cn.sLink = "https://firebasestorage.googleapis.com/v0/b/earthunited-17491.appspot.com/o/" +
                            postSnapshot.child("VideoName").getValue(String.class) +
                            "?alt=media";



                    lstMapMessages.add(cn);

                }

            }
            adapter.notifyDataSetChanged();
            NestedScrollView idScroll = root.findViewById(R.id.idScroll);
            idScroll.smoothScrollTo(0,0);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };


    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //VideoView VideoView = view.findViewById(position);

        //MediaController mediaController = new MediaController(root.getContext());
        //mediaController.setVisibility(View.GONE);
        //VideoView.setMediaController(mediaController);

    }
}