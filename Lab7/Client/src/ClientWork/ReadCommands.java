package ClientWork;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class ReadCommands implements Runnable {

    private boolean isConnected = false;
    private SocketAddress socketAddress;
    public DatagramSocket socket;
    public static ReentrantLock locker = new ReentrantLock();
    Thread clThread;

    public ReadCommands() {
        clThread = new Thread(this);
        clThread.start();
    }

    public void run() {
        while (!isConnected) {
            isConnected = true;
            ArrayList<String> history = new ArrayList<>();
            Scanner cScan = new Scanner(System.in);
            String command = "";
            System.out.println("Введите команду. Для просмотра возможных команд введите help");
            try {
                while (!command.equals("exit")) {
                    command = cScan.nextLine();
                    command = command.trim();
                    String[] lastCommand = command.trim().split(" ", 2);
                    switch (lastCommand[0]) {
                        case "help":
                            UserCommands.help();
                            history.add(command);
                            break;
                        case "info":
                            socket = connectToServer();
                            HelpObject info = new HelpObject("info");
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(info);
                            byte[] commandInBytes = outputStream.toByteArray();
                            DatagramPacket packet = new DatagramPacket(commandInBytes, commandInBytes.length, socketAddress);
                            socket.send(packet);
                            byte[] answerInBytes = new byte[16666];
                            packet = new DatagramPacket(answerInBytes, answerInBytes.length);
                            socket.receive(packet);
                            ByteArrayInputStream byteStream = new ByteArrayInputStream(answerInBytes);
                            ObjectInputStream obs = new ObjectInputStream(byteStream);
                            String result = obs.readObject().toString();
                            System.out.println(result);
                            history.add(command);
                            socket.close();
                            break;
                        case "show":
                            socket = connectToServer();
                            HelpObject show = new HelpObject("show");
                            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
                            objectOutputStream1.writeObject(show);
                            byte[] commandInBytes1 = outputStream1.toByteArray();
                            DatagramPacket packet1 = new DatagramPacket(commandInBytes1, commandInBytes1.length, socketAddress);
                            socket.send(packet1);
                            byte[] answerInBytes1 = new byte[16666];
                            packet = new DatagramPacket(answerInBytes1, answerInBytes1.length);
                            socket.receive(packet);
                            ByteArrayInputStream byteStream1 = new ByteArrayInputStream(answerInBytes1);
                            ObjectInputStream obs1 = new ObjectInputStream(byteStream1);
                            String result1 = obs1.readObject().toString();
                            System.out.println(result1);
                            history.add(command);
                            socket.close();
                            break;
                        case "add":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");

                            Authorization authorization = new Authorization();
                            authorization.authorize();

                            if (Authorization.k == 0) {
                                socket = connectToServer();
                                HelpObject add = new HelpObject("add", RouteCreation.createRoute());
                                ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
                                objectOutputStream2.writeObject(add);
                                byte[] commandInBytes2 = outputStream2.toByteArray();
                                DatagramPacket packet2 = new DatagramPacket(commandInBytes2, commandInBytes2.length, socketAddress);
                                socket.send(packet2);
                                byte[] answerInBytes2 = new byte[16666];
                                packet = new DatagramPacket(answerInBytes2, answerInBytes2.length);
                                socket.receive(packet);
                                ByteArrayInputStream byteStream2 = new ByteArrayInputStream(answerInBytes2);
                                ObjectInputStream obs2 = new ObjectInputStream(byteStream2);
                                String result2 = obs2.readObject().toString();
                                System.out.println(result2);
                                history.add(command);
                                socket.close();
                            }
                            break;
                        case "updateId":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");
                            Authorization authorization1 = new Authorization();
                            authorization1.authorize();

                            if (Authorization.k == 0) {
                                try {
                                    long idd = 0;
                                    while ((idd < 1) || (idd > 1000)) {
                                        System.out.println("Введите ID маршрута, чтобы обновить элемент коллекции. Значение ID должно быть больше 0 и меньше 1000");
                                        Scanner scanner1 = new Scanner(System.in);
                                        idd = scanner1.nextLong();
                                    }
                                    socket = connectToServer();
                                    HelpObject updateId = new HelpObject("updateId", idd, RouteCreation.createForUpdateId());
                                    ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
                                    ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);
                                    objectOutputStream3.writeObject(updateId);
                                    byte[] commandInBytes3 = outputStream3.toByteArray();
                                    DatagramPacket packet3 = new DatagramPacket(commandInBytes3, commandInBytes3.length, socketAddress);
                                    socket.send(packet3);
                                    byte[] answerInBytes3 = new byte[16666];
                                    packet = new DatagramPacket(answerInBytes3, answerInBytes3.length);
                                    socket.receive(packet);
                                    ByteArrayInputStream byteStream3 = new ByteArrayInputStream(answerInBytes3);
                                    ObjectInputStream obs3 = new ObjectInputStream(byteStream3);
                                    String result3 = obs3.readObject().toString();
                                    System.out.println(result3);
                                    history.add(command);
                                    socket.close();
                                } catch (InputMismatchException e) {
                                    System.out.println("Ошибка ввода");
                                } catch (NullPointerException e) {
                                    e.getMessage();
                                }
                            }
                            break;
                        case "removeById":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");
                            Authorization authorization2 = new Authorization();
                            authorization2.authorize();

                            if (Authorization.k == 0) {
                                try {
                                    long iddd = 0;
                                    while ((iddd < 1) || (iddd > 1000)) {
                                        System.out.println("Введите ID маршрута, чтобы удалить его. Значение ID должно быть больше 0 и меньше 1000");
                                        Scanner scanner2 = new Scanner(System.in);
                                        iddd = scanner2.nextLong();
                                    }
                                    socket = connectToServer();
                                    HelpObject removeById = new HelpObject("removeById", iddd);
                                    ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
                                    ObjectOutputStream objectOutputStream4 = new ObjectOutputStream(outputStream4);
                                    objectOutputStream4.writeObject(removeById);
                                    byte[] commandInBytes4 = outputStream4.toByteArray();
                                    DatagramPacket packet4 = new DatagramPacket(commandInBytes4, commandInBytes4.length, socketAddress);
                                    socket.send(packet4);
                                    byte[] answerInBytes4 = new byte[16666];
                                    packet = new DatagramPacket(answerInBytes4, answerInBytes4.length);
                                    socket.receive(packet);
                                    ByteArrayInputStream byteStream4 = new ByteArrayInputStream(answerInBytes4);
                                    ObjectInputStream obs4 = new ObjectInputStream(byteStream4);
                                    String result4 = obs4.readObject().toString();
                                    System.out.println(result4);
                                    history.add(command);
                                    socket.close();
                                } catch (InputMismatchException e) {
                                    System.out.println("Ошибка ввода");
                                } catch (NullPointerException e) {
                                    e.getMessage();
                                }
                            }
                            break;
                        case "clear":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");
                            Authorization authorization3 = new Authorization();
                            authorization3.authorize();

                            if (Authorization.k == 0) {
                                socket = connectToServer();
                                HelpObject clear = new HelpObject("clear");
                                ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream5 = new ObjectOutputStream(outputStream5);
                                objectOutputStream5.writeObject(clear);
                                byte[] commandInBytes5 = outputStream5.toByteArray();
                                DatagramPacket packet5 = new DatagramPacket(commandInBytes5, commandInBytes5.length, socketAddress);
                                socket.send(packet5);
                                byte[] answerInBytes5 = new byte[16666];
                                packet = new DatagramPacket(answerInBytes5, answerInBytes5.length);
                                socket.receive(packet);
                                ByteArrayInputStream byteStream5 = new ByteArrayInputStream(answerInBytes5);
                                ObjectInputStream obs5 = new ObjectInputStream(byteStream5);
                                String result5 = obs5.readObject().toString();
                                System.out.println(result5);
                                history.add(command);
                                socket.close();
                            }
                            break;
                        case "executeScript":
                            socket = connectToServer();
                            HelpObject executeScript = new HelpObject("executeScript");
                            ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream6 = new ObjectOutputStream(outputStream6);
                            objectOutputStream6.writeObject(executeScript);
                            byte[] commandInBytes6 = outputStream6.toByteArray();
                            DatagramPacket packet6 = new DatagramPacket(commandInBytes6, commandInBytes6.length, socketAddress);
                            socket.send(packet6);
                            byte[] answerInBytes6 = new byte[16666];
                            packet = new DatagramPacket(answerInBytes6, answerInBytes6.length);
                            socket.receive(packet);
                            ByteArrayInputStream byteStream6 = new ByteArrayInputStream(answerInBytes6);
                            ObjectInputStream obs6 = new ObjectInputStream(byteStream6);
                            String result6 = obs6.readObject().toString();
                            System.out.println(result6);
                            history.add(command);
                            socket.close();
                            break;
                        case "exit":
                            socket = connectToServer();
                            HelpObject exit = new HelpObject("exit");
                            ByteArrayOutputStream outputStream7 = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream7 = new ObjectOutputStream(outputStream7);
                            objectOutputStream7.writeObject(exit);
                            byte[] commandInBytes7 = outputStream7.toByteArray();
                            DatagramPacket packet7 = new DatagramPacket(commandInBytes7, commandInBytes7.length, socketAddress);
                            socket.send(packet7);
                            socket.close();
                            break;
                        case "addIfMax":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");
                            Authorization authorization4 = new Authorization();
                            authorization4.authorize();

                            if (Authorization.k == 0) {
                                socket = connectToServer();
                                HelpObject addIfMax = new HelpObject("addIfMax", RouteCreation.createRoute());
                                ByteArrayOutputStream outputStream8 = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream8 = new ObjectOutputStream(outputStream8);
                                objectOutputStream8.writeObject(addIfMax);
                                byte[] commandInBytes8 = outputStream8.toByteArray();
                                DatagramPacket packet8 = new DatagramPacket(commandInBytes8, commandInBytes8.length, socketAddress);
                                socket.send(packet8);
                                byte[] answerInBytes8 = new byte[16666];
                                packet = new DatagramPacket(answerInBytes8, answerInBytes8.length);
                                socket.receive(packet);
                                ByteArrayInputStream byteStream8 = new ByteArrayInputStream(answerInBytes8);
                                ObjectInputStream obs8 = new ObjectInputStream(byteStream8);
                                String result8 = obs8.readObject().toString();
                                System.out.println(result8);
                                history.add(command);
                                socket.close();
                            }
                            break;
                        case "removeLower":
                            System.out.println("Введите логин, если вы уже зарегестрированы. Если нет - придумайте новый: ");
                            Authorization authorization5 = new Authorization();
                            authorization5.authorize();

                            if (Authorization.k == 0) {
                                try {
                                    long idddd = 0;
                                    while ((idddd < 1) || (idddd > 1000)) {
                                        System.out.println("Введите ID маршрута, чтобы удалить из коллекции элементы, которые меньше заданного. Значение ID должно быть больше 0 и меньше 1000");
                                        Scanner scanner2 = new Scanner(System.in);
                                        idddd = scanner2.nextLong();
                                    }
                                    socket = connectToServer();
                                    HelpObject removeLower = new HelpObject("removeLower", idddd);
                                    ByteArrayOutputStream outputStream9 = new ByteArrayOutputStream();
                                    ObjectOutputStream objectOutputStream9 = new ObjectOutputStream(outputStream9);
                                    objectOutputStream9.writeObject(removeLower);
                                    byte[] commandInBytes9 = outputStream9.toByteArray();
                                    DatagramPacket packet9 = new DatagramPacket(commandInBytes9, commandInBytes9.length, socketAddress);
                                    socket.send(packet9);
                                    byte[] answerInBytes9 = new byte[16666];
                                    packet = new DatagramPacket(answerInBytes9, answerInBytes9.length);
                                    socket.receive(packet);
                                    ByteArrayInputStream byteStream9 = new ByteArrayInputStream(answerInBytes9);
                                    ObjectInputStream obs9 = new ObjectInputStream(byteStream9);
                                    String result9 = obs9.readObject().toString();
                                    System.out.println(result9);
                                    history.add(command);
                                    socket.close();
                                } catch (InputMismatchException e) {
                                    System.out.println("Ошибка ввода");
                                } catch (NullPointerException e) {
                                    e.getMessage();
                                }
                            }
                            break;
                        case "history":
                            System.out.println(history);
                            history.add(command);
                            break;
                        case "maxByTo":
                            socket = connectToServer();
                            HelpObject maxByTo = new HelpObject("maxByTo");
                            ByteArrayOutputStream outputStream10 = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream10 = new ObjectOutputStream(outputStream10);
                            objectOutputStream10.writeObject(maxByTo);
                            byte[] commandInBytes10 = outputStream10.toByteArray();
                            DatagramPacket packet10 = new DatagramPacket(commandInBytes10, commandInBytes10.length, socketAddress);
                            socket.send(packet10);
                            byte[] answerInBytes10 = new byte[16666];
                            packet = new DatagramPacket(answerInBytes10, answerInBytes10.length);
                            socket.receive(packet);
                            ByteArrayInputStream byteStream10 = new ByteArrayInputStream(answerInBytes10);
                            ObjectInputStream obs10 = new ObjectInputStream(byteStream10);
                            String result10 = obs10.readObject().toString();
                            System.out.println(result10);
                            history.add(command);
                            socket.close();
                            break;
                        case "filterStartsWithName":
                            try {
                                String nnamee = "";
                                Scanner scanner3 = new Scanner(System.in);
                                while (nnamee.length() != 3) {
                                    System.out.println("Введите 3 символа, чтобы увидеть маршрут, название которого начинается с них");
                                    nnamee = scanner3.nextLine();
                                }
                                socket = connectToServer();
                                HelpObject filterStartsWithName = new HelpObject("filterStartsWithName", nnamee);
                                ByteArrayOutputStream outputStream11 = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream11 = new ObjectOutputStream(outputStream11);
                                objectOutputStream11.writeObject(filterStartsWithName);
                                byte[] commandInBytes11 = outputStream11.toByteArray();
                                DatagramPacket packet11 = new DatagramPacket(commandInBytes11, commandInBytes11.length, socketAddress);
                                socket.send(packet11);
                                byte[] answerInBytes11 = new byte[16666];
                                packet = new DatagramPacket(answerInBytes11, answerInBytes11.length);
                                socket.receive(packet);
                                ByteArrayInputStream byteStream11 = new ByteArrayInputStream(answerInBytes11);
                                ObjectInputStream obs11 = new ObjectInputStream(byteStream11);
                                String result11 = obs11.readObject().toString();
                                System.out.println(result11);
                                history.add(command);
                                socket.close();
                            } catch (InputMismatchException e) {
                                System.out.println("Ошибка ввода");
                            } catch (NullPointerException e) {
                                e.getMessage();
                            }
                            break;
                        case "filterLessThanDistance":
                            try {
                                Integer dist = -1;
                                Scanner scanner4 = new Scanner(System.in);
                                while (dist <= 1) {
                                    System.out.println("Введите дистанцию маршрута (Integer), чтобы вывести элемент коллекции, значение поля distance которого меньше заданного. Дистанция должна быть больше 1!");
                                    dist = scanner4.nextInt();
                                }
                                socket = connectToServer();
                                HelpObject filterLessThanDistance = new HelpObject("filterLessThanDistance", dist);
                                ByteArrayOutputStream outputStream12 = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream12 = new ObjectOutputStream(outputStream12);
                                objectOutputStream12.writeObject(filterLessThanDistance);
                                byte[] commandInBytes12 = outputStream12.toByteArray();
                                DatagramPacket packet12 = new DatagramPacket(commandInBytes12, commandInBytes12.length, socketAddress);
                                socket.send(packet12);
                                byte[] answerInBytes12 = new byte[16666];
                                packet = new DatagramPacket(answerInBytes12, answerInBytes12.length);
                                socket.receive(packet);
                                ByteArrayInputStream byteStream12 = new ByteArrayInputStream(answerInBytes12);
                                ObjectInputStream obs12 = new ObjectInputStream(byteStream12);
                                String result12 = obs12.readObject().toString();
                                System.out.println(result12);
                                history.add(command);
                                socket.close();
                            } catch (InputMismatchException e) {
                                System.out.println("Ошибка ввода");
                            } catch (NullPointerException e) {
                                e.getMessage();
                            }
                            break;
                    }
                }
            } catch (IllegalStateException e) {
                System.out.println("Программа завершена");
            } catch (NoSuchElementException e) {
                e.getMessage();
            } catch (PortUnreachableException e) {
                System.out.println("Нет связи с сервером");
            } catch (IOException e) {
                System.out.println("Подключение прервано");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public DatagramSocket connectToServer() throws IOException {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socketAddress = new InetSocketAddress(address, 1111);
            socket = new DatagramSocket();
            socket.connect(socketAddress);
        } catch (ConnectException e) {
            connectToServer();
        } catch (Exception e) {
            System.out.println("Проблемы с сервером");
        }
        return socket;
    }
}