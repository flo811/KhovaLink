package khovalink;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import khovalink.gui.windows.WindowsControler;

/**
 * Entry class of KhovaLink application.
 *
 * @author flo
 */
public final class KhovaLink extends Application {

    @Override
    public void start(final Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("KhovaLink");
        stage.getIcons().add(new Image("Trefoil.png"));
        stage.show();

        WindowsControler.initAndLaunchMainWindow(stage);
    }

    @Override
    public void stop() {
        KhovaLog.close();
    }
}
