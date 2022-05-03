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

    private Account citizen = Account.Account();
    MR2DBackupFactory mr2dbackup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_info);

        mr2dbackup = new MR2DBackupFactory(citizen.getCloudUrl());

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