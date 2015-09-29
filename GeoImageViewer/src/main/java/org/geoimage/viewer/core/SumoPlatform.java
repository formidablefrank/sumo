/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geoimage.viewer.core;


import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPopupMenu;

import org.geoimage.def.GeoImageReader;
import org.geoimage.opengl.OpenGLContext;
import org.geoimage.viewer.core.batch.Sumo;
import org.geoimage.viewer.core.configuration.PlatformConfiguration;
import org.geoimage.viewer.core.gui.manager.LayerManager;
import org.geoimage.viewer.core.layers.ConsoleLayer;
import org.geoimage.viewer.core.layers.image.CacheManager;
import org.geoimage.viewer.widget.TransparentWidget;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thoorfr
 */
public class SumoPlatform extends SingleFrameApplication {
	private static org.slf4j.Logger logger=LoggerFactory.getLogger(SumoPlatform.class);

    private static boolean batchMode=false;
    private PluginsManager plManager=null;
    private static int maxPBar = 0;
    
    
    /*private static Thread currentThreadRunning=null;
    public Thread getCurrentThreadRunning() {
		return currentThreadRunning;
	}
	public static void setCurrentThreadRunning(Thread currentThread) {
		currentThreadRunning = currentThread;
	}
	public static void stopCurrentThread(){
		if(currentThreadRunning!=null&&currentThreadRunning.isAlive()){
			try{
				currentThreadRunning.interrupt();
			}catch(Exception ex){
				ex.printStackTrace();
			}	
		}
	}*/
    
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {
        	plManager=new PluginsManager();
            show(new GeoImageViewerView(this));
        } catch (Throwable ex) {
        	logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SumoPlatform
     */
    public static SumoPlatform getApplication() {
        return Application.getInstance(SumoPlatform.class);
    }
    
	
	/**
     * 
     * @return true if sumo is running in batch mode
     */
    public static boolean isBatchMode() {
		return batchMode;
	}
    
    /**
     * set batch mode = true 
     */
	public static void setInBatchMode() {
		batchMode = true;
	}
	
	
	/**
	 * default
	 */
	public static void setInteractiveMode() {
		batchMode = false;
	}

	public PluginsManager getPluginsManager(){
		return this.plManager;
	}

    public ConsoleLayer getConsoleLayer() {
        return ((GeoImageViewerView) getApplication().getMainView()).getConsole();
    }

    public OpenGLContext getGeoContext() {
        return ((GeoImageViewerView) getApplication().getMainView()).getGeoContext();
    }

    public LayerManager getLayerManager() {
        return LayerManager.getIstanceManager();
    }

    public GLCanvas getMainCanvas() {
        return ((GeoImageViewerView) getApplication().getMainView()).getMainCanvas();
    }
    
    public GeoImageViewerView getMain() {
        return (GeoImageViewerView) getApplication().getMainView();
    }
    
    static void setCacheManager(CacheManager cacheManager) {
        ((GeoImageViewerView) getApplication().getMainView()).setCacheManager(cacheManager);
    }

    public PlatformConfiguration getConfiguration() {
        return PlatformConfiguration.getConfigurationInstance();
    }

    public void addWidget(TransparentWidget widget) {
        ((GeoImageViewerView) getApplication().getMainView()).addWidget(widget);
    }
    
    
    public static void refresh() {
        ((GeoImageViewerView) getApplication().getMainView()).refresh();
    }
    

    public void setInfo(String info) {
        setInfo(info, 10000);
    }

    public static void setInfo(String info, long timeout) {
        //AG progress bar management
        int progress = 0;//AG
        GeoImageViewerView mainView=((GeoImageViewerView) getApplication().getMainView());
        if (info == null || "".equals(info)) {
        	mainView.setInfo("");
            maxPBar = 0;//AG
            mainView.setProgressValue(0);//AG
            mainView.setProgressMax(0);//AG
            mainView.iconTimer(false);//AG
            return;
        }
        if (timeout==-1) {//AG
        	mainView.setProgressMax(-1);//AG
        }
        if (info.startsWith("Adding ")) {//AG
        	mainView.setProgressMax(-1);//AG
        } else if (info.startsWith("loading ")) {//AG
            progress = new Integer(info.replace("loading ", ""));      //AG
        }//AG
        else if(timeout>0){
        	mainView.setProgressMax(0);//AG
        }
        if (maxPBar < progress) {//AG
            maxPBar = progress;//AG
            mainView.setProgressMax(maxPBar);//AG
            mainView.iconTimer(true);//AG
        }
        mainView.setInfo(info);
        mainView.setProgressValue(maxPBar - progress);//AG
        if (progress == 1) {//AG
            maxPBar = 0;//AG
            mainView.setProgressValue(0);//AG
            mainView.iconTimer(false);//AG
        }
    }

    
    public GeoImageReader getCurrentImageReader(){
    	if(isBatchMode()){
    		return Sumo.getCurrentReader();
    	}else{
    		return LayerManager.getIstanceManager().getCurrentImageLayer().getImageReader();
    	}
    	
    }
    
 
    /** 
     * search the cache in the DB, if it doesn't exist then read the properties file
     * even if the file is empty a default path is used
     */
    public String getCachePath() {
        String cache = getConfiguration().getCachePrefFolder();
        return cache;
    }
    
    
    
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            launch(SumoPlatform.class, args);
    }
   
}