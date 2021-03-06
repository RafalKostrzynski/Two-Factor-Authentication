package pl.kostrzynski.twofactorauthentication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.jetbrains.annotations.NotNull;
import pl.kostrzynski.twofactorauthentication.R;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;
import pl.kostrzynski.twofactorauthentication.model.SmartphoneDetails;
import pl.kostrzynski.twofactorauthentication.runnable.ChangePasswordRunnable;
import pl.kostrzynski.twofactorauthentication.runnable.CreatePostSaveKeyRunnable;
import pl.kostrzynski.twofactorauthentication.runnable.PostSignedMessageRunnable;
import pl.kostrzynski.twofactorauthentication.runnable.PutPublicKeyRunnable;
import pl.kostrzynski.twofactorauthentication.service.AlertDialogService;
import pl.kostrzynski.twofactorauthentication.service.ECCService;
import pl.kostrzynski.twofactorauthentication.service.PreferenceService;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FullscreenActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private View mContentView;
    private final ECCService eccService = new ECCService();
    private final PreferenceService preferenceService = new PreferenceService();
    private final AlertDialogService alertDialogService = new AlertDialogService();
    private final String POST_PUBLIC_KEY_SERVICE_URL = "https://localhost:8443/tfa/service/rest/v1/first-auth/add-public/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.fullscreen_content);
        TextView privateKeyName = findViewById(R.id.privateKeyName);
        final Button scanQRButton = findViewById(R.id.scan_code_button);
        scanQRButton.setOnClickListener(v -> scanQR());

        privateKeyName.setText(preferenceService.loadAdjectiveFromPreferences(this));
    }

    private void scanQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (IntentIntegrator.parseActivityResult(requestCode, resultCode, data) != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            createDialogForGeneratedKey(result);
        }
    }

    private void createDialogForGeneratedKey(IntentResult result) {
        if (result.getContents() != null) {
            AlertDialog dialog;
            String qrMessage = result.getContents();
            // make a signature and sent to Server
            if (isValidJsonObject(qrMessage)) {
                if (eccService.keyExists()) {
                    QRPayload qrPayload = getQRPayloadFromString(qrMessage);
                    if (qrPayload.getExpirationTime().plusSeconds(5).isAfter(LocalDateTime.now())) {
                        dialog = QrCodeDialog(qrPayload);
                    } else dialog = alertDialogService.createBuilder(this,
                            "Token expired create a new one", "Token expired!");
                } else dialog = alertDialogService.createBuilder(this,
                        "Can´t do this operation without a key", "Private key is missing!");
            }
            // Generate save and post keys
            else if (qrMessage.startsWith(POST_PUBLIC_KEY_SERVICE_URL)) {
                dialog = generateNewKeyDialog(qrMessage);
            }
            // error dialog
            else {
                dialog = alertDialogService.createBuilder(this, qrMessage,
                        "Something went wrong please try again");
            }
            dialog.show();
        } else {
            Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private AlertDialog QrCodeDialog(QRPayload qrPayload) {
        AlertDialog dialog;

        switch (qrPayload.getPurpose()) {
            case AUTHENTICATE:
                dialog = alertDialogService.createBuilder(this, qrPayload,
                        "Press 'Verify' to authenticate",
                        "One more step to authenticate",
                        "Verify", this::signMessageAndSendRequest);
                break;
            case CHANGE_KEY:
                dialog = alertDialogService.createBuilder(this, qrPayload,
                        "To update the old key pair press 'Generate keys'\n" +
                                "Do it only if you are sure about overriding the old key pair!", "Update key pair?",
                        "Generate keys", this::updateKey);
                break;
            case RESET_PASSWORD:
                dialog = alertDialogService.createBuilder(this, this, qrPayload, "Reset password request",
                        "Save new password", this::resetPassword);
                break;
            default:
                dialog = alertDialogService.createBuilder(this, "Unknown error occurred, " +
                                "please try again later",
                        "Unknown error!");
        }
        return dialog;
    }

    private AlertDialog generateNewKeyDialog(String qrMessage) {
        AlertDialog dialog;
        qrMessage = qrMessage.substring(qrMessage.lastIndexOf('/') + 1);
        if (eccService.keyExists()) {
            dialog = alertDialogService.createBuilder(this,
                    "If you want to generate a new key please visit settings page",
                    "There already is a key in your storage.");
        } else {
            dialog = alertDialogService.createBuilder(this, qrMessage,
                    "To generate a new key pair press 'Generate keys'",
                    "Generate key pair?", "Generate keys",
                    true, this::generateAndPostPublicKey);
        }
        return dialog;
    }

    private void signMessageAndSendRequest(QRPayload qrPayload) {
        String signature = getSignature(qrPayload);
        if (signature != null) {
            Thread thread = new Thread(new PostSignedMessageRunnable(this, qrPayload, signature));
            thread.start();
        }
    }

    private void resetPassword(QRPayload qrPayload, String password) {
        String signature = getSignature(qrPayload);
        if (signature != null) {
            Thread thread = new Thread(new ChangePasswordRunnable(this, password, signature, qrPayload));
            thread.start();
        }
    }

    private void updateKey(QRPayload qrPayload) {
        SmartphoneDetails smartphoneDetails = getSmartphoneDetails();
        Thread thread = new Thread(new PutPublicKeyRunnable(smartphoneDetails, qrPayload, this));
        thread.start();
    }

    private String getSignature(QRPayload qrPayload) {
        try {
            SmartphoneDetails smartphoneDetails = getSmartphoneDetails();
            String message = qrPayload.getPayload() + smartphoneDetails.getSmartphoneDetails();
            return eccService.signMessage(message);
        } catch (KeyStoreException | SignatureException | InvalidKeyException | UnrecoverableEntryException |
                IOException | CertificateException | NoSuchAlgorithmException e) {
            Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @NotNull
    private SmartphoneDetails getSmartphoneDetails() {
        @SuppressLint("HardwareIds")
        String secretAndroidId = Settings.Secure.
                getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return new SmartphoneDetails(secretAndroidId);
    }

    private void generateAndPostPublicKey(String token, boolean isPostMethod) {
        try {
            Thread createSaveAndPostKeysThread = new Thread(
                    new CreatePostSaveKeyRunnable(token, this));
            createSaveAndPostKeysThread.start();
            createSaveAndPostKeysThread.join();
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private QRPayload getQRPayloadFromString(String qrMessage) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                (json, type, jsonDeserializationContext) ->
                        LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();
        try {
            QRPayload qrPayload = gson.fromJson(qrMessage, QRPayload.class);
            return new QRPayload(qrPayload.getPurpose(), qrPayload.getPayload(),
                    qrPayload.getJwtToken(), qrPayload.getExpirationTime());
        } catch (Exception e) {
            throw new JsonSyntaxException("Invalid Json syntax");
        }
    }

    private boolean isValidJsonObject(String message) {
        try {
            getQRPayloadFromString(message);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 100);
    }

    // --------------- UI logic ---------------
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = this::hide;
}
