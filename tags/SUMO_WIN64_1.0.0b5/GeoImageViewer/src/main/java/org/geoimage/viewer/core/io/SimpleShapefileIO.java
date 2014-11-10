/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geoimage.viewer.core.io;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoimage.def.GeoTransform;
import org.geoimage.viewer.core.api.Attributes;
import org.geoimage.viewer.core.api.GeometricLayer;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 *
 * @author thoorfr
 */
public class SimpleShapefileIO extends AbstractVectorIO {

    public static String CONFIG_URL = "url";

    public SimpleShapefileIO() {
        super();
    }

    private static DataStore createDataStore(String filename, SimpleFeatureType ft, String projection) throws Exception {
        FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();

        File file = new File(filename);

        // Create a Map object used by our DataStore Factory
        // NOTE: file.toURI().toURL() is used because file.toURL() is deprecated
        Map<String, Serializable> map = Collections.singletonMap("url", (Serializable) (file.toURI().toURL()));

        DataStore myData = factory.createNewDataStore(map);

        // Create the Shapefile (empty at this point)
        System.out.println(ft.getGeometryDescriptor());
        myData.createSchema(ft);

        // Tell the DataStore what type of Coordinate Reference System (CRS) to use
        if (projection != null) {
            ((ShapefileDataStore)myData).forceSchemaCRS(CRS.decode(projection));
        }

        return myData;
    }

    public static FeatureCollection createFeatures(SimpleFeatureType ft, GeometricLayer glayer, String projection, GeoTransform gt) throws Exception {
    	 DefaultFeatureCollection collection = new DefaultFeatureCollection();        //GeometryFactory gf = new GeometryFactory();
         int id=0;
         for (Geometry geom : glayer.getGeometries()) {
             if (geom instanceof Point) {
                 Object[] data = new Object[ft.getDescriptors().size()];
                 System.arraycopy(glayer.getAttributes(geom).getValues(), 0, data, 1, data.length-1);
                 data[0] = geom;
                 SimpleFeature simplefeature = SimpleFeatureBuilder.build(ft, data, ""+id++);
                 collection.add(simplefeature);
             } else if (geom instanceof Polygon) {
                 Object[] data = new Object[glayer.getSchema().length + 1];
                 data[0] = geom;
                 System.arraycopy(glayer.getAttributes(geom).getValues(), 0, data, 1, data.length - 1);
                 SimpleFeature simplefeature = SimpleFeatureBuilder.build(ft, data, ""+id++);
                 collection.add(simplefeature);
                 data = null;
             } else if (geom instanceof LineString) {
                 Object[] data = new Object[glayer.getSchema().length + 1];
                 data[0] = geom;
                 System.arraycopy(glayer.getAttributes(geom).getValues(), 0, data, 1, data.length - 1);
                 SimpleFeature simplefeature = SimpleFeatureBuilder.build(ft, data, ""+id++);
                 collection.add(simplefeature);
             }
         }
        return collection;
    }

