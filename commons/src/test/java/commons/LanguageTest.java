package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void testEnumValuesExist() {
        assertNotNull(Language.English);
        assertNotNull(Language.Polish);
        assertNotNull(Language.Dutch);
    }

    @Test
    void testValueOf() {
        assertEquals(Language.English, Language.valueOf("English"));
        assertEquals(Language.Polish, Language.valueOf("Polish"));
        assertEquals(Language.Dutch, Language.valueOf("Dutch"));
    }

    @Test
    void testValueOfInvalidThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Language.valueOf("German");
        });
    }

    @Test
    void testValuesOrder() {
        Language[] values = Language.values();

        assertEquals(3, values.length);
        assertEquals(Language.English, values[0]);
        assertEquals(Language.Polish, values[1]);
        assertEquals(Language.Dutch, values[2]);
    }
}