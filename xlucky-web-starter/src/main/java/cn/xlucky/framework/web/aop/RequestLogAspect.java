/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */
package cn.xlucky.framework.web.aop;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日志记录
 */
@Slf4j
@Aspect
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(value = "log.request.enabled", havingValue = "true", matchIfMissing = true)
public class RequestLogAspect {
	private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
	public static final String COMMA = ",";

	/**
	 * AOP 环切 控制器 R 返回值
	 *
	 * @param point JoinPoint
	 * @return Object
	 * @throws Throwable 异常
	 */
	@Around(
			"(@within(org.springframework.stereotype.Controller) || " +
			"@within(org.springframework.web.bind.annotation.RestController))"
	)
	public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String requestUrl = Objects.requireNonNull(request).getRequestURI();
		String requestMethod = request.getMethod();

		// 构建成一条长 日志，避免并发下日志错乱
		StringBuilder beforeReqLog = new StringBuilder(300);
		// 日志参数
		List<Object> beforeReqArgs = new ArrayList<>();
		beforeReqLog.append("\n\n================  Request Start  ================\n");
		// 打印路由
		beforeReqLog.append("===> {}: {}");
		beforeReqArgs.add(requestMethod);
		beforeReqArgs.add(requestUrl);
		long startNs = 0;
		try {
			// 打印请求参数
			logIngArgs(point, beforeReqLog, beforeReqArgs);
			// 打印请求 headers
			logIngHeaders(request, beforeReqLog, beforeReqArgs);
			beforeReqLog.append("================   Request End   ================\n");

			// 打印执行时间
			startNs = System.nanoTime();
			log.info(beforeReqLog.toString(), beforeReqArgs.toArray());
		} catch (Exception e) {
			log.error("日志打印异常，url:{},信息:",requestUrl,e);
		}
		// aop 执行后的日志
		StringBuilder afterReqLog = new StringBuilder(200);
		// 日志参数
		List<Object> afterReqArgs = new ArrayList<>();
		afterReqLog.append("\n\n===============  Response Start  ================\n");
		try {
			Object result = point.proceed();
			// 打印返回结构体
			afterReqLog.append("===Result===  {}\n");
			try {
				afterReqArgs.add(JSON.toJSONString(result));
			} catch (Exception e) {
				log.error("日志打印异常，url:{},信息:",requestUrl,e);
			}
			return result;
		} finally {
			try {
				long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
				afterReqLog.append("<=== {}: {} ({} ms)\n");
				afterReqArgs.add(requestMethod);
				afterReqArgs.add(requestUrl);
				afterReqArgs.add(tookMs);
				afterReqLog.append("===============   Response End   ================\n");
				log.info(afterReqLog.toString(), afterReqArgs.toArray());
			} catch (Exception e) {
				log.error("日志打印异常，url:{},信息:",requestUrl,e);
			}
		}
	}

	/**
	 * 激励请求参数
	 *
	 * @param point         ProceedingJoinPoint
	 * @param beforeReqLog  StringBuilder
	 * @param beforeReqArgs beforeReqArgs
	 */
	public void logIngArgs(ProceedingJoinPoint point, StringBuilder beforeReqLog, List<Object> beforeReqArgs) {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		// 请求参数处理
		final Map<String, Object> paraMap = new HashMap<>(16);
		// 一次请求只能有一个 request body
		Object requestBodyValue = null;
		for (int i = 0; i < args.length; i++) {
			// 读取方法参数
			MethodParameter methodParam = getMethodParameter(method, i);
			// PathVariable 参数跳过
			PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
			if (pathVariable != null) {
				continue;
			}
			RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
			String parameterName = methodParam.getParameterName();
			Object value = args[i];
			// 如果是body的json则是对象
			if (requestBody != null) {
				requestBodyValue = value;
				continue;
			}
			// 处理 参数
			if (value instanceof HttpServletRequest) {
				paraMap.putAll(((HttpServletRequest) value).getParameterMap());
				continue;
			} else if (value instanceof WebRequest) {
				paraMap.putAll(((WebRequest) value).getParameterMap());
				continue;
			} else if (value instanceof HttpServletResponse) {
				continue;
			} else if (value instanceof MultipartFile) {
				MultipartFile multipartFile = (MultipartFile) value;
				String name = multipartFile.getName();
				String fileName = multipartFile.getOriginalFilename();
				paraMap.put(name, fileName);
				continue;
			} else if (value instanceof MultipartFile[]) {
				MultipartFile[] arr = (MultipartFile[]) value;
				if (arr.length == 0) {
					continue;
				}
				String name = arr[0].getName();
				StringBuilder sb = new StringBuilder(arr.length);
				for (MultipartFile multipartFile : arr) {
					sb.append(multipartFile.getOriginalFilename());
					sb.append(COMMA);
				}
				paraMap.put(name, removeSuffix(sb.toString(), COMMA));
				continue;
			} else if (value instanceof List) {
				List<?> list = (List<?>) value;
				AtomicBoolean isSkip = new AtomicBoolean(false);
				for (Object o : list) {
					if ("StandardMultipartFile".equalsIgnoreCase(o.getClass().getSimpleName())) {
						isSkip.set(true);
						break;
					}
				}
				if (isSkip.get()) {
					paraMap.put(parameterName, "此参数不能序列化为json");
					continue;
				}
			}
			// 参数名
			RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
			String paraName = parameterName;
			if (requestParam != null && requestParam.value() != null && requestParam.value() != "") {
				paraName = requestParam.value();
			}
			if (value == null) {
				paraMap.put(paraName, null);
			} else {
				paraMap.put(paraName, value);
			}
		}
		// 请求参数
		if (paraMap.isEmpty()) {
			beforeReqLog.append("\n");
		} else {
			beforeReqLog.append(" Parameters: {}\n");
			beforeReqArgs.add(JSON.toJSONString(paraMap));
		}
		if (requestBodyValue != null) {
			beforeReqLog.append("====Body=====  {}\n");
			beforeReqArgs.add(JSON.toJSONString(requestBodyValue));
		}
	}

	/**
	 * 记录请求头
	 *
	 * @param request       HttpServletRequest
	 * @param beforeReqLog  StringBuilder
	 * @param beforeReqArgs beforeReqArgs
	 */
	public void logIngHeaders(HttpServletRequest request,
							  StringBuilder beforeReqLog, List<Object> beforeReqArgs) {
		// 打印请求头
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = headers.nextElement();
			String headerValue = request.getHeader(headerName);
			beforeReqLog.append("===Headers===  {}: {}\n");
			beforeReqArgs.add(headerName);
			beforeReqArgs.add(headerValue);
		}
	}

	/**
	 * 去掉指定后缀
	 *
	 * @param str    字符串
	 * @param suffix 后缀
	 * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
	 */
	public static String removeSuffix(CharSequence str, CharSequence suffix) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(suffix)) {
			return "";
		}

		final String str2 = str.toString();
		if (str2.endsWith(suffix.toString())) {
			return sub(str2,0, str2.length() - suffix.length());
		}
		return str2;
	}


	/**
	 * 改进JDK subString<br>
	 * index从0开始计算，最后一个字符为-1<br>
	 * 如果from和to位置一样，返回 "" <br>
	 * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
	 * 如果经过修正的index中from大于to，则互换from和to example: <br>
	 * abcdefgh 2 3 =》 c <br>
	 * abcdefgh 2 -3 =》 cde <br>
	 *
	 * @param str       String
	 * @param fromIndex 开始的index（包括）
	 * @param toIndex   结束的index（不包括）
	 * @return 字串
	 */
	public static String sub(CharSequence str, int fromIndex, int toIndex) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		int len = str.length();

		if (fromIndex < 0) {
			fromIndex = len + fromIndex;
			if (fromIndex < 0) {
				fromIndex = 0;
			}
		} else if (fromIndex > len) {
			fromIndex = len;
		}

		if (toIndex < 0) {
			toIndex = len + toIndex;
			if (toIndex < 0) {
				toIndex = len;
			}
		} else if (toIndex > len) {
			toIndex = len;
		}

		if (toIndex < fromIndex) {
			int tmp = fromIndex;
			fromIndex = toIndex;
			toIndex = tmp;
		}

		if (fromIndex == toIndex) {
			return "";
		}

		return str.toString().substring(fromIndex, toIndex);
	}

	private static MethodParameter getMethodParameter(Method method, int parameterIndex) {
		MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
		methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
		return methodParameter;
	}

}
