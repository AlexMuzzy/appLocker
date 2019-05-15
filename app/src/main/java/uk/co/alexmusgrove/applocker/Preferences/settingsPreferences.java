package uk.co.alexmusgrove.applocker.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class settingsPreferences {
    private SharedPreferences sharedPreferences;
    private static String PREF_NAME = "uk.co.alexmusgrove.applocker";

    public settingsPreferences() {
        //blank
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getPassword(Context context) {
        return getPrefs(context).getString("password_key", "password");
    }

    public static void setPassword(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("password_key", input);
        editor.commit();
    }
}
