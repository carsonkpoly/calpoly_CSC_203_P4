import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        LinkedList<Point> path = new LinkedList<>();

        PriorityQueue<Node> open_list = new PriorityQueue<Node>(Comparator.comparing(Node::getF)); // orders by f value
        HashMap<Integer, Node> open_list_hash = new HashMap<>(); // for looking up previous nodes
        HashSet<Node> closed_list = new HashSet<>(); // for quick lookup

        // 1. Choose/know starting and ending points of the path
        Node start_node = new Node(0, ManhattanDistance(start, end), null, start);
//        Node end_node = new Node(-1, -1, null, end);

        Node cur_node = start_node;

        // 2. Add start node to the open list and mark it as the current node
//        cur_node = start_node;
        open_list.add(cur_node);
        open_list_hash.put(cur_node.hashCode(), cur_node);

        while(cur_node != null && !withinReach.test(cur_node.getLoc(), end)) {
            // 3. Analyze all valid adjacent nodes that are not on the closed list
            List<Point> filtered_neighbors = potentialNeighbors.apply(cur_node.getLoc()).filter(canPassThrough).toList();
            for (Point p : filtered_neighbors) {
                // turn into node
                int new_g = cur_node.getG() + 1;
                int h = ManhattanDistance(p, end);
                Node new_node = new Node(new_g, h, cur_node, p);
                // check if on closed list or already on open list
                if (closed_list.contains(new_node)) {
                    continue;
                }
                else if (open_list_hash.containsKey(new_node.hashCode())) {
                    // check if g value better
                    int old_g = open_list_hash.get(new_node.hashCode()).getG();
                    if (new_g < old_g) {
                        open_list_hash.get(new_node.hashCode()).setG(new_g); // update G value
                        open_list_hash.get(new_node.hashCode()).setPrev(cur_node); // update previous node
//                        open_list.remove(new_node);
//                        open_list.add(new_node);
                    }
                }
                else {
                    // if it isn't on either list, add to open list
                    open_list_hash.put(new_node.hashCode(), new_node);
                    open_list.add(new_node);
                }
            }
            // 4. Move the current node to the closed list
            open_list_hash.remove(cur_node.hashCode());
            open_list.remove(cur_node);
            closed_list.add(cur_node);
            // 5. Choose a node from the open list with the smallest f value and make it the current node
            cur_node = open_list.peek(); // open_list is already sorted by ascending f value
            // 7. Repeat until within reach of the goal, or until you can no longer search.
        }
        if (cur_node == null) {
            path.add(start);
            return path;
        }
        else {
            Node cur_path_node = cur_node; // this is the last node before end
            while (cur_path_node != start_node) {
                path.add(cur_path_node.getLoc());
                cur_path_node = cur_path_node.getPrev();
            }
            return path.reversed();
        }
    }

    private int ManhattanDistance(Point p1, Point p2) {
        // |x1 - x2| + |y1 - y2|
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}
