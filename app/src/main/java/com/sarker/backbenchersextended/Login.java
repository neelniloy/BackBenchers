package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private Button getStart;
    private int RESULT_CODE_SINGIN=999;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_login);


        getStart = findViewById(R.id.btn_get_started);
        progressBar = findViewById(R.id.login_progress_bar);
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getStart.setVisibility(View.GONE);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            getStart.setVisibility(View.VISIBLE);
        }

        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        //Attach a onClickListener
        getStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInM();
                progressBar.setVisibility(View.VISIBLE);
            }
        });


    }

    //when the signIn Button is clicked then start the signIn Intent
    private void signInM() {
        Intent singInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
    }

    // onActivityResult (Here we handle the result of the Activity )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_SINGIN) {        //just to verify the code
            //create a Task object and use GoogleSignInAccount from Intent and write a separate method to handle singIn Result.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {

            GoogleSignInAccount account = task.getResult(ApiException.class);
            //Toast.makeText(Login.this,"Signed In Successfully",Toast.LENGTH_LONG).show();
            //SignIn successful now show authentication
            FirebaseGoogleAuth(account);

            progressBar.setVisibility(View.GONE);

        } catch (ApiException e) {
            e.printStackTrace();

            //Toast.makeText(Login.this,"SignIn Failed!!!",Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login.this,"Successfully Logged in",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                else {
                    Toast.makeText(Login.this,"Failed!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}