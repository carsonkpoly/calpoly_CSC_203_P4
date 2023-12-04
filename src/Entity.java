import java.util.*;

import processing.core.PImage;

public interface Entity {
    PImage getCurrentImage();
    void nextImage();
    String log();
    String getId();
    Point getPosition();
    void setPosition(Point p);
}
