package com.homefix.tradesman.login;

import android.os.Bundle;

import com.homefix.tradesman.HomeFixApplication;
import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.common.VariableUtils;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 6/22/2016.
 */

public class LoginPresenter extends BaseActivityPresenter<LoginView> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Callback<HashMap<String, Object>> callback = new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                MyLog.e("LoginPresenter", "[onResponse]");
                VariableUtils.printMap(response.body());

                MyLog.e("LoginPresenter CALL", call.request().toString());
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                MyLog.e("LoginPresenter", "[onFailure]");
                if (t != null) t.printStackTrace();
            }
        };

//        HomeFix.getAPI().signup(
//                getView().getContext().getString(HomeFix.API_KEY_resId),
//                "Test",
//                "Plumber",
//                "testplumber@homefix.co.uk",
//                "password",
//                "TRADE").enqueue(callback);
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

        Callback<HashMap<String, Object>> callback = new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (!isViewAttached()) return;

                HashMap<String, Object> results = response != null ? response.body() : null;

                VariableUtils.printMap(results);

                if (results == null
                        || !results.containsKey("token")
                        || results.get("token") == null
                        || Strings.isEmpty((String) results.get("token"))) {
                    getView().hideAttemptingLogin();
                    getView().showDialog("Sorry, something went wrong with the login. Please try again", false);
                    return;
                }

                // cache the token
                CacheUtils.writeFile("token", (String) results.get("token"));

                // load the current user
                UserController.loadCurrentUser(true, new OnGotObjectListener<Tradesman>() {
                    @Override
                    public void onGotThing(Tradesman tradesman) {
                        if (tradesman == null) {
                            getView().hideAttemptingLogin();
                            getView().showDialog("Sorry, something went wrong with the login. Please try again", false);
                            return;
                        }

                        HomeFixApplication.setupAppAfterLogin(getView().getBaseActivity().getApplicationContext());

                        // we can take the user to the app
                        getView().goToApp();
                    }
                });
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();

                if (!isViewAttached()) return;

                getView().hideAttemptingLogin();

                if (!NetworkManager.hasConnection(getView().getContext())) {
                    getView().showDialog("Please make sure you are connected to the internet before logging in.", false);
                } else {
                    getView().showEmailError("Please make sure your email is correct");
                    getView().showPasswordError("Please make sure your password is correct");
                }
            }

        };

        HomeFix.getAPI().login(
                getView().getContext().getString(HomeFix.API_KEY_resId),
                email,
                password).enqueue(callback);
    }

    public void onContactUsClicked() {

    }

}
