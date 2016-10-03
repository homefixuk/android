package com.homefix.tradesman.login;

import com.homefix.tradesman.base.view.BaseActivityView;

/**
 * Created by samuel on 6/22/2016.
 */

public interface LoginView extends BaseActivityView {

    void showEmailError(String message);

    void showPasswordError(String message);

    void showAttemptingLogin();

    void hideAttemptingLogin();

    void showNewUser();
}
