package RouteInformation;

import java.io.IOException;
import java.io.Serializable;

public class LocationFrom implements Serializable {
    private int xx;
    private Float yy; //Поле не может быть null
    private int zz;

    public LocationFrom(int xx, Float yy, int zz) throws MyException, IOException {
        this.xx = xx;
        this.yy = yy;
        if (yy == null) throw new MyException("Поле не может быть null");
        this.zz = zz;
    }

    public int getXx() {
        return xx;
    }

    public Float getYy() {
        return yy;
    }

    public int getZz() {
        return zz;
    }

    public void setX(int xx) {
        this.xx = xx;
    }

    public void setY(Float yy) {
        this.yy = yy;
    }

    public void setZ(int zz) {
        this.zz = zz;
    }


    public String toString() {
        if (yy == null) {
            return null;
        } else {
            return (xx + "," + yy + "," + zz);
        }
    }

    LocationFrom() throws IOException {
    }
}

