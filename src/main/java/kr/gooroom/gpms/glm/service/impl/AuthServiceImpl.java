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

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.DesktopUtils;
import kr.gooroom.gpms.common.utils.Token;
import kr.gooroom.gpms.glm.service.AuthService;
import kr.gooroom.gpms.glm.service.CtrlItemVO;
import kr.gooroom.gpms.glm.service.DesktopAppInfoVO;
import kr.gooroom.gpms.glm.service.DesktopAppVO;
import kr.gooroom.gpms.glm.service.DesktopInfoService;
import kr.gooroom.gpms.glm.service.DesktopInfoVO;
import kr.gooroom.gpms.glm.service.DupClientVO;
import kr.gooroom.gpms.glm.service.GcspLoginInfoVO;
import kr.gooroom.gpms.glm.service.GcspVO;
import kr.gooroom.gpms.glm.service.LogService;
import kr.gooroom.gpms.glm.service.LoginHistoryVO;
import kr.gooroom.gpms.glm.service.PamLoginInfoVO;
import kr.gooroom.gpms.glm.service.TokenService;
import kr.gooroom.gpms.glm.service.TokenVO;
import kr.gooroom.gpms.glm.service.UserListVO;
import kr.gooroom.gpms.glm.service.UserTokenVO;
import kr.gooroom.gpms.glm.service.UserVO;

