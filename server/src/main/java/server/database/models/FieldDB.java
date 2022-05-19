package server.database.models;

public class FieldDB {
    private final String name;
    private final String type;

    public FieldDB(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getFieldString(){
        return String.format("%s %s", this.name, this.type);
    }
}
