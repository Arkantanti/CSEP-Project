package client.scenes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertSame;

public class AppViewCtrlTest {

    private AppViewCtrl ctrl;
    private MainCtrl mainCtrl;

    @BeforeEach
    void setup() {
        mainCtrl = new MainCtrl();
        ctrl = new AppViewCtrl(null, mainCtrl);
    }

    @Test
    void constructor_storesMainCtrlReference() {
        try {
            Field f = AppViewCtrl.class.getDeclaredField("mainCtrl");
            f.setAccessible(true);
            Object value = f.get(ctrl);
            assertSame(mainCtrl, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
