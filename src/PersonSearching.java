import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class PersonSearching implements MovingEntity {
//    private final EntityKind kind;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int resourceLimit;
    private int resourceCount;
    private final double actionPeriod;
    private final double animationPeriod;

    public PersonSearching(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public void executeAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(position, new ArrayList<>(List.of(Sapling.class, Tree.class)));

        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transformSearching(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    private boolean transformSearching(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Entity dude = Factory.createPersonFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.tryAddEntity(dude);
            ((ActionEntity)dude).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.getPosition())) {
            resourceCount += 1;
            ((HealthEntity)target).takeDamage(1);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(position, destPos,
                p -> (world.withinBounds(p)) && (world.getOccupant(p).isEmpty()),
                Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()) { // algorithm will create an empty list if next to target
            return position;
        } else {
            return path.get(0);
        }
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());

    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public double getActionPeriod() {
        return actionPeriod;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }
    public void setPosition(Point pos) {
        this.position = pos;
    }
}
