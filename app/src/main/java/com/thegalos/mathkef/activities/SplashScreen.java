package com.thegalos.mathkef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.thegalos.mathkef.R;

import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SplashScreen extends AppCompatActivity {
    EditText etEmail, etPassword, etName;
    TextInputLayout tilName;
    Button btnAction;
    MotionLayout motionLayout;
    FirebaseAuth mAuth;
    boolean readyToRegister = false;
    String email, password;

    EditText etPhone;
    EditText etEnterOTP;
    TextView tvTimeLeft, tvResendOtp;
    Button btnSignIn;
    Button btnResendOTP;
    CountryCodePicker ccp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    String phoneToConnect;
    boolean signIn = true;
    private boolean getName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);

        motionLayout = findViewById(R.id.motionLayout);
        btnAction = findViewById(R.id.btnAction);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tilName = findViewById(R.id.tilName);
        etName = findViewById(R.id.etName);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tilName.setVisibility(View.INVISIBLE);
        etName.setVisibility(View.INVISIBLE);
        etPhone = findViewById(R.id.etPhone);
        etEnterOTP = findViewById(R.id.etEnterOTP);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        tvTimeLeft = findViewById(R.id.tvTimeLeft);
        ccp = findViewById(R.id.ccp);

        ccp.registerCarrierNumberEditText(etPhone);
        mAuth = FirebaseAuth.getInstance();
        userIsLoggedIn();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (etName.getText().toString().length() == 0) {
                    btnAction.setEnabled(false);
                    btnSignIn.setEnabled(false);

                } else {
                    btnAction.setEnabled(true);
                    btnSignIn.setEnabled(true);
                }
            }
        });

        etEnterOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (etEnterOTP.getText().toString().length() == 0) {
                    btnAction.setEnabled(false);
                    btnSignIn.setEnabled(false);

                } else {
                    btnAction.setEnabled(true);
                    btnSignIn.setEnabled(true);
                }

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signIn) {
                    if (!etPhone.getText().toString().equals("")) {
                        phoneToConnect = ccp.getFullNumberWithPlus();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneToConnect,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                SplashScreen.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }

                } else {
                    if (getName) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Name").setValue(etEnterOTP.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("phone").setValue(user.getPhoneNumber());

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etEnterOTP.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("firebase", "User profile updated.");
                                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                        }

                    } else {
                        if (!etEnterOTP.getText().toString().equals("")) {
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etEnterOTP.getText().toString());
                            signInWithPhoneAuthCredential(credential);
                        }
                    }
                }
            }
        });

        btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnResendOTP.setVisibility(GONE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneToConnect,        // Phone number to verify
                        1,               // Timeout duration
                        TimeUnit.MINUTES,   // Unit of timeout
                        SplashScreen.this,               // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        mResendToken);             // Force Resending Token from callbacks

                motionLayout.transitionToStart();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("auth", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException)
                    Log.d("auth", "Invalid request");
                else if (e instanceof FirebaseTooManyRequestsException)
                    Log.d("auth", "The SMS quota for the project has been exceeded");
                Toast.makeText(SplashScreen.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(SplashScreen.this, "Code Sent", Toast.LENGTH_SHORT).show();
                btnSignIn.setVisibility(GONE);
                etEnterOTP.setVisibility(View.VISIBLE);
                etPhone.setEnabled(false);
                mVerificationId = verificationId;
                mResendToken = token;
                signIn = false;
                btnSignIn.setText(getString(R.string.verify_otp));
                motionLayout.setTransition(R.id.sms, R.id.otp);
                motionLayout.transitionToEnd();
                countDownStart();
            }
        };

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayout.setInteractionEnabled(false);

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    Toast.makeText(SplashScreen.this, R.string.email_not_valid, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length() < 6) {
                    Toast.makeText(SplashScreen.this, R.string.min_pass_length, Toast.LENGTH_SHORT).show();
                    return;
                }
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (!readyToRegister) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SplashScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("auth", "signInWithCredential:success");
                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                finish();

                            } else {
                                Log.d("auth", "signInWithCredential:failure", task.getException());

                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(SplashScreen.this, "No such user, please register", Toast.LENGTH_SHORT).show();
                                    tilName.setVisibility(View.VISIBLE);
                                    etName.setVisibility(View.VISIBLE);
                                    tilName.setHint(getString(R.string.full_name));
                                    btnAction.setEnabled(false);
                                    btnAction.setText(getString(R.string.register));
                                    readyToRegister = true;

                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(SplashScreen.this, "Wrong Code Entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SplashScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("auth", "user created successfully");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Name").setValue(etName.getText().toString());
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Email").setValue(email);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(etName.getText().toString())
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("firebase", "User profile updated.");
                                                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Called automatically on app start
     * if user already logged change screen to Main app page
     */
    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            motionLayout.setInteractionEnabled(false);
            Thread splash_screen = new Thread() {
                public void run() {
                    try {
                        sleep(2500);
                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                }
            };
            splash_screen.start();
        } else {
            motionLayout.setTransition(R.id.start, R.id.end);
            motionLayout.transitionToEnd();
        }
    }

    /**
     * @param credential PhoneAuthCredential object Wraps phone number and verification information for authentication purposes.
     * Firebase Auth. function checking credential and log in user on success
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("auth", "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                                if (user.getDisplayName() == null) {
                                    btnSignIn.setText(getString(R.string.finish));
                                    btnSignIn.setEnabled(false);

                                    getName = true;
                                    motionLayout.setTransition(R.id.sms, R.id.otp);
                                    motionLayout.transitionToEnd();
                                    etEnterOTP.getText().clear();
                                    etEnterOTP.setHint(R.string.full_name);
                                    etEnterOTP.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
                                    etEnterOTP.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                    signIn = false;

                                } else {
                                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                    finish();
                                }
                            }

                        } else {
                            Log.d("auth", "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SplashScreen.this, "Wrong Code Entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    /**
     * After OTP was sent to user phone when timer finish user would be able to request new OTP if needed
     */
    private void countDownStart() {
        tvTimeLeft.setVisibility(VISIBLE);
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
//                tvResendOtp.setText();
                String str = getString(R.string.request_new_otp) + "\n" + getString(R.string.in) + " " + (millisUntilFinished / 1000) + " " + getString(R.string.seconds);
                tvTimeLeft.setText(str);
            }

            public void onFinish() {
                motionLayout.setTransition(R.id.otp, R.id.newOtp);
                motionLayout.transitionToEnd();
                tvTimeLeft.setText("");
                tvResendOtp.setText("");
            }
        }.start();
    }
}
