package com.example.swipingcards.activities.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.swipingcards.R;
import com.example.swipingcards.activities.CurrentUser;
import com.example.swipingcards.activities.MainActivity;
import com.example.swipingcards.activities.StartupActivity;
import com.example.swipingcards.activities.UploadPicture;
import com.example.swipingcards.activities.UserImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    public EditText emailET, nameET, ageET;
    public Button save_Changes_Btn, sign_Out_Button;
    public RadioButton radioMan1, radioWoman1, radioMan2, radioWoman2;
    public LinearLayout gallery;
    public ImageButton addImage, removeImage;


    public ProfileFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        sign_Out_Button = v.findViewById(R.id.signout_Button);
        save_Changes_Btn = v.findViewById(R.id.pick_image_button);
        emailET = v.findViewById(R.id.emailET);
        nameET = v.findViewById(R.id.nameET);
        ageET = v.findViewById(R.id.ageET);
        radioMan1 = v.findViewById(R.id.radio_Man1);
        radioWoman1 = v.findViewById(R.id.radio_Woman1);
        radioMan2 = v.findViewById(R.id.radio_Man2);
        radioWoman2 = v.findViewById(R.id.radio_Woman2);
        gallery = v.findViewById(R.id.gallery);
        addImage = v.findViewById(R.id.addImage);
        removeImage = v.findViewById(R.id.removeImage);

        removeImage.setVisibility(View.INVISIBLE);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), UploadPicture.class);
                startActivity(intent);
            }
        });

        LayoutInflater inflater1 = LayoutInflater.from(this.getActivity());
        ImageView imageView;

        if(CurrentUser.user.userImages != null){
        for (UserImage userImage: CurrentUser.user.userImages ) {
            View view = inflater1.inflate(R.layout.item1, gallery, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage.setVisibility(View.VISIBLE);
                    removeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper
                                    (ProfileFragment.this.getActivity(), R.style.AlertDialogCustom));
                            builder.setMessage("Do you want to delete this picture ? ");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //delete picture
                                    CurrentUser.user.userImages.remove(userImage);
                                    updateUserChanges(true, userImage.imageId);
                                }

                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do nothing
                                    removeImage.setVisibility(View.INVISIBLE);
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            });
            imageView = view.findViewById(R.id.imageView1);

            //Load pictures from storage db , resize and put to image view
            Picasso.get().load(userImage.imageUrl).into(imageView);
            gallery.addView(view);
        }
        }

        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.radio_Man1:
                        // switch to fragment 1
                        CurrentUser.user.userGender = "Man";
                        break;
                    case R.id.radio_Woman1:
                        // Fragment 2
                        CurrentUser.user.userGender = "Woman";

                        break;
                }
            }
        });

        RadioGroup radioGroup2 = (RadioGroup) v.findViewById(R.id.radioGroup2);

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.radio_Man2:
                        // switch to fragment 1
                        CurrentUser.user.userPreferredGender = "Man";
                        break;
                    case R.id.radio_Woman2:
                        // Fragment 2
                        CurrentUser.user.userPreferredGender = "Woman";

                        break;
                }
            }
        });

        save_Changes_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserChanges(false,"");
            }
        });

        sign_Out_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


                //giving value to textFields
                emailET.setText(CurrentUser.user.userEmail);
                nameET.setText(CurrentUser.user.userName);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                Date firstDate = null;
                try {
                    firstDate = sdf.parse(CurrentUser.user.dateOfBirth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date secondDate = new Date();
                try {
                    secondDate = sdf.parse(sdf.format(secondDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                ageET.setText(String.valueOf((int) diff / 365));


                if (CurrentUser.user.userGender != null) {
                    if (CurrentUser.user.userGender.equals("Man")) {
                        radioMan1.toggle();
                    } else {
                        radioWoman1.toggle();
                    }

                }
                if (CurrentUser.user.userPreferredGender != null) {
                    if (CurrentUser.user.userPreferredGender.equals("Man")) {
                        radioMan2.toggle();
                    } else {
                        radioWoman2.toggle();
                    }

                }

// Inflate the layout for this fragment
        return v;
    }

    public void signOut() {
        Intent intent = new Intent(ProfileFragment.this.getActivity(), StartupActivity.class);
        startActivity(intent);
    }

    public void updateUserChanges(boolean deleteImage, String imageID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Create new empty document "record" in database
        DocumentReference newUserRef = db.collection("Users").document(CurrentUser.user.userId);

        // update empty record with User object
        newUserRef.set(CurrentUser.user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast t = Toast.makeText(ProfileFragment.this.getActivity(), "Details updated successfully", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                if (deleteImage){

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReference();

                    // Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child("Images/"+ imageID);

                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            Toast.makeText(ProfileFragment.this.getActivity(), "Image file deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileFragment.this.getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            Toast.makeText(ProfileFragment.this.getActivity(), "Failed - image not deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Intent intent = new Intent(ProfileFragment.this.getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast t = Toast.makeText(ProfileFragment.this.getActivity(), "", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        });

    }
}

