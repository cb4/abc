package abc.aspectj.ast;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Block;
import polyglot.ast.TypeNode;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.util.UniqueID;
import polyglot.util.Position;

import polyglot.visit.*;
import polyglot.types.*;

import polyglot.ext.jl.ast.MethodDecl_c;

import abc.aspectj.types.AspectJTypeSystem;
import abc.aspectj.types.InterTypeMethodInstance_c;
import abc.aspectj.types.AJContext;
import abc.aspectj.visit.*;

public class IntertypeMethodDecl_c extends MethodDecl_c
    implements IntertypeMethodDecl, ContainsAspectInfo
{
    protected TypeNode host;
    protected InterTypeMethodInstance_c itMethodInstance;

    public IntertypeMethodDecl_c(Position pos,
                                 Flags flags,
                                 TypeNode returnType,
                                 TypeNode host,
                                 String name,
                                 List formals,
                                 List throwTypes,
	  	                 Block body) {
	super(pos,flags,returnType,
              name,formals,throwTypes,body);
	this.host = host;
    }

	public TypeNode host() {
		return host;
	}
	
    protected IntertypeMethodDecl_c reconstruct(TypeNode returnType, 
						List formals, 
						List throwTypes, 
						Block body,
						TypeNode host) {
	if(host != this.host) {
	    IntertypeMethodDecl_c n =
		(IntertypeMethodDecl_c) copy();
	    n.host=host;
	    return (IntertypeMethodDecl_c) 
		n.reconstruct(returnType,formals,throwTypes,body);
	}
	return (IntertypeMethodDecl_c)
	    super.reconstruct(returnType,formals,throwTypes,body);
    }

    public Node visitChildren(NodeVisitor v) {
        List formals = visitList(this.formals, v);
        TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
	TypeNode host = (TypeNode) visitChild(this.host, v);
	return reconstruct(returnType,formals,throwTypes,body,host);
    }

    public NodeVisitor addMembersEnter(AddMemberVisitor am) {
		Type ht = host.type();
		if (ht instanceof ParsedClassType) {
			AspectJTypeSystem ts = (AspectJTypeSystem) am.typeSystem();
			
			MethodInstance mi = ts.interTypeMethodInstance(position(),
		                                	               	(ClassType) methodInstance().container(),
		                                               		(ReferenceType)ht,
		                                              		methodInstance().flags(),
		                                               		methodInstance().returnType(),
		                                               		methodInstance().name(),
		                                               		methodInstance().formalTypes(),
		                                               		methodInstance().throwTypes());
	    	((ParsedClassType)ht).addMethod(mi);
	    	itMethodInstance = (InterTypeMethodInstance_c) mi;
		}
        return am.bypassChildren(this);
    }
    
	
	/**
	* @author Oege de Moor
	* change private intertype field decl into public,
	* mangling the name.
	*/
	public IntertypeMethodDecl accessChange() {
		if (flags().isPrivate()) {
			ParsedClassType ht = (ParsedClassType) host.type();
			ht.fields().remove(itMethodInstance); // remove old instance from host type    		
			MethodInstance mmi = itMethodInstance.mangled();  // retrieve the mangled instance 		
			ht.addMethod(mmi); // add new instance to host type   		
			return (IntertypeMethodDecl) name(mmi.name()).methodInstance(mmi).flags(mmi.flags());
		}
		return this;
	}
    

	public Node typeCheck(TypeChecker tc) throws SemanticException {
		if (flags().isProtected())
			throw new SemanticException("Intertype methods cannot be protected",position());
		return super.typeCheck(tc);
	}
	
	/**
	 * @author Oege de Moor
	 * record the host type in the environment, for checking of this and super
	 */
	
	public Context enterScope(Context c) {
		AJContext nc = (AJContext) super.enterScope(c);
		TypeSystem ts = nc.typeSystem();
		return nc.pushHost(ts.staticTarget(host.type()).toClass());
	}
		
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.begin(0);
	w.write(flags.translate());
        print(returnType, w, tr);
        w.write(" ");
        print(host,w,tr);
        w.write("." + name + "("); 

        w.begin(0);

	for (Iterator i = formals.iterator(); i.hasNext(); ) {
	    Formal f = (Formal) i.next();
	    print(f, w, tr);

	    if (i.hasNext()) {
		w.write(",");
		w.allowBreak(0, " ");
	    }
	}

	w.end();

	w.write(")");

	w.begin(0);

        if (! throwTypes().isEmpty()) {
	    w.allowBreak(6);
	    w.write("throws ");

	    for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
	        TypeNode tn = (TypeNode) i.next();
		print(tn, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(4, " ");
		}
	    }
	}

	w.end();

	if (body != null) {
	    printSubStmt(body, w, tr);
	}
	else {
	    w.write(";");
	}

	w.end();

    }

    public void update(abc.weaving.aspectinfo.GlobalAspectInfo gai, abc.weaving.aspectinfo.Aspect current_aspect) {
	System.out.println("IMD host: "+host.toString());
	List formals = new ArrayList();
	Iterator fi = formals().iterator();
	while (fi.hasNext()) {
	    Formal f = (Formal)fi.next();
	    formals.add(new abc.weaving.aspectinfo.Formal(AspectInfoHarvester.toAbcType(f.type().type()),
							  f.name(), f.position()));
	}
	List exc = new ArrayList();
	Iterator ti = throwTypes().iterator();
	while (ti.hasNext()) {
	    TypeNode t = (TypeNode)ti.next();
	    exc.add(t.type().toString());
	}
	abc.weaving.aspectinfo.MethodSig impl = new abc.weaving.aspectinfo.MethodSig
	    (AspectInfoHarvester.convertModifiers(flags()),
	     current_aspect.getInstanceClass(),
	     AspectInfoHarvester.toAbcType(returnType().type()),
	     name(),
	     formals,
	     exc,
	     position());
	abc.weaving.aspectinfo.MethodSig target = new abc.weaving.aspectinfo.MethodSig
	    (AspectInfoHarvester.convertModifiers(flags()),
	     gai.getClass(host.toString()),
	     AspectInfoHarvester.toAbcType(returnType().type()),
	     name(),
	     formals,
	     exc,
	     null);
	abc.weaving.aspectinfo.IntertypeMethodDecl imd = new abc.weaving.aspectinfo.IntertypeMethodDecl
	    (target, impl, current_aspect, position());
	gai.addIntertypeMethodDecl(imd);
    }
}
	

	

     


