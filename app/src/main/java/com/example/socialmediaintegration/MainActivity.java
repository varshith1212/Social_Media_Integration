package com.example.socialmediaintegration;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaintegration.databinding.ActivityMainBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    public static final String ID_Email = "email_id";
    public static final String ID_Name = "name_id";

    ActivityMainBinding binding;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView e;
    private LoginButton login_button;
    private CallbackManager c;
    private ImageView i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content of the activity to use the activity_main.xml layout file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        binding.signInButton.setSize(SignInButton.SIZE_WIDE);

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult.launch(signInIntent);
            }
        });


        // FaceBook implementation

        //e = findViewById(R.id.e);
        login_button = findViewById(R.id.login_button);
        c = CallbackManager.Factory.create();

        login_button.setPermissions(Arrays.asList("Email","UserBirthday"));

        login_button.registerCallback(c, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                /*
                //TODO: Use the Profile class to get information about the current user.
                handleSignInResult(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LoginManager.getInstance().logOut();
                        return null;
                    }
                });
                */

            }

            @Override
            public void onCancel() {
                handleSignInResult(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(MainActivity.class.getCanonicalName(), error.getMessage());
                handleSignInResult(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        c.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker t = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null){
                e.setText("");
                Toast.makeText(MainActivity.this,"logout",Toast.LENGTH_SHORT).show();
            }
            else{
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null){
                    try{
                        String email = object.getString("email");
                        String id = object.getString("id");
                        e.setText(email);
                    }
                    catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }


    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                }
            }
    );

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            if(acct != null){
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                //Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();

                Intent googleProfile = new Intent(MainActivity.this,sighIn.class);
                String message = personEmail;
                googleProfile.putExtra(ID_Email, personEmail);
                googleProfile.putExtra(ID_Name,personName);
                googleProfile.putExtra("ID_Photo", acct.getPhotoUrl().toString());

                startActivity(googleProfile);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("GOOGLE ERROR",e.getMessage());
        }
    }
}