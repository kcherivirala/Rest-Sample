package com.fbr.rest.filter;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class JsonpCallbackFilter implements Filter {

    private static Log log = LogFactory.getLog(JsonpCallbackFilter.class);

    public void init(FilterConfig fConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        @SuppressWarnings("unchecked")
        Map<String, String[]> params = httpRequest.getParameterMap();

        OutputStream out = httpResponse.getOutputStream();
        GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);

        chain.doFilter(request, wrapper);

        if (params.containsKey("callback")) {
            if (log.isDebugEnabled())
                log.debug("Wrapping response with JSONP callback '" + params.get("callback")[0] + "'");

            out.write(new String(params.get("callback")[0] + "(").getBytes());
            out.write(wrapper.getData());
            out.write(new String(");").getBytes());

        } else {
            out.write(wrapper.getData());
        }
        wrapper.setContentType("application/javascript;charset=UTF-8");
        out.close();
    }

    public void destroy() {
    }
}

