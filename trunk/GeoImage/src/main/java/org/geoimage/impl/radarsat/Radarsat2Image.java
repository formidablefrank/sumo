package org.geoimage.impl.radarsat;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoimage.def.GeoMetadata;
import org.geoimage.def.SarImageReader;
import org.geoimage.factory.GeoTransformFactory;
import org.geoimage.impl.Gcp;
import org.geoimage.impl.GeotiffImage;
import org.geoimage.impl.TIFF;
import org.geoimage.utils.GeoUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.sun.media.imageio.plugins.tiff.TIFFImageReadParam;

/**
 * A class that reads Radarsat 2 images (geotiffs + xml). simple to quadri polarisation are supported
 * @author thoorfr
 */
public class Radarsat2Image extends SarImageReader {
	
    protected TIFF image;
    protected String[] files;
    protected int[] preloadedInterval = new int[]{0, 0};
    protected int[] preloadedData;
    protected Rectangle bounds;
    protected File productxml;
    protected Document doc;
    protected Namespace ns = Namespace.getNamespace("http://www.rsi.ca/rs2/prod/xml/schemas");
    protected Map<String, TIFF> tiffImages;
    protected Vector<String> bands;
    protected String timestampStart;
    protected String timestampStop;
    private String overviewImage;
    	
    public Radarsat2Image() {
    }

    @Override
    public int getNBand() {
        return bands.size();
    }

    @Override
    public List<Gcp> getGcps() {
        if (gcps == null) {
            parseProductXML(productxml);
        }
        return gcps;
    }

    @Override
    public String getAccessRights() {
        return "r";
    }

    @Override
    public String[] getFilesList() {
        return files;
    }

