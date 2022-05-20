package com.eu.interopehrate.mr2dbackup;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface SEHRCloudInterface {

//    @GET("/citizen/login")
    @GET
    Call<Account> login(@Url String url,
                        @Query("username") String username,
                        @Query("password") String password);


//    Call<Account> login(@Query("username") String username,
//                        @Query("password") String password);

//    @GET("/citizen/register")
    @GET
    Call<Account> register(@Url String url,
                           @Query("username") String username,
                           @Query("password") String password);

//    @GET("/citizen/buckets")
    @GET
    Call<String> listBuckets(@Url String url,
                             @Query("token") String token);

//    @GET("/citizen/buckets/{bucket}")
    @GET
    Call<String> listObjects(@Url String url,
                             @Query("token") String token);

//    @GET("/citizen/{bucket}/{object}/metadata")
    @GET
    Call<String> getMetadata(@Url String url,
                             @Query("token") String token);

//    @GET("/citizen/auditing")
    @GET
    Call<String> getAuditInfo(@Url String url,
                              @Query("token") String token);

//    @GET("/citizen/consent/download/store")
    @Streaming
    @GET
    Call<String> downloadConsentStore(@Url String url,
                                      @Query("token") String token);

//    @GET("/citizen/consent/download/share")
    @Streaming
    @GET
    Call<String> downloadConsentShare(@Url String url,
                                      @Query("token") String token);

//    @POST("/citizen/consent/withdraw/share")
    @POST
    Call<String> withdrawConsentShare(@Url String url,
                                      @Query("token") String token);

//    @POST("/citizen/consent/upload/store")
    @Multipart
    @POST
    Call<Account> uploadConsentStore(@Url String url,
                                     @Query("token") String token,
                                     @Part("consent_store") RequestBody signature);

//    @POST("/citizen/consent/upload/share")
    @Multipart
    @POST
    Call<Account> uploadConsentShare(@Url String url,
                                     @Query("token") String token,
                                     @Part("consent_share") RequestBody consent);
    
//    @GET("/citizen/removeaccount")
    @GET
    Call<Account> removeAccount(@Url String url,
                                @Query("token") String token);

//    @POST("/citizen/upload/hr")
    @Multipart
    @POST
    Call<Account> uploadHR(@Url String url,
                           @Query("token") String token,
                           @Query("metadata") String metadata,
                           @Part("hr_file") RequestBody hrFile);

    //    @GET("/citizen/{bucket}/{object}")
    @Streaming
    @GET
    Call<String> downloadHR(@Url String url,
                            @Query("token") String token);

//    @GET("/citizen/consent/download/storeisset")
//    Call<Account> checkConsentStore(@Query("token") String token);
//
//    @GET("/citizen/consent/download/shareisset")
//    Call<Account> checkConsentShare(@Query("token") String token);
    //    @Multipart
//    @POST("/citizen/upload/medicalimage")
//    Call<Account> uploadMedicalImage(@Query("token") String token,
//                                     @Query("metadata") String metadata,
//                                     @Part("medical_image") RequestBody medicalImage);
//
//    @Multipart
//    @POST("/citizen/upload/ips")
//    Call<Account> uploadIPS(@Query("token") String token,
//                            @Query("metadata") String metadata,
//                            @Part("ips") RequestBody ips);
//
//    @Multipart
//    @POST("/citizen/upload/lr")
//    Call<Account> uploadLR(@Query("token") String token,
//                           @Query("metadata") String metadata,
//                           @Part("lr") RequestBody lr);
//
//    @Multipart
//    @POST("/citizen/upload/prescription")
//    Call<Account> uploadPrescription(@Query("token") String token,
//                                     @Query("metadata") String metadata,
//                                     @Part("prescription") RequestBody prescription);

//    @Streaming
//    @GET("/citizen/download/ips")
//    Call<String> downloadIPS(@Query("token") String token);
//
//    @Streaming
//    @GET("/citizen/download/lr")
//    Call<String> downloadLR(@Query("token") String token);
//
//    @Streaming
//    @GET("/citizen/download/prescription")
//    Call<String> downloadPrescription(@Query("token") String token);
}
