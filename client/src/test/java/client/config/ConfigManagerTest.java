package client.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    // Path present in the arguments
    @Test
    void getConfigPathPresent() {
        Path path = ConfigManager.getConfigPath(new String[]{"argus", "--cfg", "direct/cfg.json"});
        Path pathExpected = Path.of("direct","cfg.json");
        assertEquals(pathExpected,path);
    }

    // Default path used
    @Test
    void getConfigPathDefault() {
        Path path = ConfigManager.getConfigPath(new String[]{"argus", "-cfg", "--cfg"});
        Path pathExpected = Path.of("client-config.json");
        assertEquals(pathExpected,path);
    }

    // File existed before
    @Test
    void loadOrCreateLoad() {
        try {
            Path tmp = Files.createTempFile("config-test", ".json");
            String json = """
                        {
                            "serverUrl": "http://custom.adress:8080/"
                        }
                        """;
            Files.writeString(tmp, json);

            Config cfg = ConfigManager.loadOrCreate(tmp);
            assertEquals("http://custom.adress:8080/", cfg.getServerUrl());

        } catch (IOException e) {
            fail();
        }
    }

    // File needs to be created
    @Test
    void loadOrCreateCreate() {
        try {
            // Get a random path to a temp file
            Path dir = Files.createTempDirectory("config-test");
            Path tmp = dir.resolve("anotherDirectory","config.json");

            Config cfg = ConfigManager.loadOrCreate(tmp);

            // Assert the default values
            assertEquals("http://localhost:8080/", cfg.getServerUrl());
            // Assert if the file was created
            assertTrue(Files.exists(tmp));

            // Assert the file was correctly written to
            Config cfg2 = ConfigManager.loadOrCreate(tmp);
            assertEquals(cfg,cfg2);
        } catch (IOException e) {
            fail(e);
        }
    }
}