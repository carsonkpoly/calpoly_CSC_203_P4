import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class PersonFull implements MovingEntity {
//    private final EntityKind kind;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int resourceLimit;
    private final double actionPeriod;
    private final double animationPeriod;

    public PersonFull(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public void executeAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Find the nearest House to target
        Optional<Entity> target = world.findNearest(position, new ArrayList<>(List.of(House.class, Ruin.class)));
        // check if there is a ruin that we can go to a ruin (find nearest)
        if (target.isPresent() && moveTo(world, target.get(), scheduler)) {
            if (target.get().getClass() == Ruin.class) {
                Entity house = Factory.createHouse(WorldLoader.HOUSE_KEY + "_" + target.get().getId(),
                        target.get().getPosition(), imageStore.getImageList(WorldLoader.HOUSE_KEY));
                world.removeEntity(scheduler, target.get());
                world.tryAddEntity(house);
            }
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
    }

    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Factory.createPersonSearching(id, position, actionPeriod, animationPeriod, resourceLimit, images);

        world.removeEntity(scheduler, this);

        world.tryAddEntity(dude);
        ((ActionEntity)dude).scheduleActions(scheduler, world, imageStore);
    }

    public void transformFighter(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Factory.createAntFighter(id, position, images, actionPeriod, animationPeriod);

        world.removeEntity(scheduler, this);

        world.tryAddEntity(dude);
        ((ActionEntity)dude).scheduleActions(scheduler, world, imageStore);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.getPosition())) {
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
