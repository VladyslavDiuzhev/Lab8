package server.commands;


import core.essentials.UserInfo;
import core.essentials.Vehicle;
import core.precommands.*;
import server.commands.interfaces.Command;
import core.interact.UserInteractor;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс обращения к командам.
 *
 * @author Владислав Дюжев
 * @version 1.0
 */
public abstract class CommandRouter {
    public static Command getCommand(Precommand precommand, Connection connection) {
        switch (precommand.getCommandName()) {
            case "register":
                if (precommand instanceof ObjectPrecommand && precommand.getArg() instanceof UserInfo) {
                    return new Register((UserInfo) precommand.getArg(), connection);
                }
                return null;
            case "authorize":
                if (precommand instanceof ObjectPrecommand && precommand.getArg() instanceof UserInfo) {
                    return new Authorize((UserInfo) precommand.getArg(), connection);
                }
                return null;
            case "help":
                return new Help();
            case "info":
                return new Info(connection);
            case "show":
                return new Show();
            case "add":
                if (precommand instanceof ObjectPrecommand && precommand.getArg() instanceof Vehicle) {
                    return new Add(precommand, connection);
                }
                return null;
            case "update":
                if (precommand instanceof ObjectIdPrecommand) {
                    return new Update(precommand, connection);
                }

            case "remove_by_id":
                if (precommand instanceof IdPrecommand) {
                    return new RemoveById((IdPrecommand) precommand, connection);
                }
                return null;
            case "clear":
                return new Clear(precommand, connection);
            case "remove_first":
                return new RemoveFirst(precommand, connection);
            case "add_if_min":
                return new AddIfMin(precommand, connection);
            case "reorder":
                return new Reorder();
            case "group_counting_by_id":
                return new GroupCountingById();
            case "filter_starts_with_name":
                if (precommand instanceof BasicPrecommand && precommand.getArg() instanceof String) {
                    return new FilterStartsWithName((String) precommand.getArg());
                }
                return null;

            case "print_unique_fuel_type":
                return new PrintUniqueFuelType();
            case "sort":
                return new Sort();

            case "info_by_id":
                if (precommand instanceof IdPrecommand) {
                    return new InfoById(precommand, connection);
                }
                return null;
            default:
                return null;
        }

    }
}
