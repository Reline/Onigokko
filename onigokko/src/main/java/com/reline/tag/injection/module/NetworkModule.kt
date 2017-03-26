package com.reline.tag.injection.module

import com.reline.tag.network.HelloService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import retrofit2.Retrofit
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.inject.Singleton
import javax.net.ssl.*

@Module
@Singleton
class NetworkModule {

    val BASE_URL = "https://projectplay.xyz:8443"
    val CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFAjCCA+qgAwIBAgISA3E0JcOWjqS23hx9LQgb9TftMA0GCSqGSIb3DQEBCwUA\n" +
            "MEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD\n" +
            "ExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xNzAyMTMwNjMwMDBaFw0x\n" +
            "NzA1MTQwNjMwMDBaMBoxGDAWBgNVBAMTD3Byb2plY3RwbGF5Lnh5ejCCASIwDQYJ\n" +
            "KoZIhvcNAQEBBQADggEPADCCAQoCggEBAMPtOdYrt1ftt7PEbD7h/q6uNzNKiY5N\n" +
            "96KtYRiSWiriLGkuezGBeIKq9X98GDJiO4nqSWOaUSDT/CZ2BAe33AC9OT++i4go\n" +
            "gjXBQuDqVt99cxeER05WuKgdVemGt+cMQsjrn/Kv5ME5pdvavO6Xe3SvuzKTkvtO\n" +
            "HSfw8CgWTf7bHzU+LImrW2CVkY1tAnKcIKKYzMZt55JnDwS6aH1CFfRevB+Rrs0/\n" +
            "ETs1fBVo8DCxpPs3jRctFGo0bsJyCIT0x/yrjCd+i7cAUtDtaMtIadubD5IVYNl9\n" +
            "egUTH0oKRiyIltFprdbqnnx0xB6cptOBm3kf5ydCu8d9Y7mTuWgPUvUCAwEAAaOC\n" +
            "AhAwggIMMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYB\n" +
            "BQUHAwIwDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQUTyQiyP2d9xrmlafotS7GlOxb\n" +
            "CwQwHwYDVR0jBBgwFoAUqEpqYwR93brm0Tm3pkVl7/Oo7KEwcAYIKwYBBQUHAQEE\n" +
            "ZDBiMC8GCCsGAQUFBzABhiNodHRwOi8vb2NzcC5pbnQteDMubGV0c2VuY3J5cHQu\n" +
            "b3JnLzAvBggrBgEFBQcwAoYjaHR0cDovL2NlcnQuaW50LXgzLmxldHNlbmNyeXB0\n" +
            "Lm9yZy8wGgYDVR0RBBMwEYIPcHJvamVjdHBsYXkueHl6MIH+BgNVHSAEgfYwgfMw\n" +
            "CAYGZ4EMAQIBMIHmBgsrBgEEAYLfEwEBATCB1jAmBggrBgEFBQcCARYaaHR0cDov\n" +
            "L2Nwcy5sZXRzZW5jcnlwdC5vcmcwgasGCCsGAQUFBwICMIGeDIGbVGhpcyBDZXJ0\n" +
            "aWZpY2F0ZSBtYXkgb25seSBiZSByZWxpZWQgdXBvbiBieSBSZWx5aW5nIFBhcnRp\n" +
            "ZXMgYW5kIG9ubHkgaW4gYWNjb3JkYW5jZSB3aXRoIHRoZSBDZXJ0aWZpY2F0ZSBQ\n" +
            "b2xpY3kgZm91bmQgYXQgaHR0cHM6Ly9sZXRzZW5jcnlwdC5vcmcvcmVwb3NpdG9y\n" +
            "eS8wDQYJKoZIhvcNAQELBQADggEBAE7MtkPu4cQvfgYDyWbjdIY1Vj/xdLLA5gS0\n" +
            "fZh2lAXCCb5Kz/DabAQUsfB2C646hIXocbo/at5Zv9iufNf48vsXDrNB4Gh94Dbp\n" +
            "9VYRq2KjpLMRId74dBRTqhr3V23563dKa9kDIZnFs4o/rJIvaYUfk+V/+tFnKMUb\n" +
            "zUuu72CRyMnPU8fFCwnNGULRhN5T2eVxDfFOC4B6QC4DEaAdLUul85wU2DZUevQU\n" +
            "pZ/GgvGrqV2LwIwQqhHjxdTuXy+Aj+O8diSNnc1MC6dlaL4sAH045grZmSAvUE0r\n" +
            "TblqGV5FUCqjdj0AIZl6tVbfHZ6JvQcG90UWVFn3pYlleay3ku4=\n" +
            "-----END CERTIFICATE-----"

    @Provides
    @Singleton
    fun provideTrustManager(): X509TrustManager {
        val inputStream = Buffer().writeUtf8(CERT).inputStream()
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(inputStream)
        if (certificates.isEmpty()) {
            throw IllegalArgumentException("expected non-empty set of trusted certificates")
        }

        // Put the certificate in a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        for ((index, certificate) in certificates.withIndex()) {
            val certificateAlias = Integer.toString(index)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val inputStream: InputStream? = null // By convention, 'null' creates an empty key store.
            keyStore.load(inputStream, password)
            return keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    @Provides
    @Singleton
    fun provideSSLSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), null)
        return sslContext.socketFactory
    }

    @Provides
    @Singleton
    fun provideInterceptor() = Interceptor { chain ->
        val request = chain.request().newBuilder()
                .header("Token", "") // todo: get token
                .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager, interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .addInterceptor(interceptor)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideHelloService(retrofit: Retrofit): HelloService {
        return retrofit.create(HelloService::class.java)
    }
}
