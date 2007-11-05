package org.jastadd.plugin.jastaddj.view;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.jastadd.plugin.AST.IJastAddNode;
import org.jastadd.plugin.jastaddj.AST.IJastAddJFindDeclarationNode;
import org.jastadd.plugin.jastaddj.AST.IJastAddJFindReferencesNode;
import org.jastadd.plugin.jastaddj.AST.IJastAddJTypeHierarhcyNode;
import org.jastadd.plugin.providers.JastAddLabelProvider;
import org.jastadd.plugin.providers.JastAddOnDemandTreeLabelProviderAdapter;
import org.jastadd.plugin.util.JastAddOnDemandNodeDoubleClickOpener;
import org.jastadd.plugin.view.JastAddBaseHierarchyView;

public class JastAddJTypeHierarchyView extends
		JastAddBaseHierarchyView<IJastAddNode> {
	public static String VIEW_ID = "org.jastadd.plugin.explore.JastAddJTypeHierarchy";
	private LabelProvider labelProvider = new JastAddLabelProvider(new LabelProvider());
	
	public static IJastAddNode filterNode(IJastAddNode input) {
		if (input == null)
			return null;
		if (!(input instanceof IJastAddJFindDeclarationNode))
			return null;
		input = ((IJastAddJFindDeclarationNode) input).declaration();
		if (!(input instanceof IJastAddJTypeHierarhcyNode))
			return null;
		return input;
	}
	
	public static void activate(IJastAddNode input) throws CoreException {
		input = filterNode(input);
		if (input == null) return;

		JastAddJTypeHierarchyView view = (JastAddJTypeHierarchyView) activate(VIEW_ID);
		view.setInput(input);
	}

	protected void configureLabel(Label label) {
		label.setText("Type hierarchy");
	}

	protected void configureTreeViewer(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new JastAddJTypeHierarchyContentProvider());
		treeViewer
				.setLabelProvider(new JastAddOnDemandTreeLabelProviderAdapter(
						labelProvider));
		treeViewer.addDoubleClickListener(new JastAddOnDemandNodeDoubleClickOpener());
	}

	public void setInput(IJastAddNode input) {
		label.setText("Type hierarchy of " + labelProvider.getText(input));
		treeViewer.setInput(input);
		treeViewer.refresh();
		treeViewer.expandToLevel(2);
		refreshLayout();
	}
}
