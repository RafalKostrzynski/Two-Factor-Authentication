package pl.kostrzynski.twofactorauthentication.service;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import static android.content.Context.MODE_PRIVATE;

public class PreferenceService {

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String TEXT = "text";

    public String loadPathFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String path = sharedPreferences.getString(TEXT, "");
        if (new File(path).exists())
            return path;
        return "";
    }

    public void savePathToSharedPreferences(Context context, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, path);
        editor.apply();
    }


}
