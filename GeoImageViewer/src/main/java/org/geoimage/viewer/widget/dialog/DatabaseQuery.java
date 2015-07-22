/*
 * DatabaseQuery.java
 *
 * Created on June 16, 2008, 4:55 PM
 */
package org.geoimage.viewer.widget.dialog;

import java.sql.Connection;
import java.sql.ResultSet;

import org.geoimage.viewer.core.api.Attributes;
import org.geoimage.viewer.core.layers.GeometricLayer;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;

/**
 *
 * @author  thoorfr
 */
public class DatabaseQuery extends javax.swing.JPanel {
	private static org.slf4j.Logger logger=LoggerFactory.getLogger(DatabaseQuery.class);

    private Connection connection;
    private ResultSet rs = null;

    /** Creates new form DatabaseQuery */
    public DatabaseQuery() {
        initComponents();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public GeometricLayer getLayer() throws Exception {
        if (rs == null) {
            return null;
        } else {
            rs.first();
            int geoCol = -1;
            String[] schema = new String[rs.getMetaData().getColumnCount()];
            String[] types = new String[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                schema[i] = rs.getMetaData().getColumnName(i + 1);
                types[i] = rs.getMetaData().getColumnClassName(i + 1);
                System.out.println(types[i]);
                if (schema[i].toLowerCase().endsWith("geom")) {
                    geoCol = i;
                }
            }
            if (geoCol == -1) {
                return null;
            }
            WKTReader wkt = new WKTReader();
            WKBReader wkb = new WKBReader();
            Geometry geom = null;
            try {
                geom = wkt.read(rs.getString(geoCol + 1));
                wkb = null;
            } catch (ParseException e) {
                try {
                    geom = wkb.read(WKBReader.hexToBytes(rs.getString(geoCol + 1)));
                    wkt = null;
                } catch (Exception e2) {
                    return null;
                }
            }

            GeometricLayer glayer = null;
            if (geom instanceof Point) {
                glayer = new GeometricLayer(GeometricLayer.POINT);
            } else if (geom instanceof Polygon) {
                glayer = new GeometricLayer(GeometricLayer.POLYGON);
            } else if (geom instanceof LineString) {
                glayer = new GeometricLayer(GeometricLayer.LINESTRING);
            }
            glayer.setName(jTextArea1.getText());
            Attributes att = Attributes.createAttributes(schema, types);
            for (String key : schema) {
                att.set(key, rs.getObject(key));
            }
            glayer.put(geom, att);
            while (rs.next()) {
                try {
                    if (wkt != null) {
                        geom = wkt.read(rs.getString(geoCol + 1));
                    } else if (wkb != null) {
                        geom = wkb.read(WKBReader.hexToBytes(rs.getString(geoCol + 1)));
                    }
                } catch (Exception e) {
                    continue;
                }
                att = Attributes.createAttributes(schema, types);
                for (String key : schema) {
                    att.set(key, rs.getObject(key));
                }
                glayer.put(geom, att);
            }
            return glayer;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.geoimage.viewer.core.GeoImageViewer.class).getContext().getResourceMap(DatabaseQuery.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setToolTipText(resourceMap.getString("jTextArea1.toolTipText")); // NOI18N
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.geoimage.viewer.core.GeoImageViewer.class).getContext().getActionMap(DatabaseQuery.class, this);
        jButton1.setAction(actionMap.get("runQuery")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public Task runQuery() {
        return new RunQueryTask(org.jdesktop.application.Application.getInstance(org.geoimage.viewer.core.GeoImageViewer.class));
    }

    private class RunQueryTask extends org.jdesktop.application.Task<ResultSet, Void> {
        private String sql;
        RunQueryTask(org.jdesktop.application.Application app) {
            super(app);
                sql = jTextArea1.getText();
        }
        @Override protected ResultSet doInBackground() {
            try {
                rs= connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
                return rs;
            } catch (Exception ex) {
            	logger.error(ex.getMessage(),ex);
                return null;
            }
        }
        @Override protected void succeeded(ResultSet result) {
            DatabaseModel model=new DatabaseModel(result);
            jTable1.setModel(model);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}