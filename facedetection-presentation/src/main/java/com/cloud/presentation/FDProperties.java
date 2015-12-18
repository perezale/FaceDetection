package com.cloud.presentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.cloud.common.NativeLibraries;


/**
 * Singleton para obtener parametros de configuración de archivo workflow.properties
 * @author lgi_unicen
 *
 */
public class FDProperties {
		

	//Singleton instance
	private static FDProperties instance = null;
	//Properties
	private static Properties prop;
	//Private Constants 
	private static String PROPFILENAME = "facedetection.properties";
		
	//Public Constants
	//DATASET
	public static final String DATASET = "DATASET";
	public static final String FACE_CASCADE_URL = "FACE_CASCADE_URL";
	public static final String EYE_CASCADE_URL = "EYE_CASCADE_URL";
	public static final String DOWNLOAD_PATH = "DOWNLOAD_PATH";
	public static final String F_ESCALAR_FACE = "F_ESCALAR_FACE";
	public static final String F_ESCALAR_EYE = "F_ESCALAR_EYE";
	public static final String MIN_NEIGHBORS_FACE = "MIN_NEIGHBORS_FACE";
	public static final String MIN_NEIGHBORS_EYE = "MIN_NEIGHBORS_EYE";
	public static final String MIN_PERCENT_SIZE_FACE = "MIN_PERCENT_SIZE_FACE";
	public static final String MAX_PERCENT_SIZE_FACE = "MAX_PERCENT_SIZE_FACE";
	public static final String MIN_PERCENT_SIZE_EYE = "MIN_PERCENT_SIZE_EYE";
	public static final String MAX_PERCENT_SIZE_EYE = "MAX_PERCENT_SIZE_EYE";
	public static final String BLUR_EFFECT = "BLUR_EFFECT";
	public static final String SHOW_FACE_OPENCV = "SHOW_FACE_OPENCV";
	public static final String RESULTADOS = "RESULTADOS";
	public static final String REMOTE_DATASET = "REMOTE_DATASET";
	public static final String REMOTE_IMAGE = "REMOTE_IMAGE";

	/**
	 * Constructor por defecto, protegido por clase Singleton
	 */
	protected FDProperties(){
		NativeLibraries.loadLibraries();
	}
	
	/**
	 * Devuelve la instancia del singleton
	 * @return Singleton WorfklowProperties
	 */
	public static FDProperties getInstance(){
		if(instance== null){
			instance = new FDProperties();
			try {
				init();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		return instance;
	}
	
	/**
	 * Lee el archivo de configuraciones
	 * @throws IOException
	 */
	private static void init() throws IOException{
		prop = new Properties();		
 
		InputStream inputStream = instance.getClass().getClassLoader().getResourceAsStream(PROPFILENAME);
 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + PROPFILENAME + "' not found in the classpath");
		}
 
	}
	
	/**
	 * Lee el parametro de configuración identificado por value
	 * @param value Nombre del parametro de configuración
	 * @return valor de configuración
	 */
	public String getPropValue(String value) {
		return prop.getProperty(value);
	}
	
}

