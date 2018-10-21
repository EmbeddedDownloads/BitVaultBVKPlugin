package org.bitvault.plugin.action.Utils;

import java.util.prefs.Preferences;


public class Prefrences implements Constants {

    private static Preferences prefs = null;
    private static Prefrences prefrences = null;

    private Prefrences() {
        prefs = Preferences.userNodeForPackage(Prefrences.class);
    }


    public static Prefrences getInstance() {

        if (prefrences == null)
            prefrences = new Prefrences();

        return prefrences;
    }


    public void setValue(final String prefKey, final String prefValue) {
        prefs.put(prefKey, prefValue);
    }

    public String getValue(final String prefKey) {
        return prefs.get(prefKey, DEFAULT_VALUE);
    }


}
