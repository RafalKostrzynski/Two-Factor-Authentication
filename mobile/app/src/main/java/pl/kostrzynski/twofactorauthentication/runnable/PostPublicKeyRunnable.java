package pl.kostrzynski.twofactorauthentication.runnable;

import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import pl.kostrzynski.twofactorauthentication.apInterface.RequestApi;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class PostPublicKeyRunnable implements Runnable{

    private final String token;
    private String androidID;
    private final byte[] publicKeyBytes;
    // TODO check url
    private final String URL = "http://192.168.178.119:8080/tfa/service/rest/v1/";

    public PostPublicKeyRunnable(String token, byte[] publicKeyBytes) {
        this.token = token;
        this.publicKeyBytes = publicKeyBytes;
    }

    public PostPublicKeyRunnable(String token, String androidID, byte[] publicKeyBytes) {
        this.token = token;
        this.androidID = androidID;
        this.publicKeyBytes = publicKeyBytes;
    }

    @Override
    public void run() {
        try {
            if (androidID == null) sendPostRequest(publicKeyBytes, token);
            else sendPostRequest(publicKeyBytes, androidID, token);
        }catch (IOException exception){
            // TODO something like return or other user communication
        }
    }

    private void sendPostRequest(byte[] publicKeyBytes, String token) throws IOException {
        SecondAuth secondAuth = new SecondAuth(publicKeyBytes);
        sendPost(secondAuth, token);
    }

    private void sendPostRequest(byte[] publicKeyBytes,String androidId, String token) throws IOException {
        SecondAuth secondAuth = new SecondAuth(publicKeyBytes, androidId);
        sendPost(secondAuth, token);
    }

    private void sendPost(SecondAuth secondAuth, String token) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestApi requestApi = retrofit.create(RequestApi.class);

        Call<Object> call = requestApi.createSecondAuth(token, secondAuth);
        Response<Object> response = call.execute();

        if(!response.isSuccessful()) throw new IllegalArgumentException(response.message());
    }

}
