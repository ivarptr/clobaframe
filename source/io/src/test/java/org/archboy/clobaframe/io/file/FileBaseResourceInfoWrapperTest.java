package org.archboy.clobaframe.io.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.impl.DefaultResourceInfoFactory;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class FileBaseResourceInfoWrapperTest {
	
	/**
	 * The content of this file is: 0x30 0x31 ... 0x39.
	 */
	private static final String DEFAULT_SAMPLE_FILE = "sample/data/test.txt";
	private String sampleFile = DEFAULT_SAMPLE_FILE;

	@Inject
	private ResourceLoader resourceLoader;
	
	//@Inject
	private ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
	
	@Inject
	private FileBaseResourceInfoWrapper fileBaseResourceInfoWrapper;
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}
	
	@Test
	public void testWrap() throws IOException{
		
		String mimeType = "application/octet-stream";
		byte[] data = new byte[]{0,1,2,3,4,5};
		InputStream in = new ByteArrayInputStream(data);
		Date now = new Date();
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(in, data.length, mimeType, now);
		
		TemporaryResources temporaryResources = new DefaultTemporaryResources();
		FileBaseResourceInfo fileBaseResourceInfo =
				fileBaseResourceInfoWrapper.wrap(resourceInfo, temporaryResources);
		
		// check file
		File file = fileBaseResourceInfo.getFile();
		assertNotNull(file);
		
		assertEquals(resourceInfo.getContentLength(), fileBaseResourceInfo.getContentLength());
		assertEquals(resourceInfo.getMimeType(), fileBaseResourceInfo.getMimeType());
		assertDateEquals(resourceInfo.getLastModified(), fileBaseResourceInfo.getLastModified());
		assertTrue(fileBaseResourceInfo.isSeekable());
		
		InputStream in1 = fileBaseResourceInfo.getContent();
		assertArrayEquals(data, IOUtils.toByteArray(in1));
		in1.close();
		
		InputStream in2 = fileBaseResourceInfo.getContent(1, 3);
		assertArrayEquals(new byte[]{1,2,3}, IOUtils.toByteArray(in2));
		in2.close();
		
		// check file content
		InputStream in3 = new FileInputStream(file);
		assertArrayEquals(data, IOUtils.toByteArray(in3));
		in3.close();
		
		// check close
		assertTrue(file.exists());
//		fileBaseResourceInfo.close();
		temporaryResources.close();
		assertFalse(file.exists());
		
	}
	
	/**
	 * Get the test resources by file name.
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private File getFileByName(String name) throws IOException{
		Resource resource = resourceLoader.getResource(name); //"file:target/test-classes/" +
		return resource.getFile();
	}

	/**
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileContent(String name) throws IOException {
		File file = getFileByName(name);
		InputStream in = new FileInputStream(file);
		byte[] data = IOUtils.toByteArray(in);
		in.close();
		return data;
	}
	
	private static void assertDateEquals(Date expected, Date actual){
		if (expected == null && actual == null){
			//
		}else if(expected == null || actual == null){
			fail("date not equals");
		}else{
			assertTrue(Math.abs(expected.getTime() - actual.getTime()) < 1000 );
		}
	}
}
