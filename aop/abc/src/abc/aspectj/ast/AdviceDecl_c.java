package arc.aspectj.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;

import polyglot.util.CodeWriter;
import polyglot.util.UniqueID;
import polyglot.util.Position;
import polyglot.util.TypedList;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.TypeNode;
import polyglot.ast.Node;

import polyglot.types.Flags;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.ClassType;

import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import polyglot.visit.TypeBuilder;

import polyglot.ext.jl.ast.MethodDecl_c;

import arc.aspectj.ast.AdviceFormal_c;

import arc.aspectj.types.AspectJTypeSystem;

public class AdviceDecl_c extends MethodDecl_c
                          implements AdviceDecl
{
    protected AdviceSpec spec;
    protected Pointcut pc;
 
    
    // if the returnVal of "after returning" or "after throwing" is
    // specified, make it an additional parameter to the advice body
    private static List locs(Formal rt, List formals) {
    	if (rt==null)
    	  return formals;
    	else {
    		List locs = ((TypedList)formals).copy();
    		locs.add(rt);
    		return locs;
    	}
    }
    
    private static List adviceFormals(List formals) {
    	List result = new TypedList(new LinkedList(), AdviceFormal.class, false);
    	for (Iterator i = formals.iterator(); i.hasNext(); ) {
    		Formal f = (Formal) i.next();
    		result.add(new AdviceFormal_c(f));
    	}
    	return result;
    }

    public AdviceDecl_c(Position pos,
                        Flags flags,
                        AdviceSpec spec,
                        List throwTypes,
                        Pointcut pc,
	  	                Block body) {
		super(pos,
	    	  flags, 
	     	  spec.returnType(),
	     	  UniqueID.newID("$advice$"),
	          adviceFormals(locs(spec.returnVal(),spec.formals())),
	          throwTypes,
	          body);
		this.spec = spec;
    	this.pc = pc;
    }
    
    
    // new visitor code
	protected AdviceDecl_c reconstruct(TypeNode returnType, 
								       List formals, 
								       List throwTypes,
								       Block body,
								       AdviceSpec spec,
								       Pointcut pc) {
		if (spec != this.spec || pc != this.pc) {
			AdviceDecl_c n = (AdviceDecl_c) copy();
			n.spec = spec;
			n.pc = pc;
			return (AdviceDecl_c) n.reconstruct(returnType, formals, throwTypes, body);
		}

		return (AdviceDecl_c) super.reconstruct(returnType, formals, throwTypes, body);
	}

	public Node visitChildren(NodeVisitor v) {	
		TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
		List formals = visitList(this.formals, v);
		List throwTypes = visitList(this.throwTypes, v);
		// do not visit spec, to avoid duplicate errors
		Pointcut pc = (Pointcut) visitChild(this.pc,v);
		Block body = (Block) visitChild(this.body, v);
		return reconstruct(returnType, formals, throwTypes, body, spec, pc);
	}

   public Context enterScope(Node child, Context c) {
   	    Context nc = super.enterScope(child,c);
   	    if (child==pc) // pointcuts should be treated as a static context
   	    	return nc.pushStatic();
   	    else
   	    	return nc;
   }
	
	 public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
		 if (ar.kind() == AmbiguityRemover.SUPER) {
			 return ar.bypassChildren(this);
		 }
		 else if (ar.kind() == AmbiguityRemover.SIGNATURES) {
			 if (body != null) {
			 	Collection bp = new LinkedList();
			 	bp.add(body);
			 	bp.add(pc);
				return ar.bypass(bp);
			 }
		 }

		 return ar;
	 }
    
    /** Type checking of proceed: keep track of the methodInstance for the current proceed
     *  the ProceedCall will query this information via the proceedInstance() 
     *  method
     * */
	static private MethodInstance proceedInstance = null;
	static private Context scope = null;
	static public MethodInstance  proceedInstance(Context c) {
		if (c==null)
		   return null;
		if (c==scope)
		   return proceedInstance;
		else return proceedInstance(c.pop());
	}

	public Context enterScope(Context c) {
		Context nc = super.enterScope(c);
		
		// inside an advice body, thisJoinPoint is in scope, but nowhere else in an aspect
		AspectJTypeSystem ts = (AspectJTypeSystem)nc.typeSystem();
	    LocalInstance jp = ts.localInstance(position(), 
	                                                                Flags.FINAL, 
	                                                                ts.JoinPoint(), 
                                                                    "thisJoinPoint");
		nc.addVariable(jp);
		LocalInstance sjp = ts.localInstance(position(), 
		                                                              Flags.FINAL, 
		                                                              ts.JoinPointStaticPart(), 
                                                                      "thisJoinPointStaticPart");
		nc.addVariable(sjp);
		
		if (spec instanceof Around)
			proceedInstance = methodInstance().name("proceed");
		else
		    proceedInstance = null;
        scope = nc;
               
		return nc;
	}
	
	/** Type check the advice: first the usual method checks, then whether the "throwing" result is
	 *  actually throwable
	 *  */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		super.typeCheck(tc);
		if (spec instanceof AfterThrowing && spec.returnVal() != null) {
			
			// get the resolved type of the returnVal, which is the last parameter
			// of the advice method:
			List formalTypes = methodInstance().formalTypes();
			Type t = (Type)(formalTypes.get(formalTypes.size()-1));
			
			if (! t.isThrowable()) {
				TypeSystem ts = tc.typeSystem();
				throw new SemanticException("type \"" + t + "\" is not a subclass of \" +" +					                        ts.Throwable() + "\".", spec.returnVal().type().position());
			}
		}
		return this;
	}
	
	/** build the type; the spec is included in the advice instance to give
	 *  intelligible error messages - see adviceInstance overrides
	 */	
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
			TypeSystem ts = tb.typeSystem();

			List l = new ArrayList(formals.size());
			for (int i = 0; i < formals.size(); i++) {
			  l.add(ts.unknownType(position()));
			}

			List m = new ArrayList(throwTypes().size());
			for (int i = 0; i < throwTypes().size(); i++) {
			  m.add(ts.unknownType(position()));
			}

			MethodInstance mi = ((AspectJTypeSystem)ts).adviceInstance(position(), ts.Object(),
												  Flags.NONE,
												  ts.unknownType(position()),
												  name, l, m, spec);
			return methodInstance(mi);
		}

	protected MethodInstance makeMethodInstance(ClassType ct, TypeSystem ts)
		throws SemanticException {

		List argTypes = new LinkedList();
		List excTypes = new LinkedList();

		for (Iterator i = formals.iterator(); i.hasNext(); ) {
			Formal f = (Formal) i.next();
			argTypes.add(f.declType());
		}

		for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
			TypeNode tn = (TypeNode) i.next();
			excTypes.add(tn.type());
		}

		Flags flags = this.flags;

		if (ct.flags().isInterface()) {
			flags = flags.Public().Abstract();
		}
	
	
		return ((AspectJTypeSystem)ts).adviceInstance(position(),
								       ct, flags, returnType.type(), name,
								       argTypes, excTypes,spec);
	}
		
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
			w.begin(0);
			w.write(flags.translate());

			print(spec,w,tr);

			w.begin(0);

			if (! throwTypes.isEmpty()) {
				w.allowBreak(6);
				w.write("throws ");

				for (Iterator i = throwTypes.iterator(); i.hasNext(); ) {
					TypeNode tn = (TypeNode) i.next();
					print(tn, w, tr);
	
					if (i.hasNext()) {
						w.write(",");
						w.allowBreak(4, " ");
					}
				}
			}

			w.end();

			w.write(":");

			w.allowBreak(0);

			print(pc, w, tr);
	
			if (body != null) 
			   printSubStmt(body, w, tr);
			else w.write(";");

			w.end();
		}
}
	

	

     


