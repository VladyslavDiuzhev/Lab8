package server.commands;

import core.precommands.IdPrecommand;
import server.commands.interfaces.Changing;
import server.commands.interfaces.Command;
import server.commands.interfaces.IdCommand;
import core.essentials.Vehicle;
import core.interact.Message;
import server.database.repositories.UserRepository;
import server.database.repositories.VehicleRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Класс команды удаления элемента по id.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class RemoveById implements Command, IdCommand, Changing {
    private final String argument;
    private Connection connection;
    private String author;

    public RemoveById(ArrayList<String> args , Connection connection) {
        this.argument = args.get(0);
        this.connection = connection;
    }

    public RemoveById(String arg, Connection connection) {
        this.connection = connection;
        this.argument = arg;
    }

    public RemoveById(IdPrecommand precommand, Connection connection) {
        this.connection = connection;
        this.argument = precommand.getId();
        this.author = precommand.getAuthor();
    }


    @Override
    public Message execute(Stack<Vehicle> stack) {
        int index = idArgToIndex(argument, stack);
        if (index == -1) {
            return new Message("inv_format", false);
        }
        UserRepository userRepository = new UserRepository(connection);
        VehicleRepository vehicleRepository = new VehicleRepository(connection);
        if (vehicleRepository.getById(Integer.parseInt(argument)).getOwnerId() == userRepository.getByLogin(author).getId()){
            if(vehicleRepository.deleteById(Integer.parseInt(argument))){
                stack.remove(index);
                Message msg = new Message("success", true);
                msg.setType("DEL");
                msg.setObject(Integer.parseInt(argument));
                return msg;
            } else {
                return new Message("gen_err", false);
            }
        }
        return new Message("not_your", false);

    }
}
