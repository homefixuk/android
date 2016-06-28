package com.homefix.tradesman.login;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by samuel on 6/22/2016.
 */

public class LoginPresenter extends BaseActivityPresenter<LoginView> {

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

        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        final Call<Tradesman> call = HomeFix.getAPI().login(params);
        call.enqueue(new Callback<Tradesman>() {
            @Override
            public void onResponse(Call<Tradesman> call, Response<Tradesman> response) {
                MyLog.e("LoginPresenter", "[login] onComplete");

                Tradesman tradesman = response != null ? response.body() : null;

                if (tradesman == null) {
                    MyLog.e("LoginPresenter", "HERE");

                    MyLog.e("LoginPresenter", response.toString());
                    MyLog.e("LoginPresenter", call.request().toString());

                    getView().hideAttemptingLogin();
                    getView().showDialog("Sorry, something went wrong with the login. Please try again", false);
                    return;
                }

                // else cache the current user
               CacheUtils.writeObjectFile("current_user", tradesman);

                // load the current user
                UserController.loadCurrentUser(getView().getContext(), new OnGotObjectListener<Tradesman>() {
                    @Override
                    public void onGotThing(Tradesman tradesman) {
                        MyLog.e("LoginPresenter", "[onGotThing]");

                        if (tradesman == null) {
                            getView().hideAttemptingLogin();
                            getView().showDialog("Sorry, something went wrong with the login. Please try again", false);
                            return;
                        }

                        // else we can take the user to the app //
                        getView().goToApp();
                    }
                });
            }

            @Override
            public void onFailure(Call<Tradesman> call, Throwable t) {
                if (MyLog.isIsLogEnabled()) t.printStackTrace();

                getView().hideAttemptingLogin();

                if (!NetworkManager.hasConnection(getView().getContext())) {
                    getView().showDialog("Please make sure you are connected to the internet before logging in.", false);
                } else {
                    getView().showEmailError("Please make sure your email is correct");
                    getView().showPasswordError("Please make sure your password is correct");
                }
            }

        });
    }

    public void onContactUsClicked() {

    }

}
