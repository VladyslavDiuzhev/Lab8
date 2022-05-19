package server.commands;

import core.essentials.UserInfo;
import core.essentials.Vehicle;
import core.interact.Message;
import server.commands.interfaces.Command;
import server.database.models.UserModel;
import server.database.repositories.UserRepository;

import java.sql.Connection;
import java.util.Stack;

public class Register implements Command {
    private UserInfo userInfo;
    private Connection conn;
    private UserRepository userRepository;
    public Register(UserInfo userInfo, Connection connection){
        this.userInfo = userInfo;
        this.conn = connection;
        this.userRepository = new UserRepository(connection);
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        UserInfo newUser = userRepository.getByLogin(userInfo.getLogin());
        if (newUser == null){
            if (userRepository.save(userInfo)){
                return new Message("Регистрация пройдена!", true);
            } else {
                return new Message("Возникла ошибка при регистрации.", false);
            }
        } else {
            return new Message("Пользователь с таким логином уже существует.", false);
        }
    }
}
