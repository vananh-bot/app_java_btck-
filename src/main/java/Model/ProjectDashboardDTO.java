package Model;

public class ProjectDashboardDTO {
    private int id;
    private String name;
    private int toDoCount, doneCount, inProgressCount;
    private double progress;

    public ProjectDashboardDTO(int id, String name, int toDoCount, int inProgressCount, int doneCount){
        this.id = id;
        this.name = name;
        this.toDoCount = toDoCount;
        this.inProgressCount = inProgressCount;
        this.doneCount = doneCount;

        int total = toDoCount + inProgressCount + doneCount;

        if(total == 0){
            this.progress = 0;
        } else {
            this.progress = (double) doneCount / total;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getToDoCount() {
        return toDoCount;
    }

    public void setToDoCount(int toDoCount) {
        this.toDoCount = toDoCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public int getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
