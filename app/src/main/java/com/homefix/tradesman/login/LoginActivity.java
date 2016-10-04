package com.homefix.tradesman.login;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.HomeFixBaseActivity;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;
import com.rey.material.widget.EditText;
import com.samdroid.common.IntentHelper;
import com.samdroid.input.AsteriskPasswordTransformationMethod;
import com.samdroid.listener.interfaces.OnGotObjectListener;
import com.samdroid.resource.ColourUtils;
import com.samdroid.string.Strings;

import java.util.Arrays;
import java.util.HashMap;

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

        try {
            // try and set the last email used
            String lastEmail = CacheUtils.readObjectFile("email", String.class);
            mEmailEdt.setText(Strings.returnSafely(lastEmail).trim().toLowerCase());
        } catch (Exception e) {
        }

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

    @Override
    public void showNewUser() {
//        IntentHelper.openEmailWithAttachment(
//                this,
//                "contact@homefix.co.uk",
//                "New Tradesman for the app",
//                "Hi Homefix, I would like an account in the Tradesman app.\n\n" +
//                        "Full Name: <insert_name>\n" +
//                        "Phone Number: <insert_phone_number>\n\n" +
//                        "Additional notes: <insert_any_additional_notes>\n\n" +
//                        "Kind Regards",
//                null);

        MaterialDialogWrapper.getMultiInputDialogWithLabels(
                this,
                "Enter your information",
                "OK",
                Arrays.asList("Name", "Contact Number", "Additional Notes"),
                Arrays.asList("Name", "Contact Number", "Additional Notes"),
                Arrays.asList("", "", ""),
                new OnGotObjectListener<HashMap<String, String>>() {
                    @Override
                    public void onGotThing(HashMap<String, String> o) {
                        if (o == null || o.isEmpty()) {
                            Toast.makeText(getContext(), "Signup request not sent", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = o.get("Name");
                        String body = "Hi Homefix, I would like an account in the Tradesman app.\n\n" +
                                "Name: " + name + " \n" +
                                "Phone Number: " + o.get("Contact Number") + "\n\n";

                        String notes = o.get("Additional Notes");
                        if (!Strings.isEmpty(notes)) body += "Additional notes: " + notes + "\n\n";

                        body += "Kind Regards";

                        IntentHelper.sendEmail(
                                getBaseActivity(),
                                "contact@homefix.co.uk",
                                name + ": new Tradesman for the Android app",
                                body,
                                null);
                    }
                }
        ).show();

    }

}
