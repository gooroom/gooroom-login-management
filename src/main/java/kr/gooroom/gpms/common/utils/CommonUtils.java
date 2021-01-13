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

package kr.gooroom.gpms.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.gooroom.gpms.common.service.CommonHeaderVO;

/**
 * @Class Name : CommonUtils.java
 * @Description : CommonUtils Class
 * @Modification Information 2017.05.08 LDH 최초생성
 *
 * @since 2017.05.08
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by Hancom All right reserved.
 */
public class CommonUtils {

	/**
	 * 클라이언트 IP 정보 추출
	 * 
	 * @return
	 */
	public static String getClientIP() {

		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String ip = req.getHeader("X-FORWARDED-FOR");
		if (ip == null || ip.trim().length() == 0) {
			ip = req.getRemoteAddr();
		} else {
			ip = ip.substring(ip.lastIndexOf(",")).trim();
		}
		return ip;
	}

	/**
	 * 현재 시간 + add시간을 MySQL DateTime 타입으로 반환
	 * 
	 * @param addMinute
	 * @return
	 */
	public static Date getAddMinMySqlDateTime(int addMinute) {

		Date now = new Date();

		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdformat = new SimpleDateFormat(pattern);

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		cal.add(Calendar.MINUTE, addMinute);

		String mysqlDateString = sdformat.format(cal.getTime());
		Date mysqlDate = null;

		try {
			mysqlDate = sdformat.parse(mysqlDateString);
		} catch (ParseException e) {
		}

		return mysqlDate;
	}

	/**
	 * 현재 시간 + add시간을 MySQL String 타입으로 반환
	 * 
	 * @param addMinute
	 * @return
	 */
	public static String getAddMinMySqlString(int addMinute) {

		Date now = new Date();

		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdformat = new SimpleDateFormat(pattern);

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		cal.add(Calendar.MINUTE, addMinute);

		String mysqlDateString = sdformat.format(cal.getTime());

		return mysqlDateString;
	}

	/**
	 * Date to yyyy-MM-dd String
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateToYMD(Date date) {

		String dateString = "";

		if (date != null) {

			String pattern = "yyyy-MM-dd";
			SimpleDateFormat sdformat = new SimpleDateFormat(pattern);

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			dateString = sdformat.format(cal.getTime());
		}

		return dateString;
	}

	/**
	 * 공통 응답 처리
	 * 
	 * @param rspStatus
	 * @param rspCode
	 * @param message
	 * @param commonHeaderVO
	 * @param resultMap
	 * @param model
	 * @return
	 */
	public static Map<String, Object> createResult(String rspStatus, String rspCode, String message,
			CommonHeaderVO commonHeaderVO, Map<String, Object> resultMap, ModelMap model) {

		Map<String, Object> result = new HashMap<String, Object>();

		commonHeaderVO.setResultInfo(rspStatus, rspCode, message);
		result.put(Constant.COMMON_HEADER, commonHeaderVO);

		if (resultMap != null) {
			result.put(Constant.COMMON_BODY, resultMap);
		}

		return result;
	}
}
