package server.commands;

import core.essentials.Vehicle;
import core.interact.Message;
import core.precommands.Precommand;

import java.sql.Connection;
import java.util.Collections;
import java.util.Stack;

/**
 * Класс управления коллекцией с помощью командной строки.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public class AddIfMin extends Add {
    public AddIfMin(boolean from_script, Connection connection) {
        super(from_script, connection);
    }

    public AddIfMin(Precommand precommand, Connection connection){
        super(precommand, connection);

    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        if (this.vehicle.getName() == null | this.vehicle.getCoordinates() == null| this.vehicle.getCreationDate() == null
                | this.vehicle.getType() == null | this.vehicle.getEnginePower() <= 0){
            return new Message("Ошибка передачи объекта (недопустимые значения полей).", false);
        }
        if (stack.isEmpty() || this.vehicle.compareTo(Collections.min(stack)) < 0) {
            this.vehicle.generateId();
            this.vehicle = vehicleRepository.saveGet(this.vehicle);
            if (vehicle != null){
                stack.add(this.vehicle);
                return new Message("Элемент успешно добавлен.", true);
            }
            return new Message("Ошибка создания объекта.", false);
        } else {
            return  new Message("Элемент не минимальный.", true);
        }

    }
}
