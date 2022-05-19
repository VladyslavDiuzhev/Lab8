package server.commands;

import core.essentials.UserInfo;
import core.essentials.Vehicle;
import core.interact.Message;
import server.commands.interfaces.Command;
import server.cryptography.EncryptorSHA_1;
import server.database.models.UserModel;

import java.sql.Connection;
import java.util.Stack;

public class Authorize implements Command {
    private UserInfo userInfo;
    private Connection conn;
    private UserModel userModel = new UserModel();

    public Authorize(UserInfo userInfo, Connection connection) {
        this.userInfo = userInfo;
        this.conn = connection;
    }

    @Override
    public Message execute(Stack<Vehicle> stack) {
        UserInfo newUser = userModel.findByLogin(conn, userInfo.getLogin());
        if (newUser != null && newUser.getPassword().equals(EncryptorSHA_1.encryptPassword(userInfo.getPassword()))) {
            return new Message("Авторизация пройдена! Добро пожаловать, " + userInfo.getLogin(), true);
        } else {
            return new Message("Авторизация не пройдена!", false);
        }
    }
}
