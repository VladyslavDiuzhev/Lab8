package server.commands;

import core.precommands.Precommand;
import server.commands.interfaces.Command;
import core.essentials.Vehicle;
import core.interact.Message;
import server.database.repositories.UserRepository;
import server.database.repositories.VehicleRepository;

import java.sql.Connection;
import java.util.Stack;

/**
 * Класс команды очистки коллекции.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class Clear implements Command {
    private String author;
    private Connection connection;
    public Clear(Precommand precommand, Connection connection) {
        this.author = precommand.getAuthor();
        this.connection = connection;
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        UserRepository userRepository = new UserRepository(connection);
        VehicleRepository vehicleRepository = new VehicleRepository(connection);
        Stack<Integer> indexes = new Stack<>();
        int i = 0;
        for (Vehicle vehicle: stack){
            if (vehicleRepository.getById(vehicle.getId()).getOwnerId() == userRepository.getByLogin(author).getId()){
                if(vehicleRepository.deleteById(vehicle.getId())){
                    indexes.add(i);
                } else {
                    return new Message("Ошибка удаления.", true);
                }
            }
            i++;
        }
        int dif =0;
        for (int index : indexes){
            stack.remove(index-dif);
            dif++;
        }
        return new Message("Все ваши объекты удалены.", true);
    }
}
