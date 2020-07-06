package RouteInformation;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private float x; //Значение поля должно быть больше -318
    private int y;

    public Coordinates(float x, int y) throws MyException {
        this.x = x;
        if (x < -318) throw new MyException("Ошибка, координата Х не может быть меньше -318");
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return (x + "," + y);
    }

    Coordinates() {
    }
}
