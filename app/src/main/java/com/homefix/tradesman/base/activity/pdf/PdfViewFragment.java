package com.homefix.tradesman.base.activity.pdf;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.homefix.tradesman.R;
import com.homefix.tradesman.base.activity.BaseCloseActivity;
import com.homefix.tradesman.base.fragment.BaseCloseFragment;
import com.homefix.tradesman.base.presenter.BaseCloseActivityPresenter;
import com.homefix.tradesman.base.presenter.BaseFragmentPresenter;
import com.homefix.tradesman.base.presenter.DefaultFragementPresenter;
import com.homefix.tradesman.base.view.BaseCloseActivityView;
import com.homefix.tradesman.base.view.BaseFragmentView;
import com.homefix.tradesman.view.MaterialDialogWrapper;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import java.io.File;

import butterknife.BindView;

/**
 * Created by samuel on 9/6/2016.
 */

public class PdfViewFragment extends BaseCloseFragment<BaseCloseActivity, BaseFragmentView, BaseFragmentPresenter<BaseFragmentView>> {

    @BindView(R.id.pdfView)
    protected PDFView pdfView;

    private String uri;
    private File file;
    private String asset;

    public PdfViewFragment() {
        super(PdfViewFragment.class.getSimpleName());
    }

    public static PdfViewFragment getInstance(String uri, File file, String asset) {
        PdfViewFragment fragment = new PdfViewFragment();
        fragment.setUri(uri);
        fragment.setFile(file);
        fragment.setAsset(asset);
        return fragment;
    }

    @Override
    protected BaseFragmentPresenter<BaseFragmentView> getPresenter() {
        if (presenter == null) presenter = new DefaultFragementPresenter(this);

        return presenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.pdf_view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (pdfView == null) {
            MyLog.e(TAG, "pdfView is NULL");
            return;
        }

        if (!Strings.isEmpty(uri)) {
            MyLog.d(TAG, "Setup from URI");
            pdfView.fromUri(Uri.parse(uri));
            return;
        }

        if (file != null) {
            MyLog.d(TAG, "Setup from File");
            pdfView.fromFile(file);
            return;
        }

        if (!Strings.isEmpty(asset)) {
            MyLog.d(TAG, "Setup from Asset");
            pdfView.fromAsset(asset);
            return;
        }

        showConfirmDialog(
                "Sorry, we're unable to show this pdf right now. Please install a PDF Reader/Viewer from the app store",
                "OK",
                "",
                new ConfirmDialogCallback() {
                    @Override
                    public void onPositive() {
                        getBaseActivity().tryClose();
                    }
                }
        );
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }
}
