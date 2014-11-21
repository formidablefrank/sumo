/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geoimage.viewer.core.layers.vectors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.geoimage.def.GeoImageReader;
import org.geoimage.def.GeoTransform;
import org.geoimage.viewer.core.Platform;
import org.geoimage.viewer.core.api.GeoContext;
import org.geoimage.viewer.core.api.GeometricLayer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author leforth
 */
public class ComplexEditVectorLayer extends SimpleEditVectorLayer {
	
	
	
    // Acts as a simple edit vector but allows additional display features
    // additional display features are stored in separate GeometricLayers
    // the class also allows more interactions such as multiple selection
    class additionalgeometries {
        private String tag;
        private Color color;
        private int lineWidth = 1;
        private String type = POINT;
        private List<Geometry> geometries;
        private boolean status;
       
        
        public additionalgeometries(String tag, Color color, int lineWidth, String type, List<Geometry> geometries, boolean status)
        {
            this.color = color;
            this.lineWidth = lineWidth;
            this.type = type;
            this.geometries = geometries;
            this.tag = tag;
            this.status = status;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getTag()
        {
            return this.tag;
        }

        public Color getColor()
        {
            return this.color;
        }

        public int getLinewidth()
        {
            return this.lineWidth;
        }

        public List<Geometry> getGeometries()
        {
            return this.geometries;
        }

        public String getType()
        {
            return this.type;
        }

    };

    private List<additionalgeometries> additionalGeometries = new ArrayList<additionalgeometries>();

    public ComplexEditVectorLayer(String layername,  GeoImageReader reader , String type, GeometricLayer layer) {
        super(layername, reader, type, layer);
    }

    @Override
    public void render(GeoContext context) {
        super.render(context);
        if (!context.isDirty() || glayer == null) {
            return;
        }

        int x = context.getX(), y = context.getY();
        float zoom = context.getZoom(), width = context.getWidth() * zoom, height = context.getHeight() * zoom;
        GL2 gl = context.getGL().getGL2();
        for(additionalgeometries geometry : additionalGeometries)
        {
            // check geometries need to be displayed
            if(geometry.isStatus())
            {
                float[] c = geometry.getColor().getColorComponents(null);
                gl.glColor3f(c[0], c[1], c[2]);
                if (geometry.getType().equalsIgnoreCase(POINT)) {
                    gl.glPointSize(geometry.getLinewidth());
                    gl.glBegin(GL.GL_POINTS);
                    for (Geometry temp : geometry.getGeometries()) {
                        Coordinate point = temp.getCoordinate();
                        gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);

                    }
                    gl.glEnd();
                    gl.glFlush();
                } else if (geometry.getType().equalsIgnoreCase(POLYGON)) {
                    for (Geometry temp : geometry.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                  //      System.out.println("--------------------------------------------------");
                        gl.glLineWidth(geometry.getLinewidth());
                        gl.glBegin(GL.GL_LINE_STRIP);
                       
                        for (Coordinate point : temp.getCoordinates()) {
                        	double xv=(point.x - x) / width;
                        	double yv= 1 - (point.y - y) / height;
                            gl.glVertex2d(xv,yv);
                            
                            if(point.x>14255 && point.x<14256){
                        //    	System.out.println("XV:"+xv+" - YV:"+yv+"   POINT:"+point.x+","+point.y + "  x:"+x+"  y:"+y);
                            }
               //             if(point.x>14000 && point.y>19000)
              //              	System.out.println("XV:"+xv+" - YV:"+yv+"   POINT:"+point.x+","+point.y + "  x:"+x+"  y:"+y);
                        }
                //        System.out.println("--------------------------------------------------");
                        Coordinate point = temp.getCoordinates()[0];
                        gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                        gl.glEnd();
                        gl.glFlush();
                    }
                } else if (geometry.getType().equalsIgnoreCase(LINESTRING)) {
                    for (Geometry temp : geometry.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        gl.glLineWidth(geometry.getLinewidth());
                        gl.glBegin(GL.GL_LINE_STRIP);
                        for (Coordinate point : temp.getCoordinates()) {
                            gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                        }
                        gl.glEnd();
                        gl.glFlush();
                    }
                }
            }
        }
        
        //Pietro added for testing
        /*if(intersections!=null){
        	for (Geometry temp : intersections) {
                if (temp.getCoordinates().length < 1) {
                    continue;
                }
                gl.glLineWidth(30);
                gl.glColor3f(100,100,100);
                gl.glBegin(GL.GL_LINE_STRIP);
                for (Coordinate point : temp.getCoordinates()) {
                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                }
                gl.glEnd();
                gl.glFlush();
            }
        }*/
        //Pietro added for testing
    /*    if(results!=null){
        	for (Geometry temp : results) {
                if (temp.getCoordinates().length < 1) {
                    continue;
                }
                gl.glLineWidth(20);
                //viola
                gl.glColor3f(100,0,100);
                gl.glBegin(GL.GL_LINE_STRIP);
                for (Coordinate point : temp.getCoordinates()) {
                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                }
                gl.glEnd();
                gl.glFlush();
            }
        }*/
    }

    public void addGeometries(String geometrytag, Color color, int lineWidth, String type, List<Geometry> geometries, boolean status)
    {
        additionalGeometries.add(new additionalgeometries(geometrytag, color, lineWidth, type, geometries, status));
    }

    private int getGeometriesByTag(String geometrytag)
    {
        for(additionalgeometries geometries : additionalGeometries)
        {
            if(geometries.getTag().equals(geometrytag))
                return additionalGeometries.indexOf(geometries);
        }

        return -1;
    }

    public boolean removeGeometriesByTag(String geometrytag)
    {
        int index = getGeometriesByTag(geometrytag);
        if(index != -1)
        {
            this.additionalGeometries.remove(index);
            return true;
        }

        return false;
    }

    public boolean tagExists(String tag)
    {
        return getGeometriesByTag(tag) == -1 ? false : true;
    }

    public List<String> getGeometriestagList()
    {
        ArrayList<String> geometriestaglist = new ArrayList<String>();

        for(additionalgeometries geometries : additionalGeometries)
        {
            geometriestaglist.add(geometries.getTag());
        }

        return geometriestaglist;
    }

    public boolean getGeometriesDisplay(String geometrytag) {
        if(tagExists(geometrytag))
            return additionalGeometries.get(getGeometriesByTag(geometrytag)).isStatus();

        return false;
    }

    public void toggleGeometriesByTag(String geometrytag, boolean status) {
        if(tagExists(geometrytag))
            additionalGeometries.get(getGeometriesByTag(geometrytag)).setStatus(status);
        Platform.getGeoContext().setDirty(true);
    }

};
