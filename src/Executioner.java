public interface Executioner extends ActionEntity {
    void executeAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