    public static SimpleFeatureType createFeatureType(String name, GeometricLayer glayer) {
        try {

            // Tell this shapefile what type of data it will store
            // Shapefile handle only : Point, MultiPoint, MultiLineString, MultiPolygon
            String sch = "";
            String[] schema = glayer.getSchema();
            String[] types = glayer.getSchemaTypes();
            for (int i = 0; i < schema.length; i++) {
                sch += "," + schema[i] + ":" + types[i];
            }
            sch = sch.substring(1);
            String geomType = "MultiPolygon";
            if (glayer.getGeometries().get(0) instanceof Point) {
                geomType = "Point";
            }
            SimpleFeatureType featureType = DataUtilities.createType(name, "geom:" + geomType + ":srid=" + glayer.getProjection().replace("EPSG:", "") + sch);

            //to create other fields you can use a string like :
            // "geom:MultiLineString,FieldName:java.lang.Integer"
            // field name can not be over 10 characters
            // use a ',' between each field
            // field types can be : java.lang.Integer, java.lang.Long,
            // java.lang.Double, java.lang.String or java.util.Date

            return featureType;


        } catch (SchemaException ex) {
            Logger.getLogger(SimpleShapefileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void writeToShapefile(DataStore data, FeatureCollection collection) {
        FeatureStore store = null;
        DefaultTransaction transaction = new DefaultTransaction();
        try {
            String featureName = data.getTypeNames()[0];
            // Tell it the name of the shapefile it should look for in our DataStore
            store = (FeatureStore) data.getFeatureSource(featureName);
            // Then set the transaction for that FeatureStore
            store.setTransaction(transaction);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            store.addFeatures(collection);
            transaction.commit();
            transaction.close();
        } catch (Exception ex) {
            try {
                transaction.rollback();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public GeometricLayer read() {
        try {
            GeometricLayer out = null;
            int margin = Integer.parseInt(java.util.ResourceBundle.getBundle("GeoImageViewer").getString("SimpleShapeFileIO.margin"));
            //margin=0;
            //create a DataStore object to connect to the physical source 
            DataStore dataStore = DataStoreFinder.getDataStore(config);
            //retrieve a FeatureSource to work with the feature data
            FeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            String geomName = featureSource.getSchema().getGeometryDescriptor().getLocalName();
            GeoTransform gt = gir.getGeoTransform();
            //FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
            double[] x0;
            double[] x1;
            double[] x2;
            double[] x3;
            double[] x01; //image center coords
            double[] x02; //image center coords
            double[] x03; //image center coords
            double[] x12; //image center coords
            double[] x21; //image center coords
            double[] x22; //image center coords
            double[] x23; //image center coords
            double[] x31; //image center coords

            x0 = gt.getGeoFromPixel(-margin, -margin, "EPSG:4326");

            x01 = gt.getGeoFromPixel(-margin, gir.getHeight()/3, "EPSG:4326");
            x02 = gt.getGeoFromPixel(-margin, gir.getHeight()/2, "EPSG:4326");
            x03 = gt.getGeoFromPixel(-margin, gir.getHeight()*2/3, "EPSG:4326");

            x1 = gt.getGeoFromPixel(-margin, margin + gir.getHeight(), "EPSG:4326");

            x12 = gt.getGeoFromPixel(margin + gir.getWidth()/2, margin +gir.getHeight(), "EPSG:4326");

            x2 = gt.getGeoFromPixel(margin + gir.getWidth(), margin + gir.getHeight(), "EPSG:4326");

            x21 = gt.getGeoFromPixel(margin + gir.getWidth(), gir.getHeight()*2/3, "EPSG:4326");
            x22 = gt.getGeoFromPixel(margin + gir.getWidth(), gir.getHeight()/2, "EPSG:4326");
            x23 = gt.getGeoFromPixel(margin + gir.getWidth(), gir.getHeight()/3, "EPSG:4326");

            x3 = gt.getGeoFromPixel(margin + gir.getWidth(), -margin, "EPSG:4326");

            x31 = gt.getGeoFromPixel(gir.getWidth()/2, -margin, "EPSG:4326");

            double minx = Math.min(x0[0], Math.min(x01[0], Math.min(x02[0], Math.min(x03[0], Math.min(x1[0], Math.min(x12[0], Math.min(x2[0], Math.min(x21[0], Math.min(x22[0], Math.min(x23[0], Math.min(x3[0], x31[0])))))))))));
            double maxx = Math.max(x0[0], Math.max(x01[0], Math.max(x02[0], Math.max(x03[0], Math.max(x1[0], Math.max(x12[0], Math.max(x2[0], Math.max(x21[0], Math.max(x22[0], Math.max(x23[0], Math.max(x3[0], x31[0])))))))))));
            double miny = Math.min(x0[1], Math.min(x01[1], Math.min(x02[1], Math.min(x03[1], Math.min(x1[1], Math.min(x12[1], Math.min(x2[1], Math.min(x21[1], Math.min(x22[1], Math.min(x23[1], Math.min(x3[1], x31[1])))))))))));
            double maxy = Math.max(x0[1], Math.max(x01[1], Math.max(x02[1], Math.max(x03[1], Math.max(x1[1], Math.max(x12[1], Math.max(x2[1], Math.max(x21[1], Math.max(x22[1], Math.max(x23[1], Math.max(x3[1], x31[1])))))))))));

            Filter filter=CQL.toFilter("BBOX("+geomName+","+minx+","+miny+","+maxx+","+maxy+")");
            System.out.println(filter);

            Polygon imageP = (Polygon) new WKTReader().read("POLYGON((" +
                    x0[0] + " " + x0[1] + "," +
                    x01[0] + " " + x01[1] + "," +
                    x02[0] + " " + x02[1] + "," +
                    x03[0] + " " + x03[1] + "," +
                    x1[0] + " " + x1[1] + "," +
                    x12[0] + " " + x12[1] + "," +
                    x2[0] + " " + x2[1] + "," +
                    x21[0] + " " + x21[1] + "," +
                    x22[0] + " " + x22[1] + "," +
                    x23[0] + " " + x23[1] + "," +
                    x3[0] + " " + x3[1] + "," +
                    x31[0] + " " + x31[1] + "," +
                    x0[0] + " " + x0[1] + "" +
                    "))");
            System.out.println(imageP);
            FeatureCollection<?, ?> fc=featureSource.getFeatures(filter);
            //TODO   sistemare questa parte commentata va in errore.. va mantenuta??
           // FeatureCollection fc = DataUtilities.collection(featureSource.getFeatures(filter));
            if (fc.isEmpty()) {
                return null;
            }
            String[] schema = createSchema(fc.getSchema().getDescriptors());
            String[] types = createTypes(fc.getSchema().getDescriptors());

            String geoName = fc.getSchema().getGeometryDescriptor().getType().getName().toString();
            out=createFromSimpleGeometry(imageP, geoName, dataStore, fc, schema, types);
            dataStore.dispose();
            fc=null;
            System.gc();
            GeometricLayer glout = GeometricLayer.createImageProjectedLayer(out, gt, "EPSG:4326");
            return glout;
        } catch (Exception ex) {
            Logger.getLogger(SimpleShapefileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private GeometricLayer createFromSimpleGeometry(Polygon imageP, String geoName, DataStore dataStore, FeatureCollection fc, String[] schema, String[] types) throws IOException{
        GeometricLayer out=null;
        if (geoName.contains("Polygon") || geoName.contains("Line")) {
                out = new GeometricLayer(GeometricLayer.POLYGON);
                out.setName(dataStore.getTypeNames()[0]);
                FeatureIterator fi = fc.features();
                while (fi.hasNext()) {
                    Feature f = fi.next();
                    try {
                        Attributes at = Attributes.createAttributes(schema, types);
                        for (int i = 0; i < f.getProperties().size(); i++) {
                            at.set(schema[i], f.getProperty(schema[i]).getValue());
                        }
                        Geometry p2 = ((Geometry) f.getDefaultGeometryProperty().getValue()).intersection(imageP);
                        //Geometry p2 = (Geometry) f.getDefaultGeometryProperty().getValue();
                        for (int i = 0; i < p2.getNumGeometries(); i++) {
                            if (!p2.getGeometryN(i).isEmpty()) {
                                out.put(p2.getGeometryN(i), at);

                            }
                        }

                    } catch (Exception e) {
                    	 Logger.getLogger(SimpleShapefileIO.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                //out.put(imageP, Attributes.createAttributes(schema, types));
            } else if (geoName.contains("Point")) {
                out = new GeometricLayer(GeometricLayer.POINT);
                FeatureIterator fi = fc.features();
                out.setName(dataStore.getTypeNames()[0]);
                while (fi.hasNext()) {
                    Feature f = fi.next();
                    Attributes at = Attributes.createAttributes(schema, types);
                    for (int i = 0; i < f.getProperties().size(); i++) {
                        at.set(schema[i],f.getProperty(schema[i]).getValue());
                    }
                    Geometry p2 = ((Geometry) (f.getDefaultGeometryProperty().getValue())).intersection(imageP);
                    if (!p2.isEmpty()) {
                        out.put(p2, at);
                    }

                }
            }
        return out;
    }

    @Override
    public void save(GeometricLayer layer, String projection) {
        try {
            layer = GeometricLayer.createWorldProjectedLayer(layer, gir.getGeoTransform(), projection);
            String filename = ((URL) config.get(CONFIG_URL)).getPath();
            System.out.println(filename);
            //new File(filename).createNewFile();
            layername = filename.substring(filename.lastIndexOf(File.separator) + 1, filename.lastIndexOf("."));
            System.out.println(layername);
            SimpleFeatureType ft = createFeatureType(layername, layer);
            //build the type
            DataStore shape = createDataStore(filename, ft, projection);
            FeatureCollection features = createFeatures(ft, layer, projection, gir.getGeoTransform());
            writeToShapefile(shape, features);
        } catch (Exception ex) {
            Logger.getLogger(SimpleShapefileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String[] createSchema(Collection<PropertyDescriptor> attributeTypes) {
        String[] out = new String[attributeTypes.size()];
        int i = 0;
        for (PropertyDescriptor at : attributeTypes) {
            out[i++] = at.getName().toString();
        }
        return out;
    }

    private static String[] createTypes(Collection<PropertyDescriptor> attributeTypes) {
        String[] out = new String[attributeTypes.size()];
        int i = 0;
        for (PropertyDescriptor at : attributeTypes) {
            out[i++] = at.getType().getBinding().getSimpleName();
        }
        return out;
    }
}
