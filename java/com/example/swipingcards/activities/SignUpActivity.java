package com.example.swipingcards.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swipingcards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
   public EditText emailField, passwordField, confirmPasswordField, userNameField, EnterAge;
   public Button register_btn;
   public FirebaseAuth mAuth;
   public FirebaseFirestore db = FirebaseFirestore.getInstance();
   public DatePickerDialog.OnDateSetListener mDateSetListener;
   public String dateOfbirth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        EnterAge = findViewById(R.id.enterAgeField);
        userNameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.editTextTextEmailAddress2);
        passwordField = findViewById(R.id.editTextTextPassword2);
        confirmPasswordField = findViewById(R.id.confirm_password);
        register_btn = findViewById(R.id.register_btn);

        EnterAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("TAG", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

//                String date = month + "/" + day + "/" + year;
                dateOfbirth = month + "/" + day + "/" + year;
                EnterAge.setText(dateOfbirth);
            }
        };

        register_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
            private void registerNewUser() {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirm_Password = confirmPasswordField.getText().toString();
                String username = userNameField.getText().toString();
                String age = EnterAge.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.equals(password,confirm_Password)) {
                    Toast.makeText(getApplicationContext(), "Password do not match, Please try again", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter username!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty (age))  {
                    Toast.makeText(getApplicationContext(), "Please enter your Age!", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Registration successful!"
                                            , Toast.LENGTH_LONG).show();

                                    FirebaseUser Auth_user = mAuth.getCurrentUser();
                                    User newUser = new User(); // creating new user and giving values.

                                    newUser.userId = Auth_user.getUid();
                                    newUser.userEmail = Auth_user.getEmail();
                                    newUser.dateOfBirth = dateOfbirth;
                                    newUser.userName = username;
                                    newUser.userImages = new ArrayList<UserImage>();

                                    //Create new empty document "record" in database
                                    DocumentReference newUserRef = db.collection("Users").document(newUser.userId);

                                    // update empty record with User object
                                    newUserRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast t = Toast.makeText(SignUpActivity.this, "New User Created", Toast.LENGTH_SHORT);
                                            t.setGravity(Gravity.CENTER, 0, 0);
                                            t.show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast t = Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT);
                                            t.setGravity(Gravity.CENTER, 0, 0);
                                            t.show();
                                        }
                                    });
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Registration failed! " + task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }

}