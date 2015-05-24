package config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Susanne Otto
 */
public class BaseFilter implements Filter {

    /**
     * Die Konfiguration des Filters
     */
    protected FilterConfig filterConfig;

    /**
     * Speichert die Konfiguration des Filters
     *
     * @param aConfig
     */
    @Override
    public void init(FilterConfig aConfig) {
        filterConfig = aConfig;
    }

    /**
     * Leitet Request und Response weiter
     *
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException, javax.servlet.ServletException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // Leitet den Request ohne Manipulationen weiter
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    /* Der Filter belegt prinzipiell keine Ressourcen */

    /**
     *
     */
    @Override
    public void destroy() {
        filterConfig = null;
    }

}

