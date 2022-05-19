package server.commands;

import core.precommands.ObjectIdPrecommand;
import core.precommands.Precommand;
import server.commands.interfaces.IdCommand;
import core.essentials.Vehicle;
import core.interact.Message;
import server.database.repositories.UserRepository;
import server.database.repositories.VehicleRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Класс команды обновления элемента коллекции по id.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class Update extends Add implements IdCommand {
    private final String argument;
    private String author;
    private Connection connection;

    public Update(boolean from_script, ArrayList<String> args, Connection connection) {
        super(from_script, connection);
        this.argument = args.get(0);
    }

    public Update(Precommand precommand,Connection connection){
        super(precommand,connection);
        ObjectIdPrecommand objectIdPrecommand = (ObjectIdPrecommand) precommand;
        this.argument = objectIdPrecommand.getId();
        this.author = precommand.getAuthor();
        this.connection = connection;
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        if (this.vehicle.getName() == null | this.vehicle.getCoordinates() == null| this.vehicle.getCreationDate() == null
                | this.vehicle.getType() == null | this.vehicle.getEnginePower() <= 0){
            return new Message("Ошибка передачи объекта (недопустимые значения полей).", false);
        }
        int index = idArgToIndex(argument, stack);
        if (index == -1) {
            return new Message("Неверный аргумент. Ожидается число (id). Или данного элемента не существует.", true);
        }
        UserRepository userRepository = new UserRepository(connection);
        VehicleRepository vehicleRepository = new VehicleRepository(connection);
        if (vehicleRepository.getById(Integer.parseInt(argument)).getOwnerId() == userRepository.getByLogin(author).getId()){
            if(vehicleRepository.updateById(Integer.parseInt(argument),vehicle)){
                stack.remove(index);
                vehicle.setOwnerId(userRepository.getByLogin(author).getId());
                vehicle.generateId(Integer.parseInt(argument));
                stack.add(index, vehicle);
                return new Message("Элемент успешно обновлен.", true);
            } else {
                return new Message("Ошибка обновления.", true);
            }
        }
        return new Message("Вы можете изменять только созданные вами объекты.", true);
//        stack.remove(index);
//        vehicle.generateId(Integer.parseInt(argument));
//        stack.add(index, vehicle);
//        return new Message("Элемент успешно обновлен.", true);
    }

}
