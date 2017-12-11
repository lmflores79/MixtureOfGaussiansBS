package com.sye.pr.bs;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.SimpleImageSequence;
import boofcv.struct.image.ImageFloat32;

/**
 * 
 * @author luis flores soberon
 *
 */
public class VideoProcessor{

	IBackgroundSubstractor backgroundSubstractor;
	
	
	/**
	 * Process the video altering the images in the {@code sequence} so that they contain only the foreground pixels. 
	 * 
	 * @param sequence - The sequence of {@link ImageFloat32} to transform.
	 */
	public void process(SimpleImageSequence<ImageFloat32> sequence) {
		   
		ImageFloat32 frame = sequence.next();
		ImageFloat32 unprocessedFrame = null;
		
		ImagePanel gui = new ImagePanel();
		ImagePanel gui2 = new ImagePanel();
		
		gui.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
		gui2.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));

		ShowImages.showWindow(gui, "Processed Image");
		ShowImages.showWindow(gui2, "Unprocessed Image");



		int frameNumber = 0;
		while (sequence.hasNext()) {
			frame = (ImageFloat32) sequence.next();
			unprocessedFrame=frame.clone();

			/**For the first frame we create a robust model by adjusting the model several times */
			if(frameNumber==0){
				backgroundSubstractor.init(frame.getHeight(),frame.getWidth());
				
				for(int i=0;i<15;i++){
					ImageFloat32 cloneFrame = frame.clone();
					backgroundSubstractor.processFrame(cloneFrame);
				}
			}else{
				backgroundSubstractor.processFrame(frame);
			}
			
			frameNumber++;
			paintFrame(frame,gui);
			paintFrame(unprocessedFrame,gui2);
			
		}
	}
	
	protected void paintFrame(ImageFloat32 frame,ImagePanel imagePanel){
		BufferedImage bufferedImage = ConvertBufferedImage.convertTo(frame, null);
		imagePanel.setBufferedImage(bufferedImage);
		imagePanel.repaint();
		
	}

	public void setBackgroundSubstractor(
			IBackgroundSubstractor backgroundSubstractor) {
		this.backgroundSubstractor = backgroundSubstractor;
	}

}
