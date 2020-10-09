package com.example.earthunited_v001.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.earthunited_v001.R;
import com.example.earthunited_v001.listAdaptor;
import com.example.earthunited_v001.ui.home.HomeFragment;
import com.example.earthunited_v001.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class globaltarget extends Fragment{


    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private listAdaptor adapter;;
    private View root;
    private HashMap<String, Integer> lstCountries =  new HashMap<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.global_target_goal, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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



                    int iTotal = 0;
                    for (Map.Entry<String, Integer> entry : sortedMapAsc.entrySet())
                    {
                        iTotal+= entry.getValue();
                    }

                    TextView textView5 = root.findViewById(R.id.textView5);
                    textView5.setText(String.valueOf(iTotal*100/300000) + "%");

                    ProgressBar progressBar = root.findViewById(R.id.progressBar);
                    progressBar.setMax(300000);
                    progressBar.setProgress(iTotal);



                    // set up the RecyclerView
                    RecyclerView recyclerView = root.findViewById(R.id.my_recycler_view);
                    recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                    adapter = new listAdaptor(root.getContext(), sortedMapAsc);

                    recyclerView.setAdapter(adapter);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });





        Button button3 = root.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, new HomeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/

                //NestedScrollView idScroll = root.findViewById(R.id.idScroll);
                //idScroll.setVisibility(View.GONE);

                HomeFragment.showAgain();


                getFragmentManager().popBackStack();
            }

        });

        return root;
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

}
