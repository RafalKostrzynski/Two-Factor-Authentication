package pl.kostrzynski.twofactorauthentication.runnable;

import android.content.Context;
import pl.kostrzynski.twofactorauthentication.apiInterface.RequestApi;
import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import pl.kostrzynski.twofactorauthentication.model.SecondAuthDto;
import pl.kostrzynski.twofactorauthentication.model.SmartphoneDetails;
import pl.kostrzynski.twofactorauthentication.service.HttpRequestService;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.IOException;

public class PostPublicKeyRunnable implements Runnable {

    private final String token;
    private final String androidID;
    private final byte[] publicKeyBytes;
    private final Context context;

    public PostPublicKeyRunnable(String token, String androidID, byte[] publicKeyBytes, Context context) {
        this.token = token;
        this.androidID = androidID;
        this.publicKeyBytes = publicKeyBytes;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            sendPostRequest(context, publicKeyBytes, androidID, token);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Error occurred while executing post method");
        }
    }

    private void sendPostRequest(Context context, byte[] publicKeyBytes, String androidId, String token) throws IOException {
        SecondAuthDto secondAuthDto = new SecondAuthDto(new SecondAuth(publicKeyBytes), new SmartphoneDetails(androidId));
        sendPost(context, secondAuthDto, token);
    }

    private void sendPost(Context context, SecondAuthDto secondAuthDto, String token) {
        HttpRequestService httpRequestService = new HttpRequestService();
        Retrofit retrofit = httpRequestService.getRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);

        Call<Void> call = requestApi.createSecondAuth(token, secondAuthDto);
        HttpRequestService.executeCreateUpdate(context, call,
                "Public key stored successfully", "Public key could not be stored");
    }
}
