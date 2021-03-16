package com.example.swipingcards.activities.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swipingcards.R;
import com.example.swipingcards.activities.CurrentUser;
import com.example.swipingcards.activities.Match;
import com.example.swipingcards.activities.User;
import com.example.swipingcards.activities.adapters.CardAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.huxq17.swipecardsview.SwipeCardsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SwipeFragment extends Fragment {
    public String userID;
    public FirebaseFirestore db;
    public SwipeCardsView swipeCardsView;
    public ArrayList<User> userList = new ArrayList<User>();

    public SwipeFragment() {
 //Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userID = CurrentUser.user.userId;
        db = FirebaseFirestore.getInstance();


        //retrieving data from db
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userList.add(document.toObject(User.class) );
                            }
                            swipeCardsView = (SwipeCardsView)view.findViewById(R.id.swipeCardsView);
                            swipeCardsView.retainLastCard(false);
                            swipeCardsView.enableSwipe(true);

                            getData();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getData() {

        CardAdapter cardAdapter = new CardAdapter(userList,SwipeFragment.this.getContext());
        swipeCardsView.setAdapter(cardAdapter);
//        swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT);
        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {

            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                if (type.equals(SwipeCardsView.SlideType.RIGHT)){

                    Toast t = Toast.makeText(SwipeFragment.this.getActivity(), userList.get(index).userId, Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                    makeMatchRecordToDB(index);
                } if (type.equals(SwipeCardsView.SlideType.LEFT)){
                    Toast t = Toast.makeText(SwipeFragment.this.getActivity(), userList.get(index).userId, Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {

            }
        });
    }

    private void makeMatchRecordToDB(int index) {
        //Create new empty document "record" in database
        DocumentReference newMatchReference = db.collection("Matches").document();

        Match match = new Match();
        match.matchId = newMatchReference.getId();

        match.userOne = CurrentUser.user.userId;
        match.userTwo = userList.get(index).userId;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        match.matchTime = formatter.format(date);

        // update restaurant iD and save it
        newMatchReference.set(match).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                
            }
        });
    }


}




