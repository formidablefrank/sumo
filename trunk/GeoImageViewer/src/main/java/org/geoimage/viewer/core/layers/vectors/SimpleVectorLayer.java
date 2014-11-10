/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geoimage.viewer.core.layers.vectors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.geoimage.analysis.VDSSchema;
import org.geoimage.common.OptionMenu;
import org.geoimage.utils.IMask;
import org.geoimage.viewer.core.GeoImageViewerView;
import org.geoimage.viewer.core.PickedData;
import org.geoimage.viewer.core.api.Attributes;
import org.geoimage.viewer.core.api.GeoContext;
import org.geoimage.viewer.core.api.GeometricLayer;
import org.geoimage.viewer.core.api.IClickable;
import org.geoimage.viewer.core.api.IImageLayer;
import org.geoimage.viewer.core.api.ILayer;
import org.geoimage.viewer.core.api.ILayerManager;
import org.geoimage.viewer.core.api.ISave;
import org.geoimage.viewer.core.api.IThreshable;
import org.geoimage.viewer.core.api.IVectorLayer;
import org.geoimage.viewer.core.io.AbstractVectorIO;
import org.geoimage.viewer.core.io.GenericCSVIO;
import org.geoimage.viewer.core.io.SimpleShapefileIO;
import org.geoimage.viewer.core.io.factory.VectorIOFactory;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

/**
 *
 * @author thoorfr
 */
public class SimpleVectorLayer implements ILayer, IVectorLayer, ISave, IMask, IClickable, IThreshable {

    public final static String POINT = GeometricLayer.POINT;
    public final static String POLYGON = GeometricLayer.POLYGON;
    public final static String LINESTRING = GeometricLayer.LINESTRING;
    public final static String MIXED = GeometricLayer.MIXED;
    protected boolean active = true;
    protected IImageLayer parent;
    protected GeometricLayer glayer;
   	protected String type;
    protected String name;
    protected float renderWidth = 1;
    protected Color color = new Color(1f, 1f, 1f);
    protected Geometry selectedGeometry;
    private symbol displaysymbol = symbol.point;
    private boolean threshable = false;
    private double minThresh = 0;
    private double maxThresh = 0;
    private double currentThresh = 0;
    
    private static org.slf4j.Logger logger=LoggerFactory.getLogger(GeoImageViewerView.class);
    
    //Pietro: for testing layer intersaction / union
    //public static ArrayList <Geometry>intersections=null;
    //public static ArrayList <Geometry>results=null;
    
    public double getMaximumThresh() {
        return maxThresh;
    }

    public double getMinimumThresh() {
        return minThresh;
    }

    public void setThresh(double thresh) {
        currentThresh = thresh;
    }

    public boolean isThreshable() {
        return threshable;
    }

    private void calculateMaxMinTresh() {
        minThresh = Double.MAX_VALUE;
        maxThresh = Double.MIN_VALUE;
        for (Attributes att : glayer.getAttributes()) {
            double temp = new Double("" + att.get(VDSSchema.SIGNIFICANCE));
            if (temp < minThresh) {
                minThresh = temp;
            }
            if (temp > maxThresh) {
                maxThresh = temp;
            }
        }
        currentThresh = minThresh - 0.01;
    }

    public int[] getHistogram(int numClasses) {
        if (threshable) {
            int[] out = new int[numClasses];
            for (Attributes att : glayer.getAttributes()) {
                double temp = new Double("" + att.get(VDSSchema.SIGNIFICANCE));
                int classe = (int) ((numClasses - 1) * (temp - minThresh) / (maxThresh - minThresh));
                out[classe]++;
            }
            return out;
        }
        return null;
    }

    public double getThresh() {
        return currentThresh;
    }

    protected GeometricLayer createThresholdedLayer(GeometricLayer layer) {
        GeometricLayer out = layer.clone();
        if (!threshable) {
            return out;
        }
        List<Geometry> remove = new ArrayList<Geometry>();
        for (Geometry geom : Collections.unmodifiableList(out.getGeometries())) {
            if (new Double("" + out.getAttributes(geom).get(VDSSchema.SIGNIFICANCE)) < currentThresh) {
                remove.add(geom);
            }
        }
        for (Geometry geom : remove) {
            out.remove(geom);
        }
        return out;

    }

