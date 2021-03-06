package com.cloud.remote;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cloud.common.IService;
import com.cloud.common.PerfRequestSyncInterceptor;
import com.cloud.dto.Image;
import com.cloud.dto.ProcessResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FaceRect implements IService {
	
	protected static Logger logger = Logger.getGlobal();
	
	private String serviceUrl;
	private String xMashapeKey;
	private HashMap<String, String> parameters;
	public static String DEFAULT_ATTRIBUTES = "true";

	public FaceRect(String serviceUrl, String xMashapeKey,String attributes) {
		this.serviceUrl = serviceUrl;
		this.xMashapeKey = xMashapeKey;
		parameters = new HashMap<String, String>();
		parameters.put("features", attributes);
	}

	public ProcessResult run(Image image) {
		ProcessResult resultado = new FaceRectProcessResult(this.serviceUrl);
		
		//TODO Completar tamaño y peso de la imagen en resultado
		resultado.setImage(image);		
	
		
			parameters.put("url",image.getRemoteUri());
	
			RestTemplate restTemplate = new RestTemplate();
			PerfRequestSyncInterceptor perfRequestSyncInterceptor = new PerfRequestSyncInterceptor();
			restTemplate.setInterceptors(Arrays.asList(new ClientHttpRequestInterceptor[]{perfRequestSyncInterceptor}));
		
		
		/*
		ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
		List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
		ris.add(ri);
		restTemplate.setInterceptors(ris);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		 */
		
		
		
		
		
		
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("X-Mashape-Key", xMashapeKey);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(serviceUrl);
		
		

		parameters.keySet().stream()
				.forEach(k -> builder.queryParam(k, parameters.get(k)));

		HttpEntity<String> entity = new HttpEntity<String>("parameters",
				headers);
				


		//Time measure
		
		
		//Actual processing	
		
		String bld = builder.build().toUriString();	
		resultado.setStartTime(System.nanoTime());
		ResponseEntity<String> resultString = restTemplate.exchange(bld, HttpMethod.GET, entity, String.class);
		//End time measure
		resultado.setEndTime(System.nanoTime());
		resultado.setInternalTime(perfRequestSyncInterceptor.totalTime);
		//System.out.println("taskInfo:"+sw.getTaskInfo() +" shortSumary: "+sw.shortSummary()+ "response_time=" + sw.prettyPrint());
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> result = new HashMap<>();
		try{
			String body = resultString.getBody();
			result = objectMapper.readValue(body, HashMap.class);
		}catch(Exception e){
			logger.log(Level.SEVERE, e.getMessage());
			result.put("Result", "Response not valid");
		}
		
		try {
			resultado.process(result);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return resultado;

	}

}
