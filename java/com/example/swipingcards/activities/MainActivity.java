            package com.example.swipingcards.activities;

    import android.os.Bundle;
    import android.widget.FrameLayout;

    import com.example.swipingcards.R;
    import com.example.swipingcards.activities.fragments.MatchesFragment;
    import com.example.swipingcards.activities.fragments.ProfileFragment;
    import com.example.swipingcards.activities.fragments.SwipeFragment;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.tabs.TabLayout;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    public class MainActivity extends AppCompatActivity {
        FrameLayout simpleFrameLayout;
        TabLayout tabLayout;
        public FirebaseFirestore db;
        public FirebaseAuth mAuth;
        public FirebaseUser Auth_user;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            Auth_user = mAuth.getCurrentUser();

            String userId = Auth_user.getUid();
//        Get User document reference
            DocumentReference userRef = db.collection("Users").document(userId);
        // updates Current user details from database
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    CurrentUser.user = documentSnapshot.toObject(User.class);
                }
            });
















            simpleFrameLayout = findViewById(R.id.simpleFrameLayout);
            tabLayout = findViewById(R.id.simpleTabLayout);
// Create a new Tab named "First"
            TabLayout.Tab firstTab = tabLayout.newTab();
            firstTab.setIcon(R.drawable.tab_profile); // set an icon for the
// first tab
            tabLayout.addTab(firstTab); // add  the tab at in the TabLayout
// Create a new Tab named "Second"
            TabLayout.Tab secondTab = tabLayout.newTab();
            secondTab.setIcon(R.drawable.tab_swipe); // set an icon for the second tab
            tabLayout.addTab(secondTab); // add  the tab  in the TabLayout
// Create a new Tab named "Third"
            TabLayout.Tab thirdTab = tabLayout.newTab();
            thirdTab.setIcon(R.drawable.tab_matches); // set an icon for the first tab
            tabLayout.addTab(thirdTab); // add  the tab at in the TabLayout


// perform setOnTabSelectedListener event on TabLayout
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
// get the current selected tab's position and replace the fragment accordingly
                    Fragment fragment = null;
                    switch (tab.getPosition()) {
                        case 0:
                            fragment = new ProfileFragment();
                            break;
                        case 1:
                            fragment = new SwipeFragment();
                            break;
                        case 2:
                            fragment = new MatchesFragment();
                            break;
                    }
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.simpleFrameLayout, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        }

