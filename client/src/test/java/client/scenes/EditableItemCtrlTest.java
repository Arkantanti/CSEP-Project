package client.scenes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditableItemCtrlTest {

    private EditableItemCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new EditableItemCtrl();
    }

    private void callOnDeleteClicked() {
        try {
            Method m = EditableItemCtrl.class.getDeclaredMethod("onDeleteClicked");
            m.setAccessible(true);
            m.invoke(ctrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setText_updatesInternalState() {
        ctrl.setText("Hello");
        assertEquals("Hello", ctrl.getText());
    }

    @Test
    void onDeleteClicked_removesFromListAndCallsOnChange() {
        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        AtomicInteger changes = new AtomicInteger();

        ctrl.bindTo(list, 0, changes::incrementAndGet, false);

        callOnDeleteClicked();

        assertEquals(1, list.size());
        assertEquals("second", list.get(0));
        assertEquals(1, changes.get());
    }
}
