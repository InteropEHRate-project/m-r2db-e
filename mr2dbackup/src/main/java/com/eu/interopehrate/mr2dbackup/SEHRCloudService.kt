package com.eu.interopehrate.mr2dbackup
//package com.andaman7.sehrlibrary.service
//
//import com.google.gson.annotations.SerializedName
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import retrofit2.http.*
//
//interface SEHRCloudService {
//
//    @GET("citizen/register")
//    suspend fun register(@Query("username") username: String, @Query("password") password: String) : SEHRLoginResponse
//
//    @GET("citizen/login")
//    suspend fun login(@Query("username") username: String, @Query("password") password: String) : SEHRLoginResponse
//
//    @Multipart
//    @POST("citizen/upload/ips")
//    suspend fun uploadIPS(@Query("token") token: String, @Query("metadata") metaData: String, @Part("ips") body : RequestBody): SEHRUploadResponse
//
//    @GET("citizen/removeaccount")
//    suspend fun removeAccount(@Query("token") token: String) : SEHRRemoveAccountResponse
//
//    data class SEHRLoginResponse (
//        val token: String?,
//        val msg: String?
//    )
//
//    data class SEHRRemoveAccountResponse (
//            val msg: String?
//    )
//
//    data class SEHRUploadResponse (
//            @SerializedName("md") val metaData: List<SEHRMetaDataUpload>,
//            @SerializedName("name") val fileName: String?,
//            @SerializedName("msg") val message: String?,
//            @SerializedName("errorCode") val errorCode: Int?
//    )
//
//    data class SEHRMetaDataUpload (
//            @SerializedName("file-type") val fileType: String?,
//            @SerializedName("hr-type")val hrType: String?
//    )
//
//}