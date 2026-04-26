package Enum;

public enum Screen {
    DASHBOARD("/dashboard/general_dashboard_view2.fxml"),
    ALL_MY_PROJECT("/project/AllMyProjectView.fxml"),
    MAIN_PROJECT_VIEW("/project/mainProjectView.fxml"),
    TASK_DETAILS("/task/taskdetails.fxml"),
    CREATE_PROJECT("/project/createProject.fxml"),
    NOTIFICATION("/notification/notification.fxml"),
    CREATE_TASK("/task/createTask.fxml"),
    LOGIN("/auth/login.fxml"),
    REGISTER("/auth/register.fxml"),
    MEMBER_LIST("/invite/MemberList.fxml"),
    INVITE_MEMBER("/invite/email.fxml"),
    ENTER_TOKEN("/invite/token.fxml");

    private final String fxmlPath;

    Screen(String fxmlPath){
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath(){
        return fxmlPath;
    }
}
