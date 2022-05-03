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

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import eu.interopehrate.encryptedcomunication.EncryptedCommunicationFactory;
import eu.interopehrate.encryptedcomunication.api.EncryptedCommunication;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;


public class DownloadActivity extends AppCompatActivity {

    private Button btnAccountMain, btnDownloadHR, btnDownloadIPS, btnDownloadLR, btnDownloadEncounter, btnDownloadImageR, btnDownloadPrescription, btnDownloadPatient, btnListBuckets, btnListObjects;
    private TextView mTextViewResult;
    private static final String TAG = "DownloadActivity";
    private Account citizen = Account.Account();
    private final String symmetricKey = "Bos0HSxY4HWrVwEZaoywbAnP8a0BWExEfl5pyHULEXQ=";

    MR2DBackupFactory mr2dbackup;
//    SEHRCloudInterface SEHRCloudInterface;
    EncryptedCommunication encryptedCommunication = EncryptedCommunicationFactory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        mr2dbackup = new MR2DBackupFactory(citizen.getCloudUrl());
//        SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);

        btnDownloadIPS = findViewById(R.id.downloadIPS);
        btnDownloadLR = findViewById(R.id.downloadLR);
        btnDownloadImageR = findViewById(R.id.downloadIR);
        btnDownloadPrescription = findViewById(R.id.downloadPrescription);
        btnDownloadPatient = findViewById(R.id.downloadPatient);
        btnDownloadEncounter = findViewById(R.id.downloadEncounter);
        btnAccountMain = findViewById(R.id.goToAccount);
        btnListBuckets = findViewById(R.id.listBuckets);
        btnListObjects = findViewById(R.id.listObjects);

        btnAccountMain.setOnClickListener(v -> startActivity(new Intent(DownloadActivity.this,
                LoggedInAccountActivity.class)));

        btnDownloadIPS.setOnClickListener(v -> {
            try {
                downloadHR("ips");
                System.out.println("IPS");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadEncounter.setOnClickListener(v -> {
            try {
                downloadHR("encounter");
                System.out.println("ENCOUNTER");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadLR.setOnClickListener(v -> {
            try {
                downloadHR("lr");
                System.out.println("LR");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadImageR.setOnClickListener(v -> {
            try {
                downloadHR("imageR");
                System.out.println("Image Report");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadPrescription.setOnClickListener(v -> {
            try {
                System.out.println("PRE");
                downloadHR("prescription");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDownloadPatient.setOnClickListener(v -> {
            try {
                System.out.println("PATIENT");
                downloadHR("patient");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnListBuckets.setOnClickListener(v -> {
            try {
                listBuckets();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnListObjects.setOnClickListener(v -> {
            try {
                listObjects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

//    Method for downloading an encrypted HR. The decryption is done within the R2D Backup library
    public void downloadHR(String fileType) throws Exception, JSONException {
        ResourceCategory dc;

        switch (fileType){
            case "ips":
                dc = DocumentCategory.PATIENT_SUMMARY;
                System.out.println(dc.toString());
                break;
            case "lr":
                dc = DocumentCategory.LABORATORY_REPORT;
                System.out.println(dc.toString());
                break;
            case "imageR":
                dc = DocumentCategory.IMAGE_REPORT;
                System.out.println(dc.toString());
                break;
            case "encounter":
                dc = FHIRResourceCategory.ENCOUNTER;
                System.out.println(dc.toString());
                break;
            case "prescription":
                dc = FHIRResourceCategory.MEDICATION_REQUEST;
                System.out.println(dc.toString());
                break;
            case "patient":
                dc = FHIRResourceCategory.PATIENT;
                System.out.println(dc.toString());
                break;
            default:
                dc = DocumentCategory.PATIENT_SUMMARY;
                break;
        }

        btnDownloadIPS.setEnabled(false);
        btnDownloadLR.setEnabled(false);
        btnDownloadPrescription.setEnabled(false);
        btnDownloadImageR.setEnabled(false);
        btnDownloadPatient.setEnabled(false);
        btnDownloadEncounter.setEnabled(false);

        mr2dbackup.get(citizen.getToken(), citizen.getUsername(), dc, citizen.getSymmetricKey(), new MR2DBackupFactory.MR2DBackupInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Account account) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if(account.getHR() != null) {
                        mTextViewResult.setText(account.getHR());
                        //                        account.setHR(null);
                    } else {
                        mTextViewResult.setText(account.getMsg()+"\n"+account.getStatus());
                    }
                    btnDownloadIPS.setEnabled(true);
                    btnDownloadLR.setEnabled(true);
                    btnDownloadPrescription.setEnabled(true);
                    btnDownloadImageR.setEnabled(true);
                    btnDownloadPatient.setEnabled(true);
                    btnDownloadEncounter.setEnabled(true);
                }, 2000);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Throwable t) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mTextViewResult.setText("Something went wrong!\n" + citizen.getMsg());
                }, 2000);
                btnDownloadIPS.setEnabled(true);
                btnDownloadLR.setEnabled(true);
                btnDownloadPrescription.setEnabled(true);
                btnDownloadImageR.setEnabled(true);
                btnDownloadPatient.setEnabled(true);
                btnDownloadEncounter.setEnabled(true);
            }
        });
    }

    public void listBuckets() throws Exception {
        mr2dbackup.listBuckets(citizen.getToken(), new MR2DBackupFactory.MR2DBackupInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Account account) throws Exception {
                if(account.getBucketsList() != null) {
                    mTextViewResult.setText(account.getMsg() + "\n" + account.getBucketsList().toString());
                } else {
                    mTextViewResult.setText("something went wrong");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mTextViewResult.setText("Something went wrong!\n");
            }
        });
    }

    public void listObjects() {
        mr2dbackup.listObjects(citizen.getToken(), "azertemergencyhcp1", citizen.getSymmetricKey(), new MR2DBackupFactory.MR2DBackupInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Account account) throws Exception {
                if(account.getObjectList() != null) {
                    System.out.println(citizen.getUsername());
                    mTextViewResult.setText(account.getObjectList().toString());
                } else {
                    mTextViewResult.setText(account.getMsg());
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
