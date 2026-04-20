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
    void initialize(){

        // đưa rootLayout cho ScreenManager
        ScreenManager.getInstance().setRootLayout(rootLayout);

        DialogManager.getInstance().setRootStack(rootStack, rootLayout);

        // mở dashboard mặc định
        ScreenManager.getInstance().show(Screen.DASHBOARD);

    }

    public BorderPane getRootLayout(){
        return rootLayout;
    }

}