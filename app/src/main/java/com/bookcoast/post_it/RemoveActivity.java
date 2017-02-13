package com.bookcoast.post_it;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class RemoveActivity extends AppCompatActivity {

    private String title;
    private Button rmvBtn;
    private EditText edit_title;
    private StorageReference mImageReference;
    //private DatabaseReference mDataBase;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String uid;
    private Query userfEvents;
    private RecyclerView recylceview;
    private DatabaseReference mDatabase;
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
        uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("event");
        //DatabaseReference chatRef = mDataBase.child("");
       Query userfEvents = mDatabase.orderByChild("uid").equalTo(uid);
        recylceview = (RecyclerView) findViewById(R.id.list);
        recylceview.setHasFixedSize(true);
        recylceview.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    protected void onStart() {
        super.onStart();
        Query userfEvents = mDatabase.orderByChild("uid").equalTo(uid);
        FirebaseRecyclerAdapter<Post,PostviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostviewHolder>(
                Post.class,
                R.layout.list_cards,
                PostviewHolder.class,
                userfEvents

        ) {
            @Override
            protected void populateViewHolder(PostviewHolder viewHolder, Post model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setdesc(model.getDescription());
                viewHolder.setelig(model.getEligibility());
                viewHolder.setcontact(model.getContact());
                viewHolder.setImage(getApplicationContext(),model.getImgurl());
            }
        };
        recylceview.setAdapter(firebaseRecyclerAdapter);


    }

    public static class PostviewHolder extends RecyclerView.ViewHolder{
        View mview;
        public PostviewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setTitle(String title){
            TextView posttitle = (TextView) mview.findViewById(R.id.tit_rec);
            posttitle.setText(title);
        }
        public void setdesc(String desc){
            TextView posttitle = (TextView) mview.findViewById(R.id.desc_rec);
            posttitle.setText(desc);
        }
        public void setelig(String elig){
            TextView posttitle = (TextView) mview.findViewById(R.id.eligibility_rec);
            posttitle.setText(elig);
        }
        public void setcontact(String contact){
            TextView posttitle = (TextView) mview.findViewById(R.id.contact_rec);
            posttitle.setText(contact);
        }
        public void setImage(Context cts, String imgurl){
            ImageView postimg = (ImageView) mview.findViewById(R.id.img_rec);
            Picasso.with(cts).load(imgurl).into(postimg);

        }
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

