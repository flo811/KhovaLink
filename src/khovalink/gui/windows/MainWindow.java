package khovalink.gui.windows;

import javafx.beans.property.BooleanProperty;
import khovalink.gui.components.Bouton;
import khovalink.gui.components.Bouton.BoutonType;

/**
 * Class representing the main window.
 *
 * @author flo
 */
public final class MainWindow extends Window {

    private final Bouton drawBtn = new Bouton("Create a Link", BoutonType.BIG);
    private final Bouton databaseBtn = new Bouton("Select a Link", BoutonType.BIG);
    private final Bouton calcBtn = new Bouton("Calculate Homology", BoutonType.BIG);
    private final Bouton quitBtn = new Bouton("Quit", BoutonType.BIG);

    /**
     * Creates a new {@code DBWindow}.
     */
    MainWindow() {
        super();

        root.getChildren().addAll(drawBtn, databaseBtn, calcBtn, quitBtn);

        quitBtn.setCancelButton(true);
        drawBtn.requestFocus();

        drawBtn.setOnAction(e -> WindowsControler.setDrawWindow(getFade()));
        databaseBtn.setOnAction(e -> WindowsControler.setDBWindow(getFade()));
        calcBtn.setOnAction(e -> WindowsControler.setCalcWindow(getFade()));
        quitBtn.setOnAction(e -> WindowsControler.exit());
    }

    /**
     * Returns the disable property of the database button.
     *
     * @return The database button's disable {@code BooleanProperty}.
     */
    public BooleanProperty getDatabaseButtonDisableProperty() {
        return databaseBtn.disableProperty();
    }

    /**
     * Returns the disable property of the calculus button.
     *
     * @return The calculus button's disable {@code BooleanProperty}.
     */
    public BooleanProperty getCalcButtonDisableProperty() {
        return calcBtn.disableProperty();
    }
}
