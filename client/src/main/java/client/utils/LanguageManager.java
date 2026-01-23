package client.utils;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;
import commons.Language;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
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
    public LanguageManager(Config config, ServerUtils serverUtils) {
        this.config = config;
        this.serverUtils = serverUtils;
    }

    /**
     * Function to get the language filters
     * @return a hashmap of the values.
     */
    public Map<Language, Boolean> getLanguagePreference(){
        Map<Language, Boolean> prefs = new HashMap<Language, Boolean>();
        prefs.put(Language.Dutch, config.isDutLanguage());
        prefs.put(Language.English, config.isEngLanguage());
        prefs.put(Language.Polish, config.isPolLanguage());

        return prefs;
    }

    /**
     * function that updates a single language pregerence
     * @param language the language that are there
     * @param value the truth value that the language is on
     */
    public void updateLanguagePreference(Language language, boolean value) throws IOException {
        switch(language){
            case Language.English -> config.setEngLanguage(value);
            case Language.Polish -> config.setPolLanguage(value);
            case Language.Dutch -> config.setDutLanguage(value);
            default -> throw new IllegalArgumentException("Unknown language");
        }

        ConfigManager.save(config);
    }

    /**
     * function to update all language preference at once
     * @param preferences the preference to update
     */
    public void updateLanguagePreferences(Map<Language, Boolean> preferences) throws IOException {
        for (Map.Entry<Language, Boolean> entry : preferences.entrySet()) {
            updateLanguagePreference(entry.getKey(), entry.getValue());
        }
    }

    /**
     * function for a default language
     * @return the default for languages
     */
    public Map<Language, Boolean> getDefaultLanguages() {
        Map<Language, Boolean> defaults = new HashMap<>();
        defaults.put(Language.English, true);  // Default English to true
        defaults.put(Language.Polish, false);
        defaults.put(Language.Dutch, false);
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
