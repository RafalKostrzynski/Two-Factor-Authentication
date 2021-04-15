package pl.kostrzynski.twofactorauthentication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import pl.kostrzynski.twofactorauthentication.R;
import pl.kostrzynski.twofactorauthentication.runnable.CreatePostSaveKeyRunnable;
import pl.kostrzynski.twofactorauthentication.service.AlertDialogService;
import pl.kostrzynski.twofactorauthentication.service.ECCService;
import pl.kostrzynski.twofactorauthentication.service.HttpRequestService;
import pl.kostrzynski.twofactorauthentication.service.PreferenceService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int READ_REQUEST_CODE = 42;
    private static final int PERMISSION_REQUEST_STORAGE_READ = 1000;
    private static final int PERMISSION_REQUEST_STORAGE_WRITE = 1001;

    private View mContentView;
    private TextView privateKeyName;

    private final String POST_PUBLIC_KEY_SERVICE_URL = "https://localhost:8443/tfa/service/rest/v1/add-public/";
    private final String PATCH_PUBLIC_KEY_SERVICE_URL = "https://localhost:8443/tfa/service/rest/v1/check-key-gen/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.fullscreen_content);

        privateKeyName = findViewById(R.id.privateKeyName);
        final Button loadPKButton = findViewById(R.id.read_PK_button);
        final Button scanQRButton = findViewById(R.id.scan_code_button);
        final Button generateECCButton = findViewById(R.id.generate_ECC_button);

        scanQRButton.setOnClickListener(v -> scanQR());

        // TODO not sure if it is needed depends on storing keys
        loadPKButton.setOnClickListener(v -> loadPK());

        // TODO Not sure if it is needed, probably not (generating will be provided via qr-scan)
        generateECCButton.setOnClickListener(v -> generateECC());

        PreferenceService preferenceService = new PreferenceService();
        String path = preferenceService.loadPathFromPreferences(this);
        privateKeyName.setText(findFileNameFromString(path));
    }

    private void scanQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    private void loadPK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE_READ);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // TODO ensure it´s needed else remove
    private void generateECC() {
        readWriteFilePermissionCheck();
        generateAndWriteKeysToStorage();
        Toast.makeText(this, "Saved in Documents!", Toast.LENGTH_SHORT).show();
    }

    private void readWriteFilePermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                Toast.makeText(this, "Write permission is needed to save your keys", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE_WRITE);
        }
    }

    private void generateAndWriteKeysToStorage() {
        Thread thread = getThreadToSaveKeys();
        thread.start();
    }

    // TODO ensure it's needed else remove
    private Thread getThreadToSaveKeys() {
        return new Thread() {
            public void run() {
                try {
                    setPriority(Thread.MAX_PRIORITY);

                    ECCService eccService = new ECCService();
                    KeyPair keyPair = eccService.generateKeyPair();
                    ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

                    Context context = FullscreenActivity.this;
                    // TODO save somehow else
                    File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File privateKeyFile = new File(path, "private.key");
                    try (FileOutputStream privateKeyOutput = new FileOutputStream(privateKeyFile)) {
                        privateKeyOutput.write(eccService.getEncodedPrivateKey(privateKey));
                    }
                    setAndSavePathTextView(privateKeyFile.getPath());
                } catch (Exception e) {
                    System.err.println("EC Exception\n" + e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    private ECPrivateKey readFileForPK(String path) {
        try {

            // TODO provide storage access framework, look into content values
            File file = new File(path);

            File newFile = new File(getExternalFilesDir(file.getParentFile().toString()), file.getName());

            FileInputStream fileInputStream = new FileInputStream(newFile);
            fileInputStream.read();

            byte[] privateKeyBytes = null; // TODO handle reading bytes
            ECCService eccService = new ECCService();
            return eccService.getPrivateKeyFromBytes(privateKeyBytes);

            // TODO set key
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, Please try again!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String findFileNameFromString(String path) {
        try {
            return path.contains("/") ?
                    path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public void setAndSavePathTextView(String path) {
        privateKeyName.setText(findFileNameFromString(path));
        PreferenceService preferenceService = new PreferenceService();
        preferenceService.savePathToSharedPreferences(this, path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":") + 1);
                readFileForPK(path);
                setAndSavePathTextView(path);
                Toast.makeText(this, "Key-path has been stored", Toast.LENGTH_SHORT).show();
            }
        } else if (IntentIntegrator.parseActivityResult(requestCode, resultCode, data) != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            createDialogForGeneratedKey(result);
        }
    }

    private void createDialogForGeneratedKey(IntentResult result) {
        if (result.getContents() != null) {
            AlertDialog dialog;
            String qrMessage = result.getContents();
            ECCService eccService = new ECCService();
            AlertDialogService alertDialogService = new AlertDialogService();
            // Generate save and post keys

            if (qrMessage.startsWith(POST_PUBLIC_KEY_SERVICE_URL)) {
                PreferenceService preferenceService = new PreferenceService();
                qrMessage = qrMessage.substring(qrMessage.lastIndexOf('/') + 1);
                if (preferenceService.keyExists(this)) {
                    dialog = alertDialogService.createBuilder(this,
                            "If you want to generate a new key please visit settings page",
                            "There already is a key in your storage.");
                } else {
                    dialog = alertDialogService.createBuilder(this, qrMessage,
                            "To generate a new key pair press 'Generate keys'",
                            "Generate key pair?", "Generate keys",
                            true, this::generateAndPostPublicKey);
                }
            } else if (qrMessage.startsWith(PATCH_PUBLIC_KEY_SERVICE_URL)) {
                qrMessage = qrMessage.substring(qrMessage.lastIndexOf('/') + 1);
                dialog = alertDialogService.createBuilder(this, qrMessage,
                        "To update the old key pair press 'Generate keys'\n" +
                                "Do it only if you are sure about overriding the old key pair!",
                        "Update key pair?", "Generate keys",
                        this::requestGenerateAndPutPublicKey);
            }
            // decode otp
            else if (eccService.isValidCiphertext(qrMessage)) {
                dialog = alertDialogService.createBuilder(this, qrMessage,
                        "Press 'Encode password' to authenticate",
                        "One more step to authenticate",
                        "Encode password", eccService::decodeMessage);
            }
            // create error dialog
            else {
                dialog = alertDialogService.createBuilder(this, qrMessage,
                        "Something went wrong please try again");
            }
            dialog.show();
        } else {
            Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateAndPostPublicKey(String token, boolean isPostMethod) {
        try {
            readWriteFilePermissionCheck();
            Thread createSaveAndPostKeysThread = new Thread(
                    new CreatePostSaveKeyRunnable(token, this, isPostMethod));
            createSaveAndPostKeysThread.start();

            createSaveAndPostKeysThread.join();
            PreferenceService preferenceService = new PreferenceService();
            String path = preferenceService.loadPathFromPreferences(this);
            privateKeyName.setText(findFileNameFromString(path));

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestGenerateAndPutPublicKey(String token) {
        HttpRequestService httpRequestService = new HttpRequestService();
        boolean checkKeyGen = false;
        try {
            checkKeyGen = httpRequestService.checkGenerateKey(token);
        } catch (InterruptedException ignored) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
        if (checkKeyGen) {
            generateAndPostPublicKey(token, false);
        } else {
            AlertDialogService alertDialogService = new AlertDialogService();
            AlertDialog dialog = alertDialogService.createBuilder(this,
                    "Server declined the request please check if change of key is possible",
                    "Couldn't create a new key");
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE_READ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == PERMISSION_REQUEST_STORAGE_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 100);
    }

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
