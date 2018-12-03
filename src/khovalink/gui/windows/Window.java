package khovalink.gui.windows;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import khovalink.gui.components.Bouton;

/**
 * Abstract class representing a window.
 *
 * @author flo
 */
class Window {

    private static Stage stage;

    private static double xRelPos;
    private static double yRelPos;

    protected final VBox root = new VBox(10);
    protected final Scene scene;

    private final Background background = new Background(new BackgroundFill(Color.color(0, 0, 1, 0.8), new CornerRadii(40), Insets.EMPTY));
    
    private final FadeTransition fade = new FadeTransition(Duration.millis(700), root);
    private final FadeTransition unfade = new FadeTransition(Duration.millis(700), root);

    /**
     * Creates a {@code Window}.
     */
    protected Window() {
        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15, 15, 15, 15));
        root.setBackground(background);

        fade.setInterpolator(Interpolator.LINEAR);
        fade.setFromValue(1);
        fade.setToValue(0);
        unfade.setInterpolator(Interpolator.LINEAR);
        unfade.setFromValue(0);
        unfade.setToValue(1);

        root.setOnKeyPressed(key -> {
            final Node focused = scene.focusOwnerProperty().get();
            if (focused instanceof Bouton && key.getCode().equals(KeyCode.ENTER)) {
                ((ButtonBase) focused).fire();
            }
        });

        scene.setOnMousePressed(e -> {
            xRelPos = stage.getX() - e.getScreenX();
            yRelPos = stage.getY() - e.getScreenY();
        });
        scene.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() + xRelPos);
            stage.setY(event.getScreenY() + yRelPos);
        });
    }

    /**
     * Returns the scene.
     *
     * @return scene The {@code Scene}.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Returns the transition to fade the window.
     *
     * @return fade The {@code FadeTransition}.
     */
    public FadeTransition getFade() {
        return fade;
    }

    /**
     * Returns the transition to unfade the window.
     *
     * @return fade The {@code FadeTransition}.
     */
    public FadeTransition getUnfade() {
        return unfade;
    }

    /**
     * Sets the stage.
     *
     * @param myStage The {@code Stage} to set.
     */
    public static void setStage(final Stage myStage) {
        stage = myStage;
    }
}
