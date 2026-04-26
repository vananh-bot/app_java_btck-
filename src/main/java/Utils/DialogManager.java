package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import Enum.Screen;

public class DialogManager {

    private static DialogManager instance;
    private StackPane rootStack;
    private BorderPane rootLayout;

    private DialogManager(){}

    public static DialogManager getInstance(){
        if(instance == null){
            instance = new DialogManager();
        }
        return instance;
    }

    public void setRootStack(StackPane rootStack, BorderPane rootLayout){
        this.rootStack = rootStack;
        this.rootLayout = rootLayout;
    }

    public void show(Screen screen){

        try{

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(screen.getFxmlPath())
            );

            Parent dialog = loader.load();

            // blur nền
            rootLayout.setEffect(new GaussianBlur(30));

            rootStack.getChildren().add(dialog);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void show(Screen screen, Object data){

        try{

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(screen.getFxmlPath())
            );

            Parent dialog = loader.load();
            Object controller = loader.getController();

            if(controller instanceof DataReceiver){
                ((DataReceiver<Object>) controller).initData(data);
            }

            // blur nền
            rootLayout.setEffect(new GaussianBlur(40));

            rootStack.getChildren().add(dialog);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void close(Node dialog){
        rootStack.getChildren().remove(dialog);

        if(rootStack.getChildren().size() <= 1){
            rootLayout.setEffect(null);
        }
    }
    public void closeAll(){
        if(rootStack != null){
            // giữ lại rootLayout, xóa toàn bộ dialog overlay
            rootStack.getChildren().removeIf(node -> node != rootLayout);
        }

        if(rootLayout != null){
            rootLayout.setEffect(null);
        }
    }


}