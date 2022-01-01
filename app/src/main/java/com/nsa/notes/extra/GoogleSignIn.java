package com.nsa.notes.extra;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nsa.notes.R;
import com.nsa.notes.db.Constants;
import com.nsa.notes.models.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GoogleSignIn {

    private SignInListener listener;

    public void setListener(SignInListener listener) {
        this.listener = listener;
    }

    private Activity activity;


    private String TAG="signIN";
    private FirebaseAuth mfirebaseAuth;
    public static FirebaseUser firebaseUser;
    private GoogleSignInAccount account;
    public final static int  RC_SIGN_IN=1234;

    public GoogleSignIn(Activity activity) {
        this.activity = activity;
        mfirebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=mfirebaseAuth.getCurrentUser();
        account= com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount(activity);



    }
    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            listener.info(Constants.SHOW_PROGRESS);

            account = task.getResult(ApiException.class);
           showToast("Google SignIn SuccessFull");
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getMessage());
            e.printStackTrace();
            listener.info(Constants.ERROR);
            showToast("Google sign in failed"+e.toString()+e.getLocalizedMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mfirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    firebaseUser = task.getResult().getUser();
                    checkUserData(account,firebaseUser);


                    showToast("Firebase Success");


                }else{
                    listener.info(Constants.ERROR);
                    showToast(""+task.getException());
                }
            }
        });
    }

    private void checkUserData(GoogleSignInAccount account, FirebaseUser firebaseUser) {
        new FireBase().userReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel model=snapshot.getValue(UserModel.class);
                    listener.data(model);
                    listener.info(Constants.SUCCESS);
                }else{
                    saveUserData(account,firebaseUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUserData(GoogleSignInAccount account, FirebaseUser fUser) {
        String date=new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
        new SavedText(activity).setText("joinDate",date);
        UserModel model=new UserModel(fUser.getUid(),account.getDisplayName(),account.getEmail(),date,false);
        new FireBase().userReference.child(fUser.getUid()).setValue(model);
        listener.info(Constants.SUCCESS);
    }

    private void showToast(String firebase_success) {
        Toast.makeText(activity, ""+firebase_success, Toast.LENGTH_SHORT).show();
    }

}
