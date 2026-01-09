package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    @Test
    void testToString() {
        assertEquals("grams", Unit.GRAM.toString());
        assertEquals("liters", Unit.LITER.toString());
        assertEquals("customs", Unit.CUSTOM.toString());
    }

    @Test
    void values() {
        Unit[] values = Unit.values();

        assertEquals(3, values.length);
        assertEquals(Unit.GRAM, values[0]);
        assertEquals(Unit.LITER, values[1]);
        assertEquals(Unit.CUSTOM, values[2]);
    }

    @Test
    void valueOf() {
        assertEquals(Unit.GRAM, Unit.valueOf("GRAM"));
        assertEquals(Unit.LITER, Unit.valueOf("LITER"));
        assertEquals(Unit.CUSTOM, Unit.valueOf("CUSTOM"));

        assertThrows(IllegalArgumentException.class, () -> Unit.valueOf("gram"));
        assertThrows(IllegalArgumentException.class, () -> Unit.valueOf("INVALID"));
    }
}