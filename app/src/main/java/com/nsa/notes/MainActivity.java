package com.nsa.notes;

import static com.nsa.notes.extra.GoogleSignIn.RC_SIGN_IN;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.nsa.notes.fragments.OnBackPressedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public static OnBackPressedListener listener;
    public static boolean isInAdd=false;
    public static boolean isInSelect=false;

    @Override
    public void onBackPressed() {
        if(!isInAdd && !isInSelect){
        super.onBackPressed();
        return;
        }
        if(listener!=null){
        listener.onBackPressed();
        }
    }
}