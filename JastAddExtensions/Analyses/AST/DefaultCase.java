
package AST;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.FileNotFoundException;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import changes.*;

public class DefaultCase extends Case implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public Object clone() throws CloneNotSupportedException {
        DefaultCase node = (DefaultCase)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    public ASTNode copy() {
      try {
          DefaultCase node = (DefaultCase)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
    public ASTNode fullCopy() {
        DefaultCase res = (DefaultCase)copy();
        for(int i = 0; i < getNumChild(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 401

  public void nameCheck() {
    if(bind(this) != this) {
      error("only one default case statement allowed");
    }
  }

    // Declared in PrettyPrint.jadd at line 726


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("default:\n");
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 209

    public DefaultCase() {
        super();


    }

    // Declared in java.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 12

  public boolean mayHaveRewrite() { return false; }

    // Declared in NameCheck.jrag at line 429
    public boolean constValue(Case c) {
        boolean constValue_Case_value = constValue_compute(c);
        return constValue_Case_value;
    }

    private boolean constValue_compute(Case c) {  return  c instanceof DefaultCase;  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
