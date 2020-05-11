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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.glm.service.TokenVO;
import kr.gooroom.gpms.glm.service.UserTokenVO;

/**
 * 토큰 관련 DAO
 *
 * @author 이영민
 * @since 2017
 * @version 1.0
 *
 */

@Repository("tokenDAO")
public class TokenDAO extends SqlSessionMetaDAO {

    /**
     * 사용자 토큰 목록 반환
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    public List<?> selectTokenList(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.selectList("tokenDAO.selectTokenList", paramMap);
    }

    /**
     * 토큰 정보 확인
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    public UserTokenVO selectTokenInfo(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.selectOne("tokenDAO.selectTokenInfo", paramMap);
    }

    /**
     * 토큰 생성
     *
     * @param tokenList
     * @return
     * @throws Exception
     */
    public int insertTokenInfo(Map<?, ?> tokenList) throws Exception {
	return sqlSessionMeta.insert("tokenDAO.insertTokenInfo", tokenList);
    }

    /**
     * 토큰정보 업데이트
     *
     * @param tokenVO
     * @return
     * @throws Exception
     */
    public int updateTokenInfo(TokenVO tokenVO) throws Exception {
	return sqlSessionMeta.update("tokenDAO.updateTokenInfo", tokenVO);
    }

    /**
     * OTP 토큰정보 상태 정보 업데이트
     * 토큰 유효 시 auth_cnt = auth_cnt + 1
     * 토큰 만료 시 status_cd = 만료
     * 
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int updateOtpTokenInfo(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.update("tokenDAO.updateOtpTokenInfo", paramMap);
    }

    /**
     * 토큰 폐기
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int deleteToken(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.delete("tokenDAO.deleteToken", paramMap);
    }

    /**
     * 토큰 폐기 by Token id
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    public int deleteTokenByTokenId(Map<?, ?> paramMap) throws Exception {
	return sqlSessionMeta.delete("tokenDAO.deleteTokenByTokenId", paramMap);
    }
}