    public static enum symbol {point, circle, square, triangle, cross};

    public SimpleVectorLayer(String layername, IImageLayer parent, String type, GeometricLayer layer) {
        this.name = layername;
        this.parent = parent;
        this.type = type;
        if (layer == null) {
            return;
        }
        this.glayer = layer;
        String test = glayer.getSchema('/');
        if (test.contains(VDSSchema.SIGNIFICANCE)) {
            calculateMaxMinTresh();
            threshable = true;
        }
      /*  if(intersections==null)
        	intersections=new ArrayList<Geometry>();
        if(results==null)
        	results=new ArrayList<Geometry>(); */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void render(GeoContext context) {
        if (!context.isDirty()) {
            return;
        }
        int x = context.getX(), y = context.getY();
        float zoom = context.getZoom(), width = context.getWidth() * zoom, height = context.getHeight() * zoom;
        GL2 gl = context.getGL().getGL2();
        float[] c = color.getColorComponents(null);
        gl.glColor3f(c[0], c[1], c[2]);

        if (glayer != null) {
            if (!threshable) {
                if (getType().equalsIgnoreCase(POINT)) {
                    switch (this.displaysymbol) {
                        case point: {
                            gl.glPointSize(this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            for (Geometry temp : glayer.getGeometries()) {
                                Coordinate point = temp.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                            if (selectedGeometry != null) {
                                gl.glPointSize(this.renderWidth * 2);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = selectedGeometry.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case circle: {
                        }
                        break;
                        case square: {
                        	//usato anche per disegnare i contorni delle detection
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 3 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case cross: {
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y);
                                gl.glVertex2d(point.x + rectwidth, point.y);
                                gl.glEnd();
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x, point.y - rectwidth);
                                gl.glVertex2d(point.x, point.y + rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case triangle: {
                            for (Geometry temp : glayer.getGeometries()) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                Coordinate point = new Coordinate(temp.getCoordinate());
                                point.x = (point.x - x) / width;
                                point.y = 1 - (point.y - y) / height;
                                double rectwidth = 0.01;
                                gl.glBegin(GL.GL_LINE_STRIP);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x, point.y + rectwidth);
                                gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                gl.glEnd();
                                gl.glFlush();
                            }

                        }
                        break;
                        default: {
                        }
                    }
                } else if (getType().equalsIgnoreCase(POLYGON)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        float rWidth=temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth;
                        
                        gl.glLineWidth(rWidth);
                        gl.glBegin(GL.GL_LINE_STRIP);
                        Coordinate[] cs = temp.getCoordinates();
                        for (int p = 0; p < cs.length; p++) {
                        	double vx=(cs[p].x - x) / width;
                        	double vy=1 - (cs[p].y - y) / height;
                            gl.glVertex2d(vx,vy);
                        }
                        //close polygon
                        Coordinate point = cs[0];
                        gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                       
                        gl.glEnd();
                        gl.glFlush();
                    }
                } else if (getType().equalsIgnoreCase(LINESTRING)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        
                        gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                        gl.glBegin(GL.GL_LINE_STRIP);
                        Coordinate[] cs = temp.getCoordinates();
                        for (int p = 0; p < cs.length; p++) {
                            gl.glVertex2d((cs[p].x - x) / width, 1 - (cs[p].y - y) / height);
                        }
                        gl.glEnd();
                        gl.glFlush();
                    }
                } else if (getType().equalsIgnoreCase(MIXED)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (temp.getCoordinates().length < 1) {
                            continue;
                        }
                        if (temp instanceof LineString) {
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        } else if (temp instanceof Polygon) {
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        } else if (temp instanceof Point) {
                            gl.glPointSize(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            Coordinate point = temp.getCoordinate();
                            gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                }
            } else {
                if (getType().equalsIgnoreCase(POINT)) {
                    switch (this.displaysymbol) {
                        case point: {
                            gl.glPointSize(this.renderWidth);
                            gl.glBegin(GL.GL_POINTS);
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                                    Coordinate point = temp.getCoordinate();
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                            }
                            gl.glEnd();
                            gl.glFlush();
                            if (selectedGeometry != null) {
                                gl.glPointSize(this.renderWidth * 2);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = selectedGeometry.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                        break;
                        case circle: {
                        }
                        break;
                        case square: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 3 : this.renderWidth);
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }
                        }
                        break;
                        case cross: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                    
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y);
                                    gl.glVertex2d(point.x + rectwidth, point.y);
                                    gl.glEnd();
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x, point.y - rectwidth);
                                    gl.glVertex2d(point.x, point.y + rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }
                        }
                        break;
                        case triangle: {
                            for (Geometry temp : glayer.getGeometries()) {
                                if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                                    gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                    Coordinate point = new Coordinate(temp.getCoordinate());
                                    point.x = (point.x - x) / width;
                                    point.y = 1 - (point.y - y) / height;
                                    double rectwidth = 0.01;
                                    gl.glBegin(GL.GL_LINE_STRIP);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x, point.y + rectwidth);
                                    gl.glVertex2d(point.x + rectwidth, point.y - rectwidth);
                                    gl.glVertex2d(point.x - rectwidth, point.y - rectwidth);
                                    gl.glEnd();
                                    gl.glFlush();
                                }
                            }

                        }
                        break;
                        default: {
                        }
                    }
                } else if (getType().equalsIgnoreCase(POLYGON)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            Coordinate point = temp.getCoordinates()[0];
                            gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                } else if (getType().equalsIgnoreCase(LINESTRING)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                            gl.glBegin(GL.GL_LINE_STRIP);
                            for (Coordinate point : temp.getCoordinates()) {
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                            }
                            gl.glEnd();
                            gl.glFlush();
                        }
                    }
                } else if (getType().equalsIgnoreCase(MIXED)) {
                    for (Geometry temp : glayer.getGeometries()) {
                        if (((Double) glayer.getAttributes(temp).get(VDSSchema.SIGNIFICANCE)) > currentThresh) {
                            if (temp.getCoordinates().length < 1) {
                                continue;
                            }
                            if (temp instanceof LineString) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_LINE_STRIP);
                                for (Coordinate point : temp.getCoordinates()) {
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                                gl.glEnd();
                                gl.glFlush();
                            } else if (temp instanceof Polygon) {
                                gl.glLineWidth(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_LINE_STRIP);
                                for (Coordinate point : temp.getCoordinates()) {
                                    gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                }
                                gl.glEnd();
                                gl.glFlush();
                            } else if (temp instanceof Point) {
                                gl.glPointSize(temp == selectedGeometry ? this.renderWidth * 2 : this.renderWidth);
                                gl.glBegin(GL.GL_POINTS);
                                Coordinate point = temp.getCoordinate();
                                gl.glVertex2d((point.x - x) / width, 1 - (point.y - y) / height);
                                gl.glEnd();
                                gl.glFlush();
                            }
                        }
                    }
                }
            }
        }

    }

    public Geometry getSelectedGeometry() {
		return selectedGeometry;
	}

	public void setSelectedGeometry(Geometry selectedGeometry) {
		this.selectedGeometry = selectedGeometry;
	}

	public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRadio() {
        return false;
    }

    public ILayerManager getParent() {
        return parent;
    }

    public String getDescription() {
        return getName();
    }

    public void dispose() {
        glayer.clear();
        glayer = null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getWidth() {
        return this.renderWidth;
    }

    public void setWidth(float width) {
        this.renderWidth = width;
    }

    public void save(String file, int formattype, String projection) {
        if (formattype==ISave.OPT_EXPORT_CSV) {
            if (!file.endsWith(".csv")) {
                file = file.concat(".csv");
            }
            Map config = new HashMap();
            config.put(GenericCSVIO.CONFIG_FILE, file);
            AbstractVectorIO csv = VectorIOFactory.createVectorIO(VectorIOFactory.GENERIC_CSV, config, ((IImageLayer) this.getParent()).getImageReader());//AndreaG changed csv(sumo) with genericcsv
            csv.save(createThresholdedLayer(glayer), projection);
        } else if (formattype==ISave.OPT_EXPORT_SHP) {
            try {
                Map config = new HashMap();
                config.put(SimpleShapefileIO.CONFIG_URL, new File(file).toURI().toURL());
                AbstractVectorIO shp = VectorIOFactory.createVectorIO(VectorIOFactory.SIMPLE_SHAPEFILE, config, ((IImageLayer) this.getParent()).getImageReader());
                shp.save(createThresholdedLayer(glayer), projection);
            } catch (MalformedURLException ex) {
                Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public OptionMenu[] getFileFormatTypes() {
    	OptionMenu[] opts=new OptionMenu[2];
    	opts[0]=new OptionMenu(ISave.OPT_EXPORT_CSV,ISave.STR_EXPORT_CSV); 
    	opts[1]=new OptionMenu(ISave.OPT_EXPORT_SHP,ISave.STR_EXPORT_SHP);
        return opts; 
    }

    public boolean intersects(int x, int y, int width, int height) {
        try {
            if (getType().equals("point")) {
                return false;
            }
            WKTReader wkt = new WKTReader();
            Geometry geom = wkt.read("POLYGON((" + x + " " + y + "," + (x + width) + " " + y + "," + (x + width) + " " + (y + height) + "," + x + " " + (y + height) + "," + x + " " + y + "))");
            for (Geometry p : glayer.getGeometries()) {
                if (p.intersects(geom)) {
                    return true;
                }
            }
            return false;
        } catch (ParseException ex) {
            Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void mouseClicked(java.awt.Point imagePosition, int button, GeoContext context) {
        this.selectedGeometry = null;
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(imagePosition.x, imagePosition.y));
        for (Geometry temp : glayer.getGeometries()) {
            //if (temp.isWithinDistance(p, 5 * context.getZoom())) {
            if (p.equalsExact(temp, 5 * context.getZoom())) {
                this.selectedGeometry = temp;
                PickedData.put(temp, glayer.getAttributes(temp));
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeometricLayer getGeometriclayer() {
        return glayer;
    }

    public void setGeometriclayer(GeometricLayer glayer) {
        this.glayer = glayer;
    }

    public boolean contains(int x, int y) {
        if (getType().equals(POINT)) {
            return false;
        }
        GeometryFactory gf = new GeometryFactory();
        Point geom = gf.createPoint(new Coordinate(x, y));
        for (Geometry p : glayer.getGeometries()) {
            if (p.contains(geom)) {
                return true;
            }
        }
        return false;
    }

    public boolean includes(int x, int y, int width, int height) {
        try {
            if (getType().equals("point")) {
                return false;
            }
            WKTReader wkt = new WKTReader();
            Geometry geom = wkt.read("POLYGON((" + x + " " + y + "," + (x + width) + " " + y + "," + (x + width) + " " + (y + height) + "," + x + " " + (y + height) + "," + x + " " + y + "))");
            for (Geometry p : glayer.getGeometries()) {
                if (geom.within(p)) {
                    return true;
                }
            }
            return false;
        } catch (ParseException ex) {
            Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // rasterize the mask clipped with the Rectangle scaled back to full size with an offset onto a BufferedImage
    public BufferedImage rasterize(Rectangle rect, int offsetX, int offsetY, double scalingFactor) {
        // create the buffered image of the size of the Rectangle
        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_BYTE_BINARY);
        GeometryFactory gf = new GeometryFactory();
        // define the clipping region in full scale
        Coordinate[] coords = new Coordinate[]{
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMaxX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMaxX() / scalingFactor)), (int) (((double) rect.getMaxY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMaxY() / scalingFactor))),
            new Coordinate((int) (((double) rect.getMinX() / scalingFactor)), (int) (((double) rect.getMinY() / scalingFactor))),};
        Polygon geom = gf.createPolygon(gf.createLinearRing(coords), null);
        Graphics g2d = image.getGraphics();
        g2d.setColor(Color.WHITE);
        for (Geometry p : glayer.getGeometries()) {
            if (p.intersects(geom)) {
                int[] xPoints = new int[p.getNumPoints()];
                int[] yPoints = new int[p.getNumPoints()];
                int i = 0;
                for (Coordinate c : p.getCoordinates()) {
                    xPoints[i] = (int) ((c.x + offsetX) * scalingFactor);
                    yPoints[i++] = (int) ((c.y + offsetY) * scalingFactor);
                }
                g2d.fillPolygon(xPoints, yPoints, p.getNumPoints());
            }
        }
        g2d.dispose();
        return image;
    }

    public Area getShape() {
        Area maskArea = new Area();
        Rectangle rect = new Rectangle(0, 0, parent.getImageReader().getWidth(), parent.getImageReader().getHeight());
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[]{
            new Coordinate((int) rect.getMinX(), (int) rect.getMinY()),
            new Coordinate((int) rect.getMaxX(), (int) rect.getMinY()),
            new Coordinate((int) rect.getMaxX(), (int) rect.getMaxY()),
            new Coordinate((int) rect.getMinX(), (int) rect.getMaxY()),
            new Coordinate((int) rect.getMinX(), (int) rect.getMinY()),};
        Polygon geom = gf.createPolygon(gf.createLinearRing(coords), null);
        for (Geometry p : glayer.getGeometries()) {
            if (p.intersects(geom)) {
                int[] xPoints = new int[p.getNumPoints()];
                int[] yPoints = new int[p.getNumPoints()];
                int i = 0;
                for (Coordinate c : p.getCoordinates()) {
                    xPoints[i] = (int) (c.x);
                    yPoints[i++] = (int) (c.y);
                }
                maskArea.add(new Area(new java.awt.Polygon(xPoints, yPoints, p.getNumPoints())));
            }
        }
        return maskArea;
    }

    public symbol getDisplaysymbol() {
        return displaysymbol;
    }

    public void setDisplaysymbol(symbol displaysymbol) {
        this.displaysymbol = displaysymbol;
    }

    
    /**
     * create the new buffered layer
     */
    public void buffer(double bufferingDistance) {
        PrecisionModel pm=new PrecisionModel(1);
        GeometryFactory gf = new GeometryFactory(pm);
   
        Geometry[] bufferedGeom=glayer.getGeometries().toArray(new Geometry[0]);
        
        for (int i=0;i<bufferedGeom.length;i++) {
        	//applico il buffer alla geometria
        	bufferedGeom[i] = bufferedGeom[i].buffer(bufferingDistance, 6, BufferParameters.CAP_ROUND);
        	
        	Geometry buff=bufferedGeom[i];
            if (buff instanceof Polygon && ((Polygon) buff).getNumInteriorRing() > 0) {
            	//bufferedGeom[i] = gf.createPolygon((LinearRing) ((Polygon) bufferedGeom[i]).getExteriorRing(), null);
            	
            	LineString p=((Polygon) buff).getExteriorRing();
            	bufferedGeom[i] = gf.createPolygon(p.getCoordinates());
            }
        }
        // then merge them
        List<Geometry> newgeoms = new ArrayList<Geometry>();
        List<Geometry> remove = new ArrayList<Geometry>();
        
        Geometry[] parse =bufferedGeom;
  
       
        //ciclo sulle nuove geometrie
        for (Geometry g : parse) {
            boolean isnew = true;
            remove.clear();
            for (Geometry newg : newgeoms) {
                if (newg.contains(g)) { //se newg contiene g -> g deve essere rimossa
                    isnew = false;
                    break;
                } else if (g.contains(newg)) { //se g contiene newg -> newg deve essere rimossa
                    remove.add(newg);
                }
            }
            if (isnew) {
                newgeoms.add(g);
            }
            newgeoms.removeAll(remove);
        }
        glayer.clear();

        newgeoms=mergePolygons2(newgeoms,bufferingDistance);

        // assign new value
        for (Geometry geom :newgeoms) {
            glayer.put(geom);
        }
    }
    
    /**
     * 
     * @param polygons
     * @return
     */
    private List<Geometry> mergePolygons2(List<Geometry> polygons,double buffer) {
    	List <List<Geometry>>intersectedGeom =new ArrayList<List<Geometry>>();
    	List <Geometry>alreadySelected =new ArrayList<Geometry>();
    	for (int i = 0; i < polygons.size(); i++) {
    		
    		Geometry a = polygons.get(i);
    		List <Geometry> l=new ArrayList<Geometry>();
    		l.add(a);
    		
    		alreadySelected.add(a);
    		
    		for (int j = i + 1; j < polygons.size();j++) {
    			final Geometry b = polygons.get(j);
    	        try{
			        if (a.intersects(b)) {
			        	l.add(b);
			        	alreadySelected.add(b);
			            //polygons.set(i, (Polygon) a.union(b));
			            //a = polygons.get(i);
			            //polygons.remove(j);
			            //done = false;
			        }
    			}catch(Exception e){
    				logger.warn(e.getMessage());
    			}
    	    }
    		if(l.size()>1||!alreadySelected.contains(l.get(0)))
    			intersectedGeom.add(l);
    	}
    	GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
    	for(List<Geometry> ll:intersectedGeom){
    		if(ll.size()>1){
    			try{
    				
    				//10 e' un valore determinato solo da diversi test ...
    				if(buffer>10){
						for(int i=0;i<ll.size();i++){
							Geometry g=ll.get(i);
							ll.set(i,g.buffer(-buffer/2));
						}
    				}
    				/*
    				Geometry u=ll.get(0);
    				for(int i=0;i<ll.size();i++){
    					Geometry g=ll.get(i);
						if(!g.equals(u)){
							u=u.union(g).buffer(0);
						}
					}*/
    				
    				CascadedPolygonUnion cascadeU=new CascadedPolygonUnion(ll);
    				Geometry union=cascadeU.union();
    				
    	            if (union instanceof Polygon && ((Polygon) union).getNumInteriorRing() > 0) {
    	            	LineString p=((Polygon) union).getExteriorRing();
    	            	union = factory.createPolygon(p.getCoordinates());
    	            }
    				polygons.add(union);
    				
	    		}catch(Exception e){
	    			logger.warn(e.getMessage());
	    			
	    			for(Geometry g:ll){
	    				polygons.add(g);
	    			}
				}
    		}else{
   				polygons.add(ll.get(0));
    		}
    	}
    	
    	
    	return polygons;
    }
    
    private List<Geometry> mergePolygons(List<Geometry> polygons) {
    	boolean done;
    	do {
    	done = true;
    	for (int i = 0; i < polygons.size(); i++) {
    		Geometry a = polygons.get(i);
    	    //if (0 != a.getNumInteriorRing()) {
    	    //    throw new RuntimeException();
    	    //}
    	    for (int j = i + 1; j < polygons.size();) {
    	        final Geometry b = polygons.get(j);
    	        if (a.intersects(b)) {
    	            polygons.set(i, (Polygon) a.buffer(0).union(b));
    	            a = polygons.get(i);
    	            polygons.remove(j);
    	            done = false;
    	        }
    	        else {
    	            j++;
    	        }
    	    }
    	}
    	} while (!done);
    	
    	return polygons;
    }
    
    
    public IMask createBufferedMask(double bufferingDistance) {
        IMask mask = null;
        try {
            mask = (IMask) (new SimpleVectorLayer(getName(), (IImageLayer) getParent(), getType(), glayer.clone()));
            mask.buffer(bufferingDistance);
        } catch (Exception ex) {
            Logger.getLogger(SimpleVectorLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mask;
    }

    public List<Geometry> getGeometries() {
        return glayer.getGeometries();
    }
}
