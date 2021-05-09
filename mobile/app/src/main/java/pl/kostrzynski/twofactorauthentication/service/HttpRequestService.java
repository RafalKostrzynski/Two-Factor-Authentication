package pl.kostrzynski.twofactorauthentication.service;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.OkHttpClient;
import okhttp3.tls.HandshakeCertificates;
import org.jetbrains.annotations.NotNull;
import pl.kostrzynski.twofactorauthentication.R;
import pl.kostrzynski.twofactorauthentication.model.utility.Resources;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpRequestService {

    private final String BASE_URL = "https://" + Resources.getHostAddress() + ":8443/tfa/service/rest/v1/";

    // this certificate is needed because the website's certificate is self-signed
    private final X509Certificate certificatePem = Resources.getCertificate();

    public static void executeCreateUpdate(Context context, Call<Void> call,
                                           String successfulMessage, String failureMessage) {
        ECCService eccService = new ECCService();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, successfulMessage, Toast.LENGTH_SHORT).show();
                    setTextField();
                } else {
                    Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
                    keyReset(eccService, context);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                keyReset(eccService, context);
                Toast.makeText(context, "Unexpected error occurred, try again later", Toast.LENGTH_SHORT).show();
            }

            private void setTextField() {
                Activity activity = (Activity) context;
                TextView keyNameTextView = activity.findViewById(R.id.privateKeyName);
                PreferenceService preferenceService = new PreferenceService();
                keyNameTextView.setText(preferenceService.generateAndSaveAdjectiveToSharedPreferences(context));
            }
        });
    }

    public static void executeVerificationAndChangePasswordCall(Context context, Call<Void> call,
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

    public static boolean requestUpdate(Call<Void> call) {
        try {
            Response<Void> response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }


    private static void keyReset(ECCService eccService, Context context) {
        try {
            eccService.deleteKey();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            Toast.makeText(context, "Unknown exception please contact our service", Toast.LENGTH_SHORT).show();
        }
    }

    @NotNull
    public Retrofit getRetrofit() {
        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(certificatePem)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                // the hostname should be an authentic website host this is only for testing purposes
                .hostnameVerifier((hostname, session) -> hostname.equals(Resources.getHostAddress()))
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public String getBearerToken(String jwtToken) {
        return "Bearer " + jwtToken;
    }
}
