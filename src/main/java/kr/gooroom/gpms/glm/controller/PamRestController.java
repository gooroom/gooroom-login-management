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

package kr.gooroom.gpms.glm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.common.service.CommonHeaderVO;
import kr.gooroom.gpms.common.service.CommonResultVO;
import kr.gooroom.gpms.common.service.CommonService;
import kr.gooroom.gpms.common.service.impl.EmailServiceImpl;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.DesktopUtils;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.common.utils.ParseCertificate;
import kr.gooroom.gpms.glm.service.AuthService;
import kr.gooroom.gpms.glm.service.DesktopAppInfoVO;
import kr.gooroom.gpms.glm.service.DesktopAppVO;
import kr.gooroom.gpms.glm.service.DesktopInfoService;
import kr.gooroom.gpms.glm.service.DesktopInfoVO;
import kr.gooroom.gpms.glm.service.DupClientVO;
import kr.gooroom.gpms.glm.service.LogService;
import kr.gooroom.gpms.glm.service.LoginHistoryVO;
import kr.gooroom.gpms.glm.service.TokenService;
import kr.gooroom.gpms.glm.service.UserTokenVO;
import kr.gooroom.gpms.glm.service.UserVO;
import kr.gooroom.gpms.job.CustomJobMaker;

/**
 * Handles requests for the Gooroom's PAM service, authorization management
 * process.
 *
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@RestController
@RequestMapping(value = "/v1/pam")
public class PamRestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "authService")
	private AuthService authService;

	@Resource(name = "tokenService")
	private TokenService tokenService;

	@Resource(name = "logService")
	private LogService logService;

	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "desktopInfoService")
	private DesktopInfoService desktopInfoService;

	@Autowired
	public EmailServiceImpl emailService;

	@Inject
	private CustomJobMaker jobMaker;

	/**
	 * check client certificate and produce client id, client ip.
	 *
	 * @param req HttpServletRequest @param res HttpServletResponse @param model
	 *            ModelMap @return Map result @throws
	 */
	@RequestMapping(value = "/checkClientCert", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> client(HttpServletRequest req, HttpServletResponse res, ModelMap model)
			throws Exception {

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);
		String clientId = "";

		CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

		logger.debug("checkClientCert clientCert[{}] \n clientIp[{}]", clientCert, clientIp);

		// client cert
		if (StringUtils.isNotEmpty(clientCert)) {
			clientCert = clientCert.replaceAll("[\n\t\r]", " ");
			clientId = ParseCertificate.parseCert(clientCert);

			if (clientId == null) {
				logger.debug("authGCSP clientCert parse Exception");

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
						model);
			}

			logger.debug("checkclient clientId[{}] \n cert{}", clientId, clientCert);
		} else {
			clientId = "empty";

			logger.debug("checkclient clientCert is empty");

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
		}

		Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put("ID", clientId);
		resultData.put("IP", clientIp);

		return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo,
				resultData, model);
	}

	private CommonResultVO processAuth(String loginId, String userPw, String clientCert, String clientIp,
									   boolean isOnlyConfirm) throws Exception {

		CommonResultVO result = new CommonResultVO();
		// 1. check client cert, and get client id from cert
		clientCert = clientCert.replaceAll("[\n\t\r]", " ");
		String clientId = ParseCertificate.parseCert(clientCert);

		if (clientId == null) {
			logger.debug("processAuth clientCert parse Exception");
			result.setResultCode(Constant.RSP_CODE_AUTH_FAIL);
			result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"));
			result.setSuccess(false);
			result.setLoginId(loginId);
			return result;
		}

		// 2. check client ip available
		result = commonService.isAvailableIp(clientId, clientIp);

		result.setClientId(clientId);
		result.setLoginId(loginId);

		if (!result.isSuccess()) {
			return result;
		}

		// 3. user auth.
		UserVO userVO = authService.checkLoginByUserId(loginId, userPw, true, clientId);
		if (userVO != null) {

			if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_OK)) {

				Map<String, Object> resultData = new HashMap<>();

				if (!isOnlyConfirm) {
					try {
						// get duplicate online client list by user
						@SuppressWarnings("unchecked")
						List<DupClientVO> dupClientList = (List<DupClientVO>) authService.getOnlineClientsByUser(loginId);
						if (dupClientList != null && dupClientList.size() > 0) {

							if (dupClientList.size() > 1 || !clientId.equals(dupClientList.get(0).getClientId())) {
								// check enable duplicate login this site
								int typeDupLogin = authService.isEnableDuplicateLogin("");
								if (typeDupLogin > 0) {
									if (!isOnlyConfirm) {
										// send mail and noti alarm
										processMsgForDuplicateLogin(Math.abs(typeDupLogin), loginId, userVO.getUserEmail(),
												dupClientList);
									}
								} else {
									// send mail and noti alarm
									processMsgForDuplicateLogin(Math.abs(typeDupLogin), loginId, userVO.getUserEmail(),
											dupClientList);

									// prohibit duplicate login
									result.setResultCode(Constant.RSP_CODE_NODUPLICATELOGIN);
									result.setResultMessage(
											MessageSourceHelper.getMessage("common.msg.auth.fail.noduplicate.login"));
									result.setSuccess(false);
									result.setResultData(resultData);
									return result;
								}
							}
						}

						// 로그인 시간 업데이트
						authService.updateLoginDateTime(loginId, null, (userVO.getFirstLoginDt() == null), clientId);

						List<?> userTokenList = tokenService.selectTokenListByClientId(loginId, clientId, null);
						if (userTokenList != null && userTokenList.size() > 0) {
							// delete token data that created before
							tokenService.deleteTokenByClientId(loginId, clientId);
							logger.debug("processAuth loginId[{}] clientId[{}] is already login", loginId, clientId);
						}
						// ########################## CREATE TOKEN ###########################
						// create token and desktop information
						resultData = authService.setPamTokenAndDesktopInfo(userVO, clientId, clientIp);


						// get password rule(compexity) for this site
						String passwordRule = (String) authService.getPasswordRule("SITEID");
						ObjectMapper mapper = new ObjectMapper();
						Map<String, Object> passwordRuleVO = new HashMap<String, Object>();
						// convert JSON string to Map
						passwordRuleVO = mapper.readValue(passwordRule, new TypeReference<Map<String, String>>() {
						});
						if (passwordRuleVO != null) {
							resultData.put("passwordRule", passwordRuleVO);
						}

						if (dupClientList != null && dupClientList.size() > 0) {
							resultData.put("duplicateClients", dupClientList);
						}
					} catch (Exception e) {
						logger.debug("processAuth db Exception");
						result.setResultCode(Constant.RSP_CODE_AUTH_FAIL);
						result.setResultMessage(MessageSourceHelper.getMessage("common.msg.fail"));
						result.setSuccess(false);
						return result;
					}
				}

				// for debug
				try {
					System.out.println("-------------------MAP을 JSON String으로 변환-----------------------START");
					ObjectMapper mapper = new ObjectMapper();
					String json2 = ""; // json2 = mapper.writeValueAsString(map2);
					json2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultData);
					System.out.println("resultData ::: \n" + json2);
					System.out.println("-------------------MAP을 JSON String으로 변환-----------------------END");
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				result.setResultCode(Constant.RSP_CODE_OK);
				result.setResultMessage("");
				result.setSuccess(true);
				result.setResultData(resultData);
				return result;

			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_LOGINTRIALEXPIRE)) {

				result.setResultCode(Constant.RSP_CODE_LOGINTRIALEXPIRE);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.expire.login"));
				result.setSuccess(false);
				return result;

			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_BEFOREELAPSEDLOGINTIME)) {

				logger.debug("auth failed. clientId[{}] loginId[{}] invalid ID/PW and locked for while", clientId,
						loginId);
				result.setResultCode(Constant.RSP_CODE_BEFOREELAPSEDLOGINTIME);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.wait.login"));
				Map<String, Object> resultData = new HashMap<>();
				resultData.put("loginRemainMinutes", String.valueOf(userVO.getLoginRemainMinutes()));
				result.setResultData(resultData);

				result.setSuccess(false);
				return result;

			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_USEREXPIRE)) {

				result.setResultCode(Constant.RSP_CODE_USEREXPIRE);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.expire.user"));
				result.setSuccess(false);
				return result;

			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_DEPTEXPIRE)) {

				result.setResultCode(Constant.RSP_CODE_DEPTEXPIRE);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.expire.dept"));
				result.setSuccess(false);
				return result;

// don't setup error code - request from gooroom terminal by cjheo
//			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_PASSWORDEXPIRE)) {
//
//				result.setResultCode(Constant.RSP_CODE_PASSWORDEXPIRE);
//				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.expire.password"));
//				result.setSuccess(false);
//				return result;

			} else if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_NOT_AUTH)) {

				logger.debug("auth failed. clientId[{}] loginId[{}] invalid ID/PW", clientId, loginId);
				result.setResultCode(Constant.RSP_CODE_AUTH_FAIL_INVALID_PW);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.password"));

				Map<String, Object> resultData = new HashMap<>();
				resultData.put("remainLoginTrial", userVO.getRemainLoginTrial());
				result.setResultData(resultData);

				result.setSuccess(false);
				return result;

			} else {

				result.setResultCode(Constant.RSP_CODE_AUTH_FAIL);
				result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail"));
				result.setSuccess(false);
				return result;
			}

		} else {

			logger.debug("auth failed. clientId[{}] loginId[{}] not exist user", clientId, loginId);
			result.setResultCode(Constant.RSP_CODE_AUTH_FAIL_NOTEXIST_ID);
			result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.user"));
			result.setSuccess(false);
			return result;
		}
	}

	private CommonResultVO processAuthByNfcToken(String loginToken, String clientCert, String clientIp)
			throws Exception {

		CommonResultVO result = new CommonResultVO();

		// 1. check client cert, and get client id from cert
		clientCert = clientCert.replaceAll("[\n\t\r]", " ");
		String clientId = ParseCertificate.parseCert(clientCert);
		if (clientId == null) {
			logger.debug("processTokenAuth clientCert parse Exception");
			result.setResultCode(Constant.RSP_CODE_AUTH_FAIL);
			result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"));
			result.setSuccess(false);
			return result;
		}

		result.setClientId(clientId);

		// 2. check client ip available
		result = commonService.isAvailableIp(clientId, clientIp);
		if (!result.isSuccess()) {
			return result;
		}

		UserTokenVO userTokenVO = authService.checkLoginByLoginToken(clientId, loginToken);
		if (userTokenVO != null) {
			Map<String, Object> resultData = new HashMap<>();
			resultData.put("user_id", userTokenVO.getUserId());
			resultData.put("nfc_secret_data", userTokenVO.getNfcSecretData());

			logger.debug("authNfcData userTokenVO login succeed. userId[{}] nfc_secret_data[{}]",
					userTokenVO.getUserId(), userTokenVO.getNfcSecretData());
			result.setResultCode(Constant.RSP_CODE_OK);
			result.setResultMessage("");
			result.setSuccess(true);
			result.setResultData(resultData);
			return result;
		} else {
			logger.debug("auth failed. clientId[{}] loginToken[{}] not exist user", clientId, loginToken);
			result.setResultCode(Constant.RSP_CODE_AUTH_FAIL);
			result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail"));
			result.setSuccess(false);
			return result;
		}
	}

	/**
	 * authorization user login with login id and password.
	 *
	 * @param loginId String login id @param userPw String user password @param req
	 *                HttpServletRequest @param res HttpServletResponse @param model
	 *                ModelMap @return Map result @throws
	 */
	@RequestMapping(value = "/auth", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> authPAM(@RequestParam(value = "user_id", required = false) String loginId,
										   @RequestParam(value = "user_pw", required = false) String userPw, HttpServletRequest req,
										   HttpServletResponse res, ModelMap model) throws Exception {

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// check parameter string valid
		if (StringUtils.isAnyBlank(loginId, userPw, clientCert, clientIp)) {
			if (StringUtils.isNotEmpty(clientCert)) {
				logger.debug("authPAM clientCert is empty");
				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), new CommonHeaderVO(), null,
						model);
			}
			logger.debug("authPAM invalid param => loginId : {} or userPw", loginId);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), new CommonHeaderVO(), null, model);
		}

		// Auth process
		try {

			CommonResultVO result = processAuth(loginId, userPw, clientCert, clientIp, false);

			System.out.println("auth : getLoginId -> " + result.getLoginId());
			System.out.println("auth : getResultCode -> " + result.getResultCode());
			System.out.println("auth : getResultMessage -> " + result.getResultMessage());
			System.out.println("auth : getResultData -> " + result.getResultData());

			try {
				System.out.println("-------------------[FINAL] MAP을 JSON String으로 변환-----------------------START");
				ObjectMapper mapper = new ObjectMapper();
				String json2 = ""; // json2 =
				mapper.writeValueAsString(result.getResultData());
				json2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.getResultData());
				System.out.println("resultData ::: \n" + json2);
				System.out.println("-------------------[FINAL] MAP을 JSON String으로 변환-----------------------END");
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (result.isSuccess()) {
				return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, result.getResultCode(), "",
						new CommonHeaderVO(), result.getResultData(), model);
			} else {
				// authorize fail
				LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
				loginHistoryVO.setUserId(loginId);
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
				loginHistoryVO.setClientId(result.getClientId());
				loginHistoryVO.setIp(clientIp);
				loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
				logService.insertLoginHistory(loginHistoryVO);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, result.getResultCode(),
						result.getResultMessage(), new CommonHeaderVO(), result.getResultData(), model);
			}
		} catch (Exception ex) {
			// authorize fail
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setUserId(loginId);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);

			// get client id if certificate is not wrong.
			String clientId = null;
			try {
				clientId = ParseCertificate.parseCert(clientCert.replaceAll("[\n\t\r]", " "));
				loginHistoryVO.setClientId(clientId);
			} catch (Exception inex) {
				clientId = null;
			}
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
			logService.insertLoginHistory(loginHistoryVO);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, "AUTH-EXCEPTION", ex.toString(),
					new CommonHeaderVO(), null, model);
		}

	}

	/**
	 * authorization user login with login id and password for confirm.
	 *
	 * @param loginId String login id @param userPw String user password @param req
	 *                HttpServletRequest @param res HttpServletResponse @param model
	 *                ModelMap @return Map result @throws
	 */
	@RequestMapping(value = "/authconfirm", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> authConfirmPAM(@RequestParam(value = "user_id", required = false) String loginId,
												  @RequestParam(value = "user_pw", required = false) String userPw, HttpServletRequest req,
												  HttpServletResponse res, ModelMap model) throws Exception {

//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");
//		System.out.println("#########                                                                          #########");
//		System.out.println("#########                              authconfirm                                 #########");
//		System.out.println("#########                                                                          #########");
//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// check parameter string valid
		if (StringUtils.isAnyBlank(loginId, userPw, clientCert, clientIp)) {
			if (StringUtils.isNotEmpty(clientCert)) {
				logger.debug("authPAM clientCert is empty");
				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), new CommonHeaderVO(), null,
						model);
			}
			logger.debug("authPAM invalid param => loginId : {} or userPw", loginId);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), new CommonHeaderVO(), null, model);
		}

		// Auth process
		try {
			CommonResultVO result = processAuth(loginId, userPw, clientCert, clientIp, true);

			if (result.isSuccess()) {
				return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, result.getResultCode(), "",
						new CommonHeaderVO(), result.getResultData(), model);
			} else {
				// authorize fail
				LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
				loginHistoryVO.setUserId(loginId);
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
				loginHistoryVO.setClientId(result.getClientId());
				loginHistoryVO.setIp(clientIp);
				loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
				logService.insertLoginHistory(loginHistoryVO);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, result.getResultCode(),
						result.getResultMessage(), new CommonHeaderVO(), result.getResultData(), model);
			}
		} catch (Exception ex) {
			// authorize fail
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setUserId(loginId);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
			// get client id if certificate is not wrong.
			String clientId = null;
			try {
				clientId = ParseCertificate.parseCert(clientCert.replaceAll("[\n\t\r]", " "));
				loginHistoryVO.setClientId(clientId);
			} catch (Exception inex) {
				clientId = null;
			}
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
			logService.insertLoginHistory(loginHistoryVO);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, "AUTHCONFIRM-EXCEPTION", ex.toString(),
					new CommonHeaderVO(), null, model);
		}
	}

	/**
	 * authorization user login with token for NFC media.
	 *
	 * @param loginId String login id @param userPw String user password @param req
	 *                HttpServletRequest @param res HttpServletResponse @param model
	 *                ModelMap @return Map result @throws
	 */
	@RequestMapping(value = "/nfc", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> authNfcData(@RequestParam(value = "login_token", required = false) String loginToken,
											   @RequestParam(value = "user_id", required = false) String loginId,
											   @RequestParam(value = "user_pw", required = false) String userPw, HttpServletRequest req,
											   HttpServletResponse res, ModelMap model) throws Exception {

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		if (StringUtils.isBlank(loginToken)) {
			// login using id/pw
			// check parameter string valid
			if (StringUtils.isAnyBlank(loginId, userPw, clientCert, clientIp)) {
				if (StringUtils.isNotEmpty(clientCert)) {
					logger.debug("nfc clientCert is empty");
					return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
							MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), new CommonHeaderVO(),
							null, model);
				}
				logger.debug("nfc invalid param => loginId : {} or userPw", loginId);
				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail"), new CommonHeaderVO(), null, model);
			}

			// Auth process
			CommonResultVO result = processAuth(loginId, userPw, clientCert, clientIp, false);

			if (result.isSuccess()) {
				return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, result.getResultCode(), "",
						new CommonHeaderVO(), result.getResultData(), model);
			} else {
				// authorize fail
				LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
				loginHistoryVO.setUserId(loginId);
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
				loginHistoryVO.setClientId(result.getClientId());
				loginHistoryVO.setIp(clientIp);
				loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
				logService.insertLoginHistory(loginHistoryVO);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, result.getResultCode(),
						result.getResultMessage(), new CommonHeaderVO(), null, model);
			}

		} else {
			// login using token
			// check parameter string valid
			if (StringUtils.isAnyBlank(loginToken, clientCert, clientIp)) {
				if (StringUtils.isNotEmpty(clientCert)) {
					logger.debug("nfc clientCert is empty");
					return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
							MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), new CommonHeaderVO(),
							null, model);
				}
				logger.debug("nfc invalid param => loginId : {} or userPw", loginId);
				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail"), new CommonHeaderVO(), null, model);
			}

			// Auth process
			CommonResultVO result = processAuthByNfcToken(loginToken, clientCert, clientIp);
			if (result.isSuccess()) {
				return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, result.getResultCode(), "",
						new CommonHeaderVO(), result.getResultData(), model);
			} else {
				// authorize fail
				LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
				loginHistoryVO.setUserId(loginId);
				loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN);
				loginHistoryVO.setClientId(result.getClientId());
				loginHistoryVO.setIp(clientIp);
				loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);
				logService.insertLoginHistory(loginHistoryVO);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, result.getResultCode(),
						result.getResultMessage(), new CommonHeaderVO(), null, model);
			}
		}
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> logoutPAM(@RequestParam(value = "login_token", required = false) String loginToken,
											 HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);
		String clientId = "";

		CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

		if (StringUtils.isBlank(loginToken)) {
			logger.debug("logoutPAM invalid param => loginToken : {}", loginToken);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}

		if (StringUtils.isNotEmpty(clientCert)) {
			clientCert = clientCert.replaceAll("[\n\t\r]", " ");

			clientId = ParseCertificate.parseCert(clientCert);

			if (clientId == null) {
				logger.debug("authGCSP clientCert parse Exception");

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
						model);
			}

			logger.debug("logoutPAM clientId[{}] cert[{}]", clientId, clientCert);
		} else {
			logger.debug("logoutPAM clientCert is empty");

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
		}

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// user authorization
		UserTokenVO userTokenVO = authService.checkLoginByLoginToken(clientId, loginToken);

		if (userTokenVO != null) {

			// PAM 로그아웃 시 해당 단말기에서 발행된 모든 토큰 폐기
			if (authService.logout(userTokenVO.getUserId(), clientId, loginToken) > 0) {
				logger.debug("logoutPAM succeed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);

				return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo,
						null, model);
			} else {

				logger.debug("logoutPAM failed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.fail"), commonHeaderVo, null, model);
			}
		}
		// fail authorization
		else {
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setToken(loginToken);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGOUT);
			loginHistoryVO.setClientId(clientId);
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

			logService.insertLoginHistory(loginHistoryVO);

			logger.debug("logoutPAM failed. clientId[{}] loginToken[{}] invalid token", clientId, loginToken);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}
	}

	@RequestMapping(value = "/password", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> changePassword(
			@RequestParam(value = "login_token", required = false) String loginToken,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "new_password", required = false) String newPassword, HttpServletRequest req,
			HttpServletResponse res, ModelMap model) throws Exception {

//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");
//		System.out.println("#########                                                                          #########");
//		System.out.println("#########                              password                                    #########");
//		System.out.println("#########                                                                          #########");
//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);
		String clientId = "";

		CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

		if (StringUtils.isAnyBlank(loginToken, password, newPassword)) {
			logger.debug("changePassword invalid param => loginToken : {} or password or newPassword", loginToken);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}

		if (StringUtils.isNotEmpty(clientCert)) {
			clientCert = clientCert.replaceAll("[\n\t\r]", " ");

			clientId = ParseCertificate.parseCert(clientCert);

			if (clientId == null) {
				logger.debug("changePassword clientCert parse Exception");

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
						model);
			}

			logger.debug("changePassword clientId[{}]\n cert{}", clientId, clientCert);
		} else {
			logger.debug("changePassword clientCert is empty");

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
		}

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// user authorization
		UserTokenVO userTokenVO = authService.checkLoginByLoginToken(clientId, loginToken);

		if (userTokenVO != null) {
			// 기존 패스워드 검사
			UserVO userVO = authService.checkLoginByUserId(userTokenVO.getUserId(), password, false, clientId);

			if (userVO == null || StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_NOT_AUTH)) {

				logger.debug("changePassword failed. userId[{}] clientId[{}] invalid password", userTokenVO.getUserId(),
						clientId);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.old.password"), commonHeaderVo, null,
						model);
			} else {
				if (authService.changePassword(userTokenVO.getUserId(), newPassword) <= 0) {

					logger.debug("changePassword failed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);

					return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
							MessageSourceHelper.getMessage("common.msg.fail"), commonHeaderVo, null, model);
				}

				logger.debug("changePassword succeed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);
			}

			return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo, null,
					model);
		}
		// fail authorization
		else {
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setToken(loginToken);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN_TOKEN);
			loginHistoryVO.setClientId(clientId);
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

			logService.insertLoginHistory(loginHistoryVO);

			logger.debug("changePassword failed. clientId[{}] loginToken[{}] invalid token", clientId, loginToken);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}
	}

	@RequestMapping(value = "/username", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> changeUserName(
			@RequestParam(value = "login_token", required = false) String loginToken,
			@RequestParam(value = "user_nm", required = false) String userName, HttpServletRequest req,
			HttpServletResponse res, ModelMap model) throws Exception {

//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");
//		System.out.println("#########                                                                          #########");
//		System.out.println("#########                              username                                    #########");
//		System.out.println("#########                                                                          #########");
//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);
		String clientId = "";

		CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

		if (StringUtils.isAnyBlank(loginToken, userName)) {
			logger.debug("changeUserName invalid param => loginToken : {}, userName : {}", loginToken, userName);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		} else if (userName.length() > 20) {
			logger.debug("changeUserName userName is too long. userName : {}, userName.length() : {}", userName,
					userName.length());
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.long.username"), commonHeaderVo, null, model);
		}

		if (StringUtils.isNotEmpty(clientCert)) {
			clientCert = clientCert.replaceAll("[\n\t\r]", " ");

			clientId = ParseCertificate.parseCert(clientCert);

			if (clientId == null) {
				logger.debug("changeUserName clientCert parse Exception");

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
						model);
			}

			logger.debug("changeUserName clientId[{}]\n cert{}", clientId, clientCert);
		} else {
			logger.debug("changeUserName clientCert is empty");

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
		}

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// user authorization
		UserTokenVO userTokenVO = authService.checkLoginByLoginToken(clientId, loginToken);

		if (userTokenVO != null) {

			// change user name
			int result = authService.changeUserNm(userTokenVO.getUserId(), userName);

			if (result <= 0) {

				logger.debug("changeUserName failed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.fail"), commonHeaderVo, null, model);
			}

			return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo, null,
					model);
		}
		// fail authorization
		else {
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setToken(loginToken);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN_TOKEN);
			loginHistoryVO.setClientId(clientId);
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

			logService.insertLoginHistory(loginHistoryVO);

			logger.debug("changeUserName failed. clientId[{}] loginToken[{}] invalid token", clientId, loginToken);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}
	}

	@RequestMapping(value = "/passphrase", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> changePassphrase(
			@RequestParam(value = "login_token", required = false) String loginToken,
			@RequestParam(value = "passphrase", required = false) String passphrase, HttpServletRequest req,
			HttpServletResponse res, ModelMap model) throws Exception {

//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");
//		System.out.println("#########                                                                          #########");
//		System.out.println("#########                              passphrase                                  #########");
//		System.out.println("#########                                                                          #########");
//		System.out.println("############################################################################################");
//		System.out.println("############################################################################################");

		String clientCert = req.getHeader(Constant.H_CERT);
		String clientIp = req.getHeader(Constant.H_REALIP);
		String clientId = "";

		CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

		if (StringUtils.isAnyBlank(loginToken, passphrase)) {
			logger.debug("changeUserName invalid param => loginToken : {}, passphrase : {}", loginToken, passphrase);
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}

		if (StringUtils.isNotEmpty(clientCert)) {
			clientCert = clientCert.replaceAll("[\n\t\r]", " ");

			clientId = ParseCertificate.parseCert(clientCert);

			if (clientId == null) {
				logger.debug("changePassphrase clientCert parse Exception");

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
						model);
			}

			logger.debug("changePassphrase clientId[{}]\n cert{}", clientId, clientCert);
		} else {
			logger.debug("changePassphrase clientCert is empty");

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
		}

		if (StringUtils.isEmpty(clientIp))
			clientIp = CommonUtils.getClientIP();

		// user authorization
		UserTokenVO userTokenVO = authService.checkLoginByLoginToken(clientId, loginToken);

		if (userTokenVO != null) {

			// change passphrase
			int result = authService.changePassphrase(userTokenVO.getUserId(), clientId, passphrase);

			if (result <= 0) {

				logger.debug("changeUserName failed. userId[{}] clientId[{}]", userTokenVO.getUserId(), clientId);

				return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
						MessageSourceHelper.getMessage("common.msg.fail"), commonHeaderVo, null, model);
			}

			return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo, null,
					model);
		}
		// fail authorization
		else {
			LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
			loginHistoryVO.setToken(loginToken);
			loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_PAM_LOGIN_TOKEN);
			loginHistoryVO.setClientId(clientId);
			loginHistoryVO.setIp(clientIp);
			loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

			logService.insertLoginHistory(loginHistoryVO);

			logger.debug("changeUserName failed. clientId[{}] loginToken[{}] invalid token", clientId, loginToken);

			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
		}
	}

	private long __ipToLong(String ipAddress) {

		if (ipAddress != null) {
			ipAddress = ipAddress.trim();

			String[] ipAddressInArray = ipAddress.split("\\.");

			long result = 0;
			for (int i = 0; i < ipAddressInArray.length; i++) {

				int power = 3 - i;
				int ip = Integer.parseInt(ipAddressInArray[i]);
				result += ip * Math.pow(256, power);

			}

			return result;
		} else {
			return -1;
		}

	}

	private void processMsgForDuplicateLogin(int gubun, String loginId, String emailAddr,
											 List<DupClientVO> dupClientList) {
		if (gubun > 0) {
			switch (gubun) {
				case 2:
					// send mail
					sendEmailForDuplicateLogin(emailAddr, dupClientList, loginId);
					break;
				case 3:
					// create notify job
					createNotifyForDuplicateLogin(loginId, dupClientList);
					break;
				case 4:
					// send mail and create notify job
					sendEmailForDuplicateLogin(emailAddr, dupClientList, loginId);
					// create notify job
					createNotifyForDuplicateLogin(loginId, dupClientList);
					break;
				default:
					// default - do nothing.
					break;
			}
		}
	}

	private void sendEmailForDuplicateLogin(String emailAddr, List<DupClientVO> dupClientList, String loginId) {
		// send mail
		// FOR TEST - EDIT LATER

		StringBuffer mainBody = new StringBuffer("<!DOCTYPE html><html><head></head><body>");
		mainBody.append("<div><h3>구름단말 중복로그인 정보입니다.</h3></div>");
		mainBody.append(
				"<table style='border-collapse: collapse;border-bottom: 1px solid black;'><tr><td style='width:200px;'>사용자아이디</td><td style='width:200px;'>");
		mainBody.append(loginId);
		mainBody.append("</td></tr></table>");
		mainBody.append("<br/>");
		mainBody.append("<table style='border-collapse: collapse;'><tr style='height:40px;'>")
				.append("<th style='width:200px;border: 1px solid gray;background-color: lightskyblue;'>단말아이디</th>")
				.append("<th style='width:200px;border: 1px solid gray;background-color: lightskyblue;'>단말이름</th>")
				.append("<th style='width:200px;border: 1px solid gray;background-color: lightskyblue;'>단말아이피</th></tr>");
		if (dupClientList != null && dupClientList.size() > 0) {
			dupClientList.forEach(vo -> {
				mainBody.append("<tr><td style='border: 1px solid gray;text-align: center;'>").append(vo.getClientId())
						.append("</td><td style='border: 1px solid gray;text-align: center;'>").append(vo.getClientNm())
						.append("</td><td style='border: 1px solid gray;text-align: center;'>").append(vo.getIp())
						.append(" (").append(vo.getLocalIp()).append(")").append("</td></tr>");
			});
		}
		mainBody.append("</table></body></html>");

		emailService.sendSimpleMessage(emailAddr, "구름단말 중복로그인 정보", mainBody.toString());
	}

	private void createNotifyForDuplicateLogin(String loginId, List<DupClientVO> dupClientList) {

		try {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login_id", loginId);
			map.put("msg", "duplicate login");
			// create client array - targets
			String[] targets = new String[dupClientList.size()];
			for (int i = 0; i < dupClientList.size(); i++) {
				targets[i] = dupClientList.get(i).getClientId();
			}

			jobMaker.createJobWithClientIds("noti", "set_multiple_login_msg", targets, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * authorization user login with login id and password.
	 *
	 * @param loginId String login id @param userPw String user password @param req
	 *                HttpServletRequest @param res HttpServletResponse @param model
	 *                ModelMap @return Map result @throws
	 */
	@RequestMapping(value = "/desktop", method = { RequestMethod.POST })
	public @ResponseBody Map<?, ?> desktopPAM(@RequestParam(value = "service_name", required = false) String confName,
											  HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

//		String clientCert = req.getHeader(Constant.H_CERT);
//		String clientIp = req.getHeader(Constant.H_REALIP);

//		if (StringUtils.isEmpty(clientIp))
//			clientIp = CommonUtils.getClientIP();

		// check parameter string valid
		if (StringUtils.isAnyBlank(confName)) {
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
					MessageSourceHelper.getMessage("common.msg.auth.fail"), new CommonHeaderVO(), null, model);
		}

		// DesktopInfo 생성
		DesktopInfoVO desktopInfoVO = desktopInfoService.getDesktopInfoByName(confName);
		Map<String, Object> resultData = new HashMap<String, Object>();
		if (desktopInfoVO != null) {

			ArrayList<DesktopAppVO> appVOs = desktopInfoVO.getApps();
			for (DesktopAppVO app : appVOs) {
				DesktopAppInfoVO infoVO = app.getDesktop();
				if (infoVO != null) {
					String exec = infoVO.getExec();
					if (StringUtils.isNotEmpty(exec) && exec.indexOf("http") > 0) {
						int idx = exec.toLowerCase().indexOf("http");
						String cmd = exec.substring(0, idx);
						String url = exec.substring(idx);
						exec = cmd + "\"" + url + "\"";
						infoVO.setExec(exec);
					}
				}
			}
			// String jsonStr = DesktopUtils.getJsonForDesktop(desktopInfoVO);
			logger.debug("setPamTokenAndDesktopInfo desktopinfo <{}>", DesktopUtils.getJsonForDesktop(desktopInfoVO));
			resultData.put("desktopInfo", desktopInfoVO);
			return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, "200", "", new CommonHeaderVO(), resultData,
					model);
		} else {
			resultData.put("desktopInfo", "");
			return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_NO_DATA,
					MessageSourceHelper.getMessage("common.msg.auth.fail.no.desktop.info"), new CommonHeaderVO(),
					resultData, model);
		}
	}
}