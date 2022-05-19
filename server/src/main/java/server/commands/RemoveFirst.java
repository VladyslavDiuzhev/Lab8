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
 * Класс команды удаления первого элемента.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class RemoveFirst implements Command {
    private Connection connection;
    private String author;
    public RemoveFirst(Precommand precommand,Connection connection) {
        this.connection = connection;
        this.author = precommand.getAuthor();
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        if (stack.size() == 0) {
            return new Message("В коллекции нет элементов.", true);
        }
        int index = 0;
        UserRepository userRepository = new UserRepository(connection);
        VehicleRepository vehicleRepository = new VehicleRepository(connection);
        if (vehicleRepository.getById(stack.get(index).getId()).getOwnerId() == userRepository.getByLogin(author).getId()){
            if(vehicleRepository.deleteById(stack.get(index).getId())){
                stack.remove(index);
                return new Message("Элемент успешно удален.", true);
            } else {
                return new Message("Ошибка удаления.", true);
            }
        }
        return new Message("Вы можете удалять только созданные вами объекты", true);
    }
}
