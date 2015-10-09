package application.controller.util;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import application.App;
import application.model.Source_;
import application.util.PathTool;
import application.controller.util.BrowserItem;

/**
 * 
 * Recursive File system object TreeItem. 
 * Implements dynamic loading of file system tree 
 * 
 */
public class BrowserTreeItem extends TreeItem<BrowserItem> {
	
    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;

    public BrowserTreeItem(final BrowserItem node){
    	super(node, null);
    }        
  
    @Override public ObservableList<TreeItem<BrowserItem>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    @Override public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            
            File f = (File) getValue().getFile();
            isLeaf = f.isFile();
        }
        return isLeaf;
    }

    private ObservableList<BrowserTreeItem> buildChildren(BrowserTreeItem treeItem) {
        File f = ((BrowserItem)treeItem.getValue()).getFile();
        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                ObservableList<BrowserTreeItem> children = FXCollections.observableArrayList();

                for (File childFile : files) {
                	BrowserItem browserItem = new BrowserItem(childFile, null);
                	browserItem.setSrcObj(App.data().FindSourceByFullRelativePath(PathTool.GetRelativePath(childFile.getPath(), App.data().getBaseFolder()), childFile.isDirectory()));
                    children.add(new BrowserTreeItem (browserItem));
                }

                return children;
            }
        }
        return FXCollections.emptyObservableList();
    }
    
    public BrowserItem findTreeItem(Source_ src) {
    	
    	BrowserItem foundItem = null;
    	
    	if( ((BrowserItem)getValue()).getSrcObj() == src) {
    		foundItem = (BrowserItem)getValue();
    	}
    	else {
	    	for (TreeItem<BrowserItem> child : super.getChildren()) {
	    		BrowserTreeItem childB = (BrowserTreeItem)child;
	    		foundItem = childB.findTreeItem(src);

	    		if(foundItem != null)
	    			break;
	    	}
    	}
    	
    	return foundItem;
    }

} 
