/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.common.utils.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;

public class AccessCheckInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AccessCheckInterceptor.class);

    private static final String AuthURIList[] = { Constant.AUTH_PAM_API_PREFIX, Constant.AUTH_GCSP_API_PREFIX };

    private boolean isAuthURI(String reqURI) {
	boolean checkURI = false;
	for (int i = 0; i < AuthURIList.length; i++) {
	    if (reqURI.startsWith(AuthURIList[i])) {
		checkURI = true;
		break;
	    }
	}
	return checkURI;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	    throws Exception {

	String reqURI = request.getRequestURI();

	if (isAuthURI(reqURI)) {
	    if (StringUtils.pathEquals(reqURI, Constant.AUTH_PAM_API)
		    || StringUtils.pathEquals(reqURI, Constant.AUTH_GCSP_API)) {

		logger.debug("AccessCheckInterceptor Request AUTH API");
	    } else {

		logger.debug("AccessCheckInterceptor Request COMMON API");
	    }
	} else {
	    logger.debug("AccessCheckInterceptor Invalid Request API");

	    throw new Exception(MessageSourceHelper.getMessage("common.msg.fail"));
	}

	return true;
	}

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
	    ModelAndView modelAndView) throws Exception {

	logger.debug("LoginCheckInterceptor postHandle");
    }

}
