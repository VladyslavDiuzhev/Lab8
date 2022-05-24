package server;

import server.commands.Authorize;
import server.commands.Register;
import server.commands.interfaces.Changing;
import server.commands.interfaces.Command;
import server.commands.interfaces.DateCommand;
import core.essentials.StackInfo;
import core.essentials.Vehicle;
import core.interact.ConsoleInteractor;
import core.interact.Message;
import core.interact.UserInteractor;
import server.commands.CommandRouter;
import core.main.VehicleStackXmlParser;
import core.precommands.Precommand;
import server.database.SetupDB;
import server.database.models.UserModel;
import server.database.repositories.VehicleRepository;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hello world!
 */
public class App {
    public static final int port = 8001;
    private static final UserInteractor adminInteractor = new ConsoleInteractor();
    private static Stack<Vehicle> collection = new Stack<>();
    private static ZonedDateTime initDateTime = ZonedDateTime.now();
    private static ServerSocket serverSocket;

    private static final int MAX_CONNECTIONS = 5;
    private static final ReentrantLock lock = new ReentrantLock();

    private static final ForkJoinPool requestPool = new ForkJoinPool(MAX_CONNECTIONS);
    private static final ExecutorService processingPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
    private static final ExecutorService answerPool = Executors.newCachedThreadPool();

    private static HashMap<String, String> authorizedUsers = new HashMap<>();
    private static final HashMap<String, UserConnection> connectionHashMap = new HashMap<>();

    //private static HashMap<String, ObjectOutputStream> outputStreamHashMap = new HashMap<>();

    private static Connection connectionDb;


    public static void main(String[] args) {
        if (!prepare()) {
            adminInteractor.broadcastMessage("Остановка запуска", true);
            return;
        }

        if (startServer()) {
            InetAddress inetAddress = serverSocket.getInetAddress();
            adminInteractor.broadcastMessage("Сервер запущен по адресу: " + inetAddress.getHostAddress() + ":" + port, true);
            adminInteractor.broadcastMessage("Для остановки нажмите ctrl + c", true);
        } else {
            return;
        }

        work();
    }

