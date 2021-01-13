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
import kr.gooroom.gpms.glm.service.DupClientVO;
import kr.gooroom.gpms.glm.service.GcspVO;
import kr.gooroom.gpms.glm.service.UserListVO;
import kr.gooroom.gpms.glm.service.UserVO;

/**
 * 인증관련
 *
 * @author 이영민
 * @since 2017
 * @version 1.0
 *
 */

@Repository("authDAO")
public class AuthDAO extends SqlSessionMetaDAO {

	/**
	 * 사용자 조회 - ID
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public UserVO selectUserInfo(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectUserInfo", paramMap);
	}

	/**
	 * 사용자 목록 조회
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<?> selectUserList(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectList("authDAO.selectUserList", paramMap);
	}

	/**
	 * 사용자 인증 - ID/PW
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public UserListVO selectCheckAuth(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectCheckAuth", paramMap);
	}

	/**
	 * 사용자 인증 가능 횟수 조회 update and select
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public long updateLoginTrial(Map<?, ?> paramMap) throws Exception {
		
		// update
		sqlSessionMeta.update("authDAO.updateLoginTrial", paramMap);
		// select
		return sqlSessionMeta.selectOne("authDAO.selectLoginTrial", paramMap);
	}
	
	/**
	 * 전체 사용자 인증 가능 횟수 초기화
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public long updateLoginTrialInit(Map<?, ?> paramMap) throws Exception {
		
		// update
		return sqlSessionMeta.update("authDAO.updateLoginTrialInit", paramMap);
	}
	
	/**
	 * 개별 사용자 인증 가능 횟수 초기화
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public long updateUserLoginTrialInit(Map<?, ?> paramMap) throws Exception {
		
		// update
		return sqlSessionMeta.update("authDAO.updateUserLoginTrialInit", paramMap);
	}

	/**
	 * 사용자 인증 - otpToken
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public UserVO selectCheckAuthByOtpToken(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectCheckAuthByOtpToken", paramMap);
	}

	/**
	 * Gcsp 인증
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public GcspVO selectGcspInfo(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectGcspInfo", paramMap);
	}

	/**
	 * 사용자 패스워드 변경
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updatePassword(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.update("authDAO.updatePassword", paramMap);
	}

	/**
	 * 사용자 이름 변경
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updateUserNm(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.update("authDAO.updateUserNm", paramMap);
	}

	/**
	 * 로그인 시각 변경
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int updateLoginDt(Map<?, ?> paramMap) {
		return sqlSessionMeta.update("authDAO.updateLoginDt", paramMap);
	}

	/**
	 * select Passphrase
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String selectPassphrase(Map<?, ?> paramMap) {
		return sqlSessionMeta.selectOne("authDAO.selectPassphrase", paramMap);
	}

	/**
	 * change passphrase (or insert)
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public int insertOrUpdatePassphrase(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.insert("authDAO.insertOrUpdatePassphrase", paramMap);
	}
	
	/**
	 * 사용자계정으로 접속된 단말 정보
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public List<DupClientVO> selectOnlineClientsByUser(Map<?, ?> paramMap) throws Exception {
		return sqlSessionMeta.selectList("authDAO.selectOnlineClientsByUser", paramMap);
	}

	/**
	 * 사용자계정으로 접속된 단말 정보
	 * 
	 * @param String siteId
	 * @return
	 * @throws Exception
	 */
	public String selectPasswordRule(String siteId) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectPasswordRule", siteId);
	}

	/**
	 * 패스워드 시도 초기화 시간 조회
	 * 
	 * @param String siteId
	 * @return
	 * @throws Exception
	 */
	public int selectLockTimeValue(String siteId) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectLockTimeValue", siteId);
	}

	/**
	 * 중복 로그인 가능 여부
	 * 
	 * @param String siteId
	 * @return
	 * @throws Exception
	 */
	public int selectIsEnableDuplicateLogin(String siteId) throws Exception {
		return sqlSessionMeta.selectOne("authDAO.selectIsEnableDuplicateLogin", siteId);
	}

}
