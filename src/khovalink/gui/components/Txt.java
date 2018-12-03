package khovalink.gui.components;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Class representing a personalized {@code Text}.
 *
 * @author flo
 */
public class Txt extends Text {

    /**
     * Enum specifying all different types of {@code Txt}.
     */
    public enum TxtType {
        TITLE, SMALL
    }

    /**
     * Creates a new {@code LinkQuad}.
     *
     * @param text The text to display.
     * @param type The type of {@code Txt}.
     */
    public Txt(final String text, final TxtType type) {
        super(text);

        setFill(Color.ANTIQUEWHITE);

        switch (type) {
            case TITLE:
                setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                break;
            case SMALL:
                setFont(Font.font("Comic Sans MS", 15));
                break;
        }
    }
}
