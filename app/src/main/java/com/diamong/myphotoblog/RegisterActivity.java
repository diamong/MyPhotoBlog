package com.diamong.myphotoblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerEmailText, registerPasswordText, registerConfirmPassword;
    private Button createAccountBtn, registerLoginBtn;
    private FirebaseAuth mAuth;
    private ProgressBar registerProgressBar;

    private void Init() {
        registerEmailText = findViewById(R.id.register_email);
        registerPasswordText = findViewById(R.id.register_password);
        registerConfirmPassword = findViewById(R.id.register_confirm_password);
        createAccountBtn = findViewById(R.id.register_btn);
        registerLoginBtn = findViewById(R.id.register_login_btn);
        registerProgressBar = findViewById(R.id.register_progressbar);
        registerProgressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Init();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmailText.getText().toString();
                String password = registerPasswordText.getText().toString();
                String confirmPassword = registerConfirmPassword.getText().toString();


                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                    registerProgressBar.setVisibility(View.VISIBLE);
                    if (password.equals(confirmPassword)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {

                                    Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error:  " + errorMessage, Toast.LENGTH_SHORT).show();

                                }
                                registerProgressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
