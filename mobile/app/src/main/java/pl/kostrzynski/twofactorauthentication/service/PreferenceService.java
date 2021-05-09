package pl.kostrzynski.twofactorauthentication.service;

import android.content.Context;
import android.content.SharedPreferences;
import pl.kostrzynski.twofactorauthentication.model.utility.Resources;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceService {

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String TEXT = "text";
    private static final String[] adjective = Resources.getAdjectives();

    public String loadAdjectiveFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String adjective = sharedPreferences.getString(TEXT, "");
        return getKeyName(adjective);
    }

    public String generateAndSaveAdjectiveToSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String adjective = getRandomAdjective();
        editor.putString(TEXT, adjective);
        editor.apply();
        return getKeyName(adjective);
    }

    private String getKeyName(String adjective) {
        return adjective.equals("") ?
                "No key stored yet" :
                adjective.substring(0, 1).toUpperCase() + adjective.substring(1) + " key available";
    }

    private String getRandomAdjective() {
        Random random = new Random();
        return adjective[random.nextInt(adjective.length - 1)];
    }

}
