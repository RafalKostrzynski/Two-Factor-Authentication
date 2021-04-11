package pl.kostrzynski.twofactorauthentication.apInterface;

import pl.kostrzynski.twofactorauthentication.model.SecondAuth;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Path;

public interface RequestApi {

    @POST("second-auth/{token}")
    Call<Void> createSecondAuth(@Path("token") String token, @Body SecondAuth secondAuth);
}
