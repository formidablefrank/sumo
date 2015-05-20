package interpolation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import metadata.IMetadata;
import metadata.S1Metadata;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
 



public class OrbitInterpolation {
	// points (Beaulne2005 suggests nOrbPointsInterp=4)
	private final int N_ORB_POINT_INTERP=4;
	private double zeroDopplerTimeFirstRef=0;
	private double zeroDopplerTimeLastRef=0;
	double[][] statevVecInterp;
	double[][] statepVecInterp;
	double[] timeStampInterp;
	double[] secondsDiffFromRefTime;
	
	private static org.slf4j.Logger logger=LoggerFactory.getLogger(OrbitInterpolation.class);
	
	public double[] getSecondsDiffFromRefTime() {
		return secondsDiffFromRefTime;
	}


	public OrbitInterpolation() {
	}

	
	public void H(){
		//HermiteInterpolator interp=new HermiteInterpolator();
	}
	
	public void orbitInterpolation(List<S1Metadata.OrbitStatePosVelox> vpList,
			long zeroDopplerTimeFirstLineSeconds,
			long zeroDopplerTimeLastLineSeconds,
			double samplingf){
			
		
		double deltaT = 1/samplingf;
		double deltaTinv = 1/deltaT;
		int nPoints=vpList.size();
		
		double reftime=0;
		double firstTime=vpList.get(0).time;
		double lastTime=vpList.get(vpList.size()-1).time;
		
		// set the reference time
		if(firstTime<(lastTime)){
			 reftime=firstTime;
		}else{
			 reftime=lastTime;
		}
		
		// Refer all the times to the same "origin" (refTime)
		int idx=0;
		
		//differences in seconds between the ref time and the positions time
		secondsDiffFromRefTime=new double[vpList.size()]; // in matlab code is timeStampInitSecondsRef
		for(S1Metadata.OrbitStatePosVelox vp:vpList){
			secondsDiffFromRefTime[idx]= (vp.time-reftime);
			idx++;
		}
		zeroDopplerTimeFirstRef=(zeroDopplerTimeFirstLineSeconds-reftime);
		zeroDopplerTimeLastRef=(zeroDopplerTimeLastLineSeconds-reftime);
		//
		
		double minT=0;
		double maxT=0;
		
		if(zeroDopplerTimeFirstRef<zeroDopplerTimeLastRef){
			minT=Math.min(0, zeroDopplerTimeFirstRef);
			maxT=Math.max(secondsDiffFromRefTime[secondsDiffFromRefTime.length-1],zeroDopplerTimeLastRef);
		}else{
			if(firstTime<(lastTime)){
				minT=Math.min(secondsDiffFromRefTime[0], zeroDopplerTimeLastRef);
				maxT=Math.max(secondsDiffFromRefTime[secondsDiffFromRefTime.length-1],zeroDopplerTimeFirstRef);
			}else{
				minT=Math.min(secondsDiffFromRefTime[secondsDiffFromRefTime.length-1], zeroDopplerTimeLastRef);
				maxT=Math.max(secondsDiffFromRefTime[0],zeroDopplerTimeFirstRef);
			}	
		}
		int limit=((Double)((maxT-minT)/deltaT)).intValue();
		
		
		Double[] timeStampInterpSecondsRef=new Double[limit+1];
		double nextVal=0;
		for(int j=0;j<=limit;j++){
			timeStampInterpSecondsRef[j]=nextVal;
			nextVal=deltaT+nextVal;
		}
		
		double zeroDopplerTimeInitRef=0;
		double zeroDopplerTimeEndRef=0;
		
		if( zeroDopplerTimeFirstRef < zeroDopplerTimeLastRef){
				zeroDopplerTimeInitRef = zeroDopplerTimeFirstRef;
				zeroDopplerTimeEndRef = zeroDopplerTimeLastRef;
		}else{
				zeroDopplerTimeInitRef = zeroDopplerTimeLastRef;
				zeroDopplerTimeEndRef = zeroDopplerTimeFirstRef;
		}
		
		/////////////////////////Hermite interpolation/////////////////////////////////
		//For each section of orbit (ie time interval between two points in timeStampInitSecondsRef),
		// do the interpolation using the 'nOrbPointsInterp' nearest orbit points (Beaulne2005 suggests nOrbPointsInterp=4)
		if(zeroDopplerTimeInitRef<timeStampInterpSecondsRef[1]){
			int size=Math.min(nPoints,N_ORB_POINT_INTERP);
			
			double[] subTimesDiffRef= Arrays.copyOfRange(secondsDiffFromRefTime, 0, nPoints);
			
			
			//Prepare the state vectors that will be used in the interpolation
			List<Double> timeStampInitSecondsRefPointsInterp=new ArrayList<Double>();
			for(int i=0;i<size;i++){
				double valMax=timeStampInterpSecondsRef[size-1];//vOrbsPoints[size-1]];
				if(timeStampInterpSecondsRef[i]<valMax){
					timeStampInitSecondsRefPointsInterp.add(timeStampInterpSecondsRef[i]);
				}
			}
			List<IMetadata.OrbitStatePosVelox>subList=vpList.subList(0,size);
			
	        //Do the interpolation only in the portion of the section that is between zeroDopplerTimeInitSecondsRef and timeStampInitSecondsRef(1)
			double initTime=zeroDopplerTimeInitRef;
			int idxInitTime =-1;
			boolean finded=false;
			double endTime = secondsDiffFromRefTime[0];
			
			int idxEndTime =-1;
			for(int i=0;i<timeStampInterpSecondsRef.length;i++){
				//search the first
				if(timeStampInterpSecondsRef[i]>=initTime&&!finded){
					idxInitTime=i;
					finded=true; //stop at the first
				}
				//search the last that verify the condition
				if(timeStampInterpSecondsRef[i]<endTime){
					idxEndTime=i;
				}
			}
			double[] t0=ArrayUtils.toPrimitive((Double[])timeStampInitSecondsRefPointsInterp.toArray());
			
			 if (idxInitTime!=-1 && idxEndTime!=-1 && initTime < endTime){
				 HermiteInterpolation hermite=new HermiteInterpolation();
				 HermiteInterpolation.InterpolationResult result=hermite.interpolation(
						 subTimesDiffRef,subList, t0, 
						 idxInitTime, idxEndTime, deltaTinv);
				 addPointsToResult(result.interpVpoints,result.interpPpoints);
				 if(timeStampInterp==null)
					 timeStampInterp=result.timeStampInterpSecondsRef;
				 else
					 timeStampInterp=ArrayUtils.addAll(timeStampInterp, result.timeStampInterpSecondsRef);
			 }
		}
		//start from 2 because we need 2 points to start interpolation
		for(int orbPoint=1;orbPoint<nPoints-2;orbPoint++){
		//Interpolation is to be done in this section					//in carlos code secondsDiffFromRefTime=timeStampInitSecondsRef
		    if(secondsDiffFromRefTime[orbPoint-1]<zeroDopplerTimeEndRef||secondsDiffFromRefTime[orbPoint]>zeroDopplerTimeEndRef){
		    	//Find the orbit points that will be used for the interpolation
		    	int idxStart=0;
		    	int idxEnd=0;
	            if( N_ORB_POINT_INTERP > nPoints){
	            	idxEnd = nPoints;// in matlab array da 1-31 con step =1
	            }else{
	                idxStart=orbPoint - new Double(Math.floor(N_ORB_POINT_INTERP/2)).intValue()+1;
	                idxEnd=idxStart + N_ORB_POINT_INTERP -1;
	                
	                //vOrbPoints = (idxStart:idxEnd);// in matlab array da idxstart a idxEnd con step =1
	                if( idxStart <= 0){
	                    // in matlab vOrbPoints = (1:nOrbPointsInterp);
	                	idxStart=0;
	                	idxEnd=N_ORB_POINT_INTERP-1;
	                }
	                if( idxEnd > nPoints){ 
	                    //vOrbPoints = ((nPoints-nOrbPointsInterp+1):nPoints);
	                    idxStart=nPoints-N_ORB_POINT_INTERP+1;
	                	idxEnd=nPoints;
	                }
	            }
	            
	            //Prepare the state vectors that will be used in the interpolation
	            // prendo i valori delle differenze tra i tempi e il timeref per i punti che mi interessano indicati da vOrbitPoins
	            double[] timeStampInitSecondsRefPoints = Arrays.copyOfRange(secondsDiffFromRefTime, idxStart, idxEnd+1);//matlab -->timeStampInitSecondsRef(vOrbPoints);
	            
	            List<Double> timeStampInitSecondsRefPointsInterp = new ArrayList<Double>();

	            int idx1=idxStart;
	            int idx2=idxEnd;
	            if(idxStart>0){
	            	idx1=idxStart-1;
	            	idx2=idxEnd-1;
	            }	
	            for(int i=0;i<timeStampInterpSecondsRef.length;i++){
	            	if(timeStampInterpSecondsRef[i]>=secondsDiffFromRefTime[idx1]&& timeStampInterpSecondsRef[i] < secondsDiffFromRefTime[idx2]){
	            		timeStampInitSecondsRefPointsInterp.add(timeStampInterpSecondsRef[i]);
	            	}
	            }				
	            
	            List<S1Metadata.OrbitStatePosVelox> stateVecPoints=vpList.subList(idxStart,idxEnd+1);
	             

	            //Do the interpolation only in the portion of the section that is between zeroDopplerTimeInitSecondsRef and zeroDopplerTimeEndSecondsRef
	            double initTime = Math.max(secondsDiffFromRefTime[orbPoint-1], zeroDopplerTimeInitRef);
	            int idxInitTime=0;
	            boolean findIdxInitTime=false;
	            //cerco l'indice del primo elemento che soddisfa questa condizione--> Matlab code  //int  idxInitTime = find(timeStampInitSecondsRefPointsInterp >= initTime,1,'first');
	            for (int i=0;i<timeStampInitSecondsRefPointsInterp.size()&&!findIdxInitTime;i++){
	            	if(timeStampInitSecondsRefPointsInterp.get(i)>=initTime){
	            		idxInitTime=i;//timeStampInitSecondsRefPointsInterp.get(i);
	            		findIdxInitTime=true;
	            	}
	            }
	            
	            double endTime=Math.min(secondsDiffFromRefTime[orbPoint], zeroDopplerTimeEndRef);
	            int idxEndTime=0;
	            boolean findIdxEndTime=false;
	            //cerco l'indice dell'ultimo elemento che soddisfa questa condizione--> Matlab code  //int  idxInitTime = find(timeStampInitSecondsRefPointsInterp >= initTime,1,'first');
	            for (int i=timeStampInitSecondsRefPointsInterp.size()-1;i>=0&&!findIdxEndTime;i--){
	            	if(timeStampInitSecondsRefPointsInterp.get(i)<endTime){
	            		idxEndTime=i;//timeStampInitSecondsRefPointsInterp.get(i);
	            		findIdxEndTime=true;
	            	}
	            }
	            
	            logger.debug("initTime:"+ initTime+ "  endTime:"+ endTime);
	            logger.debug("idxInitTime:"+ idxInitTime+ "  idxEndTime:"+ idxEndTime);
	            logger.debug("timeStampInitSecondsRefPointsInterp(idxInitTime):"+ timeStampInitSecondsRefPointsInterp.get(idxInitTime));
	            logger.debug("timeStampInitSecondsRefPointsInterp(idxEndTime):"+ timeStampInitSecondsRefPointsInterp.get(idxEndTime));
	            
	            double timeRef = timeStampInitSecondsRefPointsInterp.get(0);
	            double[] timeStampInitSecondsRefPointsInterp0 =new double[timeStampInitSecondsRefPointsInterp.size()];
	            for(int i=0;i<timeStampInitSecondsRefPointsInterp.size();i++){
	            	timeStampInitSecondsRefPointsInterp0[i]= timeStampInitSecondsRefPointsInterp.get(i)- timeRef;        
	            }		
	            
	            
	            if (findIdxEndTime && findIdxInitTime && initTime < endTime && idxInitTime < idxEndTime){
	            	HermiteInterpolation hermite=new HermiteInterpolation();
	            	HermiteInterpolation.InterpolationResult result=hermite.interpolation(
	            			timeStampInitSecondsRefPoints,stateVecPoints, timeStampInitSecondsRefPointsInterp0, 
	            			idxInitTime, idxEndTime, deltaTinv);
	            	addPointsToResult(result.interpVpoints,result.interpPpoints);					
					
	            	for(int i=0;i<result.timeStampInterpSecondsRef.length;i++){
	            		result.timeStampInterpSecondsRef[i]=result.timeStampInterpSecondsRef[i]+timeRef;
	            	}
	            	if(timeStampInterp==null)
						 timeStampInterp=result.timeStampInterpSecondsRef;
					 else
						 timeStampInterp=ArrayUtils.addAll(timeStampInterp, result.timeStampInterpSecondsRef);
	            }
		    }
		}
		
		 if( zeroDopplerTimeEndRef > secondsDiffFromRefTime[secondsDiffFromRefTime.length-1]){
	        int vOrbPointsInit = (nPoints - Math.min(nPoints,N_ORB_POINT_INTERP)+1);
	        int vOrbPointsEnd=nPoints;

	        //Prepare the state vectors that will be used in the interpolation
            double[] timeStampInitSecondsRefPoints = Arrays.copyOfRange(secondsDiffFromRefTime, vOrbPointsInit, vOrbPointsEnd);
	        
	        List<Double> timeStampInitSecondsRefPointsInterp = new ArrayList<Double>();
            for(int i=0;i<timeStampInterpSecondsRef.length;i++){
            	if(timeStampInterpSecondsRef[i]>=secondsDiffFromRefTime[1]){
            		timeStampInitSecondsRefPointsInterp.add(timeStampInterpSecondsRef[i]);
            	}
            }				
            
            List<S1Metadata.OrbitStatePosVelox> stateVecPoints=vpList.subList(vOrbPointsInit,vOrbPointsEnd+1);
	        

	       ///////Do the interpolation only in the portion of the section that is between timeStampInitSecondsRef(vOrbPoints(end)) and zeroDopplerTimeEndSecondsRef   /////
            
	        double initTime = secondsDiffFromRefTime[vOrbPointsEnd];
	        int idxInitTime=0;
            boolean findIdxInitTime=false;
            //cerco l'indice del primo elemento che soddisfa questa condizione--> Matlab code  //int  idxInitTime = find(timeStampInitSecondsRefPointsInterp >= initTime,1,'first');
            for (int i=0;i<timeStampInitSecondsRefPointsInterp.size()&&!findIdxInitTime;i++){
            	if(timeStampInitSecondsRefPointsInterp.get(i)>=initTime){
            		idxInitTime=i;//timeStampInitSecondsRefPointsInterp.get(i);
            		findIdxInitTime=true;
            	}
            }
            
	        
            double endTime = zeroDopplerTimeEndRef;
	        int idxEndTime=0;
            boolean findIdxEndTime=false;
            //cerco l'indice dell'ultimo elemento che soddisfa questa condizione--> Matlab code  //int  idxInitTime = find(timeStampInitSecondsRefPointsInterp >= initTime,1,'first');
            for (int i=timeStampInitSecondsRefPointsInterp.size()-1;i>=0&&!findIdxEndTime;i--){
            	if(timeStampInitSecondsRefPointsInterp.get(i)<endTime){
            		idxEndTime=i;//timeStampInitSecondsRefPointsInterp.get(i);
            		findIdxEndTime=true;
            	}
            }

            double timeRef = timeStampInitSecondsRefPointsInterp.get(0);
            double[] timeStampInitSecondsRefPointsInterp0 =new double[timeStampInitSecondsRefPointsInterp.size()];
            for(int i=0;i<timeStampInitSecondsRefPointsInterp.size();i++){
            	timeStampInitSecondsRefPointsInterp0[i]= timeStampInitSecondsRefPointsInterp.get(i)- timeRef;        
            }	

	        if(findIdxEndTime && findIdxInitTime && initTime < endTime){
	        	HermiteInterpolation hermite=new HermiteInterpolation();
	        	HermiteInterpolation.InterpolationResult result=hermite.interpolation(timeStampInitSecondsRefPoints,stateVecPoints, 
	        			timeStampInitSecondsRefPointsInterp0, idxInitTime, idxEndTime, deltaTinv);
				addPointsToResult(result.interpVpoints,result.interpPpoints);
	        	
	        	for(int i=0;i<result.timeStampInterpSecondsRef.length;i++){
            		result.timeStampInterpSecondsRef[i]=result.timeStampInterpSecondsRef[i]+timeRef;
            	}
	        	if(timeStampInterp==null)
					 timeStampInterp=result.timeStampInterpSecondsRef;
				 else
					 timeStampInterp=ArrayUtils.addAll(timeStampInterp, result.timeStampInterpSecondsRef);
	        }
		 }   
		
	}
	
