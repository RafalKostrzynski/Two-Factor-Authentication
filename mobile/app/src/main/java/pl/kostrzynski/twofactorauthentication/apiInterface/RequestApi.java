package pl.kostrzynski.twofactorauthentication.apiInterface;

import pl.kostrzynski.twofactorauthentication.model.SecondAuthDto;
import retrofit2.Call;
import retrofit2.http.*;

public interface RequestApi {

    @GET("for-user/check-key-gen/{token}")
    Call<Void> checkGenerateKeyPossibility(@Path("token") String token);

    @POST("second-auth/{token}")
    Call<Void> createSecondAuth(@Path("token") String token, @Body SecondAuthDto secondAuth);

    @PUT("for-user/pub-key/{token}")
    Call<Void> updateSecondAuth(@Path("token") String token, @Body SecondAuthDto secondAuth);
}
