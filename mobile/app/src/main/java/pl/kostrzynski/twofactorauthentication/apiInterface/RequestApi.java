package pl.kostrzynski.twofactorauthentication.apiInterface;

import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import retrofit2.Call;
import retrofit2.http.*;

public interface RequestApi {

    @GET("second-auth/check-key-gen/{token}")
    Call<Void> checkGenerateKeyPossibility(@Path("token") String token);

    @POST("second-auth/{token}")
    Call<Void> createSecondAuth(@Path("token") String token, @Body SecondAuth secondAuth);

    @PUT("second-auth/{token}")
    Call<Void> updateSecondAuth(@Path("token") String token, @Body SecondAuth secondAuth);
}