    @Override
    public boolean initialise(File f) {
    	try {
    		super.imgName=f.getParentFile().getName();
    		
    		SAXBuilder builder = new SAXBuilder();
    		setFile(f);
    		doc = builder.build(productxml);
    	
    		
    		
    		tiffImages = getImages();
    		if(tiffImages==null) return false;
    		
            image = tiffImages.values().iterator().next();
            
            this.displayName=super.imgName;//+ "  " +image.getImageFile().getName();
            
            parseProductXML(productxml);
            
            bounds = new Rectangle(0, 0, image.xSize, image.ySize);
            read(0,0);
        } catch (Exception ex) {
            dispose();
            Logger.getLogger(Radarsat2Image.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * 
     * @param imageFile
     * @throws IOException
     */
    protected void setFile(File imageFile) throws IOException {
        files = new String[1];
        File parent = imageFile;
        if (!parent.isDirectory()) {
            parent = imageFile.getParentFile();
        }
        for (File f : parent.listFiles()) {
            if (f.getName().endsWith("product.xml")) {
                productxml = f;
                files[0]=f.getAbsolutePath();
            }
        }
        if (productxml == null) throw new IOException("Cannot find product.xml");
                
        //set the path for the overview images
        overviewImage=new StringBuilder(parent.getAbsolutePath()).append("BrowseImage.tif").toString();
        
    }

    @Override
    public int[] readTile(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect = rect.intersection(bounds);
        int[] tile= new int[height*width];
        if (rect.isEmpty()) {
            return tile;
        }
        if (rect.y != preloadedInterval[0] | rect.y + rect.height != preloadedInterval[1]) {
            preloadLineTile(rect.y, rect.height);
        }
        int yOffset = image.xSize;
        int xinit = rect.x - x;
        int yinit = rect.y - y;
        for (int i = 0; i < rect.height; i++) {
            for (int j = 0; j < rect.width; j++) {
                int temp = i * yOffset + j + rect.x;
                tile[(i + yinit) * width + j + xinit] = preloadedData[temp];
            }
        }
        return tile;
    }


    @Override
    public int read(int x, int y) {
        TIFFImageReadParam t=new TIFFImageReadParam();
        t.setSourceRegion(new Rectangle(x, y, 1, 1));
        try {
            return image.reader.read(0, t).getRGB(x, y);
        } catch (IOException ex) {
            Logger.getLogger(GeotiffImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    @Override
    public String getBandName(int band) {
        return bands.get(band);
    }

    @Override
    public void setBand(int band) {
    	if(bands.size()>=band){
    		this.band=band;
    		this.image=tiffImages.get(bands.get(band));
    	}else{
    		this.band=0;
    		this.image=tiffImages.get(bands.get(0));
    	}	
    }

    @Override
    public void preloadLineTile(int y, int length) {
        if (y < 0) {
            return;
        }
        preloadedInterval = new int[]{y, y + length};
        Rectangle rect = new Rectangle(0, y, image.xSize, length);
        TIFFImageReadParam tirp=new TIFFImageReadParam();
        tirp.setSourceRegion(rect);
        try {
            preloadedData = image.reader.read(0, tirp).getRaster().getSamples(0, 0, image.xSize, length, 0, (int[]) null);
        } catch (Exception ex) {
            Logger.getLogger(GeotiffImage.class.getName()).log(Level.SEVERE, null, ex);
            System.gc();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if(tiffImages==null) return;
        for(TIFF t:tiffImages.values()){
            t.dispose();
        }
        tiffImages=null;
    }

    private Map<String, TIFF> getImages() {
        List<?> elements = doc.getRootElement().getChild("imageAttributes", ns).getChildren("fullResolutionImageData", ns);
        Map<String, TIFF> tiffs = new HashMap<String, TIFF>();
        bands=new Vector<String>();
        for (Object o : elements) {
            if (o instanceof Element) {
                File f = new File(productxml.getParent(), ((Element) o).getText());
                String polarisation = ((Element) o).getAttribute("pole").getValue();
                tiffs.put(polarisation, new TIFF(f,0));
                bands.add(polarisation);
            }
        }
        return tiffs;
    }

    private void parseProductXML(File productxml) {
        try {
            gcps = new ArrayList<Gcp>();

            setSatellite("RADARSAT-2");
            setSensor("SAR Payload Module");

            
            Element atts = doc.getRootElement().getChild("imageGenerationParameters", ns);

            // generalprocessinginformation
            atts = atts.getChild("generalProcessingInformation", ns);
            setMetadataXML(atts, "productType", PRODUCT, ns, String.class);
            setMetadataXML(atts, "processingFacility", PROCESSOR, ns,String.class);

            // sarprocessinginformation
            atts = doc.getRootElement().getChild("imageGenerationParameters", ns).getChild("sarProcessingInformation", ns);
            setMetadataXML(atts, "numberOfAzimuthLooks", ENL, ns,String.class);
            setMetadataXML(atts, "incidenceAngleNearRange", INCIDENCE_NEAR, ns,Double.class);
            setMetadataXML(atts, "incidenceAngleFarRange", INCIDENCE_FAR, ns,Double.class);
            double slantRange=(Double)setMetadataXML(atts, "slantRangeNearEdge", SLANT_RANGE_NEAR_EDGE, ns,Double.class);
            double satH=(Double)setMetadataXML(atts, "satelliteHeight", SATELLITE_ALTITUDE, ns,Double.class);
            
            
            // rasterattributes
            atts = doc.getRootElement().getChild("imageAttributes", ns).getChild("rasterAttributes", ns);
            if(atts.getChild("numberOfLines", ns) != null)
                setHeight(Integer.parseInt(atts.getChild("numberOfLines", ns).getText()));
            if(atts.getChild("numberOfSamplesPerLine", ns) != null)
                setWidth(Integer.parseInt(atts.getChild("numberOfSamplesPerLine", ns).getText()));
            if(atts.getChild("bitsPerSample", ns) != null)
                setNumberOfBytes(Integer.parseInt(atts.getChild("bitsPerSample", ns).getText())/8);
            
            double pixSpace=(Double)setMetadataXML(atts, "sampledPixelSpacing", AZIMUTH_SPACING, ns,Double.class);
            setMetadataXML(atts, "sampledPixelSpacing", RANGE_SPACING, ns,String.class);
            String pixelTimeOrd=(String)setMetadataXML(atts, "pixelTimeOrdering", SIMPLE_TIME_ORDERING, ns,String.class);
            
            // geolocationgrid
            atts = doc.getRootElement().getChild("imageAttributes", ns);
            atts = atts.getChild("geographicInformation", ns);
            
            Element attsEllipsoid =atts.getChild("referenceEllipsoidParameters", ns);
            
            
            double max=(Double)setMetadataXML(attsEllipsoid,MAJOR_AXIS, "semiMajorAxis",ns,Double.class);
            double min=(Double)setMetadataXML(attsEllipsoid,MINOR_AXIS, "semiMinorAxis",ns,Double.class);
            double sixeXPixel=new Double(getWidth());
            double geoH=(Double)setMetadataXML(attsEllipsoid,GEODETIC_TERRA_HEIGHT, "geodeticTerrainHeight",ns,Double.class);
            
            atts = atts.getChild("geolocationGrid", ns);
                        
            @SuppressWarnings("unchecked")
			List <Element> tiePoints=atts.getChildren("imageTiePoint", ns);
            HashMap<Double,Double> iaValues=new HashMap<Double,Double>();
            
            
            Element first=tiePoints.get(0);
            Element last=tiePoints.get(tiePoints.size()-1);
            double latMin=Double.parseDouble(first.getChild("geodeticCoordinate", ns).getChild("latitude", ns).getText());
            double latMax=Double.parseDouble(last.getChild("geodeticCoordinate", ns).getChild("latitude", ns).getText());
            
            double earthRad=GeoUtils.earthRadiusFromLatitude(Math.abs((latMax-latMin)/2),min ,max);
            double geoidHeight=0;
            
            for (Element elem : tiePoints) {
                Gcp gcp = new Gcp();
                
                Element imgCoo = elem.getChild("imageCoordinate", ns);
                double pixel=Double.parseDouble(imgCoo.getChild("pixel", ns).getText());
                double line= Double.parseDouble(imgCoo.getChild("line", ns).getText());
                
                Element geoCoo = elem.getChild("geodeticCoordinate", ns);
                double longitude=Double.parseDouble(geoCoo.getChild("longitude", ns).getText());
                double latitude=Double.parseDouble(geoCoo.getChild("latitude", ns).getText());
                
                gcp.setXpix(pixel);
                gcp.setYpix(line);
                gcp.setXgeo(longitude);
                gcp.setYgeo(latitude);
                gcp.setOriginalXpix(pixel);
                gcp.setZgeo(Double.parseDouble(geoCoo.getChild("height", ns).getText()));
                
                Double ia=iaValues.get(pixel);
                
                try{
                	
                	if(!gcps.isEmpty()){
                		Gcp previous=gcps.get(gcps.size()-1);
                		double distanceFromPrevious=GeoUtils.distance(previous.getXgeo(),previous.getYgeo(), longitude, latitude);
                		if (distanceFromPrevious>100){
                			geoidHeight=GeoUtils.getGeoidH(longitude,latitude);
                		}
                	}else{
                		geoidHeight=GeoUtils.getGeoidH(longitude,latitude);
                	}
	                
	                double dx=0;
                	if(ia==null){
		                //geocorrection
		                if(productxml.getParent().contains("_SLC")){
		                	//complex image
		                	ia=GeoUtils.gcpSlantRangeAndIncidenceAngleForComplex(slantRange,sixeXPixel,pixSpace,gcp,satH,earthRad,pixelTimeOrd);
		                	dx=GeoUtils.gcpComplexGeoCorrectionMeters(ia,geoH,geoidHeight);
		                	iaValues.put(pixel, ia);
		                }else{
		                	ia=GeoUtils.gcpIncidenceAngleForGRD(slantRange,sixeXPixel,pixSpace,gcp,satH,earthRad,pixelTimeOrd);
		                	dx=GeoUtils.gcpGrdGeoCorrectionMeters(ia,geoH,geoidHeight);
		                	iaValues.put(pixel, ia);
		                }
                	}else{
                		if(productxml.getParent().contains("_SLC")){
		                	//complex image
		                	dx=GeoUtils.gcpComplexGeoCorrectionMeters(ia,geoH,geoidHeight);
		                }else{
		                	dx=GeoUtils.gcpGrdGeoCorrectionMeters(ia,geoH,geoidHeight);
		                }
                	}   
	                //covert from meters to pixel
	                dx=(dx/pixSpace);
	                 
	                if(pixelTimeOrd.equalsIgnoreCase("Increasing"))
	                	gcp.setXpix(pixel+dx);
	                else
	                	gcp.setXpix(pixel-dx);
                }catch(Exception e){
                    Logger.getLogger(Radarsat2Image.class.getName()).log(Level.WARNING, "Error during geocorrection",e);
                    gcp.setXpix(pixel);
                }   
                
                gcp.setYpix(line);
                gcps.add(gcp);
               
            }

            String epsg = "EPSG:4326";
            geotransform = GeoTransformFactory.createFromGcps(gcps, epsg);

            // check if ascending or descending
            boolean ascending = doc.getRootElement().getChild("sourceAttributes", ns).getChild("orbitAndAttitude", ns).getChild("orbitInformation", ns).getChild("passDirection", ns).getText().equalsIgnoreCase("Ascending");
            setOrbitDirection(ascending ? new String("ASCENDING") : new String("DESCENDING"));
            Element sarProcessingInformation = doc.getRootElement().getChild("imageGenerationParameters", ns).getChild("sarProcessingInformation", ns);
            // use different values if ascending or descending
            if(ascending)
            {
                timestampStop = (String)sarProcessingInformation.getChild("zeroDopplerTimeFirstLine", ns).getText();
                timestampStart = (String)sarProcessingInformation.getChild("zeroDopplerTimeLastLine", ns).getText();
            } else {
                timestampStart = (String)sarProcessingInformation.getChild("zeroDopplerTimeFirstLine", ns).getText();
                timestampStop = (String)sarProcessingInformation.getChild("zeroDopplerTimeLastLine", ns).getText();
            }
            // change the string to match Timestamp format
            timestampStart = timestampStart.replaceAll("T", " ");
            timestampStart = timestampStart.replaceAll("Z", " ");

            timestampStop = timestampStop.replaceAll("T", " ");
            timestampStop = timestampStop.replaceAll("Z", " ");

            setTimeStampStart(timestampStart);//Timestamp.valueOf(timestampStart));
            setTimeStampStop(timestampStop);//Timestamp.valueOf(timestampStop));

            atts = doc.getRootElement().getChild("sourceAttributes", ns);
            atts = atts.getChild("radarParameters", ns);
            
            setMetadataXML(atts, "beams", BEAM, ns,Integer.class);
            setMetadataXML(atts, "acquisitionType", MODE, ns,String.class);
            setMetadataXML(atts, "polarizations", POLARISATION, ns,String.class);
            setMetadataXML(atts, "antennaPointing", LOOK_DIRECTION, ns,String.class);
            setMetadataXML(atts, "pulseRepetitionFrequency", PRF, ns,double.class);
            setMetadataXML(atts, "radarCenterFrequency", RADAR_WAVELENGTH, ns,Double.class);
            //convert to wavelength
            double radarFrequency = getRadarWaveLenght();
            setRadarWaveLenght(299792457.9 / radarFrequency);


            // orbitandattitude
            atts = doc.getRootElement().getChild("sourceAttributes", ns).getChild("orbitAndAttitude", ns).getChild("orbitInformation", ns);
            setMetadataXML(atts, "passDirection", ORBIT_DIRECTION, ns,String.class);
            // calculate satellite speed using state vectors
            atts = atts.getChild("stateVector", ns);
            if(atts != null)
            {
                double xvelocity = Double.valueOf(atts.getChildText("xVelocity", ns));
                double yvelocity = Double.valueOf(atts.getChildText("yVelocity", ns));
                double zvelocity = Double.valueOf(atts.getChildText("zVelocity", ns));
                double satellite_speed = Math.pow(Math.pow(xvelocity, 2) + Math.pow(yvelocity, 2) + Math.pow(zvelocity, 2), 0.5);
                setSatelliteSpeed(satellite_speed);
            }

            setHeadingAngle(this.getImageAzimuth());
            setSatelliteOrbitInclination(98.5795);
            setRevolutionsPerdayDouble(new Double(14 + 7/34));
            setK(0.0);
        
        } catch (Exception ex) {
            Logger.getLogger(Radarsat2Image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getRevolutionsPerdayDouble(){
    	return (Double)getMetadata(GeoMetadata.REVOLUTIONS_PERDAY);
    }
    public void setRevolutionsPerdayDouble(double data){
    	setMetadata(GeoMetadata.REVOLUTIONS_PERDAY,data);
    }
   

    @Override
    public int getNumberOfBytes() {
        return 2;
    }

    @Override
    public int getType(boolean oneBand) {
        if(oneBand || bands.size()<2) return BufferedImage.TYPE_USHORT_GRAY;
        else return BufferedImage.TYPE_INT_RGB;
    }

    @Override
    public String getFormat() {
        return getClass().getCanonicalName();
    }

    public String getInternalImage() {
  		return null;
  	}

	@Override
	public int getWidth() {
		return image.xSize;
	}

	@Override
	public int getHeight() {
		return image.ySize;
	}
	
	@Override
	public File getOverviewFile() {
		return new File(this.overviewImage);
	}

	@Override
	public boolean supportAzimuthAmbiguity() {
		return true;
	}
	@Override
	public String getImgName() {
		return imgName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}

