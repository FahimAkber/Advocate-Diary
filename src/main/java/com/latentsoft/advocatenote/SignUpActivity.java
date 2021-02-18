package com.latentsoft.advocatenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.latentsoft.advocatenote.model.AdvocateModel;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etCourt, etEmail, etPassword, etConfirmPass;
    Button btnSignUp;
    TextView tvLogIn;
    ActionBar actionBar;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        actionBar.hide();


        btnSignUp.setOnClickListener(this);


        tvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
            }
        });
    }

    private void init() {
        pb = findViewById(R.id.pb_sign_Up);
        etName = findViewById(R.id.et_sign_up_user_name);
        etCourt = findViewById(R.id.et_sign_up_Court);
        etEmail = findViewById(R.id.et_sign_up_email);
        etPassword = findViewById(R.id.et_sign_up_password);
        etConfirmPass = findViewById(R.id.et_sign_up_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogIn = findViewById(R.id.tv_sign_up_log);
        actionBar = getSupportActionBar();
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("advocate_memory", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {

        final String name = etName.getText().toString().trim();
        final String court = etCourt.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String conPass = etConfirmPass.getText().toString().trim();

        if (name.isEmpty() || court.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please provide your name or court/office.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please provide your email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty() || conPass.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please provide your password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setText("");
            Toast.makeText(SignUpActivity.this, "Please provide a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!conPass.equals(pass)) {
            Toast.makeText(SignUpActivity.this, "Please provide same password.", Toast.LENGTH_SHORT).show();
            etConfirmPass.setText("");
            return;
        }

        if (pass.length() < 6 || conPass.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Please provide more than 6 digit password.", Toast.LENGTH_SHORT).show();
            etPassword.setText(" ");
            etConfirmPass.setText(" ");
            return;
        }

        pb.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    AdvocateModel model = new AdvocateModel(name, court, email);
                    FirebaseDatabase.getInstance().getReference("user/"+mAuth.getUid()+"/advocateInfo").setValue(model);

                    sp.edit().putBoolean("isLogged", true).apply();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    pb.setVisibility(View.GONE);
                    finish();

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUpActivity.this, "Already registered!!!", Toast.LENGTH_SHORT).show();
                        etEmail.setText("");
                        etPassword.setText("");
                        etConfirmPass.setText("");
                    } else {
                        Toast.makeText(SignUpActivity.this, "Fail to sign up!!!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}
