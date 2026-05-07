package Controller;

import Cache.ProjectCache;
import DAO.ProjectDAO;
import Service.ProjectService;
import Utils.DataReceiver;
import Utils.DialogManager;
import Utils.ScreenManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import Enum.Screen;
import javafx.scene.text.Font;

public class ProjectDescriptionCard implements DataReceiver<Integer> {

    @FXML
    private ImageView back_to_project;

    @FXML
    private TextArea description;

    @FXML
    private StackPane editBtn;
    @FXML
    private StackPane overlay;

    private final ProjectService projectService =
            new ProjectService(new ProjectDAO());

    private int projectId;

    private boolean editing = false;

    @Override
    public void initData(Integer projectId){
        this.projectId = projectId;
        description.setText(ProjectCache.getInstance().get(projectId).getPreviewDescription());
        loadDescription();
    }

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

    private void loadDescription() {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return projectService.getDescriptionByProjectId(projectId);
            }
        };

        task.setOnSucceeded(e -> {
            String desc =task.getValue();

            if (desc != null) {
                description.setText(desc);
            } else description.setText("Không có mô tả dự án");
        });
        task.setOnFailed(e -> {
            description.setText("Lỗi tải dữ liệu");
        });
        new Thread(task).start();

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

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                ProjectCache.getInstance().updateDescription(projectId, projectService.convertToPreviewDescription(newDescription));
                projectService.updateDescription(projectId, newDescription);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            javafx.application.Platform.runLater(this::handleBackLogic);
        });
        new Thread(task).start();
    }

    private void handleBack(MouseEvent event) {
        handleBackLogic();
    }
    private void handleBackLogic(){
        DialogManager.getInstance().close(overlay);

        MainProjectController controller =
                ScreenManager.getInstance().getMainProjectController();

        if (controller != null) {

            String preview = ProjectCache.getInstance()
                    .get(projectId)
                    .getPreviewDescription();

            controller.updateDescription(preview);
        }
    }
}
