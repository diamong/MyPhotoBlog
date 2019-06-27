package com.diamong.myphotoblog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView blogRecyclerView;
    private BlogRecyclrAdapter blogRecyclrAdapter;
    //private RecyclerView.LayoutManager layoutManager;

    private List<BlogPost> blogPosts;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private DocumentSnapshot lastVisible;

    private Boolean isFirstPageFirstLoad = true;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //layoutManager = new LinearLayoutManager(getActivity());

        mAuth = FirebaseAuth.getInstance();

        blogPosts = new ArrayList<>();
        blogRecyclerView = view.findViewById(R.id.blog_recycler_view);
        blogRecyclrAdapter = new BlogRecyclrAdapter(blogPosts);

        //blogRecyclerView.setLayoutManager(layoutManager);
        //blogRecyclerView.setAdapter(blogRecyclrAdapter);

        blogRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blogRecyclerView.setAdapter(blogRecyclrAdapter);


        firebaseFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {

            blogRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        String desc = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(), "Reached:   " + desc, Toast.LENGTH_SHORT).show();

                        loadMorePost();
                    }
                }
            });


            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING).limit(3);

            //getActivity -> 로그아웃시 에러 해결,,,
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                    if (isFirstPageFirstLoad) {

                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }


                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId=doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            if (isFirstPageFirstLoad) {

                                blogPosts.add(blogPost);
                            } else {
                                blogPosts.add(0, blogPost);
                            }

                            blogRecyclrAdapter.notifyDataSetChanged();
                        }
                    }

                    isFirstPageFirstLoad = false;
                }
            });
        }


        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePost() {


        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("time_stamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId=doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blogPosts.add(blogPost);
                            blogRecyclrAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        lastVisible=null;
        isFirstPageFirstLoad=true;
    }
}
