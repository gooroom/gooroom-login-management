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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.glm.service.DesktopInfoVO;

public class DesktopUtils {

    private final static Logger logger = LoggerFactory.getLogger(DesktopUtils.class);

    public static String getJsonForDesktop(DesktopInfoVO desktopInfoVO) {

	String jsonStr = "";

	try {
	    ObjectMapper mapper = new ObjectMapper();

	    mapper.setSerializationInclusion(Include.NON_NULL);

	    jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(desktopInfoVO);
	} catch (Exception ex) {
	    logger.error("error in getJsonForDesktop : {}", ex.toString());
	}

	return jsonStr;
    }

}
