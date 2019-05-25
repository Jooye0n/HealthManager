package com.example.bbbb.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private String userName = "";
    private String userEmail = "";
    private String userPassword = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.85);
        getWindow().getAttributes().width = width;

        editTextName = findViewById(R.id.signup_et_name);
        editTextEmail = findViewById(R.id.signup_et_email);
        editTextPassword = findViewById(R.id.signup_et_password);

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextName.getText().toString();
                userEmail = editTextEmail.getText().toString();
                userPassword = editTextPassword.getText().toString();

                if (isValidEmail() && isValidPasswd()) {
                    Intent intent = new Intent();

                    intent.putExtra("userName", userName);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("userPassword", userPassword);

                    database = FirebaseDatabase.getInstance();
                    mDatabase = database.getReference();

                    String split[] = userEmail.split("@");

                    mDatabase.child("users").child(split[0]).child("userEmail").setValue(userEmail);
                    mDatabase.child("users").child(split[0]).child("userName").setValue(userName);

                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean isValidEmail() {
        if (userEmail.isEmpty()) {
            // 이메일 공백
            Toast.makeText(getApplicationContext(), R.string.email_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            // 이메일 형식 불일치
            Toast.makeText(getApplicationContext(), R.string.email_not_matches, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (userPassword.isEmpty()) {
            // 비밀번호 공백
            Toast.makeText(getApplicationContext(), R.string.password_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(userPassword).matches()) {
            // 비밀번호 형식 불일치
            Toast.makeText(getApplicationContext(), R.string.password_not_matches, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
