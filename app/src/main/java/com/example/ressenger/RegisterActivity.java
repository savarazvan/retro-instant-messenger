package com.example.ressenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText usernameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    Button signUpButton;
    TextView errorText;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.UsernameBox);
        emailEditText = findViewById(R.id.EmailBox);
        passwordEditText = findViewById(R.id.PasswordBox);
        passwordConfirmEditText = findViewById(R.id.PasswordConfirmBox);
        signUpButton = findViewById(R.id.SignUpButtton);
        errorText = findViewById(R.id.errorText2);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String passwordConfirm = passwordConfirmEditText.getText().toString().trim();
                signUp(username, email, password, passwordConfirm);
            }
        });

        usernameEditText.addTextChangedListener(signUpTextWatcher);
        emailEditText.addTextChangedListener(signUpTextWatcher);
        passwordEditText.addTextChangedListener(signUpTextWatcher);
        passwordConfirmEditText.addTextChangedListener(signUpTextWatcher);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
    }

    private void signUp(final String username, final String email, String password, String passwordConfirm)
    {
        if(!password.equals(passwordConfirm)) {
            errorText.setText("Passwords don't match!");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            errorText.setText("Register failed!");
                            errorText.setVisibility(View.VISIBLE);
                        }

                        else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates);
                            UserDetails userDetails = new UserDetails(username, email, user.getUid());
                            myRef.child(user.getUid()).setValue(userDetails);
                            goToMainPage();
                        }
                    }
                });
    }

    private void goToMainPage()
    {
        Intent mainPage = new Intent(this, MainActivity.class);
        startActivity(mainPage);
        finish();
    }


    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = passwordConfirmEditText.getText().toString();

            signUpButton.setEnabled(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() &&!confirmPassword.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };


}
