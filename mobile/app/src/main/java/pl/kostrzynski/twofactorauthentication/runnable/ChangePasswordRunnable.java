package pl.kostrzynski.twofactorauthentication.runnable;

import android.content.Context;
import pl.kostrzynski.twofactorauthentication.apiInterface.RequestApi;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;
import pl.kostrzynski.twofactorauthentication.service.HttpRequestService;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ChangePasswordRunnable implements Runnable {

    private final Context context;
    private final String password;
    private final String signature;
    private final QRPayload qrPayload;

    public ChangePasswordRunnable(Context context, String password, String signature, QRPayload qrPayload) {
        this.context = context;
        this.password = password;
        this.signature = signature;
        this.qrPayload = qrPayload;
    }

    @Override
    public void run() {
        changePasswordRequest(context, password, signature, qrPayload);
    }

    private void changePasswordRequest(Context context, String password, String signature, QRPayload qrPayload) {
        HttpRequestService httpRequestService = new HttpRequestService();
        Retrofit retrofit = httpRequestService.getRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);
        Call<Void> call = requestApi.changePassword(
                httpRequestService.getBearerToken(qrPayload.getJwtToken()),
                password,
                signature);
        HttpRequestService.executeVerificationAndChangePasswordCall(context, call,
                "Password changed successfully, you can now authenticate with new credentials",
                "Could not change password, try again");
    }
}
