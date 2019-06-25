package com.diamong.myphotoblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView blogRecyclerView;
    private BlogRecyclrAdapter blogRecyclrAdapter;
    //private RecyclerView.LayoutManager layoutManager;

    private List<BlogPost> blogPosts;
    private FirebaseFirestore firebaseFirestore;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //layoutManager = new LinearLayoutManager(getActivity());

        blogPosts = new ArrayList<>();
        blogRecyclerView = view.findViewById(R.id.blog_recycler_view);
        blogRecyclrAdapter = new BlogRecyclrAdapter(blogPosts);

        //blogRecyclerView.setLayoutManager(layoutManager);
        //blogRecyclerView.setAdapter(blogRecyclrAdapter);

        blogRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blogRecyclerView.setAdapter(blogRecyclrAdapter);



        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        blogPosts.add(blogPost);
                        blogRecyclrAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


}
