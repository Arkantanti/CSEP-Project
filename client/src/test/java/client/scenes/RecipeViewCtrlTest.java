package client.scenes;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipeViewCtrlTest {

    private MainCtrl sut;
    private RecipeViewCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RecipeViewCtrl(null, sut = new MainCtrl(), null);
    }

}
