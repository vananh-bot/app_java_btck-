package Controller;

import Enum.Screen;
import Utils.DialogManager;
import Utils.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private BorderPane rootLayout;

    @FXML
    private StackPane rootStack;

    @FXML
    private StackPane contentArea;

    @FXML
    void initialize(){

        ScreenManager sm = ScreenManager.getInstance();

        sm.setRootLayout(rootLayout);

        DialogManager.getInstance()
                .setRootStack(rootStack, rootLayout);

        // màn mặc định
        sm.show(Screen.DASHBOARD);

    }

    public BorderPane getRootLayout(){
        return rootLayout;
    }
    public StackPane getContentArea(){
        return contentArea;
    }

}