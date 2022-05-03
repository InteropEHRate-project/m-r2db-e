package com.eu.interopehrate.dummyApplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.eu.interopehrate.mr2dbackup.Account;
import com.eu.interopehrate.mr2dbackup.SEHRCloudInterface;
import com.eu.interopehrate.mr2dbackup.SEHRCloudClient;
import com.eu.interopehrate.mr2dbackup.MR2DBackupFactory;
import com.eu.interopehrate.mr2dbackup.SEHRCloudProviders.SEHRCloudProviders;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private EditText username, password;
    private TextView mTextViewResult;
    private Account citizen = Account.Account();
    private static final String TAG = "RegisterActivity";
    private String responseMsg;
    private int responseEC;
    private Spinner dropdown;
    private String cloud;

//    SEHRCloudInterface SEHRCloudInterface;
    MR2DBackupFactory controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTextViewResult = findViewById(R.id.text_view_result);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        btnRegister = findViewById(R.id.register);
        btnLogin = findViewById(R.id.goToLogin);

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{SEHRCloudProviders.SEHR_CLOUD_1.toString(), SEHRCloudProviders.SEHR_CLOUD_2.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
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

        btnRegister.setOnClickListener(v -> {
            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
            } else {
                citizen.setUsername(username.getText().toString());
                citizen.setPassword(password.getText().toString());

//                SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);
                register();
            }
        });

        btnLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));
    }

//    Method for registering to the S-EHR Cloud. If the username already exists
//    or the password is not at least 8 characters long a msg is received
    @SuppressLint("SetTextI18n")
    public void register(){
        controller.register(citizen.getUsername(), citizen.getPassword(), new MR2DBackupFactory.MR2DBackupInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Account account) throws Exception {
                mTextViewResult.setText("Response: "+ account.getMsg());
//                System.out.println(account.getMsg());
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