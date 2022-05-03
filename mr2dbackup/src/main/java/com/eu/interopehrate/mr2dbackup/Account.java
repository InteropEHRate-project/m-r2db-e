package com.eu.interopehrate.mr2dbackup;

import android.graphics.Bitmap;

import androidx.annotation.BoolRes;

import com.google.gson.annotations.SerializedName;
import com.google.zxing.WriterException;


import org.checkerframework.common.value.qual.BoolVal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import eu.interopehrate.encryptedcomunication.EncryptedCommunicationFactory;
import eu.interopehrate.encryptedcomunication.api.EncryptedCommunication;

// This is a Singleton class
public class Account{
    private static Account singleAccountInstance = null;

    EncryptedCommunication encryptedCommunication = EncryptedCommunicationFactory.create();

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName(value="msg", alternate = {})
    private String msg;

    @SerializedName("emergency_token")
    private String emergencyToken;

    @SerializedName("token")
    private String token;

    @SerializedName(value="errorCode", alternate = {"status"})
    private int status;

    @SerializedName("consentStoreIsAccepted")
    private String consentStoreIsAccepted;

    @SerializedName("consentShareIsAccepted")
    private String consentShareIsAccepted;

    private Boolean consentStoreAccepted;
    private Boolean consentShareAccepted;

    private String consentStore;
    private String consentShare;
    private String auditInfo;

    private String hriToken;
    private String hriEmergencyToken;


    private String hr;
    private String ips;
    private String lr;
    private String prescription;
    private String metadata;

    private JSONObject qrCodeContent = new JSONObject();

    private JSONArray bucketsList = new JSONArray();
    private JSONArray objectList = new JSONArray();

    private String symmetricKey;
    private String baseUrl = SEHRCloudClient.getBaseUrl();
    private String citizenId;

    public Account() { this("","","", "", "", "", "", ""); }

    public Account(String username, String password, String token, String emergencyToken, String consentStore, String consentShare, String consentShareIsAccepted, String consentStoreIsAccepted) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.emergencyToken = emergencyToken;
        this.consentStore = consentStore;
        this.consentShare = consentShare;
        this.consentShareIsAccepted = consentShareIsAccepted;
        this.consentStoreIsAccepted = consentStoreIsAccepted;
    }

    public static Account Account(){
        if (singleAccountInstance == null){
            singleAccountInstance = new Account();
        }

        return singleAccountInstance;
    }

    public String getHriToken() {
        return hriToken;
    }

    public void setHriToken(String hriToken) {
        this.hriToken = hriToken;
    }

    public String getHriEmergencyToken() {
        return hriEmergencyToken;
    }

    public void setHriEmergencyToken(String hriEmergencyToken) {
        this.hriEmergencyToken = hriEmergencyToken;
    }

    public String getEmergencyToken() {
        return emergencyToken;
    }

    public void setEmergencyToken(String emergencyToken) {
        this.emergencyToken = emergencyToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConsentStoreIsAccepted() {
        return consentStoreIsAccepted;
    }

    public void setConsentStoreIsAccepted(String consentStoreIsAccepted) {
        this.consentStoreIsAccepted = consentStoreIsAccepted;
    }

    public String getConsentShareIsAccepted() {
        return consentShareIsAccepted;
    }

    public void setConsentShareIsAccepted(String consentShareIsAccepted) {
        this.consentShareIsAccepted = consentShareIsAccepted;
    }


    public Boolean isConsentStoreAccepted() {
        return consentStoreAccepted;
    }

    public void setConsentStoreAccepted(Boolean consentStoreAccepted) {
        this.consentStoreAccepted = consentStoreAccepted;
    }

    public Boolean isConsentShareAccepted() {
        return consentShareAccepted;
    }

    public void setConsentShareAccepted(Boolean consentShareAccepted) {
        this.consentShareAccepted = consentShareAccepted;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getConsentStore() {
        return consentStore;
    }

    public void setConsentStore(String consentStore) {
        this.consentStore = consentStore;
    }

    public String getConsentShare() {
        return consentShare;
    }

    public void setConsentShare(String consentShare) {
        this.consentShare = consentShare;
    }

    public String getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(String auditInfo) {
        this.auditInfo = auditInfo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHR() {
        return hr;
    }

    public void setHR(String hr) {
        this.hr = hr;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getIPS() {
        return ips;
    }

    public void setIPS(String ips) {
        this.ips = ips;
    }

    public String getLR() {
        return lr;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setLR(String lr) {
        this.lr = lr;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getCloudUrl() {
        return baseUrl;
    }

    public void setCloudUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public void generateSymmetricKey() throws GeneralSecurityException, IOException {
        setSymmetricKey(encryptedCommunication.generateSymmtericKey());
    }

    public JSONArray getBucketsList() {
        return bucketsList;
    }

    public void setBucketsList(JSONArray bucketsList) {
        this.bucketsList = bucketsList;
    }

    public JSONArray getObjectList() {
        return objectList;
    }

    public void setObjectList(JSONArray objectList) {
        this.objectList = objectList;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public boolean setQRCOdeContent() throws JSONException {
        qrCodeContent.put("baseUrl", baseUrl);
        qrCodeContent.put("token", getToken());
        qrCodeContent.put("symKey", getSymmetricKey());

        if ((getToken() == null) || (getSymmetricKey() == null )){
            return false;
        }
        return true;
    }

    public Bitmap retrieveQRCode() throws JSONException, WriterException {
        boolean qrCodeContentIsCreated = setQRCOdeContent();
        if (qrCodeContentIsCreated) {
            JSONObject qrCodeContent = getQrCodeContent();

            QRGEncoder qrgEncoder = new QRGEncoder( qrCodeContent.toString(),
                                                    null,
                                                    QRGContents.Type.TEXT,
                                                    250);

            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            return bitmap;
        } else
            return null;
    }

    public JSONObject getQrCodeContent(){
        return qrCodeContent;
    }
}
