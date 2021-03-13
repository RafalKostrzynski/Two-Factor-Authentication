package pl.kostrzynski.twofactorauthentication.runnable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import pl.kostrzynski.twofactorauthentication.service.ECCService;
import pl.kostrzynski.twofactorauthentication.service.PreferenceService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;

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
            KeyPair keyPair = eccService.generateKeyPair();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

            // TODO save somehow else
            File privateKeyFile = saveKeysToStorage(eccService, privateKey);

            savePrivateKeyPathToPreferences(privateKeyFile);

             // This is used because third party apps have no access to Imei number since android 10.
            @SuppressLint("HardwareIds")
            String secretAndroidId = Settings.Secure.
                    getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            Runnable postPublicKeyRunnable = secretAndroidId == null ?
                    new PostPublicKeyRunnable(token, publicKeyBytes) :
                    new PostPublicKeyRunnable(token, secretAndroidId, publicKeyBytes);

            Thread postPublicKeyThread = new Thread(postPublicKeyRunnable);
            postPublicKeyThread.start();

        } catch (Exception e) {
            System.err.println("EC Exception\n" + e.toString());
            e.printStackTrace();
        }
    }

    private File saveKeysToStorage(ECCService eccService, ECPrivateKey privateKey) throws IOException {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File privateKeyFile = new File(path, "private.key");
        try (FileOutputStream privateKeyOutput = new FileOutputStream(privateKeyFile)) {
            privateKeyOutput.write(eccService.getEncodedPrivateKey(privateKey));
        }
        return privateKeyFile;
    }

    private void savePrivateKeyPathToPreferences(File privateKeyFile) {
        PreferenceService preferenceService = new PreferenceService();
        preferenceService.savePathToSharedPreferences(context, privateKeyFile.getPath());
    }

}
