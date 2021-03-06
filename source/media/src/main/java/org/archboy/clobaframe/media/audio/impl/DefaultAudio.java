package org.archboy.clobaframe.media.audio.impl;

import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.audio.Audio;

/**
 *
 * @author yang
 */
public class DefaultAudio implements Audio {

	private ResourceInfo resourceInfo;
	private MetaData metaData;
	
	private Format format;
	private String encoding;
	private double duration;
	private int bitrate;
	private BitrateMode bitrateMode;

	public DefaultAudio(ResourceInfo resourceInfo, Format format, String encoding, double duration, int bitrate, BitrateMode bitrateMode) {
		this.resourceInfo = resourceInfo;
		this.format = format;
		this.encoding = encoding;
		this.duration = duration;
		this.bitrate = bitrate;
		this.bitrateMode = bitrateMode;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@Override
	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	@Override
	public BitrateMode getBitrateMode() {
		return bitrateMode;
	}

	public void setBitrateMode(BitrateMode bitrateMode) {
		this.bitrateMode = bitrateMode;
	}

	@Override
	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}

	@Override
	public ResourceInfo getResourceInfo(Date lastModified) {
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
}
