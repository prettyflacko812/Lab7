package RouteInformation;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;

public class RouteCollection implements Serializable {
    public static LinkedHashSet<Route> col = new LinkedHashSet<>();
    public static ZonedDateTime dateTime = ZonedDateTime.now();
}