    private static boolean createShutdownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                adminInteractor.broadcastMessage("Остановка севера.", true);
            }));
            return true;
        } catch (Exception e) {
            adminInteractor.broadcastMessage("Не удалось настроить условие выхода.", true);
            return false;
        }
    }

    private static boolean uploadInfoDB() {
        File file1 = new File("db.cfg");
        if (!file1.exists()) {
            System.out.println("Файла не существует (" + file1.getAbsolutePath() + ")");
            return false;
        }
        if (!SetupDB.createConnection(adminInteractor, file1)) {
            return false;
        }
        SetupDB.createTables(adminInteractor);
        connectionDb = SetupDB.getConnection();
        VehicleRepository vehicleRepository = new VehicleRepository(connectionDb);
        collection = vehicleRepository.getAll();
        if (collection == null) {
            adminInteractor.broadcastMessage("Коллекция пуста.", true);
        } else {
            adminInteractor.broadcastMessage("Загружено " + (long) collection.size() + " элементов коллекции.", true);
        }

        return true;
    }

    private static boolean prepare() {
        adminInteractor.broadcastMessage("Подготовка к запуску...", true);
        if (!uploadInfoDB()) {
            return false;
        }
        return createShutdownHook();
    }

    private static boolean startServer() {
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            adminInteractor.broadcastMessage(String.format("Невозможно запустить сервер (%s)%n", e.getMessage()), true);
            return false;
        }
    }

    private static void work() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                String s = socket.getInetAddress().toString() + ":" + socket.getPort();
                synchronized (connectionHashMap) {
                    connectionHashMap.put(s, new UserConnection(socket));
                }
                authorizedUsers.put(s, "");
                adminInteractor.broadcastMessage(String.format("Клиент (%s) присоединился!", s), true);

                requestPool.execute(new RequestService(s));

            } catch (IOException e) {
                adminInteractor.broadcastMessage("Ошибка при соединении.", true);
            }
        }
    }

    static class UserConnection {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        boolean auth = false;

        public UserConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        public boolean isSocketOk() {
            return this.socket.isConnected();
        }

        public void authorize() {
            this.auth = true;
        }

        public Object read() throws IOException, ClassNotFoundException {
            return this.in.readObject();
        }

        public void send(Object obj) throws IOException {
            this.out.writeObject(obj);
        }
    }

    static class RequestService extends ForkJoinTask {
        private final String name;

        public RequestService(String name) {
            this.name = name;
        }

        @Override
        public Object getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Object value) {

        }

        @Override
        protected boolean exec() {
            try {

                while (connectionHashMap.get(name).isSocketOk()) {
                    Precommand preCommand = null;
                    try {
                        preCommand = (Precommand) connectionHashMap.get(name).read();
//                        System.out.println(preCommand.getArg());
                    } catch (ClassNotFoundException e) {
                        answerPool.submit(new AnswerService(this.name, new Message("Ошибка при обработке команды.", false)));
                    } catch (IOException e) {
                        break;
                    }
                    processingPool.submit(new ProcessingService(this.name, preCommand));
                }
                adminInteractor.broadcastMessage(String.format("Клиент (%s) отсоединился!", this.name), true);
                synchronized (connectionHashMap) {
                    connectionHashMap.remove(this.name);
                }
                authorizedUsers.remove(this.name);
                //outputStreamHashMap.remove(socket.getInetAddress().toString() + ":" + socket.getPort());
            } catch (Exception ignored) {
            }
            synchronized (connectionHashMap) {
                connectionHashMap.remove(this.name);
            }
            authorizedUsers.remove(this.name);
            return false;
        }
    }

    static class ProcessingService implements Runnable {
        private final Precommand preCommand;
        private String currentUser = "";

        public ProcessingService(String name, Precommand precommand) {
            this.preCommand = precommand;
            this.currentUser = name;
        }

        @Override
        public void run() {
            Message msg;
            lock.lock();
            preCommand.setAuthor(authorizedUsers.get(currentUser));
            System.out.println("Получена команда " + preCommand.getCommandName() + " от " + preCommand.getAuthor());
            Command command = CommandRouter.getCommand(preCommand, connectionDb);
            if (command instanceof Register) {
                if (!authorizedUsers.get(currentUser).isEmpty()) {
                    msg = new Message("Пользователь уже авторизован!", false);
                } else {
                    msg = command.execute(collection);
                }
            } else if (command instanceof Authorize) {
                if (!authorizedUsers.get(currentUser).isEmpty()) {
                    msg = new Message("Пользователь уже авторизован!", false);
                } else {
                    msg = command.execute(collection);
                    if (msg.isSuccessful()) {
                        if (authorizedUsers.containsValue(msg.getText().substring(40))) {
                            msg = new Message("Пользователь с таким именем уже авторизован!", false);
                        } else {
                            authorizedUsers.put(currentUser, msg.getText().substring(40));
                            adminInteractor.broadcastMessage("Пользователь " + authorizedUsers.get(currentUser) + "  авторизован!", true);
                        }
                    }
                }

            } else if (command instanceof DateCommand && !authorizedUsers.get(currentUser).isEmpty()) {
                msg = ((DateCommand) command).execute(collection, initDateTime);
            } else {
                if (command != null && !authorizedUsers.get(currentUser).isEmpty()) {
                    try {
                        msg = command.execute(collection);
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = new Message("Возникла ошибка.", false);
                    }

                } else if (authorizedUsers.get(currentUser).isEmpty()) {
                    msg = new Message("Только авторизованные пользователи могут выполнять команды!", false);
                } else {
                    msg = new Message("Ошибка при обработке команды.", false);
                }
                synchronized (connectionHashMap) {
                    if (command instanceof Changing && msg.isSuccessful()) {
                        for (String nameUser : connectionHashMap.keySet()) {
                            answerPool.submit(new AnswerService(nameUser, msg));
                        }
                        lock.unlock();
                        return;
                    }
                }
            }
            lock.unlock();
//            System.out.println(msg.getText());
            answerPool.submit(new AnswerService(this.currentUser, msg));
        }
    }

    static class AnswerService implements Runnable {
        private final String name;
        private final Message msg;

        public AnswerService(String name, Message msg) {
            this.name = name;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                synchronized (connectionHashMap) {
                    connectionHashMap.get(this.name).send(msg);
                }
            } catch (IOException e) {
                adminInteractor.broadcastMessage("Клиент отсоединен.", true);
            }
        }
    }

}
