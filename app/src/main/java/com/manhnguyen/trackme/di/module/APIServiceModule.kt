package com.manhnguyen.trackme.di.module

import android.content.Context
import com.manhnguyen.trackme.BuildConfig
import com.manhnguyen.trackme.common.Configs
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.data.api.ApiInterface
import com.manhnguyen.trackme.util.JsonUtil.Companion.instance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.*

@Module()
class APIServiceModule() {

    @Provides
    @Singleton
    fun aipService(): ApiInterface {
        val okHttpClient: OkHttpClient = headersInjectedHTTPClient
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    instance.gson
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(siteInfoUrl)
            .client(okHttpClient)
            .build().create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun retrofitInstance(): Retrofit {
        val okHttpClient: OkHttpClient = headersInjectedHTTPClient
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    instance.gson
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(siteInfoUrl)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun retrofitNoLog(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(instance.gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(siteInfoUrl)
            .build()
    }


    /**
     * @return HTTP client with headers injected
     */
    private val headersInjectedHTTPClient: OkHttpClient
        get() {
            var builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder = injectRequestHeadersBuilder(builder)
            if (BuildConfig.DEBUG) {
                builder = injectLoggingBuilder(builder)
            }
            builder = injectUnsafeBuilder(builder)
            builder.interceptors().add(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    return onOnIntercept(chain)
                }
            })
            return builder.build()
        }

    @Throws(IOException::class)
    private fun onOnIntercept(chain: Interceptor.Chain): Response {
        try {
            val response: Response = chain.proceed(chain.request())
            return response
        } catch (exception: SocketTimeoutException) {
            exception.printStackTrace()
        }
        return chain.proceed(chain.request())
    }

    private fun injectRequestHeadersBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                return chain.proceed(request)
            }
        })
        return builder
    }

    private fun injectLoggingBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(loggingInterceptor)
        return builder
    }

    private val unsafeHostnameVerifier: HostnameVerifier
        get() {
            return object : HostnameVerifier {
                override fun verify(
                    hostname: String,
                    session: SSLSession
                ): Boolean {
                    return true
                }
            }
        }

    private fun injectUnsafeBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.hostnameVerifier(unsafeHostnameVerifier)
        val unsafeTrustManager: X509TrustManager =
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    x509Certificates: Array<X509Certificate>,
                    s: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            }
        val trustAllCerts: Array<TrustManager> =
            arrayOf(unsafeTrustManager)
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        try {
            assert(sslContext != null)
            sslContext!!.init(null, trustAllCerts, SecureRandom())
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext!!.socketFactory
        builder.sslSocketFactory(sslSocketFactory, unsafeTrustManager)
        return builder
    }

    private val siteInfoUrl: String
        get() {
            return ""
        }




}