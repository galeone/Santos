package it.galeone_dev.servlet;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FilterSetTimeZoneToGMT implements Filter {

	public FilterSetTimeZoneToGMT() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
	    TimeZone savedZone = TimeZone.getDefault();
	    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	    chain.doFilter(request, response);
	    TimeZone.setDefault(savedZone);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
