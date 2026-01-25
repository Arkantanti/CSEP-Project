package client.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    /**
     * Extracts the configuration file path from the command-line arguments.
     * The method looks for the flag {@code --cfg} followed by a file path.
     * If it is not present a default path {@code "client-config.json"}
     * is used.
     *
     * @param args the command-line arguments passed to the application
     * @return the configuration file path to use
     */
    public static Path getConfigPath(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--cfg") && i + 1 < args.length) {
                return Path.of(args[i + 1]);
            }
        }

        // Default if not provided
        return Path.of("client-config.json");
    }

    /**
     * Loads the configuration from the given path.
     * If the file does not exist, a default configuration is created,
     * saved to disk, and returned.
     *
     * @param path the path to the config file
     * @return the loaded (or newly created) Config instance
     * @throws IOException if reading or writing the file fails
     */
    public static Config loadOrCreate(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Config config;
        if (Files.exists(path)) {
            config = mapper.readValue(path.toFile(), Config.class);
        } else {
            config = new Config();

            //Creating the directories
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            //Saving the default
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), config);
        }

        config.setConfigPath(path);
        return config;
    }

    /**
     * Saves the current state of the config back to the file.
     * 
     * @param config the config to save
     * @throws IOException if writing the file fails
     */
    public void save(Config config) throws IOException {
        Path path = config.getConfigPath();
        if (path == null) {
            throw new IllegalStateException("Config path is not set");
        }

        ObjectMapper mapper = new ObjectMapper();
        
        //Creating the directories
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), config);
    }
}
