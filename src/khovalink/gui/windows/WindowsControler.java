package khovalink.gui.windows;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import khovalink.KhovaLog;
import khovalink.gui.components.InfoProgressBar;
import khovalink.homology.LinkHomology;
import khovalink.persistence.Link;
import khovalink.persistence.LinkDAO;
import khovalink.persistence.LinkFactory;

/**
 * Static class representing a controler for windows.
 *
 * @author flo
 */
public class WindowsControler {

    private static Stage stage;

    private static MainWindow mainWindow;
    private static CalcWindow calcWindow;
    private static DrawWindow drawWindow;
    private static DBWindow dbWindow;

    private static ObjectProperty<Link> linkProperty;

    /**
     * Non instanciable class.
     */
    private WindowsControler() {
    }

    /**
     * Initializes the windows and sets the main one.
     *
     * @param myStage The {@code Stage} to initialize windows.
     */
    public static void initAndLaunchMainWindow(final Stage myStage) {
        stage = myStage;
        MainWindow.setStage(stage);

        mainWindow = new MainWindow();
        dbWindow = new DBWindow();
        calcWindow = new CalcWindow();
        drawWindow = new DrawWindow();

        linkProperty = new SimpleObjectProperty<>();
        mainWindow.getDatabaseButtonDisableProperty().bind(dbWindow.getComboEmptyBooleanBinging());
        mainWindow.getCalcButtonDisableProperty().bind(linkProperty.isNull());
        mainWindow.getScene().getRoot().setOpacity(0);
        stage.setScene(mainWindow.getScene());
        stage.centerOnScreen();
        mainWindow.getUnfade().play();
    }

    /**
     * Fades main window and calls the application closing.
     */
    public static void exit() {
        mainWindow.getFade().setOnFinished(e -> Platform.exit());
        mainWindow.getFade().play();
    }

    /**
     * Sets the main window on screen.
     *
     * @param fade The {@code FadeTransition} to fade before setting the window.
     */
    public static void setMainWindow(final FadeTransition fade) {
        mainWindow.getScene().getRoot().setOpacity(0);
        fade.setOnFinished(a -> {
            stage.setScene(mainWindow.getScene());
            stage.centerOnScreen();
            mainWindow.getUnfade().play();
        });
        fade.play();
    }

    /**
     * Sets the database window on screen.
     *
     * @param fade The {@code FadeTransition} to fade before setting the window.
     */
    public static void setDBWindow(final FadeTransition fade) {
        dbWindow.getScene().getRoot().setOpacity(0);
        fade.setOnFinished(a -> {
            stage.setScene(dbWindow.getScene());
            stage.centerOnScreen();
            dbWindow.getUnfade().play();
        });
        fade.play();
    }

    /**
     * Sets the calculus window on screen.
     *
     * @param fade The {@code FadeTransition} to fade before setting the window.
     */
    public static void setCalcWindow(final FadeTransition fade) {
        calcWindow.getScene().getRoot().setOpacity(0);
        fade.setOnFinished(a -> {
            stage.setScene(calcWindow.getScene());
            stage.centerOnScreen();
            calcWindow.getUnfade().play();
        });
        fade.play();
    }

    /**
     * Sets the draw window on screen.
     *
     * @param fade The {@code FadeTransition} to fade before setting the window.
     */
    public static void setDrawWindow(final FadeTransition fade) {
        drawWindow.getScene().getRoot().setOpacity(0);
        fade.setOnFinished(a -> {
            stage.setScene(drawWindow.getScene());
            stage.centerOnScreen();
            drawWindow.getUnfade().play();
        });
        fade.play();
    }

    /**
     * Deletes a link in database.
     *
     * @param name The name of the link to delete.
     */
    public static void deleteLinkFromDatabase(final String name) {
        try {
            LinkDAO.getInstance().delete(name);
        } catch (final IOException ex) {
            KhovaLog.addLog(ex);
            showError(ex);
        }
    }

    /**
     * Sets a link from database.
     *
     * @param name The name of the link to set.
     * @param fade The {@code FadeTransition} to fade before setting the draw
     * window.
     */
    public static void setLinkFromDatabase(final String name, final FadeTransition fade) {
        try {
            final Link link = LinkDAO.getInstance().find(name);
            setLink(link);
            drawWindow.setLink(link);
            setDrawWindow(fade);
        } catch (final ClassNotFoundException | IOException ex) {
            showError(ex);
            KhovaLog.addLog(ex);
        }
    }

    /**
     * Saves a link in the database.
     *
     * @param link The {@code Link} to save.
     *
     * @return A {@code boolean} set to {@code true} if the link has been saved
     * without error and to {@code false} otherwise.
     */
    public static boolean saveLinkToDatabase(Link link) {
        try {
            while (LinkDAO.getInstance().isPresent(link.getName())) {
                final TextInputDialog dialog = new TextInputDialog("NewName");
                dialog.setTitle("Incorrect name");
                dialog.setHeaderText("Another link already has the same name in the database.");
                dialog.setContentText("Please enter another name :");
                dialog.getEditor().setOnKeyTyped(e -> {
                    if (e.getCharacter().matches("[^\\w+-._]")) {
                        e.consume();
                    }
                });
                if (dialog.showAndWait().filter(name -> !name.isEmpty()).isPresent()) {
                    link = LinkFactory.rename(dialog.getResult(), link);
                    drawWindow.setName(link.getName());
                } else {
                    return false;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(WindowsControler.class.getName()).log(Level.SEVERE, null, ex);
        }

        final Alert alert;
        try {
            LinkDAO.getInstance().save(link);
            alert = new Alert(Alert.AlertType.INFORMATION, null);
            alert.setTitle("Confirmation");
            alert.setHeaderText("The link has been saved !");
        } catch (final IOException ex) {
            showError(ex);
            return false;
        }

        alert.showAndWait();
        setLink(link);

        return true;
    }

    /**
     * Sets a link as the current link.
     *
     * @param link The link to set.
     */
    public static void setLink(final Link link) {
        linkProperty.set(link);
    }

    /**
     * Gets the current link.
     *
     * @return The current link.
     */
    public static Link getLink() {
        return linkProperty.get();
    }

    /**
     * Launches the Khovanov homology calculus.
     */
    public static void launchCalc() {
        final LinkHomology homology = new LinkHomology(linkProperty.get());
        final InfoProgressBar bar = new InfoProgressBar(homology.progressProperty());
        final Thread thread = new Thread(homology);

        homology.messageProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                calcWindow.setText((String) newValue);
            }
        });

        homology.setOnSucceeded(e -> {
            try {
                bar.close();
                calcWindow.appendText("\n\n\tResult :\n" + homology.get().toString());
            } catch (final InterruptedException | ExecutionException ex) {
                KhovaLog.addLog(ex);
                calcWindow.appendText("\n\n--- An exception appened ! ---\n\n" + ex.getMessage());
            }
        });
        homology.setOnFailed(e -> {
            bar.close();
            calcWindow.setText("\n\n--- Calculation failed ! ---\n\n");
        });
        homology.setOnCancelled(e -> {
            bar.close();
            calcWindow.setText("\n\n--- Calculation cancelled ! ---\n\n");
        });

        bar.show();
        calcWindow.setProcess(homology);
        thread.start();
    }

    /**
     * Display an {@code Alert} with an exception message.
     *
     * @param <E> The exception's type.
     * @param ex The exception.
     */
    private static <E extends Exception> void showError(final E ex) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, null);
        alert.setTitle("Error");
        alert.setHeaderText(ex.toString());
        alert.showAndWait();
    }
}
