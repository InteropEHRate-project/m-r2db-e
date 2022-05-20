package com.eu.interopehrate.dummyApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eu.interopehrate.mr2dbackup.Account;
import com.eu.interopehrate.mr2dbackup.SEHRCloudInterface;
import com.eu.interopehrate.mr2dbackup.SEHRCloudClient;
import com.eu.interopehrate.mr2dbackup.MR2DBackupFactory;
import com.eu.interopehrate.mr2dbackup.SEHRCloudProviders.SEHRCloudProviders;
import com.google.zxing.WriterException;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import eu.interopehrate.encryptedcomunication.EncryptedCommunicationFactory;

import eu.interopehrate.encryptedcomunication.api.EncryptedCommunication;
import eu.interopehrate.protocols.common.DocumentCategory;
import eu.interopehrate.protocols.common.FHIRResourceCategory;
import eu.interopehrate.protocols.common.ResourceCategory;

public class LoggedInAccountActivity extends AppCompatActivity {

    private Button btnLogin, btnRemoveAccount, btnUploadHR, btnUploadIPS, btnUploadLR, btnUploadEncounter, btnUploadImageR, btnUploadPrescription, btnUploadPatient, btnDownloadEHR, btnGoToConsentPage, btnGoToAuditInfo, btnMetadata;
    private ImageView qrImage;
    private TextView mTextViewResult;
    private Bitmap bitmap;
    private String responseMsg;
    private static final String TAG = "RemoveAccountActivity";
    private Account citizen = Account.Account();


    private String hrData = "ips";
    private String ipsData = "ips";
    private String lrData = "lr";
    private String imageRData = "imageR";
    private String patientData = "patient";
    private String prescriptionData = "prescription";
    private String encounterData = "encounter";
    private final String symmetricKey = "Bos0HSxY4HWrVwEZaoywbAnP8a0BWExEfl5pyHULEXQ=";

    MR2DBackupFactory mr2dbackup;
//    SEHRCloudInterface SEHRCloudInterface;
    EncryptedCommunication encryptedCommunication = EncryptedCommunicationFactory.create();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_account);

        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());
        System.out.println("CITIZEN:\t"+citizen.getCloudUrl());
        mr2dbackup = new MR2DBackupFactory(citizen.getCloudUrl());

        qrImage = findViewById(R.id.qrImage);
//        btnUploadHR = findViewById(R.id.uploadHR);
        btnUploadIPS = findViewById(R.id.uploadIPS);
        btnUploadImageR = findViewById(R.id.uploadIR);
        btnUploadLR = findViewById(R.id.uploadLR);
        btnUploadPrescription = findViewById(R.id.uploadPrescription);
        btnUploadPatient = findViewById(R.id.uploadPatient);
        btnUploadEncounter = findViewById(R.id.uploadEncounter);
        btnDownloadEHR = findViewById(R.id.goToDownload);
        btnGoToAuditInfo = findViewById(R.id.goToAuditInfo);
        btnMetadata = findViewById(R.id.metadata);

        btnRemoveAccount = findViewById(R.id.removeAccount);
        btnLogin = findViewById(R.id.goToLogin);
