package pl.kostrzynski.twofactorauthentication.runnable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import pl.kostrzynski.twofactorauthentication.service.ECCService;
import pl.kostrzynski.twofactorauthentication.service.FileService;
import pl.kostrzynski.twofactorauthentication.service.PreferenceService;

import java.io.File;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;

public class CreatePostSaveKeyRunnable implements Runnable {

    private final String token;
    private final Context context;
    private final boolean isPostMethod;
    private final FileService fileService = new FileService();

    public CreatePostSaveKeyRunnable(String token, Context context, boolean isPostMethod) {
        this.token = token;
        this.context = context;
        this.isPostMethod = isPostMethod;
    }

    @Override
    public void run() {
        try {
            ECCService eccService = new ECCService();
            KeyPair keyPair = eccService.generateKeyPair();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

            File privateKeyFile = fileService.saveKeysToStorage(eccService, privateKey, context);
            savePrivateKeyPathToPreferences(privateKeyFile);

            // This is used because third party apps have no access to Imei number since android 10.
            @SuppressLint("HardwareIds")
            String secretAndroidId = Settings.Secure.
                    getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

            Runnable postPublicKeyRunnable = isPostMethod ?
                    new PostPublicKeyRunnable(token, secretAndroidId, publicKeyBytes, context) :
                    new PutPublicKeyRunnable(token, secretAndroidId, publicKeyBytes, context);

            Thread postPublicKeyThread = new Thread(postPublicKeyRunnable);
            postPublicKeyThread.start();

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePrivateKeyPathToPreferences(File privateKeyFile) {
        PreferenceService preferenceService = new PreferenceService();
        preferenceService.savePathToSharedPreferences(context, privateKeyFile.getPath());
    }

}
