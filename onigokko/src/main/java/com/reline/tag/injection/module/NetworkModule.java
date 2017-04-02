package com.reline.tag.injection.module;

import com.reline.tag.database.DatabaseAccessObject;
import com.reline.tag.network.PlayerService;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module(includes = DatabaseModule.class)
public class NetworkModule {

    private static final String BASE_URL = "https://projectplay.xyz:8443/onigokko/";
    private static final String CERT = "-----BEGIN CERTIFICATE-----\n" +
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
            "-----END CERTIFICATE-----";

    @Provides
    X509TrustManager provideTrustManager() {
        InputStream in = new Buffer().writeUtf8(CERT).inputStream();
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Put the certificate in a key store
            char[] password = "password".toCharArray();
            KeyStore keyStore = newEmptyKeyStore(password);
            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            // Use it to build an X509 trust manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Provides
    SSLSocketFactory provideSSLSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    Interceptor provideInterceptor(final DatabaseAccessObject dao) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .header("Token", dao.getToken())
                        .build();
                return chain.proceed(request);
            }
        };
    }

    @Provides
    OkHttpClient provideOkHttpClient(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager, Interceptor interceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }

    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Moshi moshi) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build();
    }

    @Provides
    PlayerService providePlayerService(Retrofit retrofit) {
        return retrofit.create(PlayerService.class);
    }
}
