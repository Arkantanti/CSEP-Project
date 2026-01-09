package client.scenes;

import javafx.application.Platform; // Import Platform
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll; // Import BeforeAll
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditableItemCtrlTest {

    private EditableItemCtrl ctrl;


    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {

        }
    }


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

    @Test
    void finishEditing_emptyString_doesNotUpdateList() {
        // 1. Setup the controller with a list
        List<String> list = new ArrayList<>();
        list.add("Original Text");

        // 2. Inject a real TextField into the private field using reflection
        TextField mockField = new TextField(""); // This now works because Toolkit is init
        injectField(ctrl, "textField", mockField);

        // 3. Bind the list
        ctrl.bindTo(list, 0, () -> {}, false); // newItem = false

        // 4. Manually call the private finishEditing method
        callPrivateMethod(ctrl, "finishEditing");

        // 5. Assert: The list should NOT have changed because the input was empty
        assertEquals("Original Text", list.get(0));
        // Verify the style was set to error (red border)
        // Note: Real TextField stores styles, so this works
        assertTrue(mockField.getStyle().contains("-fx-text-box-border: red"));
    }

    @Test
    void finishEditing_newItem_emptyString_removesFromList() {
        // 1. Setup list with a placeholder item (representing a new item being added)
        List<String> list = new ArrayList<>();
        list.add("");

        // 2. Inject TextField with empty text
        TextField mockField = new TextField("");
        injectField(ctrl, "textField", mockField);

        // 3. Bind with newItem = true
        ctrl.bindTo(list, 0, () -> {}, true);

        // 4. Call finishEditing
        callPrivateMethod(ctrl, "finishEditing");

        // 5. Assert: The item should be REMOVED because it was new and empty
        assertTrue(list.isEmpty());
    }

    // Helper method to inject private fields
    private void injectField(Object instance, String fieldName, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to call private methods
    private void callPrivateMethod(Object instance, String methodName) {
        try {
            Method m = instance.getClass().getDeclaredMethod(methodName);
            m.setAccessible(true);
            m.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}