/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * 
 *
 * @author young
 *
 */
public interface ImageGenerator {

	/**
	 * Make an blank image with the specify width, height and background color.
	 *
	 * @param width
	 * @param height
	 * @param backgroundColor
	 * @return
	 */
	Image make(int width, int height, Color backgroundColor);

}
