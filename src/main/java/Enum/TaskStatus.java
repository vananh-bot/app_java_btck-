package Enum;

public enum TaskStatus {
    TODO("Cần làm"),
    IN_PROGRESS("Đang thực hiện"),
    DONE("Hoàn thành");

    private final String display;

    TaskStatus(String display){
        this.display = display;
    }

    @Override
    public String toString(){
        return display;
    }
}
