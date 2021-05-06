package pl.kostrzynski.twofactorauthentication.runnable;

import android.content.Context;
import pl.kostrzynski.twofactorauthentication.apiInterface.RequestApi;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;
import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import pl.kostrzynski.twofactorauthentication.model.SecondAuthDto;
import pl.kostrzynski.twofactorauthentication.model.SmartphoneDetails;
import pl.kostrzynski.twofactorauthentication.service.ECCService;
import pl.kostrzynski.twofactorauthentication.service.HttpRequestService;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class PutPublicKeyRunnable implements Runnable {

    private final SmartphoneDetails smartphoneDetails;
    private final QRPayload qrPayload;
    private final Context context;

    public PutPublicKeyRunnable(SmartphoneDetails smartphoneDetails, QRPayload qrPayload, Context context) {
        this.smartphoneDetails = smartphoneDetails;
        this.qrPayload = qrPayload;
        this.context = context;
    }

    @Override
    public void run() {
        sendPutRequest(context, qrPayload.getJwtToken(), smartphoneDetails);
    }

    private void sendPutRequest(Context context, String jwtToken, SmartphoneDetails smartphoneDetails) {
        try {
            sendPut(context, jwtToken, smartphoneDetails);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private void sendPut(Context context, String jwtToken, SmartphoneDetails smartphoneDetails)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        HttpRequestService httpRequestService = new HttpRequestService();
        Retrofit retrofit = httpRequestService.getRetrofit();
        RequestApi requestApi = retrofit.create(RequestApi.class);
        jwtToken = getBearerToken(jwtToken);
        Call<Void> requestCall = requestApi.requestUpdateSecondAuth(jwtToken);
        if (HttpRequestService.requestUpdate(requestCall)) {
            ECCService eccService = new ECCService();
            byte[] keyBytes = eccService.generateKeyPair().getEncoded();
            Call<Void> updateCall = requestApi.updateSecondAuth(jwtToken,
                    new SecondAuthDto(new SecondAuth(keyBytes), smartphoneDetails));
            HttpRequestService.executeCreateUpdate(context, updateCall,
                    "Public key updated successfully",
                    "Unexpected error occurred. Please contact our service");
        }
    }

    private String getBearerToken(String jwtToken) {
        return "Bearer " + jwtToken;
    }
}
