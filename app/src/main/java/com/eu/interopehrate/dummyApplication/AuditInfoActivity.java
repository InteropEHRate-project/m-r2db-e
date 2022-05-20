package com.eu.interopehrate.dummyApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.eu.interopehrate.mr2dbackup.Account;
import com.eu.interopehrate.mr2dbackup.MR2DBackupFactory;
import com.eu.interopehrate.mr2dbackup.SEHRCloudClient;
import com.eu.interopehrate.mr2dbackup.SEHRCloudInterface;
import com.eu.interopehrate.mr2dbackup.SEHRCloudProviders.SEHRCloudProviders;

public class AuditInfoActivity extends AppCompatActivity {
    private Button btnAuditInfo, btnAccountMain;
    private TextView mTextViewResult;
    private static final String TAG = "AuditInfoActivity";

    /* NEED TO BE IMPORTED IN EVERY ACTIVITY */
    private Account citizen = Account.Account();
    MR2DBackupFactory mr2dbackup;
//    com.eu.interopehrate.mr2dbackup.SEHRCloudInterface SEHRCloudInterface;
    /* END */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_info);

        /* NEED TO BE IMPORTED IN EVERY ACTIVITY */
        mr2dbackup = new MR2DBackupFactory(citizen.getCloudUrl());
//        SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);
        /* END */

        btnAuditInfo = findViewById(R.id.auditInfo);
        btnAccountMain = findViewById(R.id.goToAccount);
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnAccountMain.setOnClickListener((v -> startActivity(new Intent(AuditInfoActivity.this,
                LoggedInAccountActivity.class))));

        btnAuditInfo.setOnClickListener(v -> {
            auditInfo();
        });
    }

    public void auditInfo(){
//        mr2dbackup.getAuditInfo(citizen.getToken(), "XVggCm3qd22ceCDlTe4LEPFmAm6driB+pSilZqs+u0k=", new MR2DBackupFactory.MR2DBackupInterface() {
//        mr2dbackup.getAuditInfo(citizen.getToken(), "UiYmJk/iWopGS2n0YIhZsgp1auRVyahR7sgFOtEC5r4=", new MR2DBackupFactory.MR2DBackupInterface() {
        mr2dbackup.getAuditInfo(citizen.getToken(), citizen.getSymmetricKey(), new MR2DBackupFactory.MR2DBackupInterface() {
            @Override
            public void onResponse(Account account) throws Exception {
                citizen.setAuditInfo(account.getAuditInfo());
                mTextViewResult.setText(citizen.getAuditInfo());
            }

            @Override
            public void onFailure(Throwable t) {
                if (t != null) {
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                }
            }
        });
    }
}