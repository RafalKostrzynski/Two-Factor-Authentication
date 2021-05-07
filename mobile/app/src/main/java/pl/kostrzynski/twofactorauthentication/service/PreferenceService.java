package pl.kostrzynski.twofactorauthentication.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceService {

    private static final String SHARED_PREFS = "sharedPreferences";
    private static final String TEXT = "text";
    private static final String[] adjective = {
            "the same", "like", "beautiful", "Certain", "The original", "beautiful", "happy", "possible", "Lovely",
            "understandable", "all", "later", "important", "frequently", "Nature", "truly", "scared", "Aerial",
            "Red", "painfully", "Clean", "hard", "wonderful", "happy", "improving", "Influential", "Yellow",
            "Dear", "basically", "perfect", "Golden", "clever", "fresh", "charming", "bright", "common",
            "direct", "real", "Heard", "with effort", "Fast", "Snow White", "hurry", "optimistic", "Main",
            "Vivid", "ice cold", "Observant", "Amazing", "horizontal", "touching", "Lots of", "Ignorant", "Polite",
            "warm", "Affectionate", "normal", "plain", "bright", "Lagging", "generous", "Boss", "Hard work",
            "Clear", "professional", "permanent", "Atmospheric", "Confidante", "Just right", "relatively", "Peaceful", "friendly",
            "Huge", "Beautiful", "Daily", "Advanced", "identical", "straight", "Stable", "satisfied", "Sturdy",
            "Long time", "obedient", "famous", "Sultry", "Many", "Crowded", "intrinsic", "Tiny", "honest",
            "Friendly", "original", "Ridiculous", "qualified", "public", "Big Red", "Powerful", "Clean", "dim",
            "Bright red", "Pink", "frightening", "extra", "Beautiful", "busy", "Cold", "Enthusiastic", "Empty",
            "Desolate", "public", "Cold", "Complete", "Grass Green", "Competent", "Furious", "Heartful", "Amateur",
            "hollow", "cool", "Long term", "Natural", "Reconciliation", "legal", "Ming Jing", "Outdated", "Low",
            "Unpleasant", "Low level", "Used", "Uncertain", "Public", "hardworking", "Little", "Busy", "Daily",
            "Important", "rare", "Non-divided", "Fear of people", "Busy", "happy", "special", "Future", "great",
            "difficult", "sad", "actual", "realistic", "abundant", "same", "huge", "patiently", "superior",
            "Dear", "Nasty", "severe", "positive", "neat", "Environmentally Friendly"};

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

    private String getKeyName(String adjective){
        return adjective.equals("") ?
                "No key stored yet" :
                adjective.substring(0, 1).toUpperCase() + adjective.substring(1)+" key available";
    }

    private String getRandomAdjective(){
        Random random = new Random();
        return adjective[random.nextInt(adjective.length-1)];
    }

}
