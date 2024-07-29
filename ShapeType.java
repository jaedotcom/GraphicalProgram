/*
 *	===============================================================================
 *	ShapeType represents the type of a shape that a shape can take.
 * 	NAME: JAE KIM
 *	=============================================================================== */

enum ShapeType { RECTANGLE, OVAL, NESTED;
	public ShapeType next() {
		return values()[(ordinal() + 1) % values().length];
	  }
	}