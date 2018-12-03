package khovalink.gui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import khovalink.gui.components.Txt.TxtType;

/**
 * Class representing a progress bar.
 *
 * @author flo
 */
public final class InfoProgressBar {

    private final Stage stage = new Stage(StageStyle.UNDECORATED);
    private final HBox root = new HBox(5);
    private final Scene scene = new Scene(root);

    private final Background background = new Background(new BackgroundFill(Color.color(0, 0, 1, 0.8), new CornerRadii(10), Insets.EMPTY));

    private final Txt percent = new Txt("", TxtType.SMALL);
    private final ProgressBar progressBar = new ProgressBar(0);

    /**
     * Creates a new {@code ProgressInfo}.
     *
     * @param property The property to link with to update progression.
     */
    public InfoProgressBar(final ReadOnlyDoubleProperty property) {
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);

        root.setBackground(background);
        root.setPadding(new Insets(2, 2, 2, 2));
        root.getChildren().addAll(progressBar, percent);
        root.setAlignment(Pos.CENTER);

        progressBar.progressProperty().bind(property);
        percent.textProperty().bind(IntegerProperty.integerExpression(property.multiply(100)).asString().concat('%'));
    }

    /**
     * Shows the {@code ProgressInfo}.
     */
    public void show() {
        stage.show();
    }

    /**
     * Closes the {@code ProgressInfo}.
     */
    public void close() {
        stage.close();
    }
}
