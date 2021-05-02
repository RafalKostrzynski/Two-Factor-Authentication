package pl.kostrzynski.twofactorauthentication.runnable;

import android.content.Context;
import pl.kostrzynski.twofactorauthentication.apiInterface.RequestApi;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;
import pl.kostrzynski.twofactorauthentication.service.HttpRequestService;
import retrofit2.Call;
import retrofit2.Retrofit;

public class PostSignedMessageRunnable implements Runnable {

    private final Context context;
    private final QRPayload qrPayload;
    private final String signature;

    public PostSignedMessageRunnable(Context context, QRPayload qrPayload, String signature) {
        this.context = context;
        this.qrPayload = qrPayload;
        this.signature = signature;
    }

    @Override
    public void run() {
        sendPost(context, qrPayload.getJwtToken(), signature);
    }

    private void sendPost(Context context, String jwtToken, String signature) {
        HttpRequestService httpRequestService = new HttpRequestService();
        Retrofit retrofit = httpRequestService.getRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);

        Call<Void> call = requestApi.verifyPayload(getBearerToken(jwtToken), signature);
        HttpRequestService.executeVerificationCall(context, call,
                "Verified successfully, you will be authenticated in a few seconds",
                "Could not verify please try again");
    }

    private String getBearerToken(String jwtToken) {
        return "Bearer " + jwtToken;
    }
}
