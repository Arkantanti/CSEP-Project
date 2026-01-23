package client.utils;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreferenceManager {
    private final Config config;
    private final ServerUtils serverUtils;
    private Map<String, Boolean> languagePreference;


    /**
     * Constructs a new FavoritesManager.
     *
     * @param config the config object containing the user specific configuration
     * @param serverUtils the object for communicating with the server
     */
    @Inject
    public PreferenceManager(Config config, ServerUtils serverUtils) {
        this.config = config;
        this.serverUtils = serverUtils;
    }

    /**
     * Function to get the language filters
     * @return a hashmap of the values.
     */
    public Map<String, Boolean> getLanguagePreference(){
        Map<String, Boolean> prefs = new HashMap<String, Boolean>();
        prefs.put("Dutch", config.isDutLanguage());
        prefs.put("English", config.isEngLanguage());
        prefs.put("Polish", config.isPolLanguage());

        return prefs;
    }

    /**
     * function that updates a single language pregerence
     * @param language the language that are there
     * @param value the truth value that the language is on
     */
    public void updateLanguagePreference(String language, boolean value) throws IOException {
        switch(language.toLowerCase()){
            case "english" -> config.setEngLanguage(value);
            case "polish" -> config.setPolLanguage(value);
            case "dutch" -> config.setDutLanguage(value);
            default -> throw new IllegalArgumentException("Unknown language");
        }

        ConfigManager.save(config);
    }

    /**
     * function to update all language preference at once
     * @param preferences the preference to update
     */
    public void updateLanguagePreferences(Map<String, Boolean> preferences) throws IOException {
        for (Map.Entry<String, Boolean> entry : preferences.entrySet()) {
            updateLanguagePreference(entry.getKey(), entry.getValue());
        }
    }

    /**
     * function for a default language
     * @return the default for languages
     */
    public Map<String, Boolean> getDefaultLanguages() {
        Map<String, Boolean> defaults = new HashMap<>();
        defaults.put("english", true);  // Default English to true
        defaults.put("polish", false);
        defaults.put("dutch", false);
        return defaults;
    }

    public boolean isEnglishEnabled() {
        return config.isEngLanguage();
    }

    public boolean isPolishEnabled() {
        return config.isPolLanguage();
    }

    public boolean isDutchEnabled() {
        return config.isDutLanguage();
    }



}
