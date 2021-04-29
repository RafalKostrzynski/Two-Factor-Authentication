package pl.kostrzynski.twofactorauthentication.service;

import android.content.Context;
import android.widget.Toast;
import okhttp3.OkHttpClient;
import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import org.jetbrains.annotations.NotNull;
import pl.kostrzynski.twofactorauthentication.apiInterface.RequestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.security.cert.X509Certificate;

public class HttpRequestService {

    private final String BASE_URL = "https://192.168.178.119:8443/tfa/service/rest/v1/";
    // this certificate is needed because the website's certificate is self-signed
    private final X509Certificate certificatePem = Certificates.decodeCertificatePem("-----BEGIN CERTIFICATE-----\n" +
            "MIIDdTCCAl2gAwIBAgIIeiGsWQWp2t8wDQYJKoZIhvcNAQELBQAwaTELMAkGA1UE\n" +
            "BhMCREUxEDAOBgNVBAgTB0hhbWJ1cmcxEDAOBgNVBAcTB0hhbWJ1cmcxDDAKBgNV\n" +
            "BAoTAzJGQTEMMAoGA1UECxMDMkZBMRowGAYDVQQDExFSYWZhbCBLb3N0cnp5bnNr\n" +
            "aTAeFw0yMTA0MDUxMTM0MjRaFw0zMTA0MDMxMTM0MjRaMGkxCzAJBgNVBAYTAkRF\n" +
            "MRAwDgYDVQQIEwdIYW1idXJnMRAwDgYDVQQHEwdIYW1idXJnMQwwCgYDVQQKEwMy\n" +
            "RkExDDAKBgNVBAsTAzJGQTEaMBgGA1UEAxMRUmFmYWwgS29zdHJ6eW5za2kwggEi\n" +
            "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCaHdqGapQU2sz7F0OZwwTFpWaI\n" +
            "GhDSoA47cNhiKUrybeX9PZv/rjINzRH0a6jvKOoqDh+D3Gt+VMb/KTwZJsp4LJ4v\n" +
            "n/yBiRfDfi8iQqn4MvXf2GUE9oHwTMHFSYZ0zb7rR4Yi6YmVrPKLBv+R7eGuLjYb\n" +
            "xxPf5eNsbKqV15U5qSBg7obqaZ5t6WHbN23FPhv0kMlKnlyCdJCgvH4NBhH4T2Yz\n" +
            "x2clb7UKJyiHrOkNsN+/uhjYrFXZHCrYfaSxJj4hD6phStQzSEm61RhVN1vt9ew4\n" +
            "btWrqejuq5IYRtksXrRGq1bjHyUcTHVr843YU2ueaKDXTajUw+KXHcmXnUN/AgMB\n" +
            "AAGjITAfMB0GA1UdDgQWBBQBYt9SgiWzlMbdS5g4XlM40Us/ezANBgkqhkiG9w0B\n" +
            "AQsFAAOCAQEANFEPNukIgBP5+DvHiKIIHupDcUFxzYxUWd00EXZdwM6RjVK+P+EC\n" +
            "kqJpjDzXQqk3EToqtS1ojVOHouVaGy3F3agq5EJ2qhyDUd4358QemoTexuiiA5Lg\n" +
            "D/ycnp/aTVTKO5ThxbNGhA+16LDRq7RmPdbabHxfB3bxQrGT1ZDNhxxlN9VDIt0R\n" +
            "RPBfg0mbEO90v6jrimTkhISHhhOf2LrP3XMu7v4AetJtvE+w2J6FVa2VR0AyYabf\n" +
            "iCHRkfLFKUoDvHi3Q9EGBxNW8PS+/sJAtGnu53XPIRwHM0v9dQkbb0ULQL6DI2dj\n" +
            "qqh5/vZPcDkQ7AwhoTt27ym1WTUbvwCgyg==\n" +
            "-----END CERTIFICATE-----\n");

    public static void executeCreateAndUpdateCall(Context context, Call<Void> call,
                                                  String successfulMessage, String failureMessage) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    Toast.makeText(context, successfulMessage, Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
                // TODO delete key
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Unexpected error occurred, try again later", Toast.LENGTH_SHORT).show();
                // TODO delete key
            }
        });
    }

    public static void executeVerificationCall(Context context, Call<Void> call,
                                               String successfulMessage, String failureMessage) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    Toast.makeText(context, successfulMessage, Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Unexpected error occurred, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkGenerateKey(String token) throws InterruptedException {
        Retrofit retrofit = getRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);
        Call<Void> call = requestApi.checkGenerateKeyPossibility(token);
        final boolean[] result = {false};

        Thread thread = new Thread(() -> {
            try {
                Response<Void> response = call.execute();
                result[0] = response.isSuccessful();
            } catch (IOException exception) {
                result[0] = false;
            }
        });
        thread.start();
        thread.join();
        return result[0];
    }

    @NotNull
    public Retrofit getRetrofit() {
        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(certificatePem)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                // the hostname should be an authentic website host this is only for testing purposes
                .hostnameVerifier((hostname, session) -> hostname.equals("192.168.178.119"))
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
