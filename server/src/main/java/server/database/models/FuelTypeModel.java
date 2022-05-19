package server.database.models;

import java.util.Arrays;

public class FuelTypeModel extends Model {
    public FuelTypeModel() {
        this.name = "FuelTypes";
        this.fields = Arrays.asList(new FieldDB("id", "SERIAL PRIMARY KEY"),
                new FieldDB("name", "varchar(256)"));
    }
}
