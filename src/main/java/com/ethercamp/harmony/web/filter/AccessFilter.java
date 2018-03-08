package com.ethercamp.harmony.web.filter;

import com.ethercamp.common.config.JedisService;
import com.ethercamp.common.exception.InvalidTokenBusinessException;
import com.ethercamp.common.util.CodeConstant;
import com.ethercamp.common.util.JedisUtil;
import com.ethercamp.common.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class AccessFilter implements Filter{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AccessFilter.class);
    /**
     * 封装，不需要过滤的list列表
     */
    private static List<String> patterns = new ArrayList<String>();




//    private JedisService jedisService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        //不存在的url 直接跳404
        patterns.add("/token/apply");
        patterns.add("/token/refresh");
        patterns.add("/");
        patterns.add("");
        patterns.add("/harmony/account/login");
        patterns.add("/harmony/account/register");

//        ServletContext sc = filterConfig.getServletContext();
//
//        AnnotationConfigEmbeddedWebApplicationContext cxt = (AnnotationConfigEmbeddedWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
//
//        if(cxt != null && cxt.getBean("jedisService") != null && jedisService == null)
//            jedisService = (JedisService) cxt.getBean("jedisService");
    }





    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String url = httpRequest.getRequestURI();
        logger.info("dofilter...info" + url);
        logger.debug("dofilter...debug" + url);
        logger.warn("dofilter...warn" + url);
        //过滤不验证的url
        boolean checkUrl = isInclude(url);

        //过滤资源文件
        String[] sufix = {".jsp", ".jpg", ".png", ".css", ".js", ".img", ".gif", "ico", ".woff", ".otf", ".eot", ".svg", ".ttf", ".html"};
        for (String s : sufix) {
            if (s.equals(".js")) {
                if (url.contains(s) && (!url.contains(".jsp") || !url.contains(".html"))) {
                    checkUrl = true;
                }
            } else {
                if (url.contains(s)) {
                    checkUrl = true;
                }
            }

        }

        if (checkUrl){
            chain.doFilter(httpRequest, httpResponse);
            return;
        } else {
                String token = httpRequest.getHeader("token");
                if(StringUtils.isEmpty(token)){
                    throw new InvalidTokenBusinessException("token参数丢失",CodeConstant.PARAM_LOST);
                }

                String appId = JedisUtil.getJedis().get(token);

                String redisToken = JedisUtil.getJedis().get(CodeConstant.TOKEN + appId);

                if(StringUtils.isEmpty(token) || StringUtils.isEmpty(appId)){
                    throw new InvalidTokenBusinessException("token或者appId参数丢失",CodeConstant.PARAM_LOST);
                }else if(StringUtils.isEmpty(redisToken)){
                    throw new InvalidTokenBusinessException("token不存在",CodeConstant.NOT_TOKEN);
                }else if(!redisToken.equals(token)){
                    throw new InvalidTokenBusinessException("异常token",CodeConstant.EXCEPTION_TOKEN);
                }else {
                    chain.doFilter(httpRequest, httpResponse);
                    return;
                }

            }

    }

    @Override
    public void destroy() {

    }


    /**
     * 是否需要过滤
     * @param url
     * @return
     */
    private boolean isInclude(String url) {
        return patterns.contains(url);
    }



}