@Service("authService")
public class AuthServiceImpl implements AuthService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "authDAO")
	private AuthDAO authDAO;

	@Resource(name = "ctrlItemDAO")
	private CtrlItemDAO ctrlItemDAO;

	@Resource(name = "tokenService")
	private TokenService tokenService;

	@Resource(name = "desktopInfoService")
	private DesktopInfoService desktopInfoService;

	@Resource(name = "logService")
	private LogService logService;

	private Token tokenFactory = new Token(Constant.TOKEN_SALT, Constant.TOKEN_ISSUER, Constant.TOKEN_INTERVAL);

	@Override
	@Transactional
	public UserVO checkLoginByUserId(String loginId, String userPw, boolean isLoginService, String clientId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);

		// Check User Exist
		UserVO userVO = authDAO.selectUserInfo(paramMap);
		// user no exist
		if (userVO == null || StringUtils.isEmpty(userVO.getUserId())) {
			return null;
		}

		// check user status normal or delete
		if(userVO.getStatus().equals(Constant.COMMON_STATUS_DELETE)) {
			userVO.setCheckCd(Constant.RSP_CODE_AUTH_FAIL_DELETE_USER);
			return userVO;
		}

		// check user auth (password)
		paramMap.put("userPasswd", userPw);
		paramMap.put("status", Constant.COMMON_STATUS_OK);
		UserListVO userAuthVO = authDAO.selectCheckAuth(paramMap);
		if (userAuthVO == null || StringUtils.isEmpty(userAuthVO.getLoginId())) {
			
			// decrease login trial count and select count
			long trialCount = authDAO.updateLoginTrial(paramMap);
			
			userVO.setRemainLoginTrial(String.valueOf((trialCount < 1) ? 0 : trialCount));
			userVO.setCheckCd(Constant.RSP_CODE_NOT_AUTH);
			return userVO;
		} else {
			// check user expired.
			if(userVO.getExpireRemainDay() != null) {
				try {
					if(Long.parseLong(userVO.getExpireRemainDay()) < 0) {
						// check user expired?
						userVO.setCheckCd(Constant.RSP_CODE_USEREXPIRE);
						return userVO;
					}
				} catch(Exception eex) {
				}
			}
			
			// check dept expired.
			if(userVO.getDeptExpireRemainDay() != null) {
				try {
					if(Long.parseLong(userVO.getDeptExpireRemainDay()) < 0) {
						// check user expired?
						userVO.setCheckCd(Constant.RSP_CODE_DEPTEXPIRE);
						return userVO;
					}
				} catch(Exception eex) {
				}
			}
			
			// check password expired.
			// don't setup error code - request from gooroom terminal by cjheo 
//			if("Y".equalsIgnoreCase(userVO.getIsPasswdExpire())) {
//				userVO.setCheckCd(Constant.RSP_CODE_PASSWORDEXPIRE);
//				return userVO;
//			}

			if(userVO.getLoginTrial() < 1) {
				// check login trial count
				int loginElapsedTime = authDAO.selectLockTimeValue("siteCd");
				if(loginElapsedTime == 0) {
					userVO.setCheckCd(Constant.RSP_CODE_LOGINTRIALEXPIRE);
					return userVO;
				} else if(loginElapsedTime > userVO.getLoginElapsedTime()) {
					userVO.setLoginRemainMinutes(loginElapsedTime - userVO.getLoginElapsedTime()); 
					userVO.setCheckCd(Constant.RSP_CODE_BEFOREELAPSEDLOGINTIME);
					return userVO;
				} else {
					// over time for retry, reset trial count
					long re = authDAO.updateLoginTrialInit(paramMap);
				}
			}
			
		}

		// 로그인 서비스 일 경우
		if (isLoginService) {
			// 로그인 시간 업데이트
			updateLoginDateTime(loginId, null, (userVO.getFirstLoginDt() == null), clientId);
		}
		
		// reset login trial count as login success
		authDAO.updateUserLoginTrialInit(paramMap);
		userVO.setCheckCd(Constant.RSP_CODE_OK);

		return userVO;
	}

	@Override
	public UserTokenVO checkLoginByLoginToken(String clientId, String loginToken) throws Exception {

		UserTokenVO userTokenVO = tokenService.selectTokenByTokenId(clientId, loginToken,
				Constant.TOKEN_LOGIN_STATUS_CODE_VALID);

		if (userTokenVO == null || StringUtils.isEmpty(userTokenVO.getUserId())) {
			return null;
		}

		return userTokenVO;
	}

	@Override
	@Transactional
	public UserVO checkLoginByOtpToken(String otpToken, String gcspId) throws Exception {

		// otpToken 상태 업데이트
		tokenService.updateOtpTokenInfo(otpToken, gcspId);

		// otpToken 상태 체크
		UserVO userVO = this.selectCheckAuthByOtpToken(otpToken, gcspId, null);

		if (userVO == null || StringUtils.isEmpty(userVO.getUserId())) {
			return null;
		}

		// 로그인 시간 업데이트
		updateLoginDateTime(userVO.getUserId(), null, false, null);

		return userVO;
	}

	@Override
	public List<?> getUserList(List<String> userList, String gcspId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginIds", userList);

		return authDAO.selectUserList(paramMap);
	}

	@Override
	@Transactional
	public int logout(String userId, String clientId, String loginToken) throws Exception {

		// 요청 단말기 전체 토큰 폐기
		int deleteResult = tokenService.deleteTokenByClientId(userId, clientId);

		if (deleteResult > 0) {

			// 로그아웃 이력 추가
			String clientIp = CommonUtils.getClientIP();

			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setUserId(userId);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGOUT);
			loginHistoryVO.setClientId(clientId);
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_SUCCESS);
			loginHistoryVO.setToken(loginToken);

			logService.insertLoginHistory(loginHistoryVO);
		}

		return deleteResult;
	}

	@Override
	@Transactional
	public Map<String, Object> setPamTokenAndDesktopInfo(UserVO userVO, String clientId, String clientIp)
			throws Exception {

		Map<String, Object> resultData = new HashMap<String, Object>();

		// otpToken 처리
		ArrayList<HashMap<String, String>> otpList = new ArrayList<HashMap<String, String>>();

		try {
			// DesktopInfo 생성
			DesktopInfoVO desktopInfoVO = desktopInfoService.getDesktopInfoByUserAndClient(userVO.getUserId(), clientId);

			if (desktopInfoVO != null) {

				// String confInfo = desktopInfoVO.getConfInfo();
				// Gson gson = new Gson();
				// JsonDesktopInfoVO jsonDesktopInfoVO = gson.fromJson(confInfo,
				// JsonDesktopInfoVO.class);
				// ArrayList<JsonAppsVO> jsonAppsListVO = jsonDesktopInfoVO.getApps();

				ArrayList<DesktopAppVO> appVOs = desktopInfoVO.getApps();

				for (DesktopAppVO app : appVOs) {
					DesktopAppInfoVO infoVO = app.getDesktop();
					if (infoVO != null) {

						String exec = infoVO.getExec();
						// otpToken 발급 체크
						if (StringUtils.isNotEmpty(exec) && exec.indexOf("{otpToken}") > 0) {

							int idx = exec.toLowerCase().indexOf("http");

							String cmd = exec.substring(0, idx);
							String url = exec.substring(idx).replaceAll(Constant.DESKTOPINFO_OTPTOKEN,
									Constant.GCSP_OTP_TOKEN_VALUE);
							String otpTokenKey = "";
							String otherUrl = "";

							MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(new URI(url)).build()
									.getQueryParams();

							for (String key : params.keySet()) {
								if (StringUtils.equals(Constant.GCSP_OTP_TOKEN_VALUE, params.getFirst(key))) {
									otpTokenKey = key;
								} else if (!StringUtils.equals("gcsp_id", key)) {
									otherUrl += ("&" + key + "=" + params.getFirst(key));
								}
							}

							url = url.replaceAll(Constant.GCSP_OTP_TOKEN_VALUE, "");

							String gcspId = params.get("gcsp_id").get(0);

							if (StringUtils.isNotEmpty(gcspId)) {
								HashMap<String, String> otpMap = new HashMap<String, String>();

								String otpToken = tokenFactory.genOtpToken(clientIp, gcspId + "#" + app.getOrder(),
										userVO.getUserId());

								otpMap.put("gcspId", gcspId);
								otpMap.put("otpToken", otpToken);
								otpList.add(otpMap);

								StringBuffer urlBuffer = new StringBuffer(cmd);

								urlBuffer.append("\"");
								urlBuffer.append(url.substring(0, url.indexOf("?")));
								urlBuffer.append("?").append(otpTokenKey).append("=").append(otpToken);
								urlBuffer.append(otherUrl);
								urlBuffer.append("\"");

								infoVO.setExec(urlBuffer.toString());
							}
						} else {
							if (StringUtils.isNotEmpty(exec) && exec.indexOf("http") > 0) {

								int idx = exec.toLowerCase().indexOf("http");
								String cmd = exec.substring(0, idx);
								String url = exec.substring(idx);
								exec = cmd + "\"" + url + "\"";

								infoVO.setExec(exec);
							}
						}
					}
				}

				// String jsonStr = DesktopUtils.getJsonForDesktop(desktopInfoVO);
				logger.debug("setPamTokenAndDesktopInfo desktopinfo <{}>",
						DesktopUtils.getJsonForDesktop(desktopInfoVO));
				resultData.put("desktopInfo", desktopInfoVO);

			} else {

				resultData.put("desktopInfo", "");

			}
		} catch (Exception e) {
			e.printStackTrace();
			resultData.put("desktopInfo", "");
		}

		try {

			// DB 저장용 토큰 생성
			String statusCd = Constant.TOKEN_LOGIN_STATUS_CODE_VALID;
			int tokenPassValue = Constant.TOKEN_PASS_VALUE;
			int authCnt = Constant.TOKEN_OTP_INIT_CNT;
			int authAllowCnt = Integer.parseInt(Constant.TOKEN_OTP_ALLOW_CNT);
			int addMinute = Integer.parseInt(Constant.TOKEN_OTP_EXPIRATION_MINUTE);
			String expirationDt = CommonUtils.getAddMinMySqlString(addMinute);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			List<TokenVO> tokenList = new ArrayList<TokenVO>();

			// loginToken 생성
			String loginToken = tokenFactory.genLoginToken(clientId, userVO.getUserId());
			TokenVO tokenVO = new TokenVO(loginToken, userVO.getUserId(), null, statusCd, tokenPassValue, authCnt,
					clientId, null);
			tokenList.add(tokenVO);

			statusCd = Constant.TOKEN_OTP_STATUS_CODE_VALID;

			for (HashMap<String, String> otpMap : otpList) {
				tokenVO = new TokenVO(otpMap.get("otpToken"), userVO.getUserId(), expirationDt, statusCd, authAllowCnt,
						authCnt, clientId, otpMap.get("gcspId"));
				tokenList.add(tokenVO);
			}

			paramMap.put("tokenList", tokenList);

			int result = tokenService.insertToken(paramMap);

			if (result > 0) {

				// 패스워드 생성 주기
				paramMap.clear();
				paramMap.put("loginId", userVO.getLoginId());
				paramMap.put("clientId", clientId);
				List<CtrlItemVO> ctrlItems = ctrlItemDAO.selectCtrlItem(paramMap);

				// 리턴 정보 생성
				PamLoginInfoVO loginInfoVO = new PamLoginInfoVO();
				loginInfoVO.setUser_id(userVO.getUserId());
				loginInfoVO.setUser_name(userVO.getUserNm());
				loginInfoVO.setLogin_token(loginToken);
				loginInfoVO.setPwd_last_day(CommonUtils.getDateToYMD(userVO.getLastPasswdChDt()));

				if (ctrlItems != null && ctrlItems.size() > 0) {
					for (CtrlItemVO ctrlItemVO : ctrlItems) {
						if (StringUtils.equals(ctrlItemVO.getPropNm(), Constant.PROP_PASSWORD_TIME)) {
							loginInfoVO.setPwd_max_day(ctrlItemVO.getPropValue());
						}
					}
				}

				loginInfoVO.setPwd_temp_yn(
						StringUtils.equals(Constant.COMMON_STATUS_TEMP, userVO.getUserPasswdStatus()) ? "Y" : "N");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				loginInfoVO.setExpire_dt((userVO.getExpireDt() != null) ? dateFormat.format(userVO.getExpireDt()) : "");
				loginInfoVO.setExpire_remain_day((userVO.getExpireRemainDay() != null) ? userVO.getExpireRemainDay() : "");
				loginInfoVO.setDept_expire_dt((userVO.getDeptExpireDt() != null) ? dateFormat.format(userVO.getDeptExpireDt()) : "");
				loginInfoVO.setDept_expire_remain_day((userVO.getDeptExpireRemainDay() != null) ? userVO.getDeptExpireRemainDay() : "");
				loginInfoVO.setPwd_expire_dt((userVO.getPwdExpireDt() != null) ? dateFormat.format(userVO.getPwdExpireDt()) : "");
				loginInfoVO.setPwd_expire_remain_day((userVO.getPwdExpireRemainDay() != null) ? userVO.getPwdExpireRemainDay() : "");

				// get passphrase data
				paramMap.clear();
				paramMap.put("loginId", userVO.getLoginId());
				paramMap.put("clientId", clientId);
				String passphrase = authDAO.selectPassphrase(paramMap);
				if (passphrase == null) {
					loginInfoVO.setPassphrase("");
				} else {
					loginInfoVO.setPassphrase(passphrase);
				}
				
				loginInfoVO.setEmail(userVO.getUserEmail());
				resultData.put("loginInfo", loginInfoVO);

				// 로그인 이력 추가
				LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
				loginHistoryVO.setUserId(userVO.getUserId());
				loginHistoryVO.setClientId(clientId);
				loginHistoryVO.setIp(clientIp);
				loginHistoryVO.setResponseCd(Constant.COMMON_MSG_SUCCESS);
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
				logService.insertLoginHistory(loginHistoryVO);

				return resultData;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultData.put("loginInfo", "");

			return resultData;
		}
	}

	@Override
	public GcspVO checkGcspInfo(String clientId, String clientIp) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gcspId", clientId);
		if (StringUtils.isNotEmpty(clientIp))
			paramMap.put("ipRanges", clientIp);
		paramMap.put("statusCd", Constant.COMMON_STATUS_OK);

		return authDAO.selectGcspInfo(paramMap);
	}

	@Override
	@Transactional
	public Map<String, Object> setGcspAuth(UserVO userVO, String clientId, String clientIp, String otpToken) {

		// 리턴 정보 생성
		GcspLoginInfoVO loginInfoVO = new GcspLoginInfoVO();
		loginInfoVO.setUser_id(userVO.getUserId());
		loginInfoVO.setUser_name(userVO.getUserNm());
		loginInfoVO.setEmail(userVO.getUserId() + "@gooroom.kr");

		try {
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setUserId(userVO.getUserId());
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_SUCCESS);

			if (clientId != null)
				loginHistoryVO.setClientId(clientId);

			// otpToken 로그인 이력
			if (otpToken != null) {
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_OTP_LOGIN);
				loginHistoryVO.setToken(otpToken);
			} else {
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_GCSP_LOGIN);
			}

			logService.insertLoginHistory(loginHistoryVO);
		} catch (Exception e) {
		}

		Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put("loginInfo", loginInfoVO);

		return resultData;
	}

	@Override
	@Transactional
	public int changePassword(String loginId, String newPassword) throws Exception {

		int result = updatePassword(loginId, newPassword);

		if (result > 0) {

			logService.insertUserHistory(loginId);
		}

		return result;
	}

	private int updatePassword(String loginId, String newPassword) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);
		paramMap.put("userPasswd", newPassword);
		paramMap.put("modUserId", loginId);
		paramMap.put("userPasswdStatus", Constant.COMMON_STATUS_OK);

		return authDAO.updatePassword(paramMap);
	}

	@Override
	@Transactional
	public int changeUserNm(String loginId, String newUserNm) throws Exception {

		int result = updateUserNm(loginId, newUserNm);

		if (result > 0) {
			logService.insertUserHistory(loginId);
		}

		return result;

	}

	private int updateUserNm(String loginId, String newUserNm) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);
		paramMap.put("userNm", newUserNm);
		paramMap.put("modUserId", loginId);

		return authDAO.updateUserNm(paramMap);
	}

	@Override
	@Transactional
	public int updateLoginDateTime(String loginId, String userPw, boolean isFirstLogin, String clientId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);
		paramMap.put("isFirstLogin", (isFirstLogin) ? "true" : null);
		paramMap.put("clientId", clientId);
		
		if (userPw != null)
			paramMap.put("userPasswd", userPw);

		return authDAO.updateLoginDt(paramMap);
	}

	private UserVO selectCheckAuthByOtpToken(String otpToken, String gcspId, String clientId) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("token", otpToken);
		paramMap.put("statusCd", Constant.TOKEN_OTP_STATUS_CODE_VALID);

		if (gcspId != null)
			paramMap.put("gcspId", gcspId);
		if (clientId != null)
			paramMap.put("clientId", clientId);

		return authDAO.selectCheckAuthByOtpToken(paramMap);
	}

	@Override
	@Transactional
	public int changePassphrase(String loginId, String clientId, String passphrase) throws Exception {

		int result = updatePassphrase(loginId, clientId, passphrase);

		if (result > 0) {
			logService.insertUserHistory(loginId);
		}

		return result;
	}

	private int updatePassphrase(String loginId, String clientId, String passphrase) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);
		paramMap.put("clientId", clientId);
		paramMap.put("passphrase", passphrase);
		paramMap.put("modUserId", loginId);

		return authDAO.insertOrUpdatePassphrase(paramMap);
	}

	@Override
	public List<?> getOnlineClientsByUser(String loginId) throws Exception {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("loginId", loginId);

		return authDAO.selectOnlineClientsByUser(paramMap);
	}

	@Override
	public String getPasswordRule(String siteId) throws Exception {

		return authDAO.selectPasswordRule(siteId);
	}
	
	@Override
	public int isEnableDuplicateLogin(String siteId) throws Exception {

		return authDAO.selectIsEnableDuplicateLogin(siteId);
	}
	
}
