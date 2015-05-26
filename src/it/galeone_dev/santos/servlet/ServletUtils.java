package it.galeone_dev.santos.servlet;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	public static Map<String, String> getParameters(HttpServletRequest request, String[] parameters, String [] optionals) throws InvalidParameterException {
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(String param : parameters) {
			String value = request.getParameter(param);
			if(value == null || value.trim().isEmpty()) {
			    throw new InvalidParameterException("Completare tutti i campi obbligatori");
			}
			map.put(param, value.trim() );
		}
		
	    for(String param : optionals) {
	        String value = request.getParameter(param);
	        map.put(param, value == null ? null : value.trim());
	    }
		
		return map;
	}
	
	public static Map<String, String> getParameters(HttpServletRequest request, String[] parameters) throws InvalidParameterException {
	    return getParameters(request, parameters, new String[0]);
	}
}
