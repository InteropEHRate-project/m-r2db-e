package com.eu.interopehrate.dummyApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eu.interopehrate.mr2dbackup.*;
import com.eu.interopehrate.mr2dbackup.SEHRCloudProviders.SEHRCloudProviders;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private EditText username, password;
    private TextView mTextViewResult;
    private Account citizen = Account.Account();
    private static final String TAG = "MainActivity";
    private String responseToken="", responseU, responseP, responseMsg;
    private Spinner dropdown;
    private String cloud;

    MR2DBackupFactory controller;

//    SEHRCloudInterface SEHRCloudInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        keystoreStuff();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = findViewById(R.id.text_view_result);

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{SEHRCloudProviders.SEHR_CLOUD_1.toString(), SEHRCloudProviders.SEHR_CLOUD_2.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setPrompt("Select S-EHR Cloud provider");
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cloud = dropdown.getSelectedItem().toString();
                mTextViewResult.setText(SEHRCloudProviders.valueOf(cloud).getCloudProviderUrl());
                controller = new MR2DBackupFactory(SEHRCloudProviders.valueOf(cloud).getCloudProviderUrl());
                citizen.setCloudUrl(SEHRCloudProviders.valueOf(cloud).getCloudProviderUrl());
                System.out.println("CONTROLLER CLOUD URL: \t"+citizen.getCloudUrl());
            }

            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println(citizen.getCloudUrl());
            }
        });


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        btnLogin = findViewById(R.id.login);
        btnRegister = findViewById(R.id.goToRegister);


        btnLogin.setOnClickListener(v -> {
            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
            } else {
                citizen.setUsername(username.getText().toString());
                citizen.setPassword(password.getText().toString());
                citizen.setSymmetricKey("XVggCm3qd22ceCDlTe4LEPFmAm6driB+pSilZqs+u0k=");
//                SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);
                login();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (!(citizen.getToken().equals(""))) {
                        startActivity(new Intent(MainActivity.this, LoggedInAccountActivity.class));
                    }  else {
                        mTextViewResult.setText(citizen.getMsg());
                    }
                }, 1000);

            }
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

//    Method for logging into the S-EHR Cloud and retrieve the token.
//    If the username is either missing or wrong, a message is returned.
    public void login(){
        if(citizen.getCitizenId() == null ||citizen.getCitizenId().equals("")) {
            System.out.println("HERE");
            controller.login(citizen.getUsername(), citizen.getPassword(), citizen.getSymmetricKey(),
                    new MR2DBackupFactory.MR2DBackupInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Account account) throws Exception {
                            citizen.setToken(account.getToken());
                            citizen.setSymmetricKey("XVggCm3qd22ceCDlTe4LEPFmAm6driB+pSilZqs+u0k=");
                            citizen.setConsentShareAccepted(account.isConsentShareAccepted());
                            citizen.setConsentStoreAccepted(account.isConsentStoreAccepted());
                            mTextViewResult.setText("MinIO response!: " + account.getMsg() +
                                    "\nToken: " + account.getToken() +
                                    "\nEC: " + account.getStatus());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (t != null) {
                                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                            }
                        }
                    });
        } else {
            System.out.println("ELSE");
            controller.login(citizen.getUsername(), citizen.getHriToken(), citizen.getPassword(), citizen.getCitizenId(), citizen.getSymmetricKey(),
                    new MR2DBackupFactory.MR2DBackupInterface() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(Account account) throws Exception {
                            citizen.setToken(account.getToken());
                            if (!account.getEmergencyToken().equals("") || account.getEmergencyToken() != null) {
                                citizen.setEmergencyToken(account.getEmergencyToken());
                                citizen.setCitizenId(account.getCitizenId());
                                citizen.setHriToken(account.getHriToken());
                                citizen.setHriEmergencyToken(account.getHriEmergencyToken());

                            }
                            citizen.setSymmetricKey("XVggCm3qd22ceCDlTe4LEPFmAm6driB+pSilZqs+u0k=");
                            citizen.setConsentShareAccepted(account.isConsentShareAccepted());
                            citizen.setConsentStoreAccepted(account.isConsentStoreAccepted());
                            mTextViewResult.setText("MinIO response!: " + account.getMsg() +
                                    "\nToken: " + account.getToken() +
                                    "\nEC: " + account.getStatus());
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

}