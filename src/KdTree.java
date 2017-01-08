import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class KdTree {
    // Line width
    private static final double LINE_WIDTH = 0.006f;

    private Node root;
    private int size;

    public KdTree() {
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new NullPointerException();

        // Set the root if null
        if (root == null) {
            // Increment the size
            this.size++;

            root = new Node(p);
            return;
        }

        // Walk through tree until a null node is reached
        Direction d = Direction.VERT;
        Node n = root;
        while (!n.eqPoint(p)) {
            if (d.direction(p, n)) {
                if (n.left == null) {
                    // Increment the size
                    this.size++;

                    n.left = new Node(p);
                    return;
                }
                n = n.left;
            } else {
                if (n.right == null) {
                    // Increment the size
                    this.size++;

                    n.right = new Node(p);
                    return;
                }
                n = n.right;
            }

            // Swap directions
            d = d.swap();
        }
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new NullPointerException();

        Direction d = Direction.VERT;
        Node n = root;
        while (n != null) {
            if (n.eqPoint(p))
                return true;

            // Walk the tree
            if (d == Direction.VERT)
                n = p.x() < n.x() ? n.left : n.right;
            else
                n = p.y() < n.y() ? n.left : n.right;

            // Swap directions
            d = d.swap();
        }
        return false;
    }

    public void draw() {
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0.0, 1.0);
        StdDraw.setYscale(0.0, 1.0);

        // Draw the root node
        StdDraw.setPenColor(Color.RED);
        StdDraw.setPenRadius(0.006f);
        StdDraw.line(root.x(), 0.0, root.x(), 1.0);

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.02f);
        root.point().draw();

        draw(root, Direction.VERT);

        StdDraw.show();
    }

    private void draw(Node p, Direction d) {
        if (p == null)
            return;

        if (d == Direction.VERT) {
            // Draw the left point
            if (p.left != null) {
                // Draw line
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.setPenRadius(LINE_WIDTH);
                StdDraw.line(0.0, p.left.y(), p.x() - LINE_WIDTH, p.left.y());

                // Draw the point
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setPenRadius(0.02f);
                p.left.point().draw();
            }

            // Draw the right point
            if (p.right != null) {
                // Draw line
                StdDraw.setPenColor(Color.BLUE);
                StdDraw.setPenRadius(LINE_WIDTH);
                StdDraw.line(p.x() + LINE_WIDTH, p.right.y(), 1.0, p.right.y());

                // Draw the point
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setPenRadius(0.02f);
                p.right.point().draw();
            }
        } else {
            // Draw the left point
            if (p.left != null) {
                // Draw line
                StdDraw.setPenColor(Color.RED);
                StdDraw.setPenRadius(LINE_WIDTH);
                StdDraw.line(p.left.x(), 0.0, p.left.x(), p.y() - LINE_WIDTH);

                // Draw the point
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setPenRadius(0.02f);
                p.left.point().draw();
            }

            // Draw the right point
            if (p.right != null) {
                // Draw line
                StdDraw.setPenColor(Color.RED);
                StdDraw.setPenRadius(LINE_WIDTH);
                StdDraw.line(p.right.x(), p.y() + LINE_WIDTH, p.right.x(), 1.0);

                // Draw the point
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setPenRadius(0.02f);
                p.right.point().draw();
            }
        }

        // Draw the rest of the tree
        draw(p.left, d.swap());
        draw(p.right, d.swap());
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new NullPointerException();

        return range(rect, root, Direction.VERT);
    }

    private List<Point2D> range(RectHV rect, Node n, Direction d) {
        if (n == null) {
            return new ArrayList<>();
        } else if (d.intersects(rect, n)) {
            // Add all from the left branch
            List<Point2D> lst = range(rect, n.left, d.swap());

            // Add all from the right branch
            lst.addAll(range(rect, n.right, d.swap()));

            // Add the node point if it's contained in the rectangle
            if (rect.contains(n.point()))
                lst.add(n.point());
            return lst;
        }

        return range(rect, d.closestSubtree(rect, n), d.swap());
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new NullPointerException();

        if (root == null)
            return null;

        return nearest(p, root, root, Direction.VERT);
    }

    private Point2D nearest(Point2D p, Node n, Node champ, Direction d) {
        if (n == null)
            return champ.point();

        // If the node is closer than champ, go down subtree closest to p
        Node ch = p.distanceSquaredTo(n.point()) < p.distanceSquaredTo(champ.point()) ? n : champ;

        // Find the champ in the closest subtree
        Point2D cp = nearest(p, d.closestSubtree(p, n), ch, d.swap());

        // Prune or compare with the furthest subree if necessary
        if (d.rectDist(p, n) < p.distanceSquaredTo(champ.point()))
            return closest(p, cp, nearest(p, d.furthestSubtree(p, n), ch, d.swap()));
        return cp;
    }

    private Point2D closest(Point2D p, Point2D q1, Point2D q2) {
        return p.distanceSquaredTo(q1) > p.distanceSquaredTo(q2) ? q2 : q1;
    }

    public static void main(String[] args) {
/*        In in = new In(args[0]);
        KdTree set = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            set.insert(new Point2D(x, y));
        }*/
        KdTree set = new KdTree();
        PointSET pset = new PointSET();
        for (int i = 0; i < 100000; i++) {
            double x = round(StdRandom.uniform(), 2);
            double y = round(StdRandom.uniform(), 2);
            //double x = StdRandom.uniform();
            //double y = StdRandom.uniform();
            set.insert(new Point2D(x, y));
            pset.insert(new Point2D(x, y));
        }

        for (int i = 0; i < 10; i++) {
            double x1 = round(StdRandom.uniform(), 2);
            double y1 = round(StdRandom.uniform(), 2);
            double x2 = round(StdRandom.uniform(), 2);
            double y2 = round(StdRandom.uniform(), 2);
            RectHV rect = new RectHV(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));

            double x = StdRandom.uniform();
            double y = StdRandom.uniform();
            Point2D p = new Point2D(x, y);

            Point2D p1 = pset.nearest(p);
            Point2D p2 = set.nearest(p);

            List<Point2D> pts1 = new ArrayList<>();
            List<Point2D> pts2 = new ArrayList<>();
            for (Point2D pt : set.range(rect))
                pts1.add(p);
            for (Point2D pt : pset.range(rect))
                pts2.add(p);

            System.out.println("Rectangle: " + rect);
            System.out.println("pset dist: " + p1.distanceTo(p));
            System.out.println(" set dist: " + p2.distanceTo(p));
            System.out.println("pset range: " + pts1.size());
            System.out.println(" set range: " + pts2.size());
            System.out.println();
        }
