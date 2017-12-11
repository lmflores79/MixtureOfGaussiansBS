package com.sye.pr.bs;

import boofcv.struct.image.ImageFloat32;

/**
 * 
 * @author luis flores soberon
 *
 */
public interface IBackgroundSubstractor {

    /**
     * Process each frame extracting the background.
     * 
     * @param frame -  The image to transform.
     */
	public void processFrame(ImageFloat32 frame);
	
	/**
	 * Sets up the algorithm configuration.
	 * 
	 * @param height -  The height of the screen.
	 * @param width -  The width of the screen.
	 */
	public void init(int height, int width);
}
