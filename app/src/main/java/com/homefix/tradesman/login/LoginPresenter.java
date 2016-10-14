package com.homefix.tradesman.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.common.AnalyticsHelper;
import com.homefix.tradesman.firebase.FirebaseUtils;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/22/2016.
 */

public class LoginPresenter extends BaseActivityPresenter<LoginView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseAuth.createUserWithEmailAndPassword("test@homefix.co.uk", "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                MyLog.e(LoginPresenter.class.getSimpleName(), "Setup account: " + task.isSuccessful());
//
//                mFirebaseAuth.signInWithEmailAndPassword("test@homefix.co.uk", "password")
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                MyLog.d(LoginPresenter.class.getSimpleName(), "signInWithCredential:onComplete:" + task.isSuccessful());
//
//                                if (task.isSuccessful()) {
//                                    DatabaseReference ref = FirebaseUtils.getCurrentTradesmanRef();
//                                    HashMap<String, Object> children = new HashMap<>();
//                                    children.put("name", "Test Plumber");
//                                    children.put("createdAt", new Date());
//                                    ref.setValue(children, new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                            MyLog.e(LoginPresenter.class.getSimpleName(), "Saved to DB: " + (databaseError != null));
//                                        }
//                                    });
//                                }
//
//                                mFirebaseAuth.signOut();
//                            }
//                        });
//            }
//        });
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

        getView().showAttemptingLogin(false);

        // cache the email
        CacheUtils.writeObjectFile("email", email);

        AnalyticsHelper.track(getView().getContext(), "loginClicked", new Bundle());

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        MyLog.d(LoginPresenter.class.getSimpleName(), "signInWithCredential:onComplete:" + task.isSuccessful());

                        // if sign in fails, display a message to the user
                        if (!task.isSuccessful()) {
                            AnalyticsHelper.track(getView().getContext(), "loginFailed", new Bundle());

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

    public void signUp(final String name, String email, String password, String confirmPassword) {
        if (!isViewAttached()) return;

        email = Strings.returnSafely(email).trim().toLowerCase();

        String errorMessage = null;
        // check name
        if (Strings.isEmpty(name)) errorMessage = "Please enter your name";
        else if (!Strings.isEmailValid(email)) errorMessage = "Please enter a valid email";
        else if (Strings.isEmpty(password)) errorMessage = "Please enter a password";
        else if (password.length() < 8) errorMessage = "Password length must be 8 or more";
        else if (password.contains(" ")) errorMessage = "Passwords cannot contain spaces";
        else if (!password.equals(confirmPassword)) errorMessage = "Passwords do not match";

        if (!Strings.isEmpty(errorMessage)) {
            Toast.makeText(getView().getContext(), errorMessage, Toast.LENGTH_LONG).show();
            getView().showNewUser();
            return;
        }

        getView().showAttemptingLogin(true);

        // create the user on firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String finalEmail = email;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // clear the cached signup details
                        CacheUtils.writeFile("Name", "");
                        CacheUtils.writeFile("Email", "");
                        CacheUtils.writeFile("Password", "");
                        CacheUtils.writeFile("Confirm Password", "");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        // setup the main user values
                        DatabaseReference userRef = FirebaseUtils.getBaseRef().child("tradesman").child(user.getUid());
                        userRef.child("name").setValue(name);

                        String[] names = name.split(" ");
                        if (names.length > 0) userRef.child("firstName").setValue(names[0]);
                        if (names.length > 1) {
                            // set the last name to be the remaining names
                            String lastName = name.replace(names[0] + " ", "");
                            userRef.child("lastName").setValue(lastName);
                        }

                        userRef.child("email").setValue(finalEmail);

                        // initial values
                        userRef.child("type").setValue("normal");
                        userRef.child("createdAt").setValue(System.currentTimeMillis());

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        HomeFixApplication.setupAppAfterLogin(getView().getBaseActivity().getApplicationContext());

                                        getView().goToApp();
                                    }
                                });
                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getView().hideAttemptingLogin();

                        // Catch any error with logging in

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            FirebaseAuthUserCollisionException e1 = (FirebaseAuthUserCollisionException) e;
                            if (e1.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                Toast.makeText(getView().getContext(), "Email already taken", Toast.LENGTH_LONG).show();
                                getView().showNewUser();
                                return;
                            }

                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            FirebaseAuthWeakPasswordException e1 = (FirebaseAuthWeakPasswordException) e;
                            Toast.makeText(getView().getContext(), e1.getReason(), Toast.LENGTH_LONG).show();
                            getView().showNewUser();
                            return;
                        }

                        if (!NetworkManager.hasConnection(getView().getContext())) {
                            getView().showDialog("Please make sure you have an internet connection and try again.", false);

                        } else {
                            getView().showDialog("Sorry, something went wrong. Please try again.", false);
                        }
                    }

                });
    }
}
