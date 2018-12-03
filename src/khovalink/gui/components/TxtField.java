package khovalink.gui.components;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

/**
 * Class representing a personalized {@code TextField}.
 *
 * @author flo
 */
public class TxtField extends TextField {

    /**
     * Creates a new {@code TxtField}.
     */
    public TxtField() {
        super();

        setFont(Font.font("Comic Sans MS", 13));
        this.setPrefColumnCount(15);
    }
}
