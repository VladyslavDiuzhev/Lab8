package server.database.repositories;

import core.essentials.UserInfo;
import server.database.models.UserModel;

import java.sql.Connection;
import java.util.Stack;

public class UserRepository implements Repository<UserInfo>{
    private final UserModel userModel = new UserModel();
    private final Connection connection;
    public UserRepository(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean save(UserInfo user) {
        return userModel.add(connection,user);
    }

    @Override
    public Stack<UserInfo> getAll() {
        return new Stack();
    }

    @Override
    public UserInfo getById(int id) {
        return userModel.findById(connection,id);
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public boolean updateById(int id, UserInfo userInfo) {
        return false;
    }

    public UserInfo getByLogin(String login) {
        return userModel.findByLogin(connection,login);
    }
}
