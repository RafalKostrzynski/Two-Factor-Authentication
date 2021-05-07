package pl.kostrzynski.twofactorauthentication.apiInterface;

import pl.kostrzynski.twofactorauthentication.model.SecondAuthDto;
import retrofit2.Call;
import retrofit2.http.*;

public interface RequestApi {

    @POST("second-auth/{token}")
    Call<Void> createSecondAuth(@Path("token") String token, @Body SecondAuthDto secondAuth);

    @GET("for-user/pub-key/update/request")
    Call<Void> requestUpdateSecondAuth(@Header("Authorization") String jwtToken);

    @PUT("for-user/pub-key/update")
    Call<Void> updateSecondAuth(@Header("Authorization") String jwtToken,
                                @Body SecondAuthDto secondAuthDto);

    @POST("second-auth/verify")
    Call<Void> verifyPayload(@Header("Authorization") String jwtToken, @Body String signature);

    @POST("second-auth/change-password")
    Call<Void> changePassword(@Header("Authorization") String jwtToken,
                              @Query("password") String password,
                              @Body String signature);
}
