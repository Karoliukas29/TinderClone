package com.example.swipingcards.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.swipingcards.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class UploadPicture extends AppCompatActivity {
    private static final int MY_PERMISSION_STORAGE = 1;
    public Button pickPictureFromGalleryBtn, savePickedPictureBtn;
    public ImageView uploadedPhotoField;
    Uri croppedImageUri;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        uploadedPhotoField = findViewById(R.id.uploaded_photo_field);
        pickPictureFromGalleryBtn = findViewById(R.id.pick_image_button);
        savePickedPictureBtn = findViewById(R.id.save_Picture_Button);
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        //savePickedPictureBtn.setEnabled(false);
        savePickedPictureBtn.setVisibility(View.INVISIBLE);


        pickPictureFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting the limit of pictures to upload
             if(CurrentUser.user.userImages.size() <6) {
                 pickPicture();
             } else {
                 Toast t = Toast.makeText(UploadPicture.this, "You reached your photos limit", Toast.LENGTH_SHORT);
                 t.setGravity(Gravity.CENTER, 0, 0);
                 t.show();
             }
            }
        });
        savePickedPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuid = UUID.randomUUID().toString();
                String imageId = uuid + "." + "jpg";
                StorageReference reference = storageReference.child(imageId);
                reference.putFile(croppedImageUri).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        String downloadURL = uri.toString();
                                        // Create new empty document "record" in database
                                        DocumentReference newUserRef = db.collection("Users").document(CurrentUser.user.userId);

                                        // creating object with imageID and imageURL
                                        UserImage userImage = new UserImage();
                                        userImage.imageId = imageId; // Setting values
                                        userImage.imageUrl = downloadURL;
                                        CurrentUser.user.userImages.add(userImage);

                                        // update empty record with User object
                                        newUserRef.set(CurrentUser.user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(UploadPicture.this, MainActivity.class);
                                                startActivity(intent);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast t = Toast.makeText(UploadPicture.this, "", Toast.LENGTH_SHORT);
                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                t.show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
            }
        });
    }
    public void pickPicture() {
        if (ContextCompat.checkSelfPermission(UploadPicture.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(UploadPicture.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadPicture.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            ActivityCompat.requestPermissions(UploadPicture.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1) //You can skip this for free form aspect ratio)
                    .start(UploadPicture.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                croppedImageUri = resultUri;
                // Set uri as Image in the ImageView:
                uploadedPhotoField.setImageURI(resultUri);
                savePickedPictureBtn.setEnabled(true);
                savePickedPictureBtn.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}


