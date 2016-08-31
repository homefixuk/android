package com.homefix.tradesman.login;

import android.os.Bundle;
import android.text.InputType;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.rey.material.widget.EditText;
import com.samdroid.input.AsteriskPasswordTransformationMethod;
import com.samdroid.resource.ColourUtils;
import com.samdroid.string.Strings;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samuel on 6/24/2016.
 */

public class LoginActivity extends HomeFixBaseActivity<LoginView, LoginPresenter> implements LoginView {

    @BindView(R.id.email_edt)
    EditText mEmailEdt;

    @BindView(R.id.password_edt)
    EditText mPasswordEdt;

    public LoginActivity() {
        super(LoginActivity.class.getSimpleName());
    }

    @Override
    public LoginPresenter getPresenter() {
        if (presenter == null) presenter = new LoginPresenter();

        return presenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginView getThisView() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup the edit text views
        mEmailEdt.setTextColor(ColourUtils.getColour(getContext(), R.color.colorAccent));
        mEmailEdt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mEmailEdt.setHint("Email");
        mEmailEdt.setHintTextColor(ColourUtils.getColour(getContext(), R.color.greyLight));

        mPasswordEdt.setTextColor(ColourUtils.getColour(getContext(), R.color.colorAccent));
        mPasswordEdt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordEdt.setHint("Password");
        mPasswordEdt.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mPasswordEdt.setHintTextColor(ColourUtils.getColour(getContext(), R.color.greyLight));
    }

    @OnClick(R.id.contact_us)
    public void onContactUsClicked() {
        getPresenter().onContactUsClicked();
    }

    @OnClick(R.id.login_btn)
    public void onLoginClicked() {
        if (mEmailEdt == null || mPasswordEdt == null) return;

        if (getPresenter() != null)
            getPresenter().doEmailPasswordLogin(
                    mEmailEdt.getText().toString(),
                    mPasswordEdt.getText().toString());
    }

    @Override
    public void showEmailError(String message) {
        if (mEmailEdt != null) mEmailEdt.setError(Strings.returnSafely(message));
    }

    @Override
    public void showPasswordError(String message) {
        if (mPasswordEdt != null) mPasswordEdt.setError(Strings.returnSafely(message));
    }

    @Override
    public void showAttemptingLogin() {
        if (mEmailEdt != null) mEmailEdt.setError("");
        if (mPasswordEdt != null) mPasswordEdt.setError("");
        showDialog("Logging in...", true);
    }

    @Override
    public void hideAttemptingLogin() {
        hideDialog();
    }

}
