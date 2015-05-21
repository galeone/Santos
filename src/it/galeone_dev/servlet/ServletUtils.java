package it.galeone_dev.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	public static Map<String, String> getParameters(HttpServletRequest request, String[] parameters) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(String param : parameters) {
			String value = request.getParameter(param);
			map.put(param, value != null ? value.trim() : value );
		}
		
		return map;
	}
}
