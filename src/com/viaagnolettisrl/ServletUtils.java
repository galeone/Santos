package com.viaagnolettisrl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	public static Map<String, String> getParameters(HttpServletRequest request, String[] parameters) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(String param : parameters) {
			map.put(param, request.getParameter(param));
		}
		
		return map;
	}
	
	public static String topHTML = "<html><head><title>Servlet response</title><body>";
	public static String bottomHTML = "</body></html>";
}
