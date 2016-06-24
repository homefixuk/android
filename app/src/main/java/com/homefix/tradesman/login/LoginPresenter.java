package com.homefix.tradesman.login;

import com.homefix.tradesman.api.HomeFix;
import com.homefix.tradesman.base.presenter.BaseActivityPresenter;
import com.homefix.tradesman.data.UserController;
import com.homefix.tradesman.model.Tradesman;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.samdroid.common.MyLog;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.string.Strings;

import java.util.HashMap;
import java.util.Map;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by samuel on 6/22/2016.
 */

public class LoginPresenter extends BaseActivityPresenter<LoginView> {

    private Tradesman mTradesman;

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

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        HomeFix.getAPI().login(params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Tradesman>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                        MyLog.e("LoginPresenter", "[login] onComplete");

                        if (mTradesman == null) {
                            getView().hideAttemptingLogin();
                            getView().showDialog("Sorry, something went wrong with the login. Please try again", false);
                            return;
                        }

                        // else cache the current user
                        CacheUtils.writeObjectFile("current_user", mTradesman);

                        // load the current user
                        UserController.loadCurrentUser(new OnGotObjectListener<Tradesman>() {
                            @Override
                            public void onGotThing(Tradesman tradesman) {
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
                    public final void onError(Throwable e) {
//                        MyLog.e("LoginPresenter", e.getMessage());

                        getView().hideAttemptingLogin();
                        getView().showEmailError("Please make sure your email is correct");
                        getView().showEmailError("Please make sure your password is correct");
                    }

                    @Override
                    public final void onNext(Tradesman tradesman) {
                        mTradesman = tradesman;
                    }
                });
    }

    public void onContactUsClicked() {

    }

}
