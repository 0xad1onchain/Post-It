package com.bookcoast.post_it;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class TeacherAddEvent extends AppCompatActivity {


    private Button publish;
    private ImageButton imageSelect;
    private EditText Title;
    private EditText Desc;
    String title, description, eligibility, contact, imgurl;
    boolean event;
    private Uri imageUri =null;
    private static  final  int GALLERY_REQUEST = 2;
    private EditText Elig;
    private StorageReference mImageReference;
    private DatabaseReference mDataBase;
    private ProgressDialog mProgress;
    private EditText Contact;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    final Firebase ref1 = new Firebase("https://post-it-81fe6.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_event);

        Firebase.setAndroidContext(this);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        ref1.setAndroidContext(this);
        mProgress=new ProgressDialog(this);
        mProgress.setCanceledOnTouchOutside(false);

        Title = (EditText) findViewById(R.id.title);
        Desc = (EditText) findViewById(R.id.description);
        Elig = (EditText) findViewById(R.id.eligiblity);
        Contact = (EditText) findViewById(R.id.contact);
        imageSelect = (ImageButton) findViewById(R.id.imageSelect);
        publish = (Button) findViewById(R.id.publish);
        mImageReference= FirebaseStorage.getInstance().getReference();
        mDataBase= FirebaseDatabase.getInstance().getReference().child("Blog");
        radioGroup = (RadioGroup) findViewById(R.id.radioType);



        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(TeacherAddEvent.this, TeacherLogin.class));
                    finish();
                }
            }
        };

        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //galleryIntent.setType("images/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
                imageSelect.setImageURI(imageUri);
            }
        });
        publish.setOnClickListener(new View.OnClickListener(){
                                             @Override
                                             public void onClick(View view){
                                                 uploading();
                                             }
                                         }

        );



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_remove:
                delete();
                return true;
            case R.id.action_signout:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uploading() {
        mProgress.setMessage("Uploading...");
        mProgress.show();
        if(imageUri != null){
            StorageReference filePath= mImageReference.child("Blog_img").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri=taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost=mDataBase.push();
                    //newPost.child("image_url").setValue(downloadUri.toString());
                    imgurl = downloadUri.toString();
                    title = Title.getText().toString();
                    description = Desc.getText().toString();
                    eligibility = Elig.getText().toString();
                    contact = Contact.getText().toString();
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);
                    String temp = radioButton.getText().toString();
                    Data obj = new Data(title, description, eligibility, contact, imgurl, event);
                    if(temp.equals("Workshop / Event"))
                    {
                        event = true;
                        ref1.child("event").child(title).setValue(obj);
                    }
                    else
                    {
                        event = false;
                        ref1.child("intern").child(title).setValue(obj);
                    }
                    Toast.makeText(getApplicationContext(), "This is my Toast message!"+temp,Toast.LENGTH_SHORT).show();


                    //ref1.child("post").child(title).setValue(obj);
                    //urltext.setText(downloadUri.toString());
                    mProgress.dismiss();
                    //Snackbar.make(view, "Thank you for using Post-It", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){
            imageUri=data.getData();
            imageSelect.setImageURI(imageUri);


        }
    }

    public void delete()
    {
        Intent intent = new Intent(TeacherAddEvent.this, RemoveActivity.class);
        startActivity(intent);
    }

    public void signout() {
        auth.signOut();
        Intent intent = new Intent(TeacherAddEvent.this, Choose_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}