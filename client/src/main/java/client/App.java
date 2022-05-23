package client;


import core.interact.*;
import core.precommands.Precommand;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;


public class App {
    private static final UserInteractor userInteractor = new ConsoleInteractor();
    private static NetInteractor serverInteractor;
    private static int PORT;
    private static String address;
    private static Socket socket;

    private static boolean authorized = false;

    public static void main(String[] args) {
        try {
            String[] ar = args[0].split(":");
            if (ar.length != 2) {
                throw new Exception();
            }
            address = ar[0];
            PORT = Integer.parseInt(ar[1]);

        } catch (Exception e) {
            userInteractor.broadcastMessage("Укажите адрес в качестве аргумента командной строки (Формат - localhost:8001).", true);
            return;
        }
        connect();
        runInteracting();
    }

    public static void connect() {
        userInteractor.broadcastMessage("Подключение к серверу...", true);
        while (true) {
            try {
                socket = new Socket(address, PORT);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                serverInteractor = new NetInteractor(objectInputStream, objectOutputStream);
                return;
            } catch (IOException e) {
                userInteractor.broadcastMessage(String.format("Не удалось подключиться к серверу (%s)%n", e.getMessage()), true);
                userInteractor.broadcastMessage("Попробовать еще (Y/n)?", true);
                String inp = userInteractor.getData();
                if (inp.equalsIgnoreCase("n")) {
                    userInteractor.broadcastMessage("Завершение работы программы", true);
                    System.exit(0);
                }
            }
        }
    }

    public static void runInteracting() {

        boolean run = true;
        boolean recon = false;
        while (run) {
            try {
                if (recon | socket.isClosed() | !socket.isConnected()) {
                    authorized = false;
                    connect();
                    recon = false;
                }
                Precommand precommand;
                if (!authorized) {
                    userInteractor.broadcastMessage("Выберите опцию (1-регистрация, 2-авторизация): ", false);
                    String inp = userInteractor.getData();
                    if (Objects.equals(inp, "1")) {
                        precommand = PreCommandRouter.getCommand("register", false, userInteractor);
                    } else if (Objects.equals(inp, "2")) {
                        precommand = PreCommandRouter.getCommand("authorize", false, userInteractor);
                    } else {
                        continue;
                    }

                } else {
                    userInteractor.broadcastMessage("\nВведите команду: ", false);
                    String potentialCommand = userInteractor.getData();
                    if (potentialCommand == null) {
                        continue;
                    }
                    precommand = PreCommandRouter.getCommand(potentialCommand, false, userInteractor);
                }


                if (precommand == null) {
                    continue;
                }
                if (precommand.getCommandName().equals("exit")) {
                    return;
                }

                if (precommand.getCommandName().equals("execute_script")) {
                    boolean res = true;
                    String filename;
                    filename = (String) precommand.getArg();
                    File f = new File(filename);

                    Scanner fileScanner;
                    try {
                        fileScanner = new Scanner(f);
                    } catch (FileNotFoundException e) {
                        userInteractor.broadcastMessage("Такого файла не существует!", true);
                        continue;
                    }
                    int line_num = 1;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        try {
                            ScriptInteractor scriptInteractor = new ScriptInteractor(fileScanner);
                            Precommand precommand1 = PreCommandRouter.getCommand(line, true, scriptInteractor);
                            if (precommand1 == null) {
                                continue;
                            }
                            if (precommand1.getCommandName().equals("exit")) {
                                return;
                            }
//                            if (command1 instanceof Preprocessable) {
//                                ((Preprocessable) command1).preprocess(scriptInteractor);
//                            }

                            serverInteractor.sendObject(precommand1);
                            Message msg = (Message) serverInteractor.readObject();
                            if (!msg.isSuccessful()) {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            userInteractor.broadcastMessage("Возникла ошибка при выполнении " + line_num + " строки:\n" + line, true);
                            res = false;
                            break;
                        }
                        line_num++;
                    }
                    if (res) {
                        userInteractor.broadcastMessage("Команды отработали штатно", true);
                    }

                    continue;
                }
                try {
                    serverInteractor.sendObject(precommand);
                } catch (IOException e) {
                    userInteractor.broadcastMessage("Ошибка при отправке команды.", true);
                    recon = true;
                    continue;
                }
                Message msg;
                try {
                    msg = (Message) serverInteractor.readObject();
                } catch (Exception e) {
                    userInteractor.broadcastMessage("Ошибка при чтении ответа сервера.", true);
                    recon = true;
                    continue;
                }

                userInteractor.broadcastMessage(msg.getText(), true);
                if (!msg.isSuccessful()) {
                    if (Objects.equals(precommand.getCommandName(), "authorize") || Objects.equals(precommand.getCommandName(), "register")) {
                        continue;
                    }
                    run = false;
                } else if (Objects.equals(precommand.getCommandName(), "authorize")) {
                    authorized = true;
                    userInteractor.broadcastMessage("Для просмотра списка команд введите 'help'.", true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                userInteractor.broadcastMessage("Ошибка " + e.getMessage(), true);
            }
        }

    }
}
