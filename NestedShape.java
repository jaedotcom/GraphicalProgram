/*
 *	===============================================================================
 *	NestedShape.java : Represents nested shapes and contains zero or more inner 
 *  shapes that bounces around this shape. NestedShape instances can be RectangleShape,
 *  OvalShape, or NestedShape instances. 
 *  NAME: JAE KIM
 *	=============================================================================== */
import java.awt.*;
import java.util.*;
class NestedShape extends RectangleShape {
    private ArrayList<Shape> innerShapes = new ArrayList<Shape>();

    public NestedShape() { 
        super();
        int newWidth = width/2;
        int newHeight = height/2;
        Shape inner = createInnerShape(0, 0, newWidth, newHeight, color, PathType.BOUNCE, ShapeType.RECTANGLE);
    }
    public NestedShape(int x, int y, int width, int height, int panelWidth, int panelHeight, Color c, PathType pt) { 
        super(x, y, width, height, panelWidth, panelHeight, c, pt);
        int newWidth = width/2;
        int newHeight = height/2;
        Shape inner = createInnerShape(0, 0, newWidth, newHeight, color, PathType.BOUNCE, ShapeType.RECTANGLE);
    }
    public NestedShape(int width, int height) { 
        super();
        this.width = width;
        this.height = height;
        this.panelWidth = DEFAULT_PANEL_WIDTH;
        this.panelHeight = DEFAULT_PANEL_HEIGHT;
        this.color = Color.black;
        RectangleShape root = new RectangleShape(x, y, width, height, panelWidth, panelHeight, color, PathType.BOUNCE);
    }
    public Shape createInnerShape(int x, int y, int w, int h, Color c, PathType pt, ShapeType st) {
        if (st == ShapeType.RECTANGLE) {
            Shape newShape = new RectangleShape(x, y, w, h, super.width, super.height, c, pt);
            innerShapes.add(newShape);
            newShape.setParent(this);
            return newShape;
        } else if (st == ShapeType.OVAL) {
                Shape newShape = new OvalShape(x, y, w, h, super.width, super.height, c, pt);
                innerShapes.add(newShape);
                newShape.setParent(this);
                return newShape;
        } else {
                Shape newShape = new NestedShape(x, y, w, h, super.width, super.height, c, pt);
                innerShapes.add(newShape);
                newShape.setParent(this);
                return newShape;
        }
    }
    public Shape getInnerShapeAt(int index) {
    	return innerShapes.get(index);
    }
    public int getSize() {
    	return innerShapes.size();
    }
    public int indexOf(Shape s) {
        return innerShapes.indexOf(s);
    }
    public void addInnerShape(Shape s) {
        innerShapes.add(s);
        s.setParent(this);   
    }
    public void removeInnerShape(Shape s) {
        s.setParent(null);
        innerShapes.remove(s);
    }
    public void removeInnerShapeAt(int index) {
        Shape removed = innerShapes.remove(index);
        removed.setParent(null);
    }
    public ArrayList<Shape> getAllInnerShapes() {
        return innerShapes;
    }
    public void setColor(Color c) {
        super.setColor(c);
        for (Shape s : innerShapes) 
            s.setColor(c);
    }
    public void move() {
        super.move();
        for (Shape s : innerShapes)
            s.move();
    }
    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.width, this.height);
        g.translate(x, y);
        g.translate(-x, -y);
        for (Shape s : innerShapes) {
            g.translate(x, y);
            s.draw(g);
            g.translate(-x, -y);
        } 
    }
}

