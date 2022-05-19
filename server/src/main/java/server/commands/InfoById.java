package server.commands;

import core.precommands.IdPrecommand;
import core.precommands.Precommand;
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
 * Класс команды получения информации об элементе по id.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class InfoById implements Command, IdCommand {
    private final String argument;

    public InfoById(ArrayList<String> args) {
        this.argument = args.get(0);
    }

    private Connection connection;

    public InfoById(Precommand precommand, Connection connection) {
        this.argument = ((IdPrecommand) precommand).getId();
        this.connection = connection;
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        int index = idArgToIndex(argument, stack);
        if (index == -1) {
            return new Message("Неверный аргумент. Ожидается число (id). Или данного элемента не существует.", true);
        }
        Vehicle vehicle = stack.get(index);
        UserRepository userRepository = new UserRepository(connection);
        System.out.println();

        String info = String.format("id: %d \n" +
                        "Создатель: %s \n" +
                        "Название: %s \n" +
                        "Тип: %s \n" +
                        "Дата создания: %s \n" +
                        "Мощность: %s \n" +
                        "Тип топлива: %s \n" +
                        "Координаты: %s", vehicle.getId(), userRepository.getById(vehicle.getOwnerId()).getLogin(),
                vehicle.getName(), vehicle.getType(), vehicle.getCreationDate(), vehicle.getEnginePower(),
                vehicle.getFuelType(), vehicle.getCoordinates());
        return new Message(info, true);
    }
}
