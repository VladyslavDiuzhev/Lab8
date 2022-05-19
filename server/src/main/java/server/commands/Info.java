package server.commands;

import server.commands.interfaces.DateCommand;
import core.essentials.Vehicle;
import core.interact.Message;
import server.database.repositories.VehicleRepository;

import java.sql.Connection;
import java.time.ZonedDateTime;
import java.util.Stack;

/**
 * Класс команды получения информации о коллекции.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class Info implements DateCommand {
    private Connection connection;

    public Info(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Message execute(Stack<Vehicle> stack, ZonedDateTime zonedDateTime) {
        VehicleRepository vehicleRepository = new VehicleRepository(connection);
        return new Message("Важная информация о коллекции:\n" +
                "\n" +
                "Тип: " + Vehicle.class.getName() + "\n" +
                "Дата инициализации: " + zonedDateTime.toString() + "\n" +
                "Максимальный id: " + vehicleRepository.getMaxId() + "\n" +
                "Количество элементов: " + stack.size(), true);
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        return new Message("", false);
    }
}
