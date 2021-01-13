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
 * GLM 인증 서비스
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

public interface AuthService {

    /**
     * 사용자 체크 (로그인 ID, PW)
     * 
     * @param loginId
     * @param userPw
     * @param isLoginService
     * @param clientId
     * @return
     * @throws Exception
     */
    public UserVO checkLoginByUserId(String loginId, String userPw, boolean isLoginService, String clientId) throws Exception;

    /**
     * loginToken 인증
     * 
     * @param String
     *            clientId
     * @param String
     *            loginToken
     * @return
     * @throws Exception
     */
    public UserTokenVO checkLoginByLoginToken(String clientId, String loginToken) throws Exception;

    /**
     * 사용자 인증 (OTP 토큰)
     * 
     * @param String
     *            otpToken
     * @param String
     *            gcspId
     * @return
     * @throws Exception
     */
    public UserVO checkLoginByOtpToken(String otpToken, String gcspId) throws Exception;

    /**
     * 사용자 목록 정보
     * 
     * @param userList
     * @param gcspId
     * @return
     * @throws Exception
     */
    public List<?> getUserList(List<String> userList, String gcspId) throws Exception;

    /**
     * 로그 아웃
     * 
     * @param String
     *            userId
     * @param String
     *            clientId
     * @param String
     *            loginToken
     * @return
     * @throws Exception
     */
    public int logout(String userId, String clientId, String loginToken) throws Exception;

    /**
     * PAM 용 사용자 토큰(login, otp) 및 데스크탑 환경 생성
     * 
     * @param userVO
     * @param clientId
     * @param clientIp
     * @return
     * @throws Exception
     */
    public Map<String, Object> setPamTokenAndDesktopInfo(UserVO userVO, String clientId, String clientIp)
	    throws Exception;

    /**
     * GCSP 용 접속 정보 체크
     * 
     * @param clientId
     * @param clientIp
     * @return
     * @throws Exception
     */
    public GcspVO checkGcspInfo(String clientId, String clientIp) throws Exception;

    /**
     * GCSP 용 로그인 생성
     * 
     * @param userVO
     * @param clientId
     * @param clientIp
     * @param otpToken
     * @return
     */
    public Map<String, Object> setGcspAuth(UserVO userVO, String clientId, String clientIp, String otpToken);

    /**
     * 사용자 비밀번호 변경
     * 
     * @param loginId
     * @param newPassword
     * @return
     * @throws Exception
     */
    public int changePassword(String loginId, String newPassword) throws Exception;

    /**
     * 사용자 이름 변경
     * 
     * @param loginId
     * @param newUserNm
     * @return
     * @throws Exception
     */
    public int changeUserNm(String loginId, String newUserNm) throws Exception;

    /**
     * 사용자 로그인 시간 갱신
     * 
     * @param loginId
     * @param userPw
     * @param isFirstLogin
     * @param clientId
     * @return
     * @throws Exception
     */
    public int updateLoginDateTime(String loginId, String userPw, boolean isFirstLogin, String clientId) throws Exception;

    /**
     * Passphrase 변경 또는 등록
     * 
     * @param loginId
     * @param clientId
     * @param passphrase
     * @return
     * @throws Exception
     */
    public int changePassphrase(String loginId, String clientId, String passphrase) throws Exception;
    
    /**
     * 접속한 계정으로 온라인중인 단말 리스트 조회 
     * 
     * @param loginId
     * @return
     * @throws Exception
     */
    public List<?> getOnlineClientsByUser(String loginId) throws Exception;

    /**
     * 비밀번호 규정 가져오기 
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    public String getPasswordRule(String siteId) throws Exception;

    /**
     * 중복 로그인 가능 여부 조회 
     * 
     * @param siteId
     * @return
     * @throws Exception
     */
    public int isEnableDuplicateLogin(String siteId) throws Exception;

}

