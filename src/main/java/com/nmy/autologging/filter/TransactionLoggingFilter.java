package com.nmy.autologging.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "TransactionLoggingFilter", urlPatterns="/")
@Configuration
public class TransactionLoggingFilter extends OncePerRequestFilter {

//    @Autowired
//    private TrxLoggingService trxLoggingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1. 캐싱
        ContentCachingRequestWrapper  requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper  responseWrapper = new ContentCachingResponseWrapper(response);

        //2. 비즈니스 로직 수행
        filterChain.doFilter(requestWrapper, responseWrapper);

        //3. 캐싱된 값 파싱
        //req
        Map<String, String> requestHeader = getRequestHeader(requestWrapper);
        Map<String, String> requestForm = getRequestForm(requestWrapper);
        String requestBody = IOUtils.toString(requestWrapper.getInputStream(), StandardCharsets.UTF_8);
        //reS
        Map<String, String> responseHeader = getRequestHeader(requestWrapper);
        String responseBody = getResponseBody(responseWrapper);

        log.debug("requestHeader::: {}", requestHeader);
        log.debug("requestForm::: {}", requestForm);
        log.debug("requestBody::: {}", requestBody);
        log.debug("responseHeader::: {}", responseHeader);
        log.debug("responseBody::: {}", responseBody);


        //4. 캐싱된 값 콘솔 로깅
//        LoggingRequest loggingRequest = trxLoggingService.logRequest(Pair.of("client", "BOX"), HttpMethod.POST, String.valueOf(requestWrapper.getRequestURL()), requestHeader, requestForm, requestBody);
//        trxLoggingService.logResponse(loggingRequest, responseHeader, responseBody);

        //5. 캐싱된 값 DB 로깅
//        trxLoggingService.dbLoggingRequest(getRequestHeader(requestWrapper), requestForm, requestBody);
//        trxLoggingService.dbLoggingResponse(responseHeader, responseBody);
    }


    /**
     * request 에 담긴 form 데이터를 추출
     *
     * @param request
     * @return
     */
    private static Map<String, String> getRequestForm(HttpServletRequest request) {
        Enumeration<String> params = request.getParameterNames();
        MultiValueMap<String, String> reqparams = new LinkedMultiValueMap<>();
        //각각의 키값을 캐싱
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            reqparams.put(replaceParam, Collections.singletonList(request.getParameter(param)));
        }
        return reqparams.toSingleValueMap();
    }

    /**
     * request 에 담긴 Header 데이터 추출
     *
     * @param request
     * @return
     */
    private static Map<String, String> getRequestHeader(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();

        Enumeration headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = (String) headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse();
            }
        }
        return null == payload ? " - " : payload;
    }

    /**
     * response 에 담긴 Header 데이터 추출
     *
     * @param response
     * @return
     */
    private static Map<String, String> getResponseHeader(HttpServletResponse response) {
        Collection<String> params = response.getHeaderNames();
        MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();
        //각각의 키값을 캐싱
        while (!params.isEmpty()) {
            String header = params.stream().findFirst().orElse("");
            params.remove(header);
            String replaceParam = header.replaceAll("\\.", "-");
            reqHeader.put(replaceParam, Collections.singletonList(response.getHeader(header)));
        }
        return reqHeader.toSingleValueMap();
    }

}
