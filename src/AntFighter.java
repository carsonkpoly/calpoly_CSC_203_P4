import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class AntFighter implements MovingEntity {
    //    private final EntityKind kind;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;

    public AntFighter(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public void executeAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fightTarget = world.findNearest(position, new ArrayList<>(List.of(Ant.class)));

        if (fightTarget.isPresent() && moveTo(world, fightTarget.get(), scheduler)) {
            world.removeEntityAt(fightTarget.get().getPosition());

            Entity dude = Factory.createPersonSearching(id, position, actionPeriod, animationPeriod, 4, imageStore.getImageList(WorldLoader.PERSON_KEY));
            world.removeEntity(scheduler, this);
            world.tryAddEntity(dude);
            ((ActionEntity)dude).scheduleActions(scheduler, world, imageStore);
        }
        else if (fightTarget.isEmpty()) {
            Entity dude = Factory.createPersonSearching(id, position, actionPeriod, animationPeriod, 4, imageStore.getImageList(WorldLoader.PERSON_KEY));
            world.removeEntity(scheduler, this);
            world.tryAddEntity(dude);
            ((ActionEntity)dude).scheduleActions(scheduler, world, imageStore);
        }
        else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), actionPeriod);
        }
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
