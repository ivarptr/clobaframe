package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImageGeneratorTest {
	
	@Inject
	private ImageGenerator imageGenerator;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testMakeImageFromCanvas() throws IOException {
		Image image1 = imageGenerator.make(200, 300, Color.darkGray);
		assertEquals(200, image1.getWidth());
		assertEquals(300, image1.getHeight());
		assertEquals(Color.darkGray.getRGB(), getImageColor(image1, 0, 0));

		// manual check
		Utils.saveImage(image1, "imageGenerator-darkGray-200x300");

		Color transparent = new Color(0, 0, 0, 0);
		Image image2 = imageGenerator.make(300, 100, transparent);
		assertEquals(300, image2.getWidth());
		assertEquals(100, image2.getHeight());
		assertEquals(transparent.getRGB(), getImageColor(image2, 0, 0));

		// manual check
		Utils.saveImage(image2, "imageGenerator-transparent-300x100");
	}
	
	private int getImageColor(Image image, int x, int y){
		BufferedImage bufferedImage = image.getBufferedImage();
		return bufferedImage.getRGB(x, y);
	}

}
