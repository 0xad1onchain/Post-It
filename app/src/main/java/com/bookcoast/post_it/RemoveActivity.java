package com.bookcoast.post_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class RemoveActivity extends AppCompatActivity {

    private String title;
    private Button rmvBtn;
    private EditText edit_title;
    private StorageReference mImageReference;
    private DatabaseReference mDataBase;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    final Firebase ref1 = new Firebase("https://post-it-81fe6.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        Firebase.setAndroidContext(this);
        edit_title = (EditText) findViewById(R.id.title_remove);
        auth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        rmvBtn = (Button) findViewById(R.id.remove_btn);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(RemoveActivity.this, TeacherLogin.class));
                    finish();
                }

            }

        };


        rmvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeEvent();
            }
        });
    }





            public void removeEvent()
            {
                title = edit_title.getText().toString();
                if (title!= null) {
                    ref1.child("event").child(title).removeValue();
                    ref1.child("intern").child(title).removeValue();
                }
            }

    }

