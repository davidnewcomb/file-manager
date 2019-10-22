package com.github.filemanager.gui.tree;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FmModel;

public class FileTree extends JTree {

	private static Logger L = LoggerFactory.getLogger(FileTree.class);
	private FmModel model;

	public FileTree(FmModel _model) {
		super(_model.getTreeModel());
		model = _model;
		setRootVisible(false);
		expandRow(0);
		setVisibleRowCount(15);

		setCellRenderer(new FileTreeCellRenderer());

		TreeSelectionListener treeSelectionListener = new FileTreeSelectionListener(model);
		addTreeSelectionListener(treeSelectionListener);

		// addTreeExpansionListener(new TreeExpansionListener() {
		//
		// @Override
		// public void treeExpanded(TreeExpansionEvent event) {
		// L.info("treeExpanded");
		// }
		//
		// @Override
		// public void treeCollapsed(TreeExpansionEvent event) {
		// L.info("treeCollapsed");
		// TreePath path = event.getPath();
		// DefaultMutableTreeNode n = (DefaultMutableTreeNode)
		// path.getLastPathComponent();
		// n.removeAllChildren();
		//
		// // FileTree o = (FileTree) event.getSource();
		// //
		// // DefaultTreeModel m = (DefaultTreeModel) o.getModel();
		// }
		// });
		// addTreeWillExpandListener(new TreeWillExpandListener() {
		//
		// @Override
		// public void treeWillExpand(TreeExpansionEvent event) throws
		// ExpandVetoException {
		// L.info("treeWillExpand");
		// }
		//
		// @Override
		// public void treeWillCollapse(TreeExpansionEvent event) throws
		// ExpandVetoException {
		// L.info("treeWillCollapse");
		// }
		// });
	}

}
