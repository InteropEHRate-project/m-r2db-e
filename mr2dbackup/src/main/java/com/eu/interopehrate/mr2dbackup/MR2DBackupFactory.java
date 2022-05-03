    package com.eu.interopehrate.mr2dbackup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.WriterException;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import eu.interopehrate.encryptedcomunication.EncryptedCommunicationFactory;
import eu.interopehrate.encryptedcomunication.api.EncryptedCommunication;
import eu.interopehrate.hri.mhri.controller.HealthRecordIndexController;
import eu.interopehrate.m_rds_sm.CryptoManagementFactory;
import eu.interopehrate.m_rds_sm.api.CryptoManagement;

import eu.interopehrate.protocols.common.ResourceCategory;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MR2DBackupFactory {
    private SEHRCloudInterface SEHRCloudInterface;
    private FhirContext ctx = FhirContext.forR4();
    Account account = new Account();

    private JSONObject qrCodeContent = new JSONObject();
    String baseUrl = SEHRCloudClient.getBaseUrl();

    private HealthRecordIndexController hriController = new HealthRecordIndexController();

    EncryptedCommunication encryptedCommunication = EncryptedCommunicationFactory.create();
//    CryptoManagement cryptoManagement = CryptoManagementFactory.create();

    public MR2DBackupFactory(String cloudUrl) {
        account.setCloudUrl(cloudUrl);
        SEHRCloudInterface = SEHRCloudClient.getClient(cloudUrl).create(SEHRCloudInterface.class);
    }

    public interface MR2DBackupInterface {
        void onResponse(Account account) throws Exception;

        void onFailure(Throwable t);
    }

    public void register(String username, String password, MR2DBackupInterface listener) {
            Call<Account> call = SEHRCloudInterface.register(account.getCloudUrl() + "/citizen/register", username, password);
            call.enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (listener != null) {
                        try {
//                        account.setMsg(response.body().getMsg());
                            listener.onResponse(response.body());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    listener.onFailure(t);
                }
            });
    }

    public void login(String username, String password, String symKey, MR2DBackupInterface listener) {
        account.setSymmetricKey(symKey);
        account.setUsername(username);
        Call<Account> call = SEHRCloudInterface.login(account.getCloudUrl()+"/citizen/login", username, password);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        try {
                            account.setToken(response.body().getToken());
                            account.setEmergencyToken(response.body().getEmergencyToken());
                            if(response.body().getConsentShareIsAccepted().equals("True")) {
                                account.setConsentShareAccepted(true);
                            } else {
                                account.setConsentShareAccepted(false);
                            }

                            if(response.body().getConsentStoreIsAccepted().equals("True")) {
                                account.setConsentStoreAccepted(true);
                            } else {
                                account.setConsentStoreAccepted(false);
                            }
                            System.out.println("TOKEN: \t"+account.getToken());

                            listener.onResponse(account);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (listener != null) {
                        try {
                            account.setMsg("Username of password is missing or wrong");
                            account.setToken("");
                            account.setEmergencyToken("");
                            account.setStatus(400);
                            listener.onResponse(account);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void login(String username, String password, String hriToken, String storedCitizenId, String symKey, MR2DBackupInterface listener) {
        account.setSymmetricKey(symKey);
        account.setUsername(username);
        Call<Account> call = SEHRCloudInterface.login(account.getCloudUrl()+"/citizen/login", username, password);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        try {
                            account.setToken(response.body().getToken());
                            account.setEmergencyToken(response.body().getEmergencyToken());
                            if(response.body().getConsentShareIsAccepted().equals("True")) {
                                account.setConsentShareAccepted(true);
                            } else {
                                account.setConsentShareAccepted(false);
                            }

                            if(response.body().getConsentStoreIsAccepted().equals("True")) {
                                account.setConsentStoreAccepted(true);
                            } else {
                                account.setConsentStoreAccepted(false);
                            }

                            if(!account.getEmergencyToken().equals("")) {
                                Thread thread = new Thread(() -> {
                                    JSONObject hriResponse = null;
                                    try {
                                        String encryptedUsername = encryptData(account.getUsername(), account.getSymmetricKey())
                                                .replace("/", "DASHREPLACEDASH")
                                                .replace("\n", "");
                                        String encryptedUrl =  encryptData(account.getCloudUrl(), account.getSymmetricKey())
                                                .replace("/", "DASHREPLACEDASH")
                                                .replace("\n", "");
                                        hriResponse = new JSONObject(hriController.findCitizen(hriToken, encryptedUsername, encryptedUrl).toString());
                                        JSONArray data = (JSONArray) hriResponse.get("data");
                                        JSONObject hriData = (JSONObject) data.get(0);
                                        account.setCitizenId(hriData.getString("citizenId"));
                                        account.setHriToken(hriResponse.getString("hriAccessToken"));
                                        account.setHriEmergencyToken(hriResponse.getString("hriEmergencyToken"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();
                                Thread.sleep(800);

                                thread = new Thread(() -> {
                                    JSONObject hriResponse = null;
                                    try {
                                        String encryptedUsername = encryptData(account.getUsername(), account.getSymmetricKey())
                                                .replace("/", "DASHREPLACEDASH")
                                                .replace("\n", "");
                                        String encryptedUrl =  encryptData(account.getCloudUrl(), account.getSymmetricKey())
                                                .replace("/", "DASHREPLACEDASH")
                                                .replace("\n", "");
                                        String encryptedEmergencyToken = encryptData(account.getEmergencyToken(), account.getSymmetricKey())
                                                .replace("/", "DASHREPLACEDASH")
                                                .replace("\n", "");
                                        System.out.println(encryptedEmergencyToken);
                                        hriResponse = new JSONObject(hriController.updateCitizen(hriToken, storedCitizenId, encryptedEmergencyToken, encryptedUsername,  encryptedUrl).toString());
                                        JSONArray data = (JSONArray) hriResponse.get("data");
                                        JSONObject hriData = (JSONObject) data.get(0);
                                        account.setCitizenId(hriData.getString("citizenId"));
                                        account.setHriToken(hriResponse.getString("hriAccessToken"));
                                        account.setHriEmergencyToken(hriResponse.getString("hriEmergencyToken"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();
                                Thread.sleep(800);
                            }

                            System.out.println("TOKEN: \t"+account.getToken());

                            listener.onResponse(account);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (listener != null) {
                        try {
                            account.setMsg("Username of password is missing or wrong");
                            account.setToken("");
                            account.setEmergencyToken("");
                            account.setStatus(400);
                            listener.onResponse(account);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void downloadConsentStore(String token, MR2DBackupInterface listener) {
        Call<String> call = SEHRCloudInterface.downloadConsentStore(account.getCloudUrl()+"/citizen/consent/download/store", token);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String consentStore = response.body();

                if (listener != null && consentStore != null) {
                    try {
                        account.setConsentStore(consentStore);
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (listener != null) {
                    account.setMsg("Consent to Store is null");
                    account.setConsentStore(null);
                    try {
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    account.setMsg("listener is null");
                    account.setConsentStore(null);
                    try {
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                account.setMsg("Consent to Store is not found");
                listener.onFailure(t);
            }
        });
    }

    public void downloadConsentShare(String token, MR2DBackupInterface listener) {
        Call<String> call = SEHRCloudInterface.downloadConsentShare(account.getCloudUrl()+"/citizen/consent/download/share", token);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String consentShare = response.body();

                if (listener != null && consentShare != null) {
                    try {
                        account.setConsentShare(consentShare);
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (listener != null) {
                    account.setMsg("Consent to Share is null");
                    account.setConsentShare(null);
                    try {
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    account.setMsg("listener is null");
                    account.setConsentShare(null);
                    try {
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                account.setMsg("Consent to Share is not found");
                listener.onFailure(t);
            }
        });
    }

    public void signAndUploadConsentStore(String token, String consent, Context context, MR2DBackupInterface listener)
            throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException, JSONException {
        String signature = signConsent(consent, context);
        JSONObject consentAndSignature = new JSONObject(consent);
        consentAndSignature.put("signature", signature);
        RequestBody consentStore = RequestBody.create(MediaType.parse("text/plain"), consentAndSignature.toString());


        Call<Account> call = SEHRCloudInterface.uploadConsentStore(account.getCloudUrl() + "/citizen/consent/upload/store", token, consentStore);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    try {
                        listener.onResponse(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
//        return signature;
    }

    public void signAndUploadConsentShare(String token, String accountName, String symKey, String consent, Context context, MR2DBackupInterface listener)
            throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException, JSONException {

        account.setUsername(accountName);
        account.setSymmetricKey(symKey);

        String signature = signConsent(consent, context);
        JSONObject consentAndSignature = new JSONObject(consent);
        consentAndSignature.put("signature", signature);
        RequestBody consentShare = RequestBody.create(MediaType.parse("text/plain"), consentAndSignature.toString());

        Call<Account> call = SEHRCloudInterface.uploadConsentShare(account.getCloudUrl()+"/citizen/consent/upload/share", token, consentShare);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    account.setEmergencyToken(response.body().getEmergencyToken());
                    try {
                        Thread thread = new Thread(() -> {
                            JSONObject hriResponse = null;
                            try {
                                System.out.println(account.getSymmetricKey());
                                String encryptedUsername = encryptData(account.getUsername(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                String encryptedUrl =  encryptData(account.getCloudUrl(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                String encryptedEmergencyToken = encryptData(account.getEmergencyToken(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                hriResponse = new JSONObject(hriController.createCitizen(encryptedEmergencyToken, encryptedUsername, encryptedUrl).toString());
                                JSONArray data = (JSONArray) hriResponse.get("data");
                                JSONObject hriData = (JSONObject) data.get(0);
                                account.setCitizenId(hriData.getString("citizenId"));
                                account.setHriToken(hriResponse.getString("hriAccessToken"));
                                account.setHriEmergencyToken(hriResponse.getString("hriEmergencyToken"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();
                        Thread.sleep(800);
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void signAndUploadConsentShare(String token, String accountName, String hriToken, String citizenId, String symKey, String consent, Context context, MR2DBackupInterface listener)
            throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException, JSONException {

        account.setUsername(accountName);
        account.setSymmetricKey(symKey);

        String signature = signConsent(consent, context);
        JSONObject consentAndSignature = new JSONObject(consent);
        consentAndSignature.put("signature", signature);
        RequestBody consentShare = RequestBody.create(MediaType.parse("text/plain"), consentAndSignature.toString());

        Call<Account> call = SEHRCloudInterface.uploadConsentShare(account.getCloudUrl()+"/citizen/consent/upload/share", token, consentShare);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    account.setEmergencyToken(response.body().getEmergencyToken());
                    try {
                        Thread thread = new Thread(() -> {
                            JSONObject hriResponse = null;
                            try {
                                System.out.println(account.getSymmetricKey());
                                String encryptedUsername = encryptData(account.getUsername(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                String encryptedUrl =  encryptData(account.getCloudUrl(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                String encryptedEmergencyToken = encryptData(account.getEmergencyToken(), account.getSymmetricKey())
                                        .replace("/", "DASHREPLACEDASH")
                                        .replace("\n", "");
                                hriResponse = new JSONObject(hriController.updateCitizen(hriToken, citizenId, encryptedEmergencyToken, encryptedUsername, encryptedUrl).toString());
                                JSONArray data = (JSONArray) hriResponse.get("data");
                                JSONObject hriData = (JSONObject) data.get(0);
                                account.setCitizenId(hriData.getString("citizenId"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();
                        Thread.sleep(800);
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void removeAccount(String token, MR2DBackupInterface listener) {
        Call<Account> call = SEHRCloudInterface.removeAccount(account.getCloudUrl()+"/citizen/removeaccount", token);
        call.enqueue(new Callback<Account>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    try {
                        listener.onResponse(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void removeAccount(String token, String hriToken, String storedCitizenId, MR2DBackupInterface listener) {
        Call<Account> call = SEHRCloudInterface.removeAccount(account.getCloudUrl()+"/citizen/removeaccount", token);
        call.enqueue(new Callback<Account>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    try {
                        Thread thread = new Thread(() -> {
                            JSONObject hriResponse = null;
                            try {
                                hriResponse = new JSONObject(hriController.deleteCitizen(hriToken, storedCitizenId).toString());
                                JSONArray data = (JSONArray) hriResponse.get("data");
                                JSONObject hriData = (JSONObject) data.get(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();
                        Thread.sleep(800);
                        listener.onResponse(response.body());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void getAuditInfo(String token, String symKey, MR2DBackupInterface listener) {
        Call<String> call = SEHRCloudInterface.getAuditInfo(account.getCloudUrl()+"/citizen/auditing",token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String auditInfo = response.body();
                if (listener != null && auditInfo != null) {
                    try {
                        String[] lines = auditInfo.split("\n");
                        String updatedAuditInfo = "";
                        int length = lines.length;
                        for (int i=0; i<length; i++) {
                            String currentLine = lines[i];
                            if(currentLine.contains("\"uploaded_hr\"")) {
                                String encryptedEHRName = StringUtils.substringBetween(currentLine, "\"uploaded_hr\": \"", "\"");
                                String encryptedEHRNameWithoutDash = encryptedEHRName.replace("DASHREPLACEDASH", "/").replace(".txt","");
                                String decryptedEHRName = "";
                                try {
                                    decryptedEHRName = decryptData(encryptedEHRNameWithoutDash, symKey);
                                } catch (Exception e){
                                    decryptedEHRName = encryptedEHRName;
                                }

                                System.out.println(encryptedEHRName + ":\t" + decryptedEHRName);
                                currentLine = currentLine.replace(encryptedEHRName, decryptedEHRName).replace(".txt", "").replace("_", " ");
                                updatedAuditInfo += currentLine+"\n";
                            }
                            else if (currentLine.contains("\"downloaded_hr\"")) {
                                String encryptedEHRName = StringUtils.substringBetween(currentLine, "\"downloaded_hr\": \"", "\"");
                                String encryptedEHRNameWithoutDash = encryptedEHRName.replace("DASHREPLACEDASH", "/").replace(".txt","");
                                String decryptedEHRName = "";
                                decryptedEHRName = decryptData(encryptedEHRNameWithoutDash, symKey);
                                System.out.println(encryptedEHRName+":\t"+decryptedEHRName);
//                                System.out.println(encryptedEHRName + ":\t" + decryptedEHRName);
                                currentLine = currentLine.replace(encryptedEHRName, decryptedEHRName).replace(".txt", "").replace("_", " ");
                                updatedAuditInfo += currentLine+"\n";
                            }
                            else {
                                updatedAuditInfo += currentLine+"\n";
                            }
                        }

                        account.setAuditInfo(updatedAuditInfo);
                        listener.onResponse(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                account.setMsg("Auditing information is not found");
                listener.onFailure(t);
            }
        });
    }

    public void withdrawConsentShare(String token, String hriToken, String storedCitizenId, MR2DBackupInterface listener) {
        Call<String> call = SEHRCloudInterface.withdrawConsentShare(account.getCloudUrl()+"/citizen/consent/withdraw/share", token);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject json = new JSONObject(response.body());
                    account.setConsentShareAccepted(json.getBoolean("consentShare"));
                    account.setStatus(json.getInt("status"));
                    account.setMsg(json.getString("msg"));
                    account.setEmergencyToken("");
                    System.out.println("CITIZEN ID:\t"+ account.getCitizenId());
                    try {
                        Thread thread = new Thread(() -> {
                            JSONObject hriResponse = null;
                            try {
                                hriResponse = new JSONObject(hriController.deleteCitizen(hriToken, storedCitizenId).toString());
                                JSONArray data = (JSONArray) hriResponse.get("data");
                                JSONObject hriData = (JSONObject) data.get(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        thread.start();
                        Thread.sleep(800);
                        listener.onResponse(account);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                account.setMsg("Something went wrong");
                listener.onFailure(t);
            }
        });
    }

    public void create(String token, ResourceCategory rc, String data, String symKey, MR2DBackupInterface listener)
            throws Exception {

        System.out.println("DC: \t" + rc.toString());
        String encryptedFileType = encryptData(rc.toString(), symKey);
        encryptedFileType = encryptedFileType.replace("\n", "");
        encryptedFileType = encryptedFileType.replace("/", "DASHREPLACEDASH");
        String metadata = "{\"file-type\":\"txt\",\n\"hr-type\":\"" + encryptedFileType + "\"}";
        System.out.println(metadata);

        // Encrypt data before uploading
        String encryptedData = encryptData(data, symKey);

        RequestBody ehr = RequestBody.create(MediaType.parse("text/plain"), encryptedData);

        Call<Account> call = SEHRCloudInterface.uploadHR(account.getCloudUrl()+"/citizen/upload/hr", token, metadata, ehr);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (listener != null) {
                    try {
//                        account.setMsg(response.body().getMsg());
                        account.setStatus(response.body().getStatus());
                        System.out.println("CREATE: \t"+response.body().getStatus());
                        listener.onResponse(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void get(String token, String bucket, ResourceCategory rc, String symKey, MR2DBackupInterface listener) throws Exception, JSONException {

        System.out.println(rc.toString());
        String encryptedFileType = encryptData(rc.toString(), symKey);
        encryptedFileType = encryptedFileType.replace("\n", "");
        encryptedFileType = encryptedFileType.replace("/", "DASHREPLACEDASH");
        encryptedFileType += ".txt";
        System.out.println(encryptedFileType);
        
//        String metadata = "{\"file-type\":\"txt\",\n\"hr-type\":\""+encryptedFileType+"\"}";

        Call<String> call = SEHRCloudInterface.downloadHR(account.getCloudUrl() + "/citizen/" + bucket + "/" + encryptedFileType, token);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

//                String isJsonValid = isJSONValid(response.body());
                if (!response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.errorBody().string());
                        account.setMsg(json.getString("msg"));
                        account.setStatus(json.getInt("errorCode"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        listener.onResponse(account);
                        account.setHR(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.isSuccessful()) {
                    Boolean isJsonValid = isJSONValid(response.body());
//                    System.out.println("RESPONSE BODY: \t"+response.body().substring(1, 100));
//                    System.out.println("IS VALID JSON: \t"+isJsonValid.toString());
                    if (isJsonValid) {
                        try {
                            JSONObject json = new JSONObject(response.body());
                            account.setMsg(json.getString("msg"));
                            if (json.has("status")) {
                                account.setStatus(json.getInt("status"));
                            } else if (json.has("errorCode")) {
                                account.setStatus(json.getInt("errorCode"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            listener.onResponse(account);
                            account.setHR(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String encryptedHR = response.body();
//                        System.out.println("Downloaded data from the S-EHR Cloud: \t" + encryptedHR);
                        String decryptedHR = null;
                        try {
                            decryptedHR = decryptData(encryptedHR, symKey);
//                            System.out.println("Downloaded data from the S-EHR Cloud (after decryption): \t" + decryptedHR);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (listener != null && decryptedHR != null) {
                            try {
                                account.setHR(decryptedHR);
                                listener.onResponse(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (listener != null) {
                            account.setMsg("decryptedHR is null");
                            account.setHR(null);
                            try {
                                listener.onResponse(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            account.setMsg("listener is null");

                            try {
                                listener.onResponse(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                account.setMsg("HR not found");
                listener.onFailure(t);
            }
        });
    }

    public void getBudlesInfo(String token, String bucket, ResourceCategory rc, String symKey, MR2DBackupInterface listener) throws Exception {
        String encryptedFileType = encryptData(rc.toString(), symKey);
        encryptedFileType = encryptedFileType.replace("\n", "");
        encryptedFileType = encryptedFileType.replace("/", "DASHREPLACEDASH");
        encryptedFileType += ".txt";
        System.out.println("encryptedFileType\t" + encryptedFileType);

        Call<String> call = SEHRCloudInterface.getMetadata(account.getCloudUrl()+"/citizen/"+bucket+"/"+encryptedFileType+"/metadata", token);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println(response.body());
                account.setMetadata(response.body());
                try {
                    listener.onResponse(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void listBuckets(String token, MR2DBackupInterface listener) throws Exception {
        Call<String> call = SEHRCloudInterface.listBuckets(account.getCloudUrl()+"/citizen/buckets", token);
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    account.setBucketsList(json.getJSONArray("buckets"));
                } catch (JSONException e) {
                    account.setBucketsList(null);
                    e.printStackTrace();
                }

                try {
                    listener.onResponse(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.onFailure(t);
            }

        });

    }

    public void listObjects(String token, String bucket, String symKey,MR2DBackupInterface listener) {
        Call<String> call = SEHRCloudInterface.listObjects(account.getCloudUrl()+"/citizen/buckets/"+bucket, token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.body());
                    System.out.println(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String encryptedName;
                ArrayList<String> objectsList = new ArrayList<>();
                String decryptedName;
                if (json.has("err")) {
                    try {
                        account.setMsg(json.getString("err"));
                        account.setObjectList(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                        String key = it.next();
                        try {
                            encryptedName = (String) json.get(key);
                            encryptedName = encryptedName.replace("DASHREPLACEDASH", "/");
                            try {
                                decryptedName = encryptedCommunication.decrypt(encryptedName, symKey);
                            } catch (Exception e) {
                                decryptedName = null;
                            }
                            if(decryptedName != null) {
                                objectsList.add(decryptedName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        JSONArray objects = new JSONArray(objectsList);
                        account.setObjectList(objects);
                    } catch (NullPointerException e) {
                        account.setObjectList(null);
                    }

                }
                try {
                    System.out.println(account.getObjectList().toString());
                    listener.onResponse(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    private String encryptData(String data, String symmetricKey) throws Exception {
        return encryptedCommunication.encrypt(data, symmetricKey);
    }

    private String decryptData(String data, String symmetricKey) throws Exception {
        return encryptedCommunication.decrypt(data, symmetricKey);
    }

    private String signConsent(String consent, Context context) throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        String CA_URL = "http://212.101.173.84:8071";
        CryptoManagement cryptoManagement = CryptoManagementFactory.create(CA_URL);


        Log.d("CONSENT", consent);


//        V0.2.0
        PrivateKey privateKey = cryptoManagement.getPrivateKey(context);
        RSAPublicKey rsaPublicKey = cryptoManagement.getPublicKey(context);

        String signedConsent = cryptoManagement.signPayload(consent, privateKey);

        Boolean verify = cryptoManagement.verifySignature(rsaPublicKey, consent.getBytes(), signedConsent.getBytes());

        Log.d("SIGNED", signedConsent);
        Log.d("VERIFIED", String.valueOf(verify));
        return signedConsent;
    }

    private boolean checkProvenanceInformation (String healthRecord) throws JSONException {
        boolean hasProvenanceTrackingInformation = false;
        IParser parser = ctx.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, healthRecord);
        String signatureFormat = bundle.getSignature().getSigFormat();
        String signature = bundle.getSignature().getData().toString();
        String targetFormat = bundle.getSignature().getTargetFormat();
        String id = bundle.getSignature().getId();
        Date when = bundle.getSignature().getWhen();
        String who = bundle.getSignature().getWho().getReference();

        return hasProvenanceTrackingInformation;
    }

    private boolean complianceCheck (String heathRecords){
        return true;
    }

    public boolean setQRCOdeContent(String citizenId, String hriEmergencyToken, String emergencyToken, String symKey) throws JSONException {
        qrCodeContent.put("citizenId", citizenId);
        qrCodeContent.put("emergencyToken", emergencyToken);
        qrCodeContent.put("hriEmergencyToken", hriEmergencyToken);
        qrCodeContent.put("symKey", symKey);

        return (account.getToken() != null) && (account.getSymmetricKey() != null);
    }

    public JSONObject getQrCodeContent() {
        return qrCodeContent;
    }

    public Bitmap retrieveQRCode(String citizenId, String hriEmergencyToken, String emergencyToken, String symKey) throws JSONException, WriterException {
        boolean qrCodeContentIsCreated = setQRCOdeContent(citizenId, hriEmergencyToken, emergencyToken, symKey);

        JSONObject qrCodeContent;
        qrCodeContent = getQrCodeContent();

        QRGEncoder qrgEncoder = new QRGEncoder(qrCodeContent.toString(),
                null,
                QRGContents.Type.TEXT,
                250);

        Bitmap bitmap = qrgEncoder.encodeAsBitmap();
        return bitmap;
    }

    private Boolean isJSONValid(String response) {
        try {
            new JSONObject(response);
        } catch (JSONException ex) {
            try {
                new JSONArray(response);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}