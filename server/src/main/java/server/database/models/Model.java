package server.database.models;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;
import org.postgresql.Driver;

public abstract class Model {
    protected List<FieldDB> fields;
    protected String name;

    public Model(){

    }

    public String getName() {
        return name;
    }

    public boolean createTable(Connection c) {

        PreparedStatement stmt;

        StringBuilder CreateSql = new StringBuilder();
        try {
            CreateSql.append("create table ").append(name).append("(\n");
            for (FieldDB el : fields) {
                CreateSql.append(el.getFieldString()).append(",");
            }
            CreateSql.deleteCharAt(CreateSql.length()-1);
            CreateSql.append(");");

            stmt = c.prepareStatement(CreateSql.toString());
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }


}
