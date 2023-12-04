public interface MovingEntity extends Executioner {
    Point nextPosition(WorldModel world, Point destPos);
    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
