package com.sye.pr.bs.mog;
import com.sye.pr.bs.IBackgroundSubstractor;

import boofcv.struct.image.ImageFloat32;


/**
 * Concrete implementation of {@link IBackgroundSubstractor} of the Stauffer and Grimson
 * "Adaptive background mixture models for real-time tracking" algorithm.
 * 
 * @see <a href="http://www.ai.mit.edu/projects/vsam/Publications/stauffer_cvpr98_track.pdf ">Stauffer and Grimson algorithm</a>
 *  
 * @author luis flores soberon
 *
 */
public class MoGBackgroundSubstractor implements IBackgroundSubstractor{
 
	
	private float[][] weight;
	private float[][] sortIndex;
	private float[][] mean;
	private float[][] var;
	private float[][] sd;
	private int K = 5;
	private float D=2.5f;
	private int B = 3;
	private float T = 0.6f;
	private float learningRate=0.2f;

	@Override
	public void processFrame(ImageFloat32 frame) {

		for (int x = 0; x < frame.getWidth(); x++) {
			for (int y = 0; y < frame.getHeight(); y++) {
				if(processPixel(x, y, frame)){
					frame.set(x,y,0);
				}else{
					frame.set(x,y,255);
				}
			}
		}
	}
	

	/**
	 * Function to decide wheter a given pixel in an image is part of the foreground or the background.
	 * 
	 * @param x      - The x coordinate to evaluate.
	 * @param y      - The y coordinate to evaluate.
	 * @param frame  - The {@link ImageFloat32} with the image.
	 * @return - true if the pixel is part of the backgroun, false otherwise.
	 */
	protected boolean processPixel(int x, int y, ImageFloat32 frame) {
		int index=(frame.getWidth()*y)+x;
		float wsum=0;
        float intensity = frame.get(x, y);

        boolean match = false;
        
        int kMatch=-1;
		
        for(int k=0; k < K; k++){
            
			float kw  = weight[index][k];
            float kMu = mean[index][k];
            float kSd = sd[index][k];
            float kVar = var[index][k];

            float meanDeviation = intensity - kMu;
            float zScore= Math.abs(meanDeviation/kSd);
            
            
            if(zScore<D){
            	if(!match){
                	kMatch=k;
                	match=true;
            	}
            	weight[index][k]=(1-learningRate)*kw+learningRate;
            	mean[index][k]=kMu+learningRate*meanDeviation;
            	var[index][k]=kVar + learningRate * (meanDeviation * meanDeviation - kVar);
            	if(var[index][k]<9){
            		var[index][k]=9;
            		sd[index][k]=3;
            	}else{
            		sd[index][k]=(float)Math.sqrt(var[index][k]);	
            	}            	 
            }else{
            	weight[index][k]=(1-learningRate)*kw;
            }
            if(sd[index][k]>0){
            	sortIndex[index][k]=weight[index][k]/sd[index][k];	
            }
            
            wsum += weight[index][k];
        }
		
		//Renormalize
		for(int k=0;k<K;k++){
			weight[index][k]=weight[index][k]/wsum;
		}
		
		
		float tmpMean=0;
		float tmpVar=0;
		float tmpSd=0;
		float tmpWeigth=0;
		float tmpSortIndex=0;
		/*
		for(int i=1;i<K;i++){
			tmpMean=mean[index][i];
			tmpVar=var[index][i];
			tmpSd=sd[index][i];
			tmpWeigth=weight[index][i];
			tmpSortIndex=sortIndex[index][i];
			
			int j=i-1;
			while(i>=0&&sortIndex[index][i]>sortIndex[index][j]){
				sortIndex[index][j+1]=sortIndex[index][j];
				mean[index][j+1]=mean[index][j];
				var[index][j+1]=var[index][j];
				sd[index][j+1]=sd[index][j];
				weight[index][j+1]=weight[index][j];
				
			}
		}*/
		for(int i=1;i<K;i++){
			for(int j=i;j>0;j--){
				if(sortIndex[index][j]>sortIndex[index][j-1]){
					tmpMean=mean[index][j-1];
					tmpVar=var[index][j-1];
					tmpSd=sd[index][j-1];
					tmpWeigth=weight[index][j-1];
					tmpSortIndex=sortIndex[index][j-1];
					mean[index][j-1]=mean[index][j];
					var[index][j-1]=var[index][j];
					sd[index][j-1]=sd[index][j];
					weight[index][j-1]=weight[index][j];
					sortIndex[index][j-1]=sortIndex[index][j];
					mean[index][j]=tmpMean;
					var[index][j]=tmpVar;
					sd[index][j]=tmpSd;
					weight[index][j]=tmpWeigth;
					sortIndex[index][j]=tmpSortIndex;
					
				}
			}
		}
		
		if(!match){
			mean[index][K-1]=intensity;
			var[index][K-1]=49;
			sd[index][K-1]=7;
		}else{
			float wSum=0;
			kMatch=-1;
			for(int k=0; k < K; k++){
	            if(kMatch<0){				
		            
		            float meanDeviation = intensity - mean[index][k];
		            float zScore= meanDeviation/sd[index][k];
		            
		            if(zScore<D){
		            	kMatch=k;
		            }
	            }

				wSum+=weight[index][k];
				if(wSum>T){
					break;
				}
			}
			
			if(kMatch>-1){
				return true;
			}
		}
		
		return false;
		
	}


	public void init(int height, int width) {

		int totalPixels = height * width;
		weight = new float[totalPixels][K];
		mean = new float[totalPixels][K];
		sortIndex = new float[totalPixels][K];
		var = new float[totalPixels][K];
		sd = new float[totalPixels][K];
		
		
		for (int i = 0; i < totalPixels; i++) {
			for (int k = 0; k < K; k++) {
				mean[i][k] = 0;
				var[i][k] = 0;
				weight[i][k] = (1f / (float)K);
				sd[i][k] =0;
			}
		}
	}


	public int getK() {
		return K;
	}


	public void setK(int k) {
		K = k;
	}


	public float getD() {
		return D;
	}


	public void setD(float d) {
		D = d;
	}


	public int getB() {
		return B;
	}


	public void setB(int b) {
		B = b;
	}


	public float getT() {
		return T;
	}


	public void setT(float t) {
		T = t;
	}


	public float getLearningRate() {
		return learningRate;
	}


	public void setLearningRate(float learningRate) {
		this.learningRate = learningRate;
	}
}
