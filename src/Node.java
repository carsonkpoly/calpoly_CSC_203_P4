public class Node {
    private int g;
    private int h;
    private int f;
    private Node prev;
    private Point loc;
    private boolean searched;

    public Node(int g, int h, Node prev, Point loc) {
        this.g = g; // distance from start
        this.h = h; // manhattan distance to end
        this.prev = prev; // previous node
        this.loc = loc; // point where this node is located
        f = g + h; // minimizing this formula gives us our pathing algorithm
//        searched = false; // to mark whether we have searched this node or not
    }

    public void setF(int f) {
        this.f = f;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

//    public void setSearched(boolean searched) {
//        this.searched = searched;
//    }

    public int getF(){
        return f;
    }

    public int getG(){
        return g;
    }

    public Point getLoc() {
        return loc;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node p) {
        prev = p;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (loc == null ? 0 : loc.hashCode()); // a node's location is it's only real identifier
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Node n = (Node) o;
        return loc.equals(n.loc);
    }
}
