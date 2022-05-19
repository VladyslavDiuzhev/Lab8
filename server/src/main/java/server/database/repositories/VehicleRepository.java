package server.database.repositories;

import core.essentials.Vehicle;
import server.database.models.UserModel;
import server.database.models.VehicleModel;

import java.sql.Connection;
import java.util.Stack;

public class VehicleRepository implements Repository<Vehicle>{

    private final VehicleModel vehicleModel = new VehicleModel();
    private final Connection connection;
    public VehicleRepository(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean save(Vehicle obj) {
        return vehicleModel.add(connection, obj);
    }

    public Vehicle saveGet(Vehicle obj) {
        return vehicleModel.addGet(connection, obj);
    }

    @Override
    public Stack<Vehicle> getAll() {
        return vehicleModel.findAll(connection);
    }

    @Override
    public Vehicle getById(int id) {
        return vehicleModel.findById(connection,id);
    }

    @Override
    public boolean deleteById(int id) {
        return vehicleModel.delete(id, connection);
    }

    @Override
    public boolean updateById(int id, Vehicle vehicle) {
        return vehicleModel.updateById(id, vehicle, connection);
    }

    public int getMaxId(){
        return vehicleModel.findMaxId(connection);
    }
}
