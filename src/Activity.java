/**
 * An action that can be taken by an entity.
 * Actions can be either an activity (involving movement, gaining health, etc)
 * or an animation (updating the image being displayed).
 */
public final class Activity implements Action {
    private final Executioner entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Executioner entity, ImageStore imageStore, WorldModel world) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }
    public void executeAction(EventScheduler scheduler) {
        entity.executeAction(world, imageStore, scheduler);
    }
}
