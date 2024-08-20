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

import kr.gooroom.gpms.glm.service.UserClientUseHistoryVO;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.glm.service.LoginHistoryVO;

/**
 * 인증관련
 *
 * @author 이영민
 * @since 2017
 * @version 1.0
 *
 */

@Repository("logDAO")
public class LogDAO extends SqlSessionMetaDAO {

    /**
     * 로그인 이력 기록
     *
     * @param loginHistoryVO
     * @return
     */
    public int insertLoginHistory(LoginHistoryVO loginHistoryVO) {
	return sqlSessionMeta.insert("logDAO.insertLoginHistory", loginHistoryVO);
    }

    /**
     * 사용자 이력 기록
     * 
     * @param loginId
     * @return
     */
    public int insertUserHistory(String loginId) {
	return sqlSessionMeta.insert("logDAO.insertUserHistory", loginId);
    }


    /**
     * 사용자 단말 사용 이력 추가
     *
     * @param userClientUseHistoryVO
     * @return
     */
    public int insertOrUpdateClientUseHist(UserClientUseHistoryVO userClientUseHistoryVO) {
        return sqlSessionMeta.insert("logDAO.insertOrUpdateClientUseHist", userClientUseHistoryVO);
    }

}
