/*
 *	===============================================================================
 *	PathType represents the type of a path that a shape can take.
 *  NAME: JAE KIM
 *	=============================================================================== */

enum PathType { BOUNCE, FALL;
	public PathType next() {
		return values()[(ordinal() + 1) % values().length];
	}
}
