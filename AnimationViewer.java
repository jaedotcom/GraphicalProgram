/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * NAME: JAE KIM
 * ==========================================================================================
 */
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field; 

class AnimationViewer extends JPanel implements Runnable, TreeModel {
	private Thread animationThread = null;		// the thread for animation
	private static int DELAY = 120;				 // the current animation speed
	ArrayList<Shape> shapes = new ArrayList<Shape>(); //create the ArrayList to store shapes
	private ShapeType currentShapeType=Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType=Shape.DEFAULT_PATHTYPE;	// the current path type
	private Color currentColor=Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private int currentPanelWidth=Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT, currentWidth=Shape.DEFAULT_WIDTH, currentHeight=Shape.DEFAULT_HEIGHT;
	private NestedShape root;
	private ArrayList<TreeModelListener> treeModelListener = new ArrayList<TreeModelListener>();

	public AnimationViewer() {
		start();
		addMouseListener(new MyMouseAdapter());
		root = new NestedShape(currentPanelWidth, currentPanelHeight);
	}

	public boolean isLeaf(Object node) {
        return !(node instanceof NestedShape);
    }

    public boolean isRoot(Shape selectedNode) {
        return (selectedNode == root);
    }

    public Shape getChild(Object parent, int index) {
        if (!(parent instanceof NestedShape)) {
            return null;
        }
        else if (index < 0 || index > ((NestedShape)parent).getSize()) {
            return null;
        }
        return ((NestedShape) parent).getInnerShapeAt(index);
    }

    public int getChildCount(Object parent) {
        if (!(parent instanceof NestedShape)) {
            return 0;
        }
        return ((NestedShape) parent).getSize();
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (!(parent instanceof NestedShape)) {
            return -1;
        }
        else {
            return ((NestedShape) parent).indexOf( (Shape) child);
        }
    }

	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked( MouseEvent e ) {
			boolean found = false;
			for (Shape currentShape: root.getAllInnerShapes())
				if ( currentShape.contains( e.getPoint()) ) { // if the mousepoint is within a shape, then set the shape to be selected/deselected
					currentShape.setSelected( ! currentShape.isSelected() );
					found = true;
				}
			if (!found){
				Shape newChildShape = root.createInnerShape(e.getX(), e.getY(), getCurrentWidth(), getCurrentHeight(), getCurrentColor(), getCurrentPathType(), getCurrentShapeType());
				insertNodeInto(newChildShape, root);
			}
		}
	}

    public void addTreeModelListener(final TreeModelListener tml) {
        treeModelListener.add(tml);
    }

    public void removeTreeModelListener(final TreeModelListener tml) {
        treeModelListener.remove(tml);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {	
	}

	public void insertNodeInto(Shape newChild, NestedShape parent) {
		Object[] objArr = new Object[]{newChild};
		int[] objInt = new int[]{parent.getSize()-1};
		fireTreeNodesInserted(this, parent.getPath(), objInt, objArr);
	}

	public NestedShape getRoot() {
		return root;
	}

	protected void createNewShape(int x, int y) {
		switch (currentShapeType) {
			case RECTANGLE: {
				shapes.add( new RectangleShape(x, y,currentWidth,currentHeight,currentPanelWidth,currentPanelHeight,currentColor,currentPathType));
				break;
			} case OVAL: {
				shapes.add( new OvalShape(x, y,currentWidth,currentHeight,currentPanelWidth,currentPanelHeight,currentColor,currentPathType));
				break;
			}
		}
	}

	public void addShapeNode(NestedShape selectedNode) {
		if (selectedNode == root) {
			Shape newChild = selectedNode.createInnerShape(0, 0, getCurrentWidth(), getCurrentHeight(), getCurrentColor(), getCurrentPathType(), getCurrentShapeType());
			insertNodeInto(newChild, selectedNode);
		} else {
			Shape newChild = selectedNode.createInnerShape(0, 0, getCurrentWidth()/2, getCurrentHeight()/2, getCurrentColor(), getCurrentPathType(), getCurrentShapeType());
			insertNodeInto(newChild, selectedNode);
		}
	}

	public void setCurrentColor(Color bc) {
		currentColor = bc;
			for (Shape currentShape: root.getAllInnerShapes())
				if (currentShape.isSelected())
					currentShape.setColor(currentColor);
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape: root.getAllInnerShapes()) {
		currentShape.move();
		currentShape.draw(g);
		currentShape.drawHandles(g);
		}
	}

	public void removeNodeFromParent(Shape selectedNode) {
		Shape parent = selectedNode.getParent();
		int indexFromParent = ((NestedShape)parent).indexOf(selectedNode);
		((NestedShape)parent).removeInnerShape(selectedNode);
		Object[] objArr = new Object[]{selectedNode};
		int[] objInt = new int[]{indexFromParent};
		fireTreeNodesRemoved(this, parent.getPath(), objInt, objArr);
	}

	public void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
			for (final TreeModelListener tml : treeModelListener)
			  tml.treeNodesInserted(event);
	}

	public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children) {
		final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
			for (final TreeModelListener tml : treeModelListener)
			tml.treeNodesRemoved(event);
	}

	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight() ;
		for (Shape currentShape: root.getAllInnerShapes())
			currentShape.setPanelSize(currentPanelWidth,currentPanelHeight );
	}

	public void setCurrentShapeType(ShapeType value) { currentShapeType = value; }
	public void setCurrentPathType(PathType value) { currentPathType = value; }
	public ShapeType getCurrentShapeType() { return currentShapeType; }
	public PathType getCurrentPathType() { return currentPathType; }
	public int getCurrentWidth() { return currentWidth; }
	public int getCurrentHeight() { return currentHeight; }
	public Color getCurrentColor() { return currentColor; }
	public void update(Graphics g){ paint(g); }
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}

	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}

	public void run() {
		Thread myThread = Thread.currentThread();
		while(animationThread==myThread) {
			repaint();
			pause(DELAY);
		}
	}

	private void pause(int milliseconds) {
		try {
			Thread.sleep((long)milliseconds);
		} catch(InterruptedException ie) {}
	}
}
