package DTO;

import java.time.LocalDateTime;

public class ProjectDashboardDTO {
    private int id;
    private String name;
    private int ownerId;
    private String ownerName;
    private int toDoCount, doneCount, inProgressCount;
    private String previewDescription;

    public ProjectDashboardDTO(int id, String name, int toDoCount, int inProgressCount, int doneCount, int ownerId, String ownerName, String previewDescription){
        this.id = id;
        this.name = name;
        this.toDoCount = toDoCount;
        this.inProgressCount = inProgressCount;
        this.doneCount = doneCount;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.previewDescription = previewDescription;
    }
    public ProjectDashboardDTO(){}

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
        int total = toDoCount + inProgressCount + doneCount;
        return total == 0 ? 0 : (double) doneCount / total;
    }
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPreviewDescription() {
        return previewDescription;
    }

    public void setPreviewDescription(String previewDescription) {
        this.previewDescription = previewDescription;
    }
}
