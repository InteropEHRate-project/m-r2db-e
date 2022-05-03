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

    @GET
    Call<Account> login(@Url String url,
                        @Query("username") String username,
                        @Query("password") String password);

    @GET
    Call<Account> register(@Url String url,
                           @Query("username") String username,
                           @Query("password") String password);

    @GET
    Call<String> listBuckets(@Url String url,
                             @Query("token") String token);

    @GET
    Call<String> listObjects(@Url String url,
                             @Query("token") String token);

    @GET
    Call<String> getMetadata(@Url String url,
                             @Query("token") String token);

    @GET
    Call<String> getAuditInfo(@Url String url,
                              @Query("token") String token);

    @Streaming
    @GET
    Call<String> downloadConsentStore(@Url String url,
                                      @Query("token") String token);


    @Streaming
    @GET
    Call<String> downloadConsentShare(@Url String url,
                                      @Query("token") String token);

    @POST
    Call<String> withdrawConsentShare(@Url String url,
                                      @Query("token") String token);

    @Multipart
    @POST
    Call<Account> uploadConsentStore(@Url String url,
                                     @Query("token") String token,
                                     @Part("consent_store") RequestBody signature);

    @Multipart
    @POST
    Call<Account> uploadConsentShare(@Url String url,
                                     @Query("token") String token,
                                     @Part("consent_share") RequestBody consent);
    
    @GET
    Call<Account> removeAccount(@Url String url,
                                @Query("token") String token);

    @Multipart
    @POST
    Call<Account> uploadHR(@Url String url,
                           @Query("token") String token,
                           @Query("metadata") String metadata,
                           @Part("hr_file") RequestBody hrFile);

    @Streaming
    @GET
    Call<String> downloadHR(@Url String url,
                            @Query("token") String token);
}
