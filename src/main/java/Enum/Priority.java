package Enum;

public enum Priority {
    LOW("Thấp"),
    MEDIUM("Trung bình"),
    HIGH("Cao");

    private final String display;

    Priority(String display){
        this.display = display;
    }

    @Override
    public String toString(){
        return display;
    }
}
