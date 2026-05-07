package Controller;

import DAO.ProjectDAO;
import Service.ProjectService;
import Utils.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import Enum.Screen;
import javafx.scene.text.Font;

public class ProjectDescriptionCard {

    @FXML
    private ImageView back_to_project;

    @FXML
    private TextArea description;

    @FXML
    private StackPane editBtn;

    private final ProjectService projectService =
            new ProjectService(new ProjectDAO());

    private int projectId;

    private boolean editing = false;

    @FXML
    public void initialize() {

        description.setEditable(false);
        description.setFocusTraversable(false);

        editBtn.setOnMouseClicked(e -> enableEdit());

        back_to_project.setOnMouseClicked(this::handleBack);

        description.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && editing) {

                saveDescription();

                editing = false;

                description.setEditable(false);
                description.setFocusTraversable(false);
            }
        });
        description.setFont(REG);
    }
    private static final Font REG =
            Font.loadFont(
                    ProjectDescriptionCard.class.getResourceAsStream(
                            "/fonts/Inter_18pt-Medium.ttf"
                    ),
                    14
            );

    public void setProjectId(int projectId) {

        this.projectId = projectId;

        loadDescription();
    }

    private void loadDescription() {

        String desc =
                projectService.getDescriptionByProjectId(projectId);

        if (desc != null) {
            description.setText(desc);
        }
    }

    private void enableEdit() {

        editing = true;

        description.setEditable(true);
        description.setFocusTraversable(true);

        description.requestFocus();

        description.positionCaret(
                description.getText().length()
        );
    }

    private void saveDescription() {

        String newDescription = description.getText();

        ProjectDAO projectDAO = new ProjectDAO();

        var project = projectDAO.findById(projectId);

        if (project != null) {

            project.setDescription(newDescription);

            boolean updated = projectDAO.update(project);

            if (updated) {
                System.out.println("Description updated");
            } else {
                System.out.println("Update failed");
            }
        }
    }

    private void handleBack(MouseEvent event) {

        ScreenManager.getInstance()
                .show(Screen.MAIN_PROJECT_VIEW, projectId);
    }
}
