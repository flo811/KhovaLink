package khovalink.gui.windows;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import khovalink.gui.components.Bouton;
import khovalink.gui.components.Bouton.BoutonType;
import khovalink.gui.components.Txt;
import khovalink.gui.components.Txt.TxtType;
import khovalink.gui.components.TxtField;
import khovalink.gui.windows.canvas.LinkCanvas;
import khovalink.persistence.GraphicalLink;
import khovalink.persistence.Link;
import khovalink.persistence.LinkException;
import khovalink.persistence.LinkFactory;

/**
 * Class representing the main window.
 *
 * @author flo
 */
public final class DrawWindow extends Window {

    private final Bouton validateBtn = new Bouton("Validate", BoutonType.SMALL);
    private final Bouton saveBtn = new Bouton("Save", BoutonType.SMALL);
    private final Bouton wipeBtn = new Bouton("Wipe", BoutonType.SMALL);
    private final Bouton backBtn = new Bouton("Back", BoutonType.SMALL);

    private final Txt infoTxt = new Txt("Draw a link or directly type the informations :", TxtType.TITLE);
    private final Txt nameTxt = new Txt("Link name :", TxtType.SMALL);
    private final Txt nbCross = new Txt("Crossing number :", TxtType.SMALL);
    private final Txt nbrCompoTxt = new Txt("Component number :", TxtType.SMALL);
    private final Txt gaussCodeTxt = new Txt("Gauss code :", TxtType.SMALL);
    private final Txt signsTxt = new Txt("Crossings signs (+/-) :", TxtType.SMALL);

    private final TxtField nameTF = new TxtField();
    private final TxtField nbCrossTF = new TxtField();
    private final TxtField gaussCodeTF = new TxtField();
    private final TxtField nbrComponentsTF = new TxtField();
    private final TxtField signsTF = new TxtField();

    private final LinkCanvas canvas = new LinkCanvas();

    private final VBox nameVB = new VBox(5, nameTxt, nameTF);
    private final VBox nbCrossVB = new VBox(5, nbCross, nbCrossTF);
    private final VBox componentVB = new VBox(5, nbrCompoTxt, nbrComponentsTF);
    private final VBox gaussCodeVB = new VBox(5, gaussCodeTxt, gaussCodeTF);
    private final VBox signsVB = new VBox(5, signsTxt, signsTF);
    private final VBox rightVB = new VBox(60, nameVB, nbCrossVB, componentVB, gaussCodeVB, signsVB);
    private final HBox centerHB = new HBox(10, canvas, rightVB);
    private final HBox southHB = new HBox(30, validateBtn, saveBtn, wipeBtn, backBtn);

    private final SimpleObjectProperty<GraphicalLink> graphicalLinkProperty = canvas.getGraphicalLinkProperty();