//        The symmetric key that should be used by the citizen can be obtained by this function
        try {
            citizen.generateSymmetricKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        citizen.setSymmetricKey("XVggCm3qd22ceCDlTe4LEPFmAm6driB+pSilZqs+u0k=");
//        Andaman7 key
//        citizen.setSymmetricKey("UiYmJk/iWopGS2n0YIhZsgp1auRVyahR7sgFOtEC5r4=");

//      This part of the code is used for the QRCode creation and visualization.
        String textForTextView = "<b>SK:</b> " + citizen.getSymmetricKey() +
                "<br><b>Token:</b> " + citizen.getToken() +
                "<br><b>Emergency Token:</b> " + citizen.getEmergencyToken() +
                "<br><b>Username:</b> " + citizen.getUsername() +
                "<br><b>eToken:</b> " + citizen.getEmergencyToken()+
                "<br><b>hriToken:</b> " + citizen.getHriToken()+
                "<br><b>hriEToken:</b> " + citizen.getHriEmergencyToken()+
                "<br><b>CID:</b> " + citizen.getCitizenId() +
                "<br><b>Consent Store Accepted:</b> " + citizen.isConsentStoreAccepted() +
                "<br><b>Consent Share Accepted:</b> " + citizen.isConsentShareAccepted()
                ;
        mTextViewResult.setText(Html.fromHtml(textForTextView));
        if (!citizen.getEmergencyToken().equals("")) {
            try {
                retrieveQrCode();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

//        SEHRCloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);

        btnUploadImageR.setOnClickListener(v -> {
//                Read prescription sample file
            try {
                readFile("imageR");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uploadHR("imageR");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadPrescription.setOnClickListener(v -> {
//                Read prescription sample file
            try {
                readFile("prescription");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uploadHR("prescription");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadIPS.setOnClickListener(v -> {
//                Read ips sample file
            try {
                readFile("ips");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uploadHR("ips");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadLR.setOnClickListener(v -> {
//                Read lr sample file
            try {
                readFile("lr");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uploadHR("lr");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadEncounter.setOnClickListener(v -> {
//                Read lr sample file
            try {
                readFile("encounter");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                uploadHR("encounter");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnUploadPatient.setOnClickListener(v -> {
//            Read Patient sample file
            try {
                readFile("patient");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                uploadHR("patient");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnRemoveAccount.setBackgroundColor(Color.RED);
        btnRemoveAccount.setOnClickListener(v -> {
//                accountInterface = SEHRCloudClient.getClient().create(AccountInterface.class);
            mTextViewResult.setText("Token: " + citizen.getToken()+
                                    "\nUsername: " + citizen.getUsername());
            removeAccount();

            Handler handler = new Handler();
            handler.postDelayed(() -> startActivity(new Intent(LoggedInAccountActivity.this,
                    MainActivity.class)), 1000);
        });

        btnMetadata.setOnClickListener(v -> {
            try {
                getMetadata(citizen.getUsername(), DocumentCategory.PATIENT_SUMMARY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(LoggedInAccountActivity.this,
                                               MainActivity.class)));

        btnDownloadEHR.setBackgroundColor(Color.GREEN);
        btnDownloadEHR.setTextColor(Color.BLACK);
        btnDownloadEHR.setOnClickListener(v -> startActivity(new Intent(LoggedInAccountActivity.this,
                DownloadActivity.class)));

        btnGoToConsentPage = findViewById(R.id.goToConsent);

        btnGoToConsentPage.setOnClickListener(v -> startActivity(new Intent(LoggedInAccountActivity.this,
                ConsentActivity.class)));

        btnGoToAuditInfo.setOnClickListener(v -> startActivity(new Intent(LoggedInAccountActivity.this,
                AuditInfoActivity.class)));
    }

//    Method for deleting / removing the account and all of
//    its content that is uploaded in the S-EHR Cloud
    public void removeAccount(){

        if(citizen.getCitizenId() == null ||citizen.getCitizenId().equals("")) {
            mr2dbackup.removeAccount(citizen.getToken(), new MR2DBackupFactory.MR2DBackupInterface() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Account account) throws Exception {
                    mTextViewResult.setText("MinIO response: " + account.getMsg());
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t != null) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                }
            });
        } else {
            mr2dbackup.removeAccount(citizen.getToken(), citizen.getHriToken(), citizen.getCitizenId(), new MR2DBackupFactory.MR2DBackupInterface() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Account account) throws Exception {
                    mTextViewResult.setText("MinIO response: " + account.getMsg());
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

    private void retrieveQrCode() throws JSONException, WriterException {
        bitmap = mr2dbackup.retrieveQRCode(citizen.getCitizenId(), citizen.getHriEmergencyToken(), citizen.getEmergencyToken(), citizen.getSymmetricKey());
        qrImage.setImageBitmap(bitmap);
    }

    //    Method for uploading an encrypted HR
    private void uploadHR(String fileType) throws Exception {
        String data;
        ResourceCategory dc;
        switch (fileType) {
            case "ips":
                data = ipsData;
                dc = DocumentCategory.PATIENT_SUMMARY;
                break;
            case "lr":
                data = lrData;
                dc = DocumentCategory.LABORATORY_REPORT;
                break;
            case "prescription":
                data = prescriptionData;
                dc = FHIRResourceCategory.MEDICATION_REQUEST;
                break;
            case "encounter":
                data = encounterData;
                dc = FHIRResourceCategory.ENCOUNTER;
                break;
            case "imageR":
                data = imageRData;
                dc = DocumentCategory.IMAGE_REPORT;
                break;
            case "patient":
                data = patientData;
                dc = FHIRResourceCategory.PATIENT;
                break;
            default:
                data = ipsData;
                dc = DocumentCategory.PATIENT_SUMMARY;
                break;
        }
        System.out.println(fileType);
        mr2dbackup.create(citizen.getToken(), dc, data, citizen.getSymmetricKey(),
                        new MR2DBackupFactory.MR2DBackupInterface() {
                            @SuppressLint("SetTextI18n")

                            @Override
                            public void onResponse(Account account) throws Exception {
                                mTextViewResult.setText("MinIO response: " + account.getMsg());
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if (t != null) {
                                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                                }
                            }
                        });
        }

    private void getMetadata(String bucket, ResourceCategory rc) throws Exception {
        mr2dbackup.getBudlesInfo(citizen.getToken(), bucket, DocumentCategory.PATIENT_SUMMARY, citizen.getSymmetricKey(),
                new MR2DBackupFactory.MR2DBackupInterface() {
                    @Override
                    public void onResponse(Account account) throws Exception {
                        if(!account.getMetadata().equals(""))
                            mTextViewResult.setText(account.getMetadata());
                        else
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

    private void readFile(String ehrType) throws IOException {
        String fileName = null;
        switch (ehrType){
            case "ips":
                fileName = "IPSSample-Small.json";
                break;
            case "lr":
                fileName = "LRSample.json";
                break;
            case "prescription":
                fileName = "PrescriptionSample.json";
                break;
            case "imageR":
                fileName = "ImageReportSample.json";
                break;
            case "encounter":
                fileName = "EncountersSample.json";
                break;
            case "patient":
                fileName = "PatientSample.json";
                break;
        }
        String ehr = "";
        try {
            InputStream stream = getAssets().open(fileName);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            ehr = new String(buffer);
        } catch (IOException e){
            e.printStackTrace();
        }
        switch (ehrType){
            case "ips":
                ipsData = ehr;
                break;
            case "lr":
                lrData = ehr;
                break;
            case "imageR":
                imageRData = ehr;
                break;
            case "encounter":
                encounterData = ehr;
                break;
            case "prescription":
                prescriptionData = ehr;
                break;
            case "patient":
                patientData = ehr;
                break;
        }
    }
}