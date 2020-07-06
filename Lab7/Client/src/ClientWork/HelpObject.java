package ClientWork;

import RouteInformation.*;

import java.io.Serializable;

public class HelpObject implements Serializable {
    private String com;
    private Route route;
    private long id;
    private String name;
    private Integer dist;
    private String hist;
    private String login;

    HelpObject(String com) {
        this.com = com;
    }

    HelpObject(String com, String name) {
        this.com = com;
        this.name = name;
    }

    HelpObject(String com, Route route) {
        this.com = com;
        this.route = route;
    }

    HelpObject(String com, long id, Route route) {
        this.com = com;
        this.id = id;
        this.route = route;
    }

    HelpObject(String com, long id) {
        this.com = com;
        this.id = id;
    }

    HelpObject(String com, Integer dist) {
        this.com = com;
        this.dist = dist;
    }

    HelpObject(String com, Route route, String login) {
        this.com = com;
        this.route = route;
        this.login = login;
    }

    public String getCom() {
        return com;
    }

    public Route getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public Integer getDist() {
        return dist;
    }

    public String getHist() {
        return hist;
    }

    public String toString() {
        return "Command: " + com + "\n";
    }
}
