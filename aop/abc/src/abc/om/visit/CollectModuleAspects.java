/* abc - The AspectBench Compiler
 * Copyright (C) 2005 Neil Ongkingco
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*
 * Created on Jun 18, 2005
 *
 */
package abc.om.visit;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import abc.om.AbcExtension;
import abc.om.ExtensionInfo;
import abc.om.ast.DummyAspectDecl_c;
import abc.om.ast.ModuleDecl;
import abc.om.ast.OpenModNodeFactory;
import abc.om.modulestruct.ModuleNode;
import abc.om.modulestruct.ModuleNodeModule;
import abc.weaving.aspectinfo.AbcFactory;
import abc.weaving.aspectinfo.Aspect;

/**
 * Attaches the dummy aspect to a module.
 * @author Neil Ongkingco
 */
public class CollectModuleAspects extends ContextVisitor {
    ExtensionInfo ext = null;
    
    public CollectModuleAspects(Job job, TypeSystem ts, OpenModNodeFactory nf, ExtensionInfo ext) {
        super(job, ts, nf);
        this.ext = ext;
    }

    protected NodeVisitor enterCall(Node parent, Node n)
            throws SemanticException {
        //attaches an Aspect to a moduleNode, which is used in generating the
        //cflow counter initializations
		abc.om.AbcExtension abcExt = (abc.om.AbcExtension) abc.main.Main.v().getAbcExtension();

        if (n instanceof ModuleDecl) {
            ModuleDecl decl = (ModuleDecl) n;
            DummyAspectDecl_c dummyAspectDecl = (DummyAspectDecl_c) parent;
            Aspect dummyAspect = ext.getAbcExtension().getGlobalAspectInfo().getAspect(
                    				AbcFactory.AbcClass(dummyAspectDecl.type())
                    				);
            AbcExtension.debPrintln(AbcExtension.COLLECT_MODULE_ASPECTS_DEBUG,
                    dummyAspect.toString());
            ModuleNodeModule module = (ModuleNodeModule)abcExt.moduleStruct.getNode(decl.name(), 
                    				ModuleNode.TYPE_MODULE);
            module.setDummyAspect(dummyAspect);
        }
        return super.enterCall(parent, n);
    }

}
