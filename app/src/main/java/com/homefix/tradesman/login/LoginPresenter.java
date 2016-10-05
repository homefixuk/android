package com.homefix.tradesman.login;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by samuel on 6/22/2016.
 */

public class LoginPresenter extends BaseActivityPresenter<LoginView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword("test@homefix.co.uk", "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                MyLog.e(LoginPresenter.class.getSimpleName(), "Setup account: " + task.isSuccessful());

                mFirebaseAuth.signInWithEmailAndPassword("test@homefix.co.uk", "password")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                MyLog.d(LoginPresenter.class.getSimpleName(), "signInWithCredential:onComplete:" + task.isSuccessful());

                                if (task.isSuccessful()) {
                                    DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
                                    HashMap<String, Object> children = new HashMap<>();
                                    children.put("name", "Test Plumber");
                                    children.put("createdAt", new Date());
                                    ref.setValue(children, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            MyLog.e(LoginPresenter.class.getSimpleName(), "Saved to DB: " + (databaseError != null));
                                        }
                                    });
                                }

                                mFirebaseAuth.signOut();
                            }
                        });
            }
        });
    }

    public void doEmailPasswordLogin(String email, String password) {
        if (!isViewAttached()) return;

        // remove all spaces before and after, and make sure it is lowercase
        email = Strings.returnSafely(email).trim().toLowerCase();

        // check email
        if (!Strings.isEmailValid(email)) {
            getView().showEmailError("Please enter a valid email");
            return;
        }

        // check password
        if (Strings.isEmpty(password)) {
            getView().showPasswordError("Please enter a valid password");
            return;
        }

        getView().showAttemptingLogin();

        // cache the email
        CacheUtils.writeObjectFile("email", email);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        MyLog.d(LoginPresenter.class.getSimpleName(), "signInWithCredential:onComplete:" + task.isSuccessful());

                        // if sign in fails, display a message to the user
                        if (!task.isSuccessful()) {
                            getView().hideAttemptingLogin();

                            if (!NetworkManager.hasConnection(getView().getContext())) {
                                getView().showDialog("Please make sure you are connected to the internet before logging in.", false);
                            } else {
                                getView().showEmailError("Please make sure your email is correct");
                                getView().showPasswordError("Please make sure your password is correct");
                            }

                        } else {
                            HomeFixApplication.setupAppAfterLogin(getView().getBaseActivity().getApplicationContext());

                            // we can take the user to the app
                            getView().goToApp();
                        }
                    }
                });
    }

    public void onContactUsClicked() {
        if (!isViewAttached()) return;

        getView().showNewUser();
    }

}
