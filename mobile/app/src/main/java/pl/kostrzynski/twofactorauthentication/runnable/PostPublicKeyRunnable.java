package pl.kostrzynski.twofactorauthentication.runnable;

import android.content.Context;
import android.widget.Toast;
import pl.kostrzynski.twofactorauthentication.apInterface.RequestApi;
import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class PostPublicKeyRunnable implements Runnable {

    private final String token;
    private String androidID;
    private final byte[] publicKeyBytes;
    private final Context context;
    // TODO check url
    private final String URL = "https://192.168.178.119:8080/tfa/service/rest/v1/";

    public PostPublicKeyRunnable(String token, byte[] publicKeyBytes, Context context) {
        this.token = token;
        this.publicKeyBytes = publicKeyBytes;
        this.context = context;
    }

    public PostPublicKeyRunnable(String token, String androidID, byte[] publicKeyBytes, Context context) {
        this.token = token;
        this.androidID = androidID;
        this.publicKeyBytes = publicKeyBytes;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            if (androidID == null) sendPostRequest(context, publicKeyBytes, token);
            else sendPostRequest(context, publicKeyBytes, androidID, token);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Error occurred while executing post method");
        }
    }

    private void sendPostRequest(Context context, byte[] publicKeyBytes, String token) throws IOException {
        SecondAuth secondAuth = new SecondAuth(publicKeyBytes);
        sendPost(context, secondAuth, token);
    }

    private void sendPostRequest(Context context, byte[] publicKeyBytes, String androidId, String token) throws IOException {
        SecondAuth secondAuth = new SecondAuth(publicKeyBytes, androidId);
        sendPost(context, secondAuth, token);
    }

    private void sendPost(Context context, SecondAuth secondAuth, String token) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestApi requestApi = retrofit.create(RequestApi.class);

        Call<Object> call = requestApi.createSecondAuth(token, secondAuth);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.isSuccessful()) Toast.makeText(context, "Public key stored successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "Public key could not be stored", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(context, "Unexpected error occurred, try again later", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
