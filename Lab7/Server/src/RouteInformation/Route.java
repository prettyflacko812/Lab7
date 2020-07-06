package RouteInformation;

import WorkWithCommands.Receiver;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Route implements Serializable {
    public long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    public java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private LocationFrom from; //Поле может быть null
    private LocationTo to; //Поле может быть null
    private Integer distance; //Поле не может быть null, Значение поля должно быть больше 1
    private String login;

    public Route(String name, long id, Coordinates coordinates, LocationFrom from, LocationTo to, Integer distance, String login) throws MyException {
        this.id = id;
        this.name = name;
        if ((name.trim().length() == 0) || (name == null))
            throw new MyException("Ошибка, строка названия пути не может быть пустой");
        this.coordinates = coordinates;
        if (coordinates == null) throw new MyException("Поле не может быть null");
        this.from = from;
        this.to = to;
        this.distance = distance;
        if (distance == null || distance < 1) throw new MyException("Поле не может быть null");
        this.creationDate = ZonedDateTime.now();
        this.login = login;
    }

    public Route(String name, Coordinates coordinates, LocationFrom from, LocationTo to, Integer distance) throws MyException {
        this.id = randomId();
        this.name = name;
        if ((name.trim().length() == 0) || (name == null))
            throw new MyException("Ошибка, строка названия пути не может быть пустой");
        this.coordinates = coordinates;
        if (coordinates == null) throw new MyException("Поле не может быть null");
        this.from = from;
        this.to = to;
        this.distance = distance;
        if (distance == null || distance < 1) throw new MyException("Поле не может быть null");
        this.creationDate = ZonedDateTime.now();
    }

    public Route(long id) throws MyException, IOException {
        this.id = id;
        name = "";
        coordinates = new Coordinates();
        creationDate = ZonedDateTime.now();
        from = new LocationFrom();
        to = new LocationTo();
        distance = 0;
    }

    public Route() throws IOException {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.time.ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public LocationFrom getLocationFrom() {
        return from;
    }

    public LocationTo getLocationTo() {
        return to;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setLocationFrom(LocationFrom from) {
        this.from = from;
    }

    public void setLocationTo(LocationTo to) {
        this.to = to;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setCreationDate(java.time.ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long randomId() {
        long id = (long) (Math.random() * (1000 + 1));
        return id;
    }

    public String toString() {
        return ("ID: " + id + "\n" + "Название маршрута: " + name + "\n" + "Ваши координаты: " + coordinates
                + "\n" + "Дата инициализации: " + creationDate + "\n" + "Координаты начала маршрута: " + from + "\n"
                + "Координаты конца маршрута: " + to + "\n" + "Длина маршрута: " + distance + "\n");
    }

    public String makeString() {
        if (from == null && to == null) {
            return id + "," + name + "," + coordinates.toString() + ","
                    + creationDate + "," + null + "," + null + "," + distance;
        } else if (from == null) {
            return id + "," + name + "," + coordinates.toString() + ","
                    + creationDate + "," + null + "," + to.toString() + "," + distance;
        } else if (to == null) {
            return id + "," + name + "," + coordinates.toString() + ","
                    + creationDate + "," + from.toString() + "," + null + "," + distance;
        } else {
            return id + "," + name + "," + coordinates.toString() + ","
                    + creationDate + "," + from.toString() + "," + to.toString() + "," + distance;
        }
    }
    public void setLogin(){
        login = Receiver.login;
    }
    public String getLogin(){
        return login;
    }
    public void setNewd(int ix){
        id =ix;
    }
    public void setNewId(){
        id = (int)(Math.random()*(1000+1));
    }
}

