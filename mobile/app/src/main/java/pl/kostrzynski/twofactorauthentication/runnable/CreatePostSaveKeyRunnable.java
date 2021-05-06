package pl.kostrzynski.twofactorauthentication.runnable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import pl.kostrzynski.twofactorauthentication.service.ECCService;

import java.security.PublicKey;

public class CreatePostSaveKeyRunnable implements Runnable {

    private final String token;
    private final Context context;

    public CreatePostSaveKeyRunnable(String token, Context context) {
        this.token = token;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            ECCService eccService = new ECCService();
            PublicKey publicKey = eccService.generateKeyPair();

            // This is used because third party apps have no access to Imei number since android 10.
            @SuppressLint("HardwareIds")
            String secretAndroidId = Settings.Secure.
                    getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            byte[] publicKeyBytes = publicKey.getEncoded();

            Runnable postPublicKeyRunnable = new PostPublicKeyRunnable(token, secretAndroidId, publicKeyBytes, context);
            Thread postPublicKeyThread = new Thread(postPublicKeyRunnable);
            postPublicKeyThread.start();

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