/*        RectHV rect = new RectHV(0.6, 0.0, 1.0, 0.6);
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.setPenRadius(LINE_WIDTH);
        rect.draw();

        set.draw();
        Iterable<Point2D> pts = set.range(rect);
        System.out.println("Range: " + pts);*/
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static class Node {
        private final Point2D point;
        private Node left;
        private Node right;

        public Node(Point2D point) {
            this.point = point;
        }

        public boolean eqPoint(Point2D p) {
            return point.equals(p);
        }

        public Point2D point() {
            return point;
        }

        public double x() {
            return point.x();
        }

        public double y() {
            return point.y();
        }

        @Override
        public String toString() {
            return point.toString();
        }
    }

    private enum Direction {
        HORI, VERT;

        // Swap directions
        public Direction swap() {
            return this == Direction.VERT ? Direction.HORI : Direction.VERT;
        }

        // Find the right comparison based on line direction
        public boolean direction(Point2D p, Node n) {
            return this == Direction.VERT ? p.x() < n.x() : p.y() < n.y();
        }

        public Node closestSubtree(Point2D p, Node n) {
            return direction(p, n) ? n.left : n.right;
        }

        public Node furthestSubtree(Point2D p, Node n) {
            return direction(p, n) ? n.right : n.left;
        }

        public double rectDist(Point2D p, Node n) {
            return Math.pow(this == Direction.VERT ? p.x() - n.x() : p.y() - n.y(), 2);
        }

        public Node closestSubtree(RectHV rect, Node n) {
            if (this == Direction.VERT)
                return n.x() < rect.xmin() ? n.right : n.left;
            return n.y() < rect.ymin() ? n.right : n.left;
        }

        public boolean intersects(RectHV rect, Node n) {
            return this == Direction.VERT
                    ? n.x() <= rect.xmax() && n.x() >= rect.xmin()
                    : n.y() <= rect.ymax() && n.y() >= rect.ymin();
        }
    }
}
