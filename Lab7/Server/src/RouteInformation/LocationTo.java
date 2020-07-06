package RouteInformation;

import java.io.IOException;
import java.io.Serializable;

public class LocationTo implements Serializable {
    private float x;
    private Float y; //Поле не может быть null
    private int z;

    public LocationTo(float x, Float y, int z) throws MyException, IOException {
        this.x = x;
        this.y = y;
        if (y == null) throw new MyException("Поле не может быть null");
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public void setZ(int y) {
        this.z = z;
    }

    public String toString() {
        if (y == null) {
            return null;
        } else {
            return (x + "," + y + "," + z);
        }
    }

    LocationTo() throws IOException {
    }
}
