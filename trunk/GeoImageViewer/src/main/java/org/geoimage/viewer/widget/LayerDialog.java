/*
 * LayerDialog.java
 *
 * Created on March 31, 2008, 12:44 PM
 */

package org.geoimage.viewer.widget;
import org.geoimage.viewer.core.api.IComplexVDSVectorLayer;
import org.geoimage.viewer.core.api.IComplexVectorLayer;
import org.geoimage.viewer.core.api.IEditable;
import org.geoimage.viewer.core.api.IImageLayer;
import org.geoimage.viewer.core.api.ILayer;
import org.geoimage.viewer.core.api.ISave;
import org.geoimage.viewer.core.api.ISelect;
import org.geoimage.viewer.core.api.IVectorLayer;
import org.geoimage.viewer.core.layers.vectors.SimpleVectorLayer;

/**
 *
 * @author  thoorfr
 */
public class LayerDialog extends javax.swing.JDialog {
    
    /** Creates new form LayerDialog */
    public LayerDialog(java.awt.Frame parent, boolean modal, ILayer layer) {
        super(parent, modal);
        this.setAlwaysOnTop(false);
        initComponents();
        jTabbedPane1.add("Description",new LayerPanel(layer));
        if(layer instanceof IImageLayer) jTabbedPane1.add("Image parameters",new ImagePanel((IImageLayer)layer));
        if(layer instanceof IVectorLayer) jTabbedPane1.add("Style",new VectorPanel((IVectorLayer)layer));
        if(layer instanceof SimpleVectorLayer)
        {
            if(layer instanceof IComplexVectorLayer)
            {
                if(layer instanceof IComplexVDSVectorLayer)
                    jTabbedPane1.add("Data", new GeometricInteractiveVDSLayerPanel(layer));
            } else {
                jTabbedPane1.add("Data", new GeometricLayerPanel(((SimpleVectorLayer)layer).getGeometriclayer()));
            }
        }  
        if(layer instanceof IEditable) jTabbedPane1.add("Edit",new EditorPanel((IEditable)layer));
        //jTabbedPane1.add("Info",new InfoPanel());
        if(layer instanceof ISave) jTabbedPane1.add("Save",new SavePanel((ISave)layer));
        if(layer instanceof ISelect) jTabbedPane1.add("Select",new SelectPanel((ISelect)layer));
        pack();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();

        setTitle("Layer Description");
        getContentPane().setLayout(new java.awt.GridLayout(1, 1));
        getContentPane().add(jTabbedPane1);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Image Settings");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
    
}
