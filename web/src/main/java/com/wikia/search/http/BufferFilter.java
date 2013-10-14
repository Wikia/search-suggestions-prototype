package com.wikia.search.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class BufferFilter implements Filter {
    private static Logger  logger = LoggerFactory.getLogger(BufferFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        OutputStream out = response.getOutputStream();
        GenericResponseWrapper wrapper = new GenericResponseWrapper(httpServletResponse);
        chain.doFilter(request,wrapper);
        if ( httpServletResponse.isCommitted() ) {
            logger.warn("Trying to replace content of already committed response.");
        }
        response.setContentLength(wrapper.getData().length);
        out.write(wrapper.getData());
        out.close();
    }

    @Override
    public void destroy() {
    }

    static public class GenericResponseWrapper extends HttpServletResponseWrapper {
        private ByteArrayOutputStream output;

        public GenericResponseWrapper(HttpServletResponse response) {
            super(response);
            output=new ByteArrayOutputStream();
        }

        private byte[] getData() {
            return output.toByteArray();
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new FilterServletOutputStream(output);
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(getOutputStream(),true);
        }

        @Override
        public void flushBuffer() throws IOException {
            // do nothing
            // prevent buffers from being flushed.
        }
    }
    public static class FilterServletOutputStream extends ServletOutputStream {

        private DataOutputStream stream;

        public FilterServletOutputStream(OutputStream output) {
            stream = new DataOutputStream(output);
        }

        public void write(int b) throws IOException  {
            stream.write(b);
        }

        public void write(byte[] b) throws IOException  {
            stream.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException  {
            stream.write(b,off,len);
        }

    }
}