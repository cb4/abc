/*
 * Created on May 13, 2005
 *
 */

package abc.om;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polyglot.util.Position;

import abc.aspectj.parse.AbcLexer;
import abc.aspectj.parse.LexerAction_c;
import abc.om.parse.OMAbcLexer;
import abc.om.parse.sym;
import abc.om.visit.ModuleStructure;
import abc.om.weaving.aspectinfo.OMGlobalAspectInfo;
import abc.weaving.aspectinfo.AbstractAdviceDecl;
import abc.weaving.aspectinfo.GlobalAspectInfo;
import abc.weaving.aspectinfo.Pointcut;
import abc.weaving.matching.AdviceApplication;
import abc.weaving.matching.ShadowMatch;
import abc.weaving.matching.WeavingEnv;
import abc.weaving.matching.AdviceApplication.ResidueConjunct;
import abc.weaving.residues.Residue;
import polyglot.types.SemanticException;
import soot.*;
import abc.weaving.matching.*;

/**
 * @author Neil Ongkingco
 *  
 */
public class AbcExtension extends abc.main.AbcExtension {
    //debug
    public static final boolean debug = false;
    private static boolean isLoaded = false;
    private GlobalAspectInfo globalAspectInfo = null;

    public static Position generated = new Position("openmod generated");
    
    public AbcExtension() { 
        super();
        isLoaded = true;
    }
    
    public static boolean isLoaded() {
        return isLoaded;
    }

    public abc.aspectj.ExtensionInfo makeExtensionInfo(Collection jar_classes,
            Collection aspect_sources) {
        return new abc.om.ExtensionInfo(jar_classes, aspect_sources,this);
    }

    public GlobalAspectInfo getGlobalAspectInfo() {
        if (globalAspectInfo == null) {
            globalAspectInfo = new OMGlobalAspectInfo();
        }
        return globalAspectInfo;
    }
    
    public void initLexerKeywords(AbcLexer lexer) {
        // Add the base keywords
        super.initLexerKeywords(lexer);
        OMAbcLexer omLexer = (OMAbcLexer) lexer;

        omLexer.addJavaKeyword("module", new LexerAction_c(
                new Integer(sym.MODULE), new Integer(omLexer.module_state())));
        omLexer.addModuleKeyword("module", new LexerAction_c(new Integer(
                sym.MODULE), new Integer(omLexer.module_state())));
        omLexer.addModuleKeyword("__sig", new LexerAction_c(new Integer(
                sym.SIGNATURE), new Integer(omLexer.module_state())));
        omLexer.addModuleKeyword("pointcut", new LexerAction_c(new Integer(
                sym.POINTCUT), new Integer(omLexer.pointcut_state())));
        omLexer.addModuleKeyword("aspect", new LexerAction_c(new Integer(
                sym.ASPECT), new Integer(omLexer.module_state())));
        omLexer.addModuleKeyword("method", new LexerAction_c(new Integer(
                sym.METHOD), new Integer(omLexer.pointcut_state())));
        omLexer.addModuleKeyword("constrain", 
                new LexerAction_c(new Integer(sym.CONSTRAIN), 
                new Integer(omLexer.module_state())));

        //overrride the class keyword
        omLexer.addGlobalKeyword("class", 
                new LexerAction_c(new Integer(sym.CLASS)) {
            
	            public int getToken(AbcLexer lexer) {
	                OMAbcLexer omLexer = (OMAbcLexer) lexer;
	                if (!omLexer.getLastTokenWasDot()) {
	                    int nextState;
	                    if (omLexer.currentState() == omLexer.aspectj_state()) {
	                        nextState = omLexer.aspectj_state();
	                    } else if (omLexer.currentState() == omLexer.module_state()) {
	                    	nextState = omLexer.pointcut_state();
	                    } else {
	                    	nextState = omLexer.java_state();
	                    }
	                    omLexer.enterLexerState(nextState);
	                }
	                return token.intValue();
	            }
        	}
        );

    }
    
    
    
    public List residueConjuncts(final AbstractAdviceDecl ad, 
            final Pointcut pc,
            final ShadowMatch sm, 
            final SootMethod method, 
            final SootClass cls, 
            final WeavingEnv we) {
        //complete rewrite, so that warnings will still be generated by
        //openModMatchesAt()
        List result = new ArrayList();
        result.add(new ResidueConjunct() {
        	             public Residue run() throws SemanticException {
        	             	return ad.preResidue(sm);
        	             }});
        //replace matchesAt with openModMatchesAt
        result.add(new ResidueConjunct() {
        	             public Residue run() throws SemanticException {
							return ModuleStructure.v().openModMatchesAt(
							        ad.getPointcut(),
							        sm,
							        ad.getAspect(),
							        we,
							        cls,
							        method,
							        ad);
        	             }
                        });

       result.add(new ResidueConjunct() {
                        public Residue run() throws SemanticException {
                        	return ad.getAdviceSpec().matchesAt(we,sm,ad);
                        }
                       });
        result.add(new ResidueConjunct() {
        		        public Residue run() throws SemanticException {
        		        	return ad.postResidue(sm);
        		        }
                       });
        return result;
    }
    
    
    public static void debPrintln(String str) {
        if (debug) {
            System.out.println(str);
        }
    }

    public static void debPrint(String str) {
        if (debug) {
            System.out.print(str);
        }
    }
}