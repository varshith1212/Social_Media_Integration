package com.example.socialmediaintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class sighIn extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigh_in);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String Email = intent.getStringExtra(MainActivity.ID_Email);
        String Name = intent.getStringExtra(MainActivity.ID_Name);

        ImageView photo = findViewById(R.id.profileImage);

        /*Intent image_intent = getIntent();
        String Photo= image_intent.getStringExtra("ID_Photo");
        Uri fileUri = Uri.parse(Photo);
        photo.setImageURI(fileUri);*/

        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("ID_Photo"));

        Picasso.with(this)
                .load(myUri)
                .into(photo);

        // Capture the layout's TextView and set the string as its text
        TextView email = findViewById(R.id.email);
        email.setText(Email);

        TextView name = findViewById(R.id.name);
        name.setText(Name);

        Button sighOut = findViewById(R.id.signOutBtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        sighOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(sighIn.this,"signOut",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
