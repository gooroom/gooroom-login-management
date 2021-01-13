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

package kr.gooroom.gpms.glm.service;

import java.util.List;
import java.util.Map;

/**
 * 토큰 관리 서비스
 *
 * @author ymlee
 * @since 2017
 * @version 1.0
 *
 *          <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------       --------    ---------------------------
 *   2017.
 *          </pre>
 */

public interface TokenService {

    /**
     * 토큰 발급 정보 확인
     * 
     * @param clientId
     * @param token
     * @param statusCd
     *            (optional) : loginToken, otpToken 구분
     * @return
     * @throws Exception
     */
    public UserTokenVO selectTokenByTokenId(String clientId, String token, String statusCd) throws Exception;

    /**
     * 클라이언트 토큰 발급 정보 확인
     * 
     * @param userId
     * @param clientId
     * @param statusCd
     *            (optional) : loginToken, otpToken 구분
     * @return
     * @throws Exception
     */
    public List<?> selectTokenListByClientId(String userId, String clientId, String statusCd) throws Exception;

    /**
     * 사용자 토큰 목록 반환 (사용자가 로그인 된 모든 단말기)
     * 
     * @param userId
     * @param statusCd
     *            (optional) : loginToken, otpToken 구분
     * @return
     * @throws Exception
     */
    public List<?> selectTokenListByUserId(String userId, String statusCd) throws Exception;

    /**
     * 토큰 설정
     * 
     * @param tokenList
     * @return
     * @throws Exception
     */
    public int insertToken(Map<String, Object> tokenList) throws Exception;

    /**
     * 토큰 정보 갱신
     * 
     * @param tokenVO
     * @return
     * @throws Exception
     */
    public int updateToken(TokenVO tokenVO) throws Exception;

    /**
     * OTP 토큰정보 상태 정보 업데이트
     * 토큰 유효 시 auth_cnt = auth_cnt + 1
     * 토큰 만료 시 status_cd = 만료
     * 
     * @param otpToken
     * @param clientId
     * @return
     * @throws Exception
     */
    public int updateOtpTokenInfo(String otpToken, String clientId) throws Exception;

    /**
     * 토큰 폐기
     * 
     * @param userId
     * @param token
     * @return
     * @throws Exception
     */
    public int deleteTokenByTokenId(String userId, String token) throws Exception;

    /**
     * 토큰 폐기
     * 
     * @param userId
     * @param clientId
     * @return
     * @throws Exception
     */
    public int deleteTokenByClientId(String userId, String clientId) throws Exception;
    
}
