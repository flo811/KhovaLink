package khovalink.gui.windows;

import java.io.IOException;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import khovalink.KhovaLog;
import khovalink.gui.components.Bouton;
import khovalink.gui.components.Bouton.BoutonType;
import khovalink.persistence.LinkDAO;

/**
 * Class representing the database window.
 *
 * @author flo
 */
public final class DBWindow extends Window {

    private final ComboBox<String> combo = new ComboBox<>();

    private final Bouton deleteBtn = new Bouton("Delete", BoutonType.SMALL);
    private final Bouton validateBtn = new Bouton("Select", BoutonType.SMALL);
    private final Bouton backBtn = new Bouton("Back", BoutonType.SMALL);

    private final HBox hBox = new HBox(30, validateBtn, backBtn);

    /**
     * Creates a new {@code DBWindow}.
     *
     * @param names An {@code ObservableList<String>} to initialize the
     * {@code ComboBox<String>}.
     */
    DBWindow() {
        super();

        root.getChildren().addAll(combo, deleteBtn, hBox);

        SortedList<String> items;
        try {
            items = LinkDAO.getInstance().findAll();
        } catch (final IOException ex) {
            final ObservableList<String> empty = FXCollections.emptyObservableList();
            items = empty.sorted();
            KhovaLog.addLog(ex);
        }

        combo.setItems(items);
        combo.setPromptText("Select a link :");
        combo.setStyle("-fx-font: 15px \"Comic Sans MS\";");
        hBox.setAlignment(Pos.CENTER);

        backBtn.setCancelButton(true);
        scene.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.DELETE)) {
                deleteBtn.fire();
            }
        });

        deleteBtn.disableProperty().bind(combo.valueProperty().isNull());
        validateBtn.disableProperty().bind(deleteBtn.disableProperty());

        combo.setOnAction(e -> validateBtn.requestFocus());
        validateBtn.setOnAction(e -> WindowsControler.setLinkFromDatabase(combo.getValue(), getFade()));
        deleteBtn.setOnAction(e -> WindowsControler.deleteLinkFromDatabase(combo.getValue()));
        backBtn.setOnAction(e -> WindowsControler.setMainWindow(getFade()));
    }

    /**
     * Returns a combo's empty {@code BooleanBinding}.
     *
     * @return The combo's empty {@code BooleanBinding}.
     */
    public BooleanBinding getComboEmptyBooleanBinging() {
        return combo.itemsProperty().isEqualTo(FXCollections.emptyObservableList());
    }
}
