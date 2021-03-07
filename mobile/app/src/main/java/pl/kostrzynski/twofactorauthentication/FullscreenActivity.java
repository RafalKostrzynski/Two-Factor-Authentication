package pl.kostrzynski.twofactorauthentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.json.JSONException;
import org.json.JSONObject;
import pl.kostrzynski.twofactorauthentication.service.ECCHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

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

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String TEXT = "text";
    private byte[] publicKeyByteArray;

    private View mContentView;
    private TextView privateKeyName;

    private final String POST_PUBLIC_KEY_SERVICE_URL = "http://localhost:8080/tfa/service/rest/v1/add-public/";

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
        loadPKButton.setOnClickListener(v -> loadPK());
        generateECCButton.setOnClickListener(v -> generateECC());

        String path = loadPathFromPreferences();
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

    private Thread getThreadToSaveKeys() {
        return new Thread() {
            public void run() {
                try {
                    setPriority(Thread.MAX_PRIORITY);

                    ECCHandler eccHandler = new ECCHandler();
                    KeyPair keyPair = eccHandler.generateKeyPair();
                    ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

                    Context context = FullscreenActivity.this;
                    // TODO save somehow else
                    File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File privateKeyFile = new File(path, "private.key");
                    try (FileOutputStream privateKeyOutput = new FileOutputStream(privateKeyFile)) {
                        privateKeyOutput.write(eccHandler.getEncodedPrivateKey(privateKey));
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
            ECCHandler eccHandler = new ECCHandler();
            return eccHandler.getPrivateKeyFromBytes(privateKeyBytes);

            // TODO set key
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, Please try again!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String loadPathFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String path = sharedPreferences.getString(TEXT, "");
        if (new File(path).exists())
            return path;
        return "";
    }

    private void savePathToSharedPreferences(String path) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, path);
        editor.apply();
    }

    private String findFileNameFromString(String path) {
        try {
            return path.contains("/") ?
                    path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) : "";
        } catch (Exception e) {
            return "";
        }
    }

    private void setAndSavePathTextView(String path) {
        privateKeyName.setText(findFileNameFromString(path));
        savePathToSharedPreferences(path);
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
            if (result.getContents() != null) {
                AlertDialog dialog;
                String qrMessage = result.getContents();

                // TODO if its generate key request give a toast with (key successfully stored) else set textField to encoded OTP
                if (qrMessage.startsWith(POST_PUBLIC_KEY_SERVICE_URL)) {
                    if (generateAndPostPublicKey()) {

                    }
                    dialog = createBuilder(this, qrMessage, "Generate public key?", "Generate key");
                } else {
                    dialog = createBuilder(this, qrMessage, "Scanned qr-code", "");
                }
                dialog.show();
            } else {
                Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean generateAndPostPublicKey() {
        try {
            readWriteFilePermissionCheck();
            Thread generateECCThread = getThreadToSaveKeys();
            Thread postPublicKeyThread = postPublicKeyThread();
            generateECCThread.start();
            generateECCThread.join();
            postPublicKeyThread.start();

            // TODO complete post
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Thread postPublicKeyThread() {
        return new Thread() {
            public void run() {
                ECCHandler eccHandler = new ECCHandler();
                ECPublicKey publicKey = eccHandler.getPublicKeyFromPrivateKey(readFileForPK(loadPathFromPreferences()));
                byte[] publicKeyBytes = eccHandler.getEncodedPublicKey(publicKey);
                sendPostRequest(publicKeyBytes);
            }
        };
    }

    private void sendPostRequest(byte[] publicKeyBytes) {
        try {
            URL url = new URL("https://reqres.in/api/users");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("publicKeyBytes", publicKeyBytes);

            try (OutputStream os = con.getOutputStream()) {
                os.write(publicKeyBytes, 0, publicKeyBytes.length);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AlertDialog createBuilder(Context context, String qrMessage, String title, String positiveButtonString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(qrMessage);
        builder.setTitle(title);
        builder.setPositiveButton(positiveButtonString, (dialog, which) -> scanQR())
                .setNegativeButton("Cancel", (dialog, which) ->
                        finish());
        AlertDialog dialog = builder.create();
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
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
}
