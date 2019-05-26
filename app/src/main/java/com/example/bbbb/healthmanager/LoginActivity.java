package com.example.bbbb.healthmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth mAuth;

    // 이름 생년월일 이메일 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;

    private String userName = "";
    private String userEmail = "";
    private String userPassword = "";

    private ProgressDialog mProgressDialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어베이스 인증 객체 선언
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);

        if (getIntent().getBooleanExtra("signOut", false)) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        if (currentUser != null) {
            intent = new Intent(LoginActivity.this, MainActivity.class);

            userEmail = currentUser.getEmail();
            intent.putExtra("userEmail", userEmail);

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if(resultCode == RESULT_OK){
                userName = data.getStringExtra("userName");
                userEmail = data.getStringExtra("userEmail");
                userPassword = data.getStringExtra("userPassword");
//                userBirth = data.getStringExtra("userBirth");

                createUser(userEmail, userPassword);
            }
        }
    }

    public void singUp(View view) {
        Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivityForResult(signUpIntent, 1);
    }

    public void signIn(View view) {
        userEmail = editTextEmail.getText().toString();
        userPassword = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            loginUser(userEmail, userPassword);
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (userEmail.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (userPassword.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(userPassword).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(getApplicationContext(), R.string.success_signup, Toast.LENGTH_SHORT).show();

                            // DB에 이름 넣기.

                        } else {
                            // 회원가입 실패
                            Toast.makeText(getApplicationContext(), R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 로그인
    private void loginUser(String email, String password) {
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(getApplicationContext(), R.string.success_login, Toast.LENGTH_SHORT).show();

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);

                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userEmail", userEmail);

                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패
                            Toast.makeText(getApplicationContext(), R.string.failed_login, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.hide();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.btn_signIn).setVisibility(View.GONE);
            findViewById(R.id.btn_signUp).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_signIn).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_signUp).setVisibility(View.VISIBLE);
        }
        hideProgressDialog();
    }
}
