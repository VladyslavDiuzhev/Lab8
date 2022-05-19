package server.database.models;

import java.util.Arrays;

public class VehicleTypeModel extends Model {
    public VehicleTypeModel(){
        this.name = "VehicleTypes";
        this.fields = Arrays.asList(new FieldDB("id", "SERIAL PRIMARY KEY"),
                new FieldDB("name","varchar(256)"));
    }
}
