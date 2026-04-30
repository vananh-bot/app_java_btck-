package Utils;

import Controller.SidebarController;
import Enum.Screen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class ScreenManager {
    private SidebarController sidebarController;


    private Screen currentScreen;
    private Screen previousScreen;

    public void setSidebarController(SidebarController sidebarController){
        this.sidebarController = sidebarController;
    }

    private static ScreenManager instance;

    private BorderPane rootLayout;

    private ScreenManager(){}

    public static ScreenManager getInstance(){
        if(instance == null){
            instance = new ScreenManager();
        }
        return instance;
    }

    public Screen getCurrentScreen(){
        return currentScreen;
    }

    public Screen getPreviousScreen(){
        return previousScreen;
    }


    public void setRootLayout(BorderPane rootLayout){
        this.rootLayout = rootLayout;
    }
    public BorderPane getRootLayout(){
        return rootLayout;
    }

    // ================= SHOW NORMAL =================
    public void show(Screen screen){

        if(screen == null || rootLayout == null) return;

        try{

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(screen.getFxmlPath())
            );

            Parent view = loader.load();

            // update state
            previousScreen = currentScreen;
            currentScreen = screen;

            // update sidebar highlight
            if (sidebarController != null) {
                sidebarController.setActive1(screen);
            }

            // close dialogs
            DialogManager.getInstance().closeAll();

            // change view
            rootLayout.setCenter(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void back(){
        if(previousScreen != null){
            show(previousScreen);
        }
    }

    public void show(Screen screen, Object data){

        try{

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(screen.getFxmlPath())
            );

            Parent view = loader.load();
            Object controller = loader.getController();

            if(controller instanceof DataReceiver){
                ((DataReceiver<Object>) controller).initData(data);
            }

            if (sidebarController != null) {
                sidebarController.setActive1(screen);
            }

            DialogManager.getInstance().closeAll();

            rootLayout.setCenter(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}