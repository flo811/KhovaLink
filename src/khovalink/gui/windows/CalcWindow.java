package khovalink.gui.windows;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import khovalink.gui.components.Bouton;
import khovalink.gui.components.Bouton.BoutonType;
import khovalink.homology.LinkHomology;

/**
 * Class representing the calculation window.
 *
 * @author flo
 */
public final class CalcWindow extends Window {

    private final TextArea tArea = new TextArea("");

    private final Bouton calculate = new Bouton("Calculate", BoutonType.SMALL);
    private final Bouton stop = new Bouton("Stop", BoutonType.SMALL);
    private final Bouton wipe = new Bouton("Wipe", BoutonType.SMALL);
    private final Bouton back = new Bouton("Back", BoutonType.SMALL);

    private final HBox hBox = new HBox(30, calculate, stop, wipe, back);
    private final VBox vBox = new VBox(15, tArea, hBox);

    private final SimpleBooleanProperty isCalculating = new SimpleBooleanProperty(false);

    private LinkHomology process;

    /**
     * Creates a new {@code CalcWindow}.
     */
    CalcWindow() {
        super();

        root.getChildren().add(vBox);

        tArea.setPrefSize(650, 700);
        tArea.setEditable(false);
        tArea.setFont(Font.font("Comic Sans MS", 13));
        hBox.setAlignment(Pos.CENTER);

        back.setCancelButton(true);
        calculate.requestFocus();

        calculate.disableProperty().bind(isCalculating);
        stop.disableProperty().bind(isCalculating.not());
        wipe.disableProperty().bind(tArea.textProperty().isEmpty());

        calculate.setOnAction(e -> WindowsControler.launchCalc());
        stop.setOnAction(e -> process.cancel());
        wipe.setOnAction(e -> tArea.clear());
        back.setOnAction(e -> WindowsControler.setMainWindow(getFade()));
    }

    /**
     * Appends text in the {@code TextArea}.
     *
     *
     * @param text The text to append.
     */
    public void setText(final String text) {
        tArea.setText(text);
    }

    public void appendText(final String text) {
        tArea.appendText(text);
    }

    /**
     * Sets the process variable.
     *
     * @param homology The process.
     */
    public void setProcess(LinkHomology homology) {
        process = homology;
        isCalculating.bind(process.runningProperty());
    }
}