	/**
	 * 
	 * @param resultV
	 * @param resultP
	 */
	private void addPointsToResult(double[][] resultV,double[][] resultP){
		if(this.statevVecInterp==null){
			statevVecInterp=resultV;
		}else{
			statevVecInterp=ArrayUtils.addAll(statevVecInterp, resultV);
		}
		if(statepVecInterp==null){
			statepVecInterp=resultP;
		}else{
			statepVecInterp=ArrayUtils.addAll(statepVecInterp, resultP);
		}
		logger.debug("-->statepVecInterp Length:"+statepVecInterp.length);
	}
	
		
		
	public double getZeroDopplerTimeFirstRef() {
		return zeroDopplerTimeFirstRef;
	}


	public double getZeroDopplerTimeLastRef() {
		return zeroDopplerTimeLastRef;
	}



	
	public double[][] getStatevVecInterp() {
		return statevVecInterp;
	}


	public double[][] getStatepVecInterp() {
		return statepVecInterp;
	}


	public double[] getTimeStampInterp() {
		return timeStampInterp;
	}


	public static void main(String args[]){
		S1Metadata meta =new S1Metadata();
		//meta.initMetaData("C:\\tmp\\sumo_images\\S1_PRF_SWATH_DEVEL\\S1A_IW_GRDH_1SDV_20150219T053530_20150219T053555_004688_005CB5_3904.SAFE\\annotation\\s1a-iw-grd-vv-20150219t053530-20150219t053555-004688-005cb5-001.xml");
		meta.initMetaData("C:\\tmp\\sumo_images\\test_interpolation\\S1A_IW_GRDH_1SDV_20141016T173306_20141016T173335_002858_0033AF_FA6D.SAFE\\annotation\\s1a-iw-grd-vv-20141016t173306-20141016t173335-002858-0033af-001.xml");
		//meta.initMetaData("G:\\sat\\S1A_IW_GRDH_1SDH_20140607T205125_20140607T205150_000949_000EC8_CDCE.SAFE\\annotation\\s1a-iw-grd-hh-20140607t205125-20140607t205150-000949-000ec8-001.xml");

		
		
		OrbitInterpolation orbitInterpolation=new OrbitInterpolation();
		orbitInterpolation.orbitInterpolation(meta.getOrbitStatePosVelox(),
				meta.getZeroDopplerTimeFirstLineSeconds().getTimeInMillis(),
				meta.getZeroDopplerTimeLastLineSeconds().getTimeInMillis(),
				meta.getSamplingf());
	}
	
}