    /**
     * Creates a new {@code DrawWindow}.
     */
    DrawWindow() {
        super();

        root.getChildren().addAll(infoTxt, centerHB, southHB);

        rightVB.setAlignment(Pos.CENTER);
        southHB.setAlignment(Pos.CENTER);

        backBtn.setCancelButton(true);

        validateBtn.disableProperty().bind(nameTF.textProperty().isEmpty()
                .or(nbCrossTF.textProperty().isEmpty())
                .or(nbrComponentsTF.textProperty().isEmpty())
                .or(gaussCodeTF.textProperty().isEmpty())
                .or(signsTF.textProperty().isEmpty().and(nbCrossTF.textProperty().isNotEqualTo("0"))));
        saveBtn.disableProperty().bind(validateBtn.disableProperty());
        wipeBtn.disableProperty().bind(nameTF.textProperty().isEmpty()
                .and(nbCrossTF.textProperty().isEmpty())
                .and(signsTF.textProperty().isEmpty())
                .and(nbrComponentsTF.textProperty().isEmpty())
                .and(gaussCodeTF.textProperty().isEmpty())
                .and(canvas.getIsEmptyProperty()));

        graphicalLinkProperty.addListener((grLink, oldGrLink, newGrLink) -> {
            if (newGrLink != null) {
                final String name = nameTF.getText();
                final String newName = name.isEmpty() || "NewKnot".equals(name) ? graphicalLinkProperty.get().getCurves().size() == 1 ? "NewKnot" : "NewLink" : name;
                setLinkInfos(LinkFactory.createSafe(newName, newGrLink));
            }
        });

        scene.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.DELETE)) {
                wipeBtn.fire();
            } else if (key.getCode().equals(KeyCode.ENTER)) {
                if (scene.focusOwnerProperty().get() instanceof Bouton) {
                    ((ButtonBase) scene.focusOwnerProperty().get()).fire();
                } else {
                    validateBtn.fire();
                }
            }
        });

        nameTF.setOnKeyTyped(e -> {
            if (e.getCharacter().matches("[^\\w+-._]")) {
                e.consume();
            } else if ("NewKnot".equals(nameTF.getText()) || "NewLink".equals(nameTF.getText())) {
                nameTF.clear();
            }
        });
        nbCrossTF.setOnKeyTyped(e -> {
            if (e.getCharacter().matches("[^0-9]")) {
                e.consume();
            } else {
                canvas.clear();
            }
        });
        nbrComponentsTF.setOnKeyTyped(e -> {
            if (e.getCharacter().matches("[^0-9]")) {
                e.consume();
            } else {
                canvas.clear();
            }
        });
        gaussCodeTF.setOnKeyTyped(e -> {
            if (e.getCharacter().matches("[^0-9+-\\[\\]]")) {
                e.consume();
            } else {
                canvas.clear();
            }
        });
        signsTF.setOnKeyTyped(e -> {
            if (e.getCharacter().matches("[^+-]")) {
                e.consume();
            } else {
                canvas.clear();
            }
        });

        validateBtn.setOnAction(e -> {
            final Link link = getLink();
            if (link != null) {
                WindowsControler.setLink(link);
                WindowsControler.setCalcWindow(getFade());
            }
        });
        saveBtn.setOnAction(e -> {
            final Link linkToSave = getLink();
            if (linkToSave != null) {
                if (WindowsControler.saveLinkToDatabase(linkToSave)) {
                    WindowsControler.setMainWindow(getFade());
                }
            }
        });
        wipeBtn.setOnAction(e -> {
            canvas.clear();
            clearFields();
            nameTF.setText("");
        });
        backBtn.setOnAction(e -> WindowsControler.setMainWindow(getFade()));
    }

    /**
     * Sets a link in the canvas and fills information's fields.
     *
     * @param link The link to set.
     */
    public void setLink(final Link link) {
        canvas.setLink(link.getGraphicalLink());
        setName(link.getName());
    }

    /**
     * Sets the name of the link.
     *
     * @param name The link's name.
     */
    public void setName(final String name) {
        nameTF.setText(name);
    }

    /**
     * Sets the information fields with the link's ones.
     *
     * @param link The link from wich to get informations.
     */
    public void setLinkInfos(final Link link) {
        setName(link.getName());
        nbCrossTF.setText(Integer.toString(link.getNbCross()));
        nbrComponentsTF.setText(Integer.toString(link.getNbCompo()));

        final StringBuilder gaussStr = new StringBuilder("[");
        for (final int[] gau : link.getGauss()) {
            for (int i : gau) {
                gaussStr.append(i > 0 ? '+' : "").append(i);
            }
            gaussStr.append("][");
        }
        gaussStr.deleteCharAt(gaussStr.length() - 1);
        gaussCodeTF.setText(gaussStr.toString());

        final StringBuilder signsStr = new StringBuilder("");
        for (boolean sign : link.getSigns()) {
            signsStr.append(sign ? "+" : "-");
        }
        signsTF.setText(signsStr.toString());

        validateBtn.requestFocus();
    }

    /**
     * Returns a {@code Link} from the canvas's or from fields's informations.
     *
     * @return The link.
     */
    private Link getLink() {
        Link linkToSave;
        try {
            if (graphicalLinkProperty.isNull().get()) {
                linkToSave = LinkFactory.create(nameTF.getText(), nbrComponentsTF.getText(), nbCrossTF.getText(),
                        gaussCodeTF.getText(), signsTF.getText());
            } else {
                linkToSave = LinkFactory.create(nameTF.getText(), nbrComponentsTF.getText(), nbCrossTF.getText(),
                        gaussCodeTF.getText(), signsTF.getText(), graphicalLinkProperty.get());
            }
        } catch (final LinkException ex) {
            final Alert alert = new Alert(Alert.AlertType.WARNING, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("The link is invalid for the following reasons :");
            alert.showAndWait();
            return null;
        }

        if (!linkToSave.getName().equals(nameTF.getText())) {
            linkToSave = LinkFactory.rename(nameTF.getText(), linkToSave);
        }

        return linkToSave;
    }

    /**
     * Clears the fields except the name one.
     */
    private void clearFields() {
        nbCrossTF.setText("");
        nbrComponentsTF.setText("");
        gaussCodeTF.setText("");
        signsTF.setText("");
    }
}
