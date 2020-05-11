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

package kr.gooroom.gpms.glm.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.gooroom.gpms.glm.service.DesktopInfoService;
import kr.gooroom.gpms.glm.service.DesktopInfoVO;

@Service("desktopInfoService")
public class DesktopInfoServiceImpl implements DesktopInfoService {

	@Resource(name = "desktopInfoDAO")
	private DesktopInfoDAO desktopInfoDAO;

	@Override
	public DesktopInfoVO getDesktopInfo(String confId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("confId", confId);

		return desktopInfoDAO.selectDesktopInfo(paramMap);
	}

	@Override
	public DesktopInfoVO getDesktopInfoByName(String confName) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("confName", confName);

		return desktopInfoDAO.selectDesktopInfoByName(paramMap);
	}

	@Override
	public DesktopInfoVO getDesktopInfoByUserAndClient(String userId, String clientId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("clientId", clientId);

		return desktopInfoDAO.selectDesktopInfoByUserAndClient(paramMap);
	}

}
