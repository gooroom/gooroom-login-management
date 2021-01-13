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

import java.util.Map;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.glm.service.DesktopInfoVO;

/**
 * 토큰 DAO
 *
 * @author 이영민
 * @since 2017
 * @version 1.0
 *
 */

@Repository("desktopInfoDAO")
public class DesktopInfoDAO extends SqlSessionMetaDAO {

	/**
	 * DesktopInfo 반환
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public DesktopInfoVO selectDesktopInfo(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("desktopInfoDAO.selectDesktopInfoList", paramMap);
	}

	/**
	 * DesktopInfo 반환
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public DesktopInfoVO selectDesktopInfoByName(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("desktopInfoDAO.selectDesktopInfoByName", paramMap);
	}

	/**
	 * DesktopInfo 반환
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public DesktopInfoVO selectDesktopInfoByUserAndClient(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("desktopInfoDAO.selectDesktopInfoByUserAndClient", paramMap);
	}

}
