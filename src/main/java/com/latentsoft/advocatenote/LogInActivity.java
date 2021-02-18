package com.latentsoft.advocatenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class LogInActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnLogIn;
    TextView tvSignUp;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        init();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogIn();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                finish();
            }
        });


    }

    private void userLogIn() {
        pb.setVisibility(View.VISIBLE);
        btnLogIn.setVisibility(View.GONE);

        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setText("");
            Toast.makeText(this, "Please provide a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            etPass.setText("");
            Toast.makeText(this, "Password must have 6 digit.", Toast.LENGTH_SHORT).show();
            return;
        }

        pb.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.setVisibility(View.GONE);
                if(task.isSuccessful()){

                    sp.edit().putBoolean("isLogged", true).apply();
                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LogInActivity.this, "Invalid Log In!!!", Toast.LENGTH_SHORT).show();
                    etPass.setText("");
                }
            }
        });

    }

    private void init() {
        pb = findViewById(R.id.pb_log_in);
        sp = getSharedPreferences("advocate_memory", MODE_PRIVATE);
        etEmail = findViewById(R.id.et_log_in_email);
        etPass = findViewById(R.id.et_log_in_password);
        btnLogIn = findViewById(R.id.btn_log_in);
        tvSignUp = findViewById(R.id.tv_log_in_sign_up);
        mAuth = FirebaseAuth.getInstance();
    }
}
