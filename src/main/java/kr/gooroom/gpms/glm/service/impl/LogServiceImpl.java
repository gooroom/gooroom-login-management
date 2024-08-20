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

import jakarta.annotation.Resource;

import kr.gooroom.gpms.glm.service.UserClientUseHistoryVO;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.glm.service.LogService;
import kr.gooroom.gpms.glm.service.LoginHistoryVO;

@Service("logService")
public class LogServiceImpl implements LogService {

    @Resource(name = "logDAO")
    private LogDAO logDAO;

    @Override
    public int insertLoginHistory(LoginHistoryVO loginHistoryVO) {
	return logDAO.insertLoginHistory(loginHistoryVO);
    }

    @Override
    public int insertUserHistory(String loginId) {
	return logDAO.insertUserHistory(loginId);
    }

    @Override
    public int insertOrUpdateUserClientUseHistory(UserClientUseHistoryVO userClientUseHistoryVO) {
        return logDAO.insertOrUpdateClientUseHist(userClientUseHistoryVO);
    }

}
