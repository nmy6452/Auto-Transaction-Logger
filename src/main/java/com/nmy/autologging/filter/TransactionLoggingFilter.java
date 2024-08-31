package com.nmy.autologging.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmy.autologging.component.transactionREQ;
import com.nmy.autologging.component.Transaction;
import com.nmy.autologging.component.transactionRES;
import com.nmy.autologging.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.ObjectProvider;
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
    private AppConfig config;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
//    private ObjectProvider<Transaction> transactionProvider;
    private Transaction transaction;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uuid = UUID.randomUUID().toString();
        MDC.put("uuid", uuid);

        //1. 캐싱
        ContentCachingRequestWrapper  requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper  responseWrapper = new ContentCachingResponseWrapper(response);

        //2. 비즈니스 로직 수행
        filterChain.doFilter(requestWrapper, responseWrapper);

        //3. 캐싱된 값 파싱
        //req
        transactionREQ tranRequest = new transactionREQ();
        Map<String, String> requestHeader = getRequestHeader(requestWrapper);
        Map<String, String> requestForm = getRequestForm(requestWrapper);
        String requestBody = IOUtils.toString(requestWrapper.getInputStream(), StandardCharsets.UTF_8);

        tranRequest.setHost(request.getRemoteHost());
        tranRequest.setMethod(request.getMethod());
        tranRequest.setPath(request.getServletPath());
        tranRequest.setQuery(request.getQueryString());
        tranRequest.setForm(requestForm);
        tranRequest.setRequestBody(requestBody);
        tranRequest.setHeader(requestHeader);
        transaction.setRequest(tranRequest);

        //reS
        transactionRES tranResponse = new transactionRES();
        Map<String, String> responseHeader = getRequestHeader(requestWrapper);
        String responseBody = getResponseBody(responseWrapper);

        tranResponse.setResponseBody(responseBody);
        tranResponse.setHeader(responseHeader);
        transaction.setResponse(tranResponse);


        if (config.isLogConsole()){
            transaction.consoleLog();
        }
        if (config.isLogDb()){
            transaction.dbLog();
        }

        MDC.clear();


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

    /**
     * response 에 담긴 Body 데이터 추출
     *
     * @param response
     * @return
     */
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
