package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowableTest {

    @Test
    void getName() {
        Showable showable = new Showable() {
            @Override
            public String getName() {
                return "TestName";
            }
        };

        assertEquals("TestName", showable.getName());
    }
}