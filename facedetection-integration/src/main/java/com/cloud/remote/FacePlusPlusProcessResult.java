package com.cloud.remote;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.cloud.dto.Coordinate2D;
import com.cloud.dto.FaceDetection;
import com.cloud.dto.Image;
import com.cloud.dto.ProcessResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FacePlusPlusProcessResult extends ProcessResult {

	public FacePlusPlusProcessResult(String endpoint) {
		super(endpoint);
	}

	/*
	 * private Image parseImageData(Map<String, Object> processResult) throws
	 * Exception { String url = String.valueOf(processResult.get("url"));//El
	 * webservice retorna la url completa con la extension String extensionImage
	 * ="."+FilenameUtils.getExtension(url); url =
	 * FilenameUtils.removeExtension(url); Integer imgWidth =
	 * Integer.valueOf(String.valueOf(processResult .get("img_width"))); Integer
	 * imgheight = Integer.valueOf(String.valueOf(processResult
	 * .get("img_height"))); Boolean download = false; return new
	 * Image(url,imgWidth, imgheight, download ); }
	 */

	public FaceDetection parseFace(Map<String, Object> attributes) {
		FaceDetection faceDetection = new FaceDetection();

		if (!attributes.containsKey("position"))
			return faceDetection;

		Map<String, Object> position = (Map<String, Object>) attributes.get("position");
		if (!position.containsKey("eye_left"))
			return faceDetection;
		Map<String, Double> eye_left = (Map<String, Double>) position.get("eye_left");
		if (!position.containsKey("eye_right"))
			return faceDetection;
		Map<String, Double> eye_right = (Map<String, Double>) position.get("eye_right");

		Integer[] eyeLeft = new Integer[] { new Double(eye_left.get("x") / 100 * this.image.getWidth()).intValue(),
				new Double(eye_left.get("y") / 100 * this.image.getHeight()).intValue(), };
		Integer[] eyeRight = new Integer[] { new Double(eye_right.get("x") / 100 * this.image.getWidth()).intValue(),
				new Double(eye_right.get("y") / 100 * this.image.getHeight()).intValue(), };
		Coordinate2D ojoIzquierdo = new Coordinate2D(eyeLeft[0], eyeLeft[1]);
		Coordinate2D ojoDerecho = new Coordinate2D(eyeRight[0], eyeRight[1]);

		faceDetection.setLeftEye(ojoIzquierdo);
		faceDetection.setRightEye(ojoDerecho);
		return faceDetection;
	}

	@Override
	public void process(Map<String, Object> processResult) throws Exception {
		this.setServiceOutput(processResult.toString());

	//	this.setImage(parseImageData(processResult));
		// Get detected faces
		if (!processResult.containsKey("face"))	return ;
		List<Object> faces = (List<Object>) processResult.get("face");

		// For each face, recover eye positions
		for (Object face : faces) {
			Map<String, Object> attributes = (Map<String, Object>) face;
			this.image.addDetections(parseFace(attributes));
		}

	}

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

}
