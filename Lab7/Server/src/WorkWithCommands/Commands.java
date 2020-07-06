package WorkWithCommands;

import MainPack.Server;

import java.io.*;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Date;

import RouteInformation.*;
import org.postgresql.util.PSQLException;

import static ClientWork.ReadCommands.locker;

public class Commands implements Comparator<Route> {

    public Commands() throws IOException {
    }

    public static String help() {
        return "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add {element} : добавить новый элемент в коллекцию\n" +
                "updateId {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "removeById id : удалить элемент из коллекции по его id\n" +
                "clear : очистить коллекцию\n" +
                "executeScript file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "addIfMax {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "removeLower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "history : вывести последние команды (без их аргументов)\n" +
                "maxByTo : вывести название маршрута, значение координаты X поля to которого является максимальным\n" +
                "filterStartsWithName name : вывести элементы, значение поля name которых начинается с заданной подстроки\n" +
                "filterLessThanDistance distance : вывести элементы, значение поля distance которых меньше заданного\n";
    }

    public static String info() {
        String info;
        if (RouteCollection.col.size() != 0) {
            info = "Тип коллекции: LinkedHashSet" + "\n" +
                    "Размерность коллекции равна: " + RouteCollection.col.size() + "\n" +
                    "Дата инициализации: " + RouteCollection.dateTime;
        } else info = "Коллекция пуста";
        return info;
    }

    public static String show() {
        String show;
        if (RouteCollection.col.size() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            RouteCollection.col.forEach((r) -> sb.append(r.toString()).append(","));
            sb.deleteCharAt(sb.lastIndexOf(","));
            return String.valueOf(sb.append("]"));
        } else show = "Коллекция пуста";
        return show;
    }

    public static String add(Route route) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        String answer = "";
        String sqlStatement = "INSERT INTO Routes (creationDate, name, coorX, coorY, fromX, fromY, fromZ, toX, toY, toZ, distance, login) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(sqlStatement);
            java.time.ZonedDateTime ddate = route.getCreationDate();
            java.util.Date dddate = Date.from(ddate.toInstant());
            java.sql.Date sqlDate = new java.sql.Date(dddate.getTime());
            preparedStatement.setDate(1, sqlDate);
            preparedStatement.setString(2, route.getName());
            preparedStatement.setFloat(3, route.getCoordinates().getX());
            preparedStatement.setInt(4, route.getCoordinates().getY());
            int k = 0;
            try {
                preparedStatement.setInt(5, route.getLocationFrom().getXx());
                k = 0;
            } catch (NullPointerException e) {
                k++;
                preparedStatement.setNull(5, Types.NULL);
                preparedStatement.setNull(6, Types.NULL);
                preparedStatement.setNull(7, Types.NULL);
            }
            if (k == 0) {
                preparedStatement.setFloat(6, route.getLocationFrom().getYy());
                preparedStatement.setInt(7, route.getLocationFrom().getZz());
            }
            int j = 0;
            try {
                preparedStatement.setFloat(8, route.getLocationTo().getX());
                j = 0;
            } catch (NullPointerException e) {
                j++;
                preparedStatement.setNull(8, Types.NULL);
                preparedStatement.setNull(9, Types.NULL);
                preparedStatement.setNull(10, Types.NULL);
            }
            if (j == 0) {
                preparedStatement.setFloat(9, route.getLocationTo().getY());
                preparedStatement.setInt(10, route.getLocationTo().getZ());
            }
            preparedStatement.setInt(11, route.getDistance());
            preparedStatement.setString(12, Receiver.login);
            preparedStatement.executeUpdate();
            connectToBD();
            answer = "Элемент добавлен в коллекцию";
        } catch (SQLException e) {
            e.printStackTrace();
            answer += "Возникла ошибка";
        } /*catch (NullPointerException ignored) {
        } */finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return answer;
    }

    public static String updateId(long id, Route route) throws SQLException {
        locker.lock();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        String answer = "";
        String condition = "SELECT * FROM Routes WHERE login = " + "'" + Receiver.login + "'";
        java.time.ZonedDateTime ddate = route.getCreationDate();
        java.util.Date dddate = Date.from(ddate.toInstant());
        java.sql.Date sqlDate = new java.sql.Date(dddate.getTime());
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(condition, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = preparedStatement.executeQuery();
            int k = 0;
            while (resultSet.next()) {
                if ((resultSet.getInt("id")) == id) {
                    resultSet.updateDate("creationdate", sqlDate);
                    resultSet.updateString("name", route.getName());
                    resultSet.updateFloat("coorX", route.getCoordinates().getX());
                    resultSet.updateInt("coorY", route.getCoordinates().getY());
                    resultSet.updateInt("fromX", route.getLocationFrom().getXx());
                    resultSet.updateFloat("fromY", route.getLocationFrom().getYy());
                    resultSet.updateInt("fromZ", route.getLocationFrom().getZz());
                    resultSet.updateFloat("toX", route.getLocationTo().getX());
                    resultSet.updateFloat("toY", route.getLocationTo().getY());
                    resultSet.updateInt("toZ", route.getLocationTo().getZ());
                    resultSet.updateInt("distance", route.getDistance());
                    resultSet.updateRow();
                    k++;
                }
            }
            if (k == 0) {
                answer = "У пользователя " + Receiver.login + " нет доступа к данному эелементу. Либо элемент с ID " +
                        id + " не существует";
            } else {
                answer = "Значения элемента изменены";
                RouteCollection.col.clear();
                connectToBD();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            answer += "Возникла ошибка";
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            locker.unlock();
        }
        return answer;
    }

    public static String removeById(long id) {
        locker.lock();
        PreparedStatement preparedStatement;
        Connection connection;
        String answer = "";
        String select = "DELETE FROM Routes WHERE id = " + id + " AND login = " + "'" + Receiver.logins.getFirst() + "'" + ";";
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(select);
            int k = preparedStatement.executeUpdate();
            if (k == 1) {
                answer = "Элемент удалён";
                RouteCollection.col.clear();
                connectToBD();
            } else if (k == 0) {
                answer = "У пользователя " + Receiver.login + " нет доступа к данному эелементу. Либо элемент с ID " +
                        id + " не существует";
            } else {
                answer = "Элемент удалён";
                RouteCollection.col.clear();
                connectToBD();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
        return answer;
    }

    public static String clear() {
        String answer = "";
        try {
            Connection conn = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            String del = "DELETE FROM Routes WHERE login = " + "'" + Receiver.login + "'" + ";";
            Statement statement = conn.createStatement();
            statement.execute(del);
            RouteCollection.col.clear();
            connectToBD();
            answer = "Все элементы, принадлежавшие пользователю " + Receiver.login + ", удалены";
        } catch (PSQLException e) {
            answer = Receiver.login + " не можеть удалить элементы коллекции, так как они принадлежат другому пользователю";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public String addIfMax(Route route) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        //String answer = "";
        //Float newCoorX = route.getCoordinates().getX();
        String sqlStatement = "INSERT INTO Routes (creationDate, name, coorX, coorY, fromX, fromY, fromZ, toX, toY, toZ, distance, login) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        RouteCollection.col.add(route);

        int k = 0;
        int minCom;
        ArrayList<Integer> com = new ArrayList<>();
        Iterator<Route> it = RouteCollection.col.iterator();
        while (it.hasNext()) {
            route = it.next();
            k++;
            com.add(compare(getLast(RouteCollection.col), route));
        }
        if (k > 0) {
            minCom = Collections.min(com);
            if (minCom == -1) {
                RouteCollection.col.remove(getLast(RouteCollection.col));
                return "Элемент не добавлен в коллекцию, так как он не привышает максимальный";
            } else try {
                connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
                preparedStatement = connection.prepareStatement(sqlStatement);
                java.time.ZonedDateTime ddate = route.getCreationDate();
                java.util.Date dddate = Date.from(ddate.toInstant());
                java.sql.Date sqlDate = new java.sql.Date(dddate.getTime());
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setString(2, route.getName());
                preparedStatement.setFloat(3, route.getCoordinates().getX());
                preparedStatement.setInt(4, route.getCoordinates().getY());
                int a = 0;
                try {
                    preparedStatement.setInt(5, route.getLocationFrom().getXx());
                    a = 0;
                } catch (NullPointerException e) {
                    a++;
                    preparedStatement.setNull(5, Types.NULL);
                    preparedStatement.setNull(6, Types.NULL);
                    preparedStatement.setNull(7, Types.NULL);
                }
                if (a == 0) {
                    preparedStatement.setFloat(6, route.getLocationFrom().getYy());
                    preparedStatement.setInt(7, route.getLocationFrom().getZz());
                }
                int b = 0;
                try {
                    preparedStatement.setFloat(8, route.getLocationTo().getX());
                    b = 0;
                } catch (NullPointerException e) {
                    b++;
                    preparedStatement.setNull(8, Types.NULL);
                    preparedStatement.setNull(9, Types.NULL);
                    preparedStatement.setNull(10, Types.NULL);
                }
                if (b == 0) {
                    preparedStatement.setFloat(9, route.getLocationTo().getY());
                    preparedStatement.setInt(10, route.getLocationTo().getZ());
                }
                preparedStatement.setInt(11, route.getDistance());
                preparedStatement.setString(12, Receiver.login);
                preparedStatement.executeUpdate();
                connectToBD();
                //answer = "Элемент добавлен в коллекцию";
            } catch (SQLException e) {
                e.printStackTrace();
                //answer += "Возникла ошибка";
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            return "Новый элемент добавлен в коллекцию";
        } else if (k == 0) {
            try {
                connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
                preparedStatement = connection.prepareStatement(sqlStatement);
                java.time.ZonedDateTime ddate = route.getCreationDate();
                java.util.Date dddate = Date.from(ddate.toInstant());
                java.sql.Date sqlDate = new java.sql.Date(dddate.getTime());
                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setString(2, route.getName());
                preparedStatement.setFloat(3, route.getCoordinates().getX());
                preparedStatement.setInt(4, route.getCoordinates().getY());
                int q = 0;
                try {
                    preparedStatement.setInt(5, route.getLocationFrom().getXx());
                    q = 0;
                } catch (NullPointerException e) {
                    q++;
                    preparedStatement.setNull(5, Types.NULL);
                    preparedStatement.setNull(6, Types.NULL);
                    preparedStatement.setNull(7, Types.NULL);
                }
                if (q == 0) {
                    preparedStatement.setFloat(6, route.getLocationFrom().getYy());
                    preparedStatement.setInt(7, route.getLocationFrom().getZz());
                }
                int w = 0;
                try {
                    preparedStatement.setFloat(8, route.getLocationTo().getX());
                    w = 0;
                } catch (NullPointerException e) {
                    w++;
                    preparedStatement.setNull(8, Types.NULL);
                    preparedStatement.setNull(9, Types.NULL);
                    preparedStatement.setNull(10, Types.NULL);
                }
                if (w == 0) {
                    preparedStatement.setFloat(9, route.getLocationTo().getY());
                    preparedStatement.setInt(10, route.getLocationTo().getZ());
                }
                preparedStatement.setInt(11, route.getDistance());
                preparedStatement.setString(12, Receiver.login);
                preparedStatement.executeUpdate();
                connectToBD();
                //answer = "Элемент добавлен в коллекцию";
            } catch (SQLException e) {
                e.printStackTrace();
                //answer += "Возникла ошибка";
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            return "Новый элемент добавлен в коллекцию";
        }
        return null;
    }

    public static <E> E getLast(Collection<E> c) {
        E last = null;
        for (E e : c) last = e;
        return last;
    }

    @Override
    public int compare(Route o1, Route o2) {
        if (o1.getCoordinates().getX() > o2.getCoordinates().getX()) {
            return 1;
        } else if (o1.getCoordinates().getX() == o2.getCoordinates().getX()) {
            return 0;
        } else return -1;
    }


    public static String removeLower(long id) {
        locker.lock();
        PreparedStatement preparedStatement;
        Connection connection;
        String answer = "";
        String select = "DELETE FROM Routes WHERE id < " + id + " AND login = " + "'" + Receiver.logins.getFirst() + "'" + ";";
        try {
            connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD);
            preparedStatement = connection.prepareStatement(select);
            int k = preparedStatement.executeUpdate();
            if (k == 1) {
                answer = "Элементы удалены";
                RouteCollection.col.clear();
                connectToBD();
            } else if (k == 0) {
                answer = "У пользователя " + Receiver.login + " нет доступа к данным эелементам. Либо элементов меньше нет";
            } else {
                answer = "Элементы удалены";
                RouteCollection.col.clear();
                connectToBD();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
        return answer;
    }

    public static String maxByTo() {
        Float maxToX = 0F;
        int k = 0;
        ArrayList<Float> count1 = new ArrayList<>();
        try {
            Iterator<Route> it = RouteCollection.col.iterator();
            while (it.hasNext()) {
                Route route = it.next();
                k++;
                if (route.getLocationTo() != null) {
                    count1.add(route.getLocationTo().getX());
                }
            }
            if (k > 0) {
                maxToX = Collections.max(count1);
                Iterator<Route> itt = RouteCollection.col.iterator();
                while (itt.hasNext()) {
                    Route routt = itt.next();
                    if (routt.getLocationTo() != null) {
                        if (maxToX == routt.getLocationTo().getX())
                            return "Название маршрута с максимальной координатой X поля to: " + routt.getName();
                    }
                }
            } else return "Коллекция пуста";
        } catch (InputMismatchException | ConcurrentModificationException ignored) {
        } catch (NoSuchElementException e) {
            System.out.println("Такого элемента нет");
        }
        return null;
    }

    public static String filterStartsWithName(String name) {
        int k = 0;
        Iterator<Route> it = RouteCollection.col.iterator();
        while (it.hasNext()) {
            Route route = it.next();
            String name1 = route.getName();
            if (name1.contains(name)) {
                k++;
            }
        }
        if (k == 0) return "В коллекции нет такого маршрута " + name;
        else {
            Iterator<Route> it1 = RouteCollection.col.iterator();
            while (it1.hasNext()) {
                Route route = it1.next();
                String name1 = route.getName();
                if (name1.contains(name)) {
                    return "Маршрут " + route;
                }
            }
        }
        return null;
    }

    public static String filterLessThanDistance(Integer dist) {
        int k = 0;
        Iterator<Route> it = RouteCollection.col.iterator();
        while (it.hasNext()) {
            Route route = it.next();
            Integer dis = route.getDistance();
            if (dis.equals(dist)) {
                k++;
            }
        }
        if (k == 0) return "В коллекции нет элемента с такой дистанцией";
        else {
            Iterator<Route> itt = RouteCollection.col.iterator();
            while (itt.hasNext()) {
                Route route = itt.next();
                Integer dis = route.getDistance();
                if (dis < dist) {
                    return "Маршрут " + route;
                }
            }
        }
        return null;
    }

    private int scriptStuck = 0;
    private Route routT = new Route();

    public ArrayList<String> executeScript() {
        try {
            ArrayList<String> extraData = new ArrayList<>();
            Server ser = new Server();
            if (!(ser.getScriptName().toString().equals("script.txt")))
                System.out.println("Файл не найден");
            else {
                String command = "";
                String[] lastCommand;
                try {
                    FileReader fr = new FileReader(ser.getScriptName());
                    Scanner scrScan = new Scanner(fr);
                    while ((scrScan.hasNextLine()) && (scriptStuck < 5)) {
                        command = scrScan.nextLine();
                        lastCommand = command.trim().split(" ", 2);
                        switch (lastCommand[0]) {
                            case "help":
                                help();
                                extraData.add(help());
                                break;
                            case "info":
                                info();
                                extraData.add(info());
                                break;
                            case "show":
                                show();
                                extraData.add(show());
                                break;
                            case "add":
                                try {
                                    int check = RouteCollection.col.size();
                                    Route route1 = new Route();
                                    route1.setId(routT.randomId());
                                    route1.setName(scrScan.nextLine());
                                    float coorX = Float.parseFloat(scrScan.nextLine());
                                    int coorY = Integer.parseInt(scrScan.nextLine());
                                    route1.setCoordinates(new Coordinates(coorX, coorY));
                                    route1.setCreationDate(ZonedDateTime.now());
                                    route1.setLocationFrom(null);
                                    try {
                                        int fromX = Integer.parseInt(scrScan.nextLine());
                                        Float fromY = Float.parseFloat(scrScan.nextLine());
                                        int fromZ = Integer.parseInt(scrScan.nextLine());
                                        route1.setLocationFrom(new LocationFrom(fromX, fromY, fromZ));
                                    } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                    }
                                    route1.setLocationTo(null);
                                    try {
                                        float toX = Float.parseFloat(scrScan.nextLine());
                                        Float toY = Float.parseFloat(scrScan.nextLine());
                                        int toZ = Integer.parseInt(scrScan.nextLine());
                                        route1.setLocationTo(new LocationTo(toX, toY, toZ));
                                    } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                    }
                                    Integer dist = Integer.parseInt(scrScan.nextLine());
                                    route1.setDistance(dist);
                                    RouteCollection.col.add(route1);
                                    if ((RouteCollection.col.size() != check) && (!getLast(RouteCollection.col).getName().equals("")))
                                        System.out.println("Новый маршрут добавлен в коллекцию");
                                    else {
                                        System.out.println("Ошибка ввода");
                                        RouteCollection.col.remove(getLast(RouteCollection.col));
                                    }
                                } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException e) {
                                    System.out.println("Неверный формат введенных данных");
                                }
                                break;
                            case "updateId":
                                try {
                                    long saveId = -1;
                                    java.time.ZonedDateTime dd = null;
                                    long idScanner = -1;
                                    int k = 0;
                                    idScanner = Long.parseLong(scrScan.nextLine());
                                    Iterator<Route> it = RouteCollection.col.iterator();
                                    while (it.hasNext()) {
                                        Route rr = it.next();
                                        if (rr.getId() == idScanner) {
                                            dd = rr.getCreationDate();
                                            saveId = rr.getId();
                                            it.remove();
                                            k++;
                                        }
                                    }
                                    if (k == 0) {
                                        System.out.println("Элемента c тким ID нет в коллекции");
                                    } else {
                                        int check = RouteCollection.col.size();
                                        Route route2 = new Route();
                                        route2.setId(saveId);
                                        route2.setName(scrScan.nextLine());
                                        float coorX = Float.parseFloat(scrScan.nextLine());
                                        int coorY = Integer.parseInt(scrScan.nextLine());
                                        route2.setCoordinates(new Coordinates(coorX, coorY));
                                        route2.setCreationDate(dd);
                                        route2.setLocationFrom(null);
                                        try {
                                            int fromX = Integer.parseInt(scrScan.nextLine());
                                            Float fromY = Float.parseFloat(scrScan.nextLine());
                                            int fromZ = Integer.parseInt(scrScan.nextLine());
                                            route2.setLocationFrom(new LocationFrom(fromX, fromY, fromZ));
                                        } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                        }
                                        route2.setLocationTo(null);
                                        try {
                                            float toX = Float.parseFloat(scrScan.nextLine());
                                            Float toY = Float.parseFloat(scrScan.nextLine());
                                            int toZ = Integer.parseInt(scrScan.nextLine());
                                            route2.setLocationTo(new LocationTo(toX, toY, toZ));
                                        } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                        }
                                        Integer dist = Integer.parseInt(scrScan.nextLine());
                                        route2.setDistance(dist);
                                        RouteCollection.col.add(route2);
                                        if ((RouteCollection.col.size() != check) && (!getLast(RouteCollection.col).getName().equals("")))
                                            System.out.println("Маршрут обновлен");
                                        else {
                                            System.out.println("Ошибка ввода");
                                            RouteCollection.col.remove(getLast(RouteCollection.col));
                                        }
                                    }
                                } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException e) {
                                    System.out.println("Неверный формат введенных данных");
                                }
                                break;
                            case "clear":
                                RouteCollection.col.clear();
                                System.out.println("Все элементы коллекции удалены");
                                break;
                            case "maxByTo":
                                maxByTo();
                                break;
                            case "removeById":
                                removeByIdScr(scrScan.nextLine());
                                break;
                            case "removeLower":
                                removeLowerScr(scrScan.nextLine());
                                break;
                            case "filterStartsWithName":
                                filterStartsWithNameScr(scrScan.nextLine());
                                break;
                            case "filterLessThanDistance":
                                filterLessThanDistanceScr(scrScan.nextLine());
                                break;
                            case "addIfMax":
                                int minCom = 0;
                                ArrayList<Integer> com = new ArrayList<>();

                                try {
                                    int check = RouteCollection.col.size();
                                    Route route2 = new Route();
                                    route2.setId(routT.randomId());
                                    route2.setName(scrScan.nextLine());
                                    float coorX = Float.parseFloat(scrScan.nextLine());
                                    int coorY = Integer.parseInt(scrScan.nextLine());
                                    route2.setCoordinates(new Coordinates(coorX, coorY));
                                    route2.setCreationDate(ZonedDateTime.now());
                                    route2.setLocationFrom(null);
                                    try {
                                        int fromX = Integer.parseInt(scrScan.nextLine());
                                        Float fromY = Float.parseFloat(scrScan.nextLine());
                                        int fromZ = Integer.parseInt(scrScan.nextLine());
                                        route2.setLocationFrom(new LocationFrom(fromX, fromY, fromZ));
                                    } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                    }
                                    route2.setLocationTo(null);
                                    try {
                                        float toX = Float.parseFloat(scrScan.nextLine());
                                        Float toY = Float.parseFloat(scrScan.nextLine());
                                        int toZ = Integer.parseInt(scrScan.nextLine());
                                        route2.setLocationTo(new LocationTo(toX, toY, toZ));
                                    } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException ignored) {
                                    }
                                    Integer dist = Integer.parseInt(scrScan.nextLine());
                                    route2.setDistance(dist);
                                    RouteCollection.col.add(route2);

                                    Iterator<Route> it = RouteCollection.col.iterator();
                                    while (it.hasNext()) {
                                        Route route = it.next();
                                        com.add(compare(getLast(RouteCollection.col), route));
                                    }

                                    if ((RouteCollection.col.size() != check) && (!getLast(RouteCollection.col).getName().equals(""))) {
                                        minCom = Collections.min(com);
                                        if (minCom == -1) {
                                            System.out.println("Элемент не добавлен в коллекцию, так как он не привышает максимальный");
                                            RouteCollection.col.remove(getLast(RouteCollection.col));
                                        } else System.out.println("Новый элемент добавлен в коллекцию");
                                    } else {
                                        System.out.println("Ошибка ввода");
                                        RouteCollection.col.remove(getLast(RouteCollection.col));
                                    }
                                } catch (InputMismatchException e) {
                                    System.out.println("Ошибка ввода");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ConcurrentModificationException e) {
                                    System.out.println();
                                } catch (MyException | NoSuchElementException | NullPointerException | NumberFormatException | StringIndexOutOfBoundsException e) {
                                    System.out.println("Неверный формат введенных данных");
                                }
                                break;
                            case "executeScript":
                                scriptStuck++;
                                executeScript();
                                break;
                            case "exit":
                                System.exit(0);
                            default:
                                System.out.println("Такой команды не существует: " + command);
                        }
                    }
                    fr.close();
                } catch (FileNotFoundException e) {
                    System.out.println("Файл не найден");
                }
            }
            return extraData;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Файл не найден");
        }
        return null;
    }

    public void removeByIdScr(String str) {
        try {
            long idScan = -1;
            int k = 0;
            Long id = Long.parseLong(str);
            idScan = id;
            Iterator<Route> it = RouteCollection.col.iterator();
            try {
                while (it.hasNext()) {
                    Route route = it.next();
                    if (route.getId() == idScan) {
                        RouteCollection.col.remove(route);
                        System.out.println("Элемент коллекции удален");
                        k++;
                    }
                }
            } catch (ConcurrentModificationException e) {
                System.out.println();
            }
            if (k == 0) {
                System.out.println("Элемента с таким ID в коллекции нет");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, вы должны были указать ID элемента, который вы хотлеи удалить");
        }
    }

    public void removeLowerScr(String str) {
        try {
            long idScan = -1;
            int k = 0;
            int check = RouteCollection.col.size();
            int compCheck = RouteCollection.col.size();
            long idd = 0;
            Long id = Long.parseLong(str);
            idScan = id;
            ArrayList<Long> removed = new ArrayList<>();
            Iterator<Route> it = RouteCollection.col.iterator();
            try {
                while (it.hasNext()) {
                    Route route = it.next();
                    if (route.getId() == idScan) {
                        k++;
                        idd = route.getId();
                    }
                }
                if (k > 0) {
                    Iterator<Route> itt = RouteCollection.col.iterator();
                    while (itt.hasNext()) {
                        Route routee = itt.next();
                        if (routee.getId() < idScan) {
                            itt.remove();
                            check++;
                            removed.add(routee.getId());
                        }
                    }
                    if (check != compCheck)
                        System.out.println("Из коллекции удалены элементы с ID: " + removed);
                    else
                        System.out.println("В коллекции нет элемента, у которого ID меньше " + idd);
                } else System.out.println("В коллекции нет элемента с таким ID");
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода");
            } catch (ConcurrentModificationException e) {
                System.out.println();
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, вы должны были указать ID элемента");
        }
    }

    public void filterStartsWithNameScr(String str) {
        try {
            String nameScan = "";
            int k = 0;
            nameScan = str;
            Iterator<Route> it = RouteCollection.col.iterator();
            try {
                while (it.hasNext()) {
                    Route route = it.next();
                    String s = route.getName();
                    String n = s.substring(0, 3);
                    if (n.equals(nameScan)) {
                        System.out.println(route);
                        k++;
                    }
                }
                if (k == 0)
                    System.out.println("В коллекции нет маршрута, название которого бы начиналось с данной строки, либо ваша строка не содержит ровно 3 символа");
            } catch (InputMismatchException | StringIndexOutOfBoundsException e) {
                System.out.println("Ошибка ввода");
            } catch (ConcurrentModificationException e) {
                System.out.println();
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, вы должны были указать первые 3 символа названия элемента");
        }
    }

    public void filterLessThanDistanceScr(String str) {
        try {
            long idScan = -1;
            int k = 0;
            Integer dist = Integer.parseInt(str);
            idScan = dist;
            Iterator<Route> it = RouteCollection.col.iterator();
            try {
                while (it.hasNext()) {
                    Route route = it.next();
                    if (route.getDistance() == idScan) {
                        k++;
                    }
                }
                if (k > 0) {
                    Iterator<Route> itt = RouteCollection.col.iterator();
                    while (itt.hasNext()) {
                        Route routee = itt.next();
                        if (routee.getDistance() < idScan) {
                            System.out.println(routee);
                        }
                    }
                } else System.out.println("В коллекции нет элемента с такой дистанцией");
            } catch (InputMismatchException e) {
                System.out.println("Ошибка ввода");
            } catch (ConcurrentModificationException e) {
                System.out.println();
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка, вы должны были указать дистанцию");
        }
    }

    public static void connectToBD() {
        RouteCollection.col.clear();
        String sql = "SELECT * FROM Routes";
        try (Connection connection = DriverManager.getConnection(Receiver.URL, Receiver.USERNAME, Receiver.PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getInt("id");
                java.util.Date creationDate = resultSet.getDate("creationDate");
                String name = resultSet.getString("name");
                float coorX = resultSet.getFloat("coorX");
                int coorY = resultSet.getInt("coorY");
                int fromX = resultSet.getInt("fromX");
                Float fromY = resultSet.getFloat("fromY");
                int fromZ = resultSet.getInt("fromZ");
                float toX = resultSet.getFloat("toX");
                Float toY = resultSet.getFloat("toY");
                int toZ = resultSet.getInt("toZ");
                Integer dist = resultSet.getInt("distance");
                String login = resultSet.getString("login");
                Route routeek = new Route(name, id, new Coordinates(coorX, coorY), new LocationFrom(fromX, fromY, fromZ),
                        new LocationTo(toX, toY, toZ), dist, login);
                RouteCollection.col.add(routeek);
            }
        } catch (MyException | IOException | SQLException e) {
            System.out.println("Возникла ошибка");
            e.printStackTrace();
        }
    }
}
