
package abc.weaving.aspectinfo;

import polyglot.util.Position;

import soot.*;

import java.util.*;

/** A formal parameter to a method or constructor. */
public class Formal extends Syntax {
    private AbcType type;
    private String name;

    public Formal(AbcType type, String name, Position pos) {
	super(pos);
	this.type = type;
	this.name = name;
    }

    public AbcType getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    public String toString() {
	return type+" "+name;
    }
}
