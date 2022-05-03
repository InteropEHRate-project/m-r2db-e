package com.eu.interopehrate.dummyApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.eu.interopehrate.mr2dbackup.Account;
import com.eu.interopehrate.mr2dbackup.MR2DBackupFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class ConsentActivity extends AppCompatActivity {

    private Button btnDownloadConsentStore, btnDownloadConsentShare, btnUploadConsentStore, btnUploadConsentShare, btnWithdrawShare, btnAccountMain;
    private TextView mTextViewResult;
    private static final String TAG = "ConsentActivity";

    private Account citizen = Account.Account();

    MR2DBackupFactory controller;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        keystore 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        System.out.println(citizen.getCloudUrl());
        controller = new MR2DBackupFactory(citizen.getCloudUrl());
//        SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);
        btnDownloadConsentStore = findViewById(R.id.downloadConsentStore);
        btnDownloadConsentShare = findViewById(R.id.downloadConsentShare);
        btnUploadConsentStore = findViewById(R.id.uploadConsentStore);
        btnUploadConsentShare = findViewById(R.id.uploadConsentShare);
        btnWithdrawShare = findViewById(R.id.withdrawShare);
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnAccountMain = findViewById(R.id.goToAccount);

        btnAccountMain.setOnClickListener(v -> startActivity(new Intent(ConsentActivity.this,
                LoggedInAccountActivity.class)));


        btnDownloadConsentStore.setOnClickListener(v -> {
            try {
                downloadConsentStore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadConsentShare.setOnClickListener(v -> {
            try {
                downloadConsentShare(citizen.getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadConsentStore.setOnClickListener(v -> {
            try {
                uploadConsentStore(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadConsentShare.setOnClickListener(v -> {
            try {
                uploadConsentShare(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnWithdrawShare.setOnClickListener(v -> {
            try{
                withdrawShare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void downloadConsentStore() {
        btnDownloadConsentStore.setEnabled(false);
        btnDownloadConsentShare.setEnabled(false);
        btnUploadConsentStore.setEnabled(false);
        btnUploadConsentShare.setEnabled(false);

        controller.downloadConsentStore(citizen.getToken(), new MR2DBackupFactory.MR2DBackupInterface() {
            @Override
            public void onResponse(Account account) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTextViewResult.setText(account.getConsentStore());
                    citizen.setConsentStore(account.getConsentStore());
                    btnDownloadConsentStore.setEnabled(true);
                    btnDownloadConsentShare.setEnabled(true);
                    btnUploadConsentStore.setEnabled(true);
                    btnUploadConsentShare.setEnabled(true);
                }, 1000);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Throwable t) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTextViewResult.setText("Something went wrong!\n");
                    btnDownloadConsentStore.setEnabled(true);
                    btnDownloadConsentShare.setEnabled(true);
                    btnUploadConsentStore.setEnabled(true);
                    btnUploadConsentShare.setEnabled(true);
                }, 1000);

            }
        });
    }

    public void downloadConsentShare(String token) {
        btnDownloadConsentStore.setEnabled(false);
        btnDownloadConsentShare.setEnabled(false);
        btnUploadConsentStore.setEnabled(false);
        btnUploadConsentShare.setEnabled(false);

        controller.downloadConsentShare(citizen.getToken(), new MR2DBackupFactory.MR2DBackupInterface() {
            @Override
            public void onResponse(Account account) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTextViewResult.setText(account.getConsentShare());
                    citizen.setConsentShare(account.getConsentShare());
                    btnDownloadConsentStore.setEnabled(true);
                    btnDownloadConsentShare.setEnabled(true);
                    btnUploadConsentStore.setEnabled(true);
                    btnUploadConsentShare.setEnabled(true);
                }, 1000);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Throwable t) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTextViewResult.setText("Something went wrong!\n");
                    btnDownloadConsentStore.setEnabled(true);
                    btnDownloadConsentShare.setEnabled(true);
                    btnUploadConsentStore.setEnabled(true);
                    btnUploadConsentShare.setEnabled(true);
                }, 1000);

            }
        });

    }

    public void uploadConsentStore(Context context) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException, JSONException {
        controller.signAndUploadConsentStore(citizen.getToken(), citizen.getConsentStore(), context,
                new MR2DBackupFactory.MR2DBackupInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Account account) throws Exception {
                mTextViewResult.setText(account.getMsg());
            }

            @Override
            public void onFailure(Throwable t) {
                if (t != null) {
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                }
            }
        });
    }

    public void uploadConsentShare(Context context) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchProviderException, JSONException {

        if(citizen.getCitizenId() == null ||citizen.getCitizenId().equals("")) {
            controller.signAndUploadConsentShare(citizen.getToken(), citizen.getUsername(), citizen.getSymmetricKey(), citizen.getConsentShare(), context,
                    new MR2DBackupFactory.MR2DBackupInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Account account) throws Exception {
                            citizen.setEmergencyToken(account.getEmergencyToken());
                            citizen.setCitizenId(account.getCitizenId());
                            citizen.setHriEmergencyToken(account.getHriEmergencyToken());
                            citizen.setHriToken(account.getHriToken());
                            mTextViewResult.setText(citizen.getCitizenId() + "\n" + citizen.getEmergencyToken());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (t != null) {
                                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                            }
                        }
                    });
        } else {
            controller.signAndUploadConsentShare(citizen.getToken(), citizen.getHriToken(), citizen.getUsername(), citizen.getCitizenId(), citizen.getSymmetricKey(), citizen.getConsentShare(), context,
                    new MR2DBackupFactory.MR2DBackupInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Account account) throws Exception {
                            citizen.setEmergencyToken(account.getEmergencyToken());
                            citizen.setCitizenId(account.getCitizenId());
                            citizen.setHriEmergencyToken(account.getHriEmergencyToken());
                            citizen.setHriToken(account.getHriToken());
                            mTextViewResult.setText(citizen.getCitizenId() + "\n" + citizen.getEmergencyToken() + "\n" + citizen.getHriToken() + "\n" + citizen.getHriEmergencyToken());
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

    public void withdrawShare() {
        controller.withdrawConsentShare(citizen.getToken(), citizen.getHriToken(), citizen.getCitizenId(), new MR2DBackupFactory.MR2DBackupInterface() {
            @Override
            public void onResponse(Account account) throws Exception {
                citizen.setMsg(account.getMsg());
                citizen.setStatus(account.getStatus());
                citizen.setEmergencyToken(account.getEmergencyToken());
                mTextViewResult.setText(citizen.getMsg());

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void keystoreStuff(){
        try {
            InputStream inAl = getAssets().open("alias");
            OutputStream outAl = null;

            try {
                outAl = new FileOutputStream("/data/user/0/com.eu.interopehrate.mr2dbackup/files/alias");
                byte[] bufAl = new byte[1024];
                int len;
                while((len=inAl.read(bufAl))>0){
                    outAl.write(bufAl,0,len);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // Ensure that the InputStreams are closed even if there's an exception.
                try {
                    if ( outAl != null ) {
                        outAl.close();
                    }

                    // If you want to close the "in" InputStream yourself then remove this
                    // from here but ensure that you close it yourself eventually.
                    inAl.close();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        try {
            InputStream inKS = getAssets().open("keystore.p12");
            OutputStream outKS = null;

            try {
                outKS = new FileOutputStream("/data/user/0/com.eu.interopehrate.mr2dbackup/files/keystore.p12");
                byte[] bufKS = new byte[1024];
                int len;
                while((len=inKS.read(bufKS))>0){
                    outKS.write(bufKS,0,len);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // Ensure that the InputStreams are closed even if there's an exception.
                try {
                    if ( outKS != null ) {
                        outKS.close();
                    }

                    // If you want to close the "in" InputStream yourself then remove this
                    // from here but ensure that you close it yourself eventually.
                    inKS.close();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

    }
}