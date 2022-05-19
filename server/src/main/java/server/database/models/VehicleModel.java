package server.database.models;

import core.essentials.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Stack;

public class VehicleModel extends Model {
    public VehicleModel() {
        this.name = "Vehicles";
        this.fields = Arrays.asList(new FieldDB("id", "SERIAL PRIMARY KEY"),
                new FieldDB("name", "varchar(256)"),
                new FieldDB("y", "float8"),
                new FieldDB("x", "int"),
                new FieldDB("creation_date", "varchar(256)"),
                new FieldDB("engine_power", "float8"),
                new FieldDB("type", "varchar(256)"),
                new FieldDB("fuel_type", "varchar(256)"),
                new FieldDB("owner_id", "int"));
    }

    public Vehicle findById(Connection connection, int id) {
        Vehicle vehicle = new Vehicle();
        PreparedStatement stmt;
        StringBuilder CreateSql = new StringBuilder();
        try {
            CreateSql.append(String.format("SELECT * FROM %s WHERE id='%s'", this.name, id));
            stmt = connection.prepareStatement(CreateSql.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setCoordinates(new Coordinates(rs.getLong("x"), rs.getDouble("y")));
                vehicle.setEnginePower(rs.getFloat("engine_power"));
                vehicle.generateId(rs.getInt("id"));
                vehicle.setName(rs.getString("name"));
                vehicle.setFuelType(FuelType.getByName(rs.getString("fuel_type")));
                vehicle.setType(VehicleType.getByName(rs.getString("type")));
                vehicle.setCreationDate(ZonedDateTime.parse(rs.getString("creation_date")));
            }
        } catch (Exception e) {
            return null;
        }
        return vehicle;
    }

    public Stack<Vehicle> findAll(Connection connection) {
        Stack<Vehicle> stack = new Stack<>();
        PreparedStatement stmt;
        StringBuilder CreateSql = new StringBuilder();
        try {
            CreateSql.append(String.format("SELECT * FROM %s", this.name));
            stmt = connection.prepareStatement(CreateSql.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
//                System.out.println("LOl");
                Vehicle vehicle = new Vehicle();
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setCoordinates(new Coordinates(rs.getLong("x"), rs.getDouble("y")));
                vehicle.setEnginePower(rs.getFloat("engine_power"));
                vehicle.generateId(rs.getInt("id"));
                vehicle.setName(rs.getString("name"));
                vehicle.setFuelType(FuelType.getByName(rs.getString("fuel_type")));
                vehicle.setType(VehicleType.getByName(rs.getString("type")));
                vehicle.setCreationDate(ZonedDateTime.parse(rs.getString("creation_date")));
                stack.add(vehicle);
            }
        } catch (Exception e) {
            System.out.println("Ошибка выдачи коллекции.");
            return null;
        }
        return stack;
    }

    public boolean add(Connection connection, Vehicle vehicle) {
        PreparedStatement stmt;
        StringBuilder CreateSql = new StringBuilder();
        try {
            CreateSql.append(String.format("INSERT INTO %s (name, type, fuel_type, x, y, engine_power, owner_id, creation_date) VALUES ", this.name));
            CreateSql.append(String.format("('%s', '%s', '%s', %s, %s, %s, %d, '%s')",
                    vehicle.getName(),
                    vehicle.getType().toString(),
                    vehicle.getFuelType().toString(),
                    vehicle.getCoordinates().getX().toString(),
                    String.valueOf(vehicle.getCoordinates().getY()).replace(',', '.'),
                    String.valueOf(vehicle.getEnginePower()).replace(',', '.'),
                    vehicle.getOwnerId(),
                    vehicle.getCreationDate().toString()));
//            System.out.println(CreateSql);
            stmt = connection.prepareStatement(CreateSql.toString());
            stmt.execute();

            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    public Vehicle addGet(Connection connection, Vehicle vehicle) {
        if (add(connection, vehicle)) {
            PreparedStatement stmt;
            StringBuilder CreateSql = new StringBuilder();
            try {
                CreateSql.append(String.format("SELECT * FROM %s ORDER BY id DESC LIMIT 1", this.name));
                stmt = connection.prepareStatement(CreateSql.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    vehicle.generateId(rs.getInt("id"));
                }
            } catch (Exception e) {
                return null;
            }
            return vehicle;
        }
        return null;
    }

    public int findMaxId(Connection connection) {
        PreparedStatement stmt;
        StringBuilder CreateSql = new StringBuilder();
        try {
            CreateSql.append(String.format("SELECT id FROM %s ORDER BY id DESC LIMIT 1", this.name));
            stmt = connection.prepareStatement(CreateSql.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    public boolean delete(int id, Connection connection) {
        PreparedStatement stmt;
        StringBuilder CreateSql = new StringBuilder();

        try {
            CreateSql.append(String.format("DELETE FROM %s WHERE id = %d", this.name, id));
            stmt = connection.prepareStatement(CreateSql.toString());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateById(int id, Vehicle vehicle, Connection connection) {
        PreparedStatement stmt;
        String[] s;
        try {
            s = new String[]{String.format("UPDATE %s SET name='%s' WHERE id=%d", this.name, vehicle.getName(), id),
                    String.format("UPDATE %s SET type='%s' WHERE id=%d", this.name, vehicle.getType().toString(), id),
                    String.format("UPDATE %s SET fuel_type='%s' WHERE id=%d", this.name, vehicle.getFuelType().toString(), id),
                    String.format("UPDATE %s SET creation_date='%s' WHERE id=%d", this.name, vehicle.getCreationDate().toString(), id),
                    String.format("UPDATE %s SET y=%s WHERE id=%d", this.name, String.valueOf(vehicle.getCoordinates().getY()).replace(',', '.'), vehicle.getId()),
                    String.format("UPDATE %s SET x=%s WHERE id=%d", this.name, vehicle.getCoordinates().getX(), id),
                    String.format("UPDATE %s SET engine_power=%s WHERE id=%d", this.name, String.valueOf(vehicle.getEnginePower()).replace(',', '.'), id),
            };
            for (String req : s) {
                stmt = connection.prepareStatement(req);
                stmt.execute();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
