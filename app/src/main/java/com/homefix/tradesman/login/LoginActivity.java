package com.homefix.tradesman.login;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.rey.material.widget.EditText;
import com.samdroid.input.AsteriskPasswordTransformationMethod;
import com.samdroid.resource.ColourUtils;
import com.samdroid.string.Strings;

/**
 * Created by samuel on 6/24/2016.
 */

public class LoginActivity extends HomeFixBaseActivity<LoginView, LoginPresenter> implements LoginView {

    View mContactUs;
    TextView mContactUsTxt;
    EditText mEmailEdt, mPasswordEdt;
    Button mLoginBtn;

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
    public void injectDependencies() {
        super.injectDependencies();

        mEmailEdt = (EditText) findViewById(R.id.email_edt);
        mPasswordEdt = (EditText) findViewById(R.id.password_edt);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mContactUs = findViewById(R.id.contact_us);
        mContactUsTxt = (TextView) findViewById(R.id.contact_us_txt);
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

        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEmailEdt == null || mPasswordEdt == null) return;

                if (getPresenter() != null)
                    getPresenter().doEmailPasswordLogin(
                            mEmailEdt.getText().toString(),
                            mPasswordEdt.getText().toString());
            }

        });

        mContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().onContactUsClicked();
            }
        });
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
