package abc.weaving.matching;

import java.util.List;

import soot.*;
import soot.tagkit.Host;

import abc.weaving.aspectinfo.AbstractAdviceDecl;
import abc.weaving.residues.*;

/** The results of matching at an preinitialization shadow
 *  @author Ganesh Sittampalam
 *  @date 05-May-04
 */
public class PreinitializationShadowMatch extends BodyShadowMatch {

    private PreinitializationShadowMatch(SootMethod container) {
	super(container);
    }

    public List/*<SootClass>*/ getExceptions() {
	return container.getExceptions();
    }

    public static PreinitializationShadowMatch matchesAt(MethodPosition pos) {
	if(!(pos instanceof WholeMethodPosition)) return null;
	if(abc.main.Debug.v().traceMatcher) System.err.println("Preinitialization");

	SootMethod container=pos.getContainer();
	if(!container.getName().equals(SootMethod.constructorName)) return null;
	return new PreinitializationShadowMatch(container);
    }

    public Host getHost() {
	// FIXME: should point to first real statement or something
	return container;
    }

    public SJPInfo makeSJPInfo() {
	return new SJPInfo
	    ("preinitialization","ConstructorSignature","makeConstructorSig",
	     SJPInfo.makeConstructorSigData(container),getHost());
    }

    protected AdviceApplication doAddAdviceApplication
	(MethodAdviceList mal,AbstractAdviceDecl ad,Residue residue) {

	PreinitializationAdviceApplication aa
	    =new PreinitializationAdviceApplication(ad,residue);
	mal.addPreinitializationAdvice(aa);
	return aa;
    }

    public ContextValue getThisContextValue() {
        return null;
    }

    public boolean supportsAround() {
	return false;
    }

    public String joinpointName() {
	return "preinitialization";
    }

}
