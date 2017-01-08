import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {

    private Set<Point2D> ptSet;

    public PointSET() {
        ptSet = new TreeSet<>();
    }

    public boolean isEmpty() {
        return ptSet.isEmpty();
    }

    public int size() {
        return ptSet.size();
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new NullPointerException();
        ptSet.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new NullPointerException();
        return ptSet.contains(p);
    }

    public void draw() {
        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.setPenRadius(0.008f);
        ptSet.forEach(p -> p.draw());
        StdDraw.show();
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new NullPointerException();

        List<Point2D> pts = new ArrayList<>();
        for (Point2D p : ptSet)
            if (rect.contains(p))
                pts.add(p);
        return pts;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new NullPointerException();

        Point2D min = null;
        for (Point2D pt : ptSet)
            if (min == null || p.distanceTo(pt) < p.distanceTo(min))
                min = pt;
        return min;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        PointSET set = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            set.insert(new Point2D(x, y));
        }

        StdDraw.setPenColor(Color.BLUE);
        StdDraw.setPenRadius(0.008f);
        Point2D pt = new Point2D(0.7, 0.8);
        pt.draw();

        StdDraw.setPenColor(Color.RED);
        StdDraw.setPenRadius(0.008f);
        RectHV rect = new RectHV(0.2, 0.1, 0.6, 1.1);
        rect.draw();

        StdDraw.setPenColor(Color.CYAN);
        StdDraw.setPenRadius(0.008f);
        RectHV rect2 = new RectHV(0.0, 0.0, 1, 1);
        rect.draw();

        set.draw();

        System.out.println(set.nearest(pt));
        System.out.println(set.range(rect));
        System.out.println(set.range(rect2));
    }
}
