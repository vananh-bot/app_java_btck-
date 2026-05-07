package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ProjectDescriptionCard {

    @FXML
    private ImageView back_to_project;

    @FXML
    private TextArea description;

    @FXML
    private StackPane editBtn;

    private boolean editing = false;

    @FXML
    private void handleEditDescription() {

        editing = !editing;

        description.setEditable(editing);
        description.setFocusTraversable(editing);

        if (editing) {

            description.requestFocus();
            description.positionCaret(
                    description.getText().length()
            );

        } else {

            saveDescription();
        }
    }

    private void saveDescription() {


    }

}
