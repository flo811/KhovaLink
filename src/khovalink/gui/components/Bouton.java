package khovalink.gui.components;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Class representing a personalized {@code Button}.
 *
 * @author flo
 */
public class Bouton extends Button {

    /**
     * Enum specifying all different types of {@code Button}.
     */
    public enum BoutonType {
        BIG, SMALL
    }

    /**
     * Creates a new {@code Bouton}.
     *
     * @param text The text to display.
     * @param type The type of {@code Bouton}.
     */
    public Bouton(final String text, final BoutonType type) {
        super(text);

        setTextFill(Color.MIDNIGHTBLUE);
        setFont(Font.font("Comic Sans MS", 15));
        
        switch (type) {
            case BIG:
                setPrefWidth(200);
                setPrefHeight(75);
                break;
            case SMALL:
                setPrefWidth(95);
                setPrefHeight(50);
                break;
        }
    }
}
