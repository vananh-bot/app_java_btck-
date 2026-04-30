package Utils;

public class ProjectContext {
    private static int currentProjectId;

    public static void setProjectId(int id) {
        currentProjectId = id;
    }

    public static int getProjectId() {
        return currentProjectId;
    }
}