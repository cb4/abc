package abc.aspectj.types;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Iterator;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.types.*;

import polyglot.ast.Typed;

import abc.aspectj.ast.AdviceSpec;
import abc.aspectj.types.AspectJFlags;

import soot.javaToJimple.jj.types.JjTypeSystem_c;

public class AspectJTypeSystem_c 
       extends JjTypeSystem_c 
       implements AspectJTypeSystem {
    
    // importing the aspectJ runtime classes
	protected ClassType JOINPOINT_;
	
    public ClassType JoinPoint()  { if (JOINPOINT_ != null) return JOINPOINT_;
									 return JOINPOINT_ = load("org.aspectj.lang.JoinPoint"); }

	public ClassType JoinPointStaticPart() { 
		ClassType jp = JoinPoint();
		return jp.memberClassNamed("StaticPart");
	}
	
	protected ClassType NOASPECTBOUND_;
	
	public ClassType NoAspectBound() { if (NOASPECTBOUND_ !=null) return NOASPECTBOUND_;
										return NOASPECTBOUND_ = load("org.aspectj.lang.NoAspectBoundException");
	}
    
    // weeding out the wrong flags on aspects
	protected final Flags ASPECT_FLAGS = AspectJFlags.privilegedaspect(AspectJFlags.aspectclass(ACCESS_FLAGS.Abstract()));
 
	public void checkTopLevelClassFlags(Flags f) throws SemanticException {
		    if (AspectJFlags.isAspectclass(f)) {
		       if (!f.clear(ASPECT_FLAGS).equals(Flags.NONE))
		       throw new SemanticException("Cannot declare aspect with flag(s) " +
		                                   f.clear(ASPECT_FLAGS));
		       return;
		    }
		    super.checkTopLevelClassFlags(f);
	}
    		
	public MethodInstance adviceInstance(Position pos,
										ReferenceType container, Flags flags,
										Type returnType, String name,
										List argTypes, List excTypes, AdviceSpec spec) {

		   assert_(container);
		   assert_(returnType);
		   assert_(argTypes);
		   assert_(excTypes);
	   return new AdviceInstance_c(this, pos, container, flags,
					   returnType, name, argTypes, excTypes,spec);
	}	
   
	public MethodInstance pointcutInstance(Position pos,
											ReferenceType container, Flags flags,
											Type returnType, String name,
											List argTypes, List excTypes) {

			   assert_(container);
			   assert_(returnType);
			   assert_(argTypes);
			   assert_(excTypes);
		   return new PointcutInstance_c(this, pos, container, flags,
						   returnType, name, argTypes, excTypes);
		}	
	
	public FieldInstance interTypeFieldInstance(
		                                 	Position pos, String id, ClassType origin,
										  	ReferenceType container, Flags flags,
							  				Type type, String name) {
		assert_(origin);
		assert_(container);
		assert_(type);
		return new InterTypeFieldInstance_c(this, pos, id, origin, container, flags, type, name);
	}
	
	public MethodInstance interTypeMethodInstance(Position pos,String id, ClassType origin,
													ReferenceType container, Flags flags, Flags oflags,
													Type returnType, String name,
													List argTypes, List excTypes){
		assert_(origin);
		assert_(container);
		assert_(returnType);
		assert_(argTypes);
		assert_(excTypes);
		return new InterTypeMethodInstance_c(this, pos, id, origin, container, flags, oflags,
		  										returnType, name, argTypes, excTypes);
														
	}
	
	public ConstructorInstance interTypeConstructorInstance(Position pos,String id, ClassType origin,
														ClassType container, Flags flags,
														List argTypes, List excTypes) {
		assert_(origin);
		assert_(container);
		assert_(argTypes);
		assert_(excTypes);
		return new InterTypeConstructorInstance_c(this,pos,id,origin,container,flags,argTypes,excTypes);														
	}
		   
    public boolean isAccessible(MemberInstance mi, ClassType ctc) {
        if (mi instanceof InterTypeMemberInstance) {
        	// the following code has been copied from TypeSystem_c.isAccessible
        	// the only change is the definition of target
			ReferenceType target = ((InterTypeMemberInstance) mi).origin(); 
														// accessibility of intertype declarations
		                                                // is with respect to origin, not container
		//    System.out.println("isAccessible: mi="+mi+" ctc= "+ctc + "target="+target);
			Flags flags = mi.flags();
			if (flags.isPublic()) return true;
			if (equals(target, ctc)) return true;
			if (! target.isClass()) return false;

			ClassType ctt = target.toClass();

			// If the current class and the target class are both in the
			// same class body, then protection doesn't matter, i.e.
			// protected and private members may be accessed. Do this by 
			// working up through ctc's containers.
			if (isEnclosed(ctc, ctt)) return true;                    
			if (isEnclosed(ctt, ctc)) return true;                        
			ClassType ctcContainer = ctc;
		while (!ctcContainer.isTopLevel()) {
			ctcContainer = ctcContainer.outer();
			if (isEnclosed(ctt, ctcContainer)) return true;                        
		};

		// Check for package level scope.
		// FIXME: protected too?
		if (ctt.package_() == null && ctc.package_() == null &&
			flags.isPackage())
			return true;

	
		if (ctt.package_() != null && ctt.package_().equals (ctc.package_()) &&
			(flags.isPackage() || flags.isProtected())) {
			return true;
		}
		
		return false; // ITDs cannot be protected
        }
    	else return super.isAccessible(mi,ctc);
    }
    
    
    
    private boolean hostHasMember(AJContext c, MemberInstance mi) {
       if (mi instanceof FieldInstance)
       		return c.varInHost(((FieldInstance)mi).name());
       if (mi instanceof MethodInstance) {
       	// note: purely name-based, as this is prior to full disambiguation!
    		return c.methodInHost(((MethodInstance)mi).name());
    	}
    	return false;
    }
    
    public boolean refHostOfITD(AJContext c, Typed qualifier, MemberInstance mi) {
	   return c.inInterType() && 
	               (  (qualifier == null && mi==null) ||
	                  (qualifier != null && c.hostClass().hasEnclosingInstance(qualifier.type().toClass())) ||
	                   hostHasMember(c,mi)) ;
	   /*
	   !(!c.inInterType() 
					|| c.staticInterType()      // not so sure about this
					|| (c.nested() && qualifier == null) // and this
					|| (c.inInterType() && qualifier != null && 
						c.currentClass().hasEnclosingInstance(qualifier.type().toClass())));   */
    }
    
	public Context createContext() {
	   return new AJContext_c(this);
	}
	
	/** All flags allowed for a member class. */
	 protected final Flags MEMBER_CLASS_FLAGS = TOP_LEVEL_CLASS_FLAGS.Static().set(AspectJFlags.ASPECTCLASS);

	 public void checkMemberClassFlags(Flags f) throws SemanticException {
			if (! f.clear(MEMBER_CLASS_FLAGS).equals(Flags.NONE)) {
			throw new SemanticException(
			"Cannot declare a member class with flag(s) " +
			f.clear(MEMBER_CLASS_FLAGS) + ".");
		}

			if (f.isFinal() && f.isInterface()) {
				throw new SemanticException("Cannot declare a final interface.");
			}

		checkAccessFlags(f);
		}


		/**
		   * Requires: all type arguments are canonical.
		   *
		   * Returns the fieldMatch named 'name' defined on 'type' visible from
		   * currrClass.  If no such field may be found, returns a fieldmatch
		   * with an error explaining why. name and currClass may be null, in which case
		   * they will not restrict the output.
		   * 
		   * This needs to be overridden for AspectJ because it is possible for
		   * the currClass to have multiple fields by the desired name, introduced
		   * by different aspects, that have different accessibility characteristics. 
		   **/
		
		  public FieldInstance findField(ReferenceType container, String name,
									 ClassType currClass) throws SemanticException {
			  	assert_(container);
			  	if (container == null) {
				  	throw new InternalCompilerError("Cannot access field \"" + name +
					  	"\" within a null container type.");
			  	}

			  	List /*FieldInstance*/ fis = findFields(container, name);
			 	List acceptable = new ArrayList();
			 
			  	for (Iterator fisit = fis.iterator(); fisit.hasNext() ; ) {
			  		FieldInstance fi= (FieldInstance) fisit.next();
			  		if (isAccessible(fi,currClass))
			  			acceptable.add(fi);
			  	}
			  
			  	if (acceptable.size() == 0){
				  	throw new SemanticException("Cannot access " + name + " in " + container + " from " + currClass + ".");
			 	 }
				if (acceptable.size() > 1) {
					throw new SemanticException("Ambiguous reference to " + name + " - multiple fields in " + container);
			  	}
			  	return (FieldInstance) acceptable.get(0);
		  	}

		  public List /*FieldInstance*/ findFields(ReferenceType container, String name)
										 throws SemanticException {
			  assert_(container);

			  	if (container == null) {
				  throw new InternalCompilerError("Cannot access field \"" + name +
					  "\" within a null container type.");
			  	}

				List result = new ArrayList();
			  	Stack s = new Stack();
			  	s.push(container);

			  	while (! s.isEmpty()) {
				  	Type t = (Type) s.pop();

			  		if (! t.isReference()) {
				  		throw new SemanticException("Cannot access a field in " +
				  											" non-reference type " + t + ".");
			  		}

				  	ReferenceType rt = t.toReference();

					result.addAll(fieldsNamed(rt.fields(),name));

					if (result.size() > 0)
						return result;
		
				  	if (rt.isClass()) {
					  	// Need to check interfaces for static fields.
					  	ClassType ct = rt.toClass();

					  	for (Iterator i = ct.interfaces().iterator(); i.hasNext(); ) {
						  	Type it = (Type) i.next();
						  	s.push(it);
					  	}
				  	}

				  	if (rt.superType() != null) {
					  	s.push(rt.superType());
				  	}
		  	}

			if (result.size() == 0)
		  			throw new NoMemberException(NoMemberException.FIELD, 
										  "Field \"" + name +
						  "\" not found in type \"" +
										  container + "\".");
			return result;
	}
	
		private List fieldsNamed(List fieldInstances, String name){
			List result = new ArrayList();
			for (Iterator fit = fieldInstances.iterator(); fit.hasNext(); ) {
				FieldInstance fi = (FieldInstance) fit.next();
				if (fi.name().equals(name)) 
					result.add(fi);
			}
			return result;
		}
		
		/**
		 * Assert that <code>ct</code> implements all abstract methods required;
		 * that is, if it is a concrete class, then it must implement all
		 * interfaces and abstract methods that it or it's superclasses declare.
		 */
		public void checkClassConformance(ClassType ct) throws SemanticException {
			if (ct.flags().isAbstract() || ct.flags().isInterface()) {
				// ct is abstract or an interface, and so doesn't need to 
				// implement everything.
				return;
			}
        
			// build up a list of superclasses and interfaces that ct 
			// extends/implements that may contain abstract methods that 
			// ct must define.
			List superInterfaces = abstractSuperInterfaces(ct);

			// check each abstract method of the classes and interfaces in 
			// superInterfaces
			for (Iterator i = superInterfaces.iterator(); i.hasNext(); ) {
				ReferenceType rt = (ReferenceType)i.next();
				for (Iterator j = rt.methods().iterator(); j.hasNext(); ) {
					MethodInstance mi = (MethodInstance)j.next();
					
					// FOLLOWING LINES ARE CHANGES FOR ASPECTJ:
					ClassType container;
					if (mi instanceof InterTypeMemberInstance)
						container = ((InterTypeMemberInstance) mi).origin();
					else
						container = ct;
					// END OF CHANGES
					
					if (!mi.flags().isAbstract()) {
						// the method isn't abstract, so ct doesn't have to 
						// implement it.
						continue;
					}
                
					boolean implFound = false;
					ReferenceType curr = ct;
					while (curr != null && !implFound) {
						List possible = curr.methods(mi.name(), mi.formalTypes());
						for (Iterator k = possible.iterator(); k.hasNext(); ) {
							MethodInstance mj = (MethodInstance)k.next();
							
							// NEXT LINE IS CHANGED FOR ASPECTJ:
							if (!mj.flags().isAbstract() && isAccessible(mj, container)) {
								// May have found a suitable implementation of mi.
								// If the method instance mj is not declared
								// in the class type ct, then we need to check
								// that it has appropriate protections.
								if (!equals(ct, mj.container())) {
									try {
										// check that mj can override mi, which
										// includes access protection checks.
										checkOverride(mj, mi);                                
									}
									catch (SemanticException e) {
										// change the position of the semantic
										// exception to be the class that we
										// are checking.
										throw new SemanticException(e.getMessage(),
											ct.position());
									}
								}
								else {
									// the method implementation mj we found was 
									// declared in ct. So other checks will take
									// care of access issues
								}
								implFound = true;
								break;
							}                        
						}

						curr = curr.superType() ==  null ? 
							   null : curr.superType().toReference();
					}
                

					// did we find a suitable implementation of the method mi?
					if (!implFound) {
						throw new SemanticException(ct.fullName() + " should be " +
								"declared abstract; it does not define " +
								mi.signature() + ", which is declared in " +
								rt.toClass().fullName(), ct.position());
					}
				}
			}
		}
		
		/** All flags allowed for a method. */
		protected final Flags AJ_METHOD_FLAGS = AspectJFlags.intertype(AspectJFlags.interfaceorigin(METHOD_FLAGS));

		public void checkMethodFlags(Flags f) throws SemanticException {
			  if (! f.clear(AJ_METHOD_FLAGS).equals(Flags.NONE)) {
			  throw new SemanticException(
			  "Cannot declare method with flags " +
			  f.clear(METHOD_FLAGS) + ".");
		  		}

			  if (f.isAbstract() && f.isPrivate() && ! AspectJFlags.isIntertype(f)) {
			  throw new SemanticException(
			  "Cannot declare method that is both abstract and private.");
			  }

			  if (f.isAbstract() && f.isStatic()) {
			  throw new SemanticException(
			  "Cannot declare method that is both abstract and static.");
			  }

			  if (f.isAbstract() && f.isFinal()) {
			  throw new SemanticException(
			  "Cannot declare method that is both abstract and final.");
			  }

			  if (f.isAbstract() && f.isNative()) {
			  throw new SemanticException(
			  "Cannot declare method that is both abstract and native.");
			  }
		  }

}
