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
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.glm.service.CtrlItemVO;

/**
 * 컨트롤 아이템 관련
 *
 * @author 이영민
 * @since 2017
 * @version 1.0
 *
 */

@Repository("ctrlItemDAO")
public class CtrlItemDAO extends SqlSessionMetaDAO {

    /**
     * 컨트롤 아이템 조회
     * 
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<CtrlItemVO> selectCtrlItem(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.selectList("ctrlItemDAO.selectCtrlItem", paramMap);
    }

    /**
     * 컨트롤 아이템 조회
     * 
     * @param clientId String
     * @return
     * @throws Exception
     */
    public List<CtrlItemVO> selectAvailableIpRule(String clientId) throws Exception {
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("clientId", clientId);
	return sqlSessionMeta.selectList("ctrlItemDAO.selectAvailableIpRule", map);
    }

}
