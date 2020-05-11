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

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.glm.service.TokenService;
import kr.gooroom.gpms.glm.service.TokenVO;
import kr.gooroom.gpms.glm.service.UserTokenVO;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

	@Resource(name = "tokenDAO")
	private TokenDAO tokenDAO;

	@Override
	public UserTokenVO selectTokenByTokenId(String clientId, String token, String statusCd) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientId", clientId);
		paramMap.put("token", token);
		if (statusCd != null)
			paramMap.put("statusCd", statusCd);

		return tokenDAO.selectTokenInfo(paramMap);
	}

	@Override
	public List<?> selectTokenListByClientId(String userId, String clientId, String statusCd) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("clientId", clientId);
		if (statusCd != null)
			paramMap.put("statusCd", statusCd);

		return tokenDAO.selectTokenList(paramMap);
	}

	@Override
	public List<?> selectTokenListByUserId(String userId, String statusCd) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		if (statusCd != null)
			paramMap.put("statusCd", statusCd);

		return tokenDAO.selectTokenList(paramMap);
	}

	@Override
	public int insertToken(Map<String, Object> tokenList) throws Exception {

		return tokenDAO.insertTokenInfo(tokenList);
	}

	@Override
	public int updateToken(TokenVO tokenVO) throws Exception {
		return 0;
	}

	@Override
	public int updateOtpTokenInfo(String otpToken, String gcspId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("token", otpToken);
		paramMap.put("statusCd", Constant.TOKEN_OTP_STATUS_CODE_EXPIRE);
		paramMap.put("currntStatusCd", Constant.TOKEN_OTP_STATUS_CODE_VALID);
		if (gcspId != null)
			paramMap.put("gcspId", gcspId);

		return tokenDAO.updateOtpTokenInfo(paramMap);
	}

	@Override
	public int deleteTokenByTokenId(String userId, String token) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("token", token);

		return tokenDAO.deleteTokenByTokenId(paramMap);
	}

	@Override
	public int deleteTokenByClientId(String userId, String clientId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("clientId", clientId);

		return tokenDAO.deleteToken(paramMap);
	}
}
