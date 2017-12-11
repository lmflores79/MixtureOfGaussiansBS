package com.sye.pr.bs;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import boofcv.io.image.SimpleImageSequence;
import boofcv.io.video.VideoMjpegCodec;
import boofcv.io.wrapper.images.JpegByteImageSequence;
import boofcv.struct.image.ImageFloat32;

import com.sye.pr.bs.mog.MoGBackgroundSubstractor;

/**
 * 
 * @author luis flores soberon
 *
 */
public class Main {

	public static void main(String[] args)throws IOException{
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"app-config.xml");
		VideoProcessor vp = (VideoProcessor)appContext.getBean("videoProcessor");

		VideoMjpegCodec codec = new VideoMjpegCodec();
		List<byte[]> data = codec.read(new FileInputStream(
				"c:/Luis/test5.mjpeg"));
		SimpleImageSequence sequence = new JpegByteImageSequence(ImageFloat32.class,
				data, true);

		
		vp.setBackgroundSubstractor( new MoGBackgroundSubstractor());


		vp.process(sequence);
		

	}

}
