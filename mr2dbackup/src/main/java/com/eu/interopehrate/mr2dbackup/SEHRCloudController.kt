package com.eu.interopehrate.mr2dbackup
//package com.andaman7.sehrlibrary.controller
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import com.andaman7.sehrlibrary.service.AppPreferencesService
//import com.andaman7.sehrlibrary.service.SEHRCloudService
//import com.google.gson.Gson
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.WriterException
//import com.google.zxing.qrcode.QRCodeWriter
//import eu.interopehrate.encryptedcomunication.EncryptedCommunicationFactory
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.future.future
//import okhttp3.HttpUrl
//import okhttp3.MediaType
//import okhttp3.OkHttpClient
//import okhttp3.RequestBody
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.IOException
//import java.util.concurrent.CompletableFuture
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//@Singleton
//class SEHRCloudController @Inject constructor(baseUrl: HttpUrl) {
//
//    companion object {
//        const val ERROR_TOKEN_INVALID = 403
//        const val ERROR_ENCRYPTION_INVALID = 420
//        const val ERROR_DATA_INVALID = 420
//    }
//
//    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
//            .connectTimeout(5, TimeUnit.SECONDS)
//            .writeTimeout(5, TimeUnit.SECONDS)
//            .readTimeout(5, TimeUnit.SECONDS)
//            .build()
//
//    val retrofit : Retrofit = Retrofit.Builder().client(okHttpClient)
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    val service = retrofit.create(SEHRCloudService::class.java)
//
//    @Throws(IOException::class)
//    suspend fun register(username: String, password: String) = handleRequest { service.register(username, password) }
//
//    @Throws(IOException::class)
//    suspend fun login(username: String, password: String) = handleRequest { service.login(username, password) }
//
//    @Throws(IOException::class)
//    suspend fun removeAccount(token: String) = handleRequest { service.removeAccount(token) }
//
////    public suspend fun uploadIPS(token: String, ips: String) = service.uploadIPS(token, ips)
//
//    @Throws(IOException::class)
//    suspend fun uploadIPS(token: String, ips: String): SEHRCloudService.SEHRUploadResponse {
//
//        //meta data
//        val metaData: SEHRCloudService.SEHRMetaDataUpload = SEHRCloudService.SEHRMetaDataUpload("txt", "ips")
//        val metaDataString = Gson().toJson(metaData)
//
//        val data = encryptData(ips)
//
//        if (data.isBlank())
//            return SEHRCloudService.SEHRUploadResponse(listOf(), null, "error encrypted data", ERROR_ENCRYPTION_INVALID)
//
//        val body: RequestBody = RequestBody.create(MediaType.parse("text/plain"), data)
//
//        val handleRequest = handleRequest { service.uploadIPS(token, metaDataString, body) }
//
//        return handleRequest.getOrDefault(SEHRCloudService.SEHRUploadResponse(listOf(), null, "error with data", ERROR_DATA_INVALID))
//    }
//
//    fun generateSymKey(): String {
//        val generateSymmtericKey = EncryptedCommunicationFactory.create().generateSymmtericKey()
////        AppPreferencesService.symKey = generateSymmtericKey
//        AppPreferencesService.symKey = "UiYmJk/iWopGS2n0YIhZsgp1auRVyahR7sgFOtEC5r4="
//        return generateSymmtericKey
//    }
//
//    fun encryptData(data: String): String {
//        val symKey = AppPreferencesService.symKey
//        if (AppPreferencesService.symKey.isEmpty())
//            return ""
//
//        return EncryptedCommunicationFactory.create().encrypt(data, symKey);
//    }
//
//    fun decryptData(data: String): String {
//        val symKey = AppPreferencesService.symKey
//        if (AppPreferencesService.symKey.isEmpty())
//            return ""
//
//        return EncryptedCommunicationFactory.create().decrypt(data, symKey);
//    }
//
//    suspend fun <T: Any> handleRequest(requestFunc: suspend () -> T): kotlin.Result<T> {
//        return try {
//            Result.success(requestFunc.invoke())
//        } catch (he: Exception) {
//            Result.failure(he)
//        }
//    }
//
//    fun retreiveQRCode(): Bitmap? {
//        val qrCode = createQRCodeContent()
//
//        qrCode?.let {
//            val writer = QRCodeWriter()
//            try {
//                val bitMatrix = writer.encode(Gson().toJson(it), BarcodeFormat.QR_CODE, 150, 150)
//                val width = bitMatrix.width
//                val height = bitMatrix.height
//                val bmp: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//                for (x in 0 until width) {
//                    for (y in 0 until height) {
//                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//                    }
//                }
//                return bmp
//            } catch (e: WriterException) {
//                e.printStackTrace()
//            }
//        }
//
//        return null
//    }
//
//    fun createQRCodeContent() : QrCode? {
//        val uri = retrofit.baseUrl().uri().toString()
//        val symKey = AppPreferencesService.symKey
//        val token = AppPreferencesService.token
//
//        if (token.isBlank() || symKey.isBlank())
//            return null
//
//        return QrCode(uri, symKey, token)
//    }
//
//    // for java
//    fun uploadIPSAsync(token: String, ips: String): CompletableFuture<SEHRCloudService.SEHRUploadResponse?> =
//            GlobalScope.future {
//                uploadIPS(token, ips)
//            }
//
//    data class QrCode(
//            var uri: String,
//            var symKey: String,
//            var token: String
//    )
//
//
//
//}