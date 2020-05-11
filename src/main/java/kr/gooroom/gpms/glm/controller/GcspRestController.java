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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.gooroom.gpms.common.service.CommonHeaderVO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.common.utils.ParseCertificate;
import kr.gooroom.gpms.glm.service.AuthService;
import kr.gooroom.gpms.glm.service.GcspVO;
import kr.gooroom.gpms.glm.service.LogService;
import kr.gooroom.gpms.glm.service.LoginHistoryVO;
import kr.gooroom.gpms.glm.service.TokenService;
import kr.gooroom.gpms.glm.service.UserVO;

@RestController
@RequestMapping(value = "/v1/gcsp")
public class GcspRestController {

    private static final Logger logger = LoggerFactory.getLogger(GcspRestController.class);

    @Resource(name = "authService")
    private AuthService authService;

    @Resource(name = "tokenService")
    private TokenService tokenService;

    @Resource(name = "logService")
    private LogService logService;

    @RequestMapping(value = "/auth", method = { RequestMethod.POST })
    public @ResponseBody Map<?, ?> authGCSP(@RequestParam(value = "user_id", required = false) String loginId,
	    @RequestParam(value = "user_pw", required = false) String userPw, HttpServletRequest req,
	    HttpServletResponse res, ModelMap model) throws Exception {

	String clientCert = req.getHeader(Constant.H_CERT);
	String clientIp = req.getHeader(Constant.H_REALIP);
	String gcspId = null;

	CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

	if (StringUtils.isAnyBlank(loginId, userPw)) {
	    logger.debug("authGCSP failed. invalid parameter");

	    logger.debug("authGCSP invalid param => loginId : {} or userPw", loginId);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.required.param"), commonHeaderVo, null, model);
	}

	if (StringUtils.isNotEmpty(clientCert)) {
	    clientCert = clientCert.replaceAll("[\n\t\r]", " ");
	    gcspId = ParseCertificate.parseCert(clientCert);

	    if (gcspId == null) {
		logger.debug("authGCSP clientCert parse Exection");

		return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
			MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
			model);
	    }

	    logger.debug("authGCSP clientId[{}]\n cert{}", gcspId, clientCert);
	} else {

	    logger.debug("authGCSP clientCert is empty");

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
	}

	logger.debug("authGCSP clientIp[" + clientIp + "]");

	if (StringUtils.isEmpty(clientIp)) {
	    clientIp = CommonUtils.getClientIP();

	    logger.debug("authGCSP Header clientIp is empty. Common Ip[" + clientIp + "]");
	}

	if (!StringUtils.equalsIgnoreCase(Constant.GCSP_API_IP_CHECK_YN, "Y"))
	    clientIp = null;

	GcspVO gcspVO = authService.checkGcspInfo(gcspId, clientIp);

	if (gcspVO == null) {

	    logger.debug("authGCSP failed. Gcsp Info is null. check request gcsp Info - gcspId : {}, clientIp : {}",
		    gcspId, clientIp);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.gcsp") + " ID[" + gcspId + "] IP["
			    + clientIp + "]",
		    commonHeaderVo, null, model);
	}

	String returnMsg = "";

	// 사용자 인증 체크
	UserVO userVO = authService.checkLoginByUserId(loginId, userPw, true, null);
	String rsp_code = "";

	if (userVO != null) {

	    if (StringUtils.equals(userVO.getCheckCd(), Constant.RSP_CODE_OK)) {

		Map<String, Object> resultData = authService.setGcspAuth(userVO, gcspId, clientIp, null);

		logger.debug("authGCSP succeed. loginId[{}] clientId[{}]", loginId, gcspId);
		logger.debug("authGCSP resultData - ");
		resultData.forEach((key, value) -> {
		    logger.debug("{} : {}", key, value.toString());
		});

		return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, returnMsg,
			commonHeaderVo, resultData, model);
	    } else {
		rsp_code = Constant.RSP_CODE_AUTH_FAIL_INVALID_PW;
		returnMsg = MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.password");
		logger.debug("authGCSP failed. gcspId[{}], loginId[{}] invalid ID/PW", gcspId, loginId);
	    }

	} else {
	    rsp_code = Constant.RSP_CODE_AUTH_FAIL_NOTEXIST_ID;
	    returnMsg = MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.user");
	    logger.debug("authGCSP failed. gcspId[{}], loginId[{}] not exist user", gcspId, loginId);

	}

	// 인증 실패
	LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
	loginHistoryVO.setUserId(loginId);
	loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_GCSP_LOGIN);
	loginHistoryVO.setClientId(gcspId);
	loginHistoryVO.setIp(clientIp);
	loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

	logService.insertLoginHistory(loginHistoryVO);

	return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, rsp_code, returnMsg, commonHeaderVo, null, model);
    }

    @RequestMapping(value = "/token", method = { RequestMethod.POST })
    public @ResponseBody Map<?, ?> tokenAuthGCSP(@RequestParam(value = "otp_token", required = false) String otpToken,
	    HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

	String clientCert = req.getHeader(Constant.H_CERT);
	String clientIp = req.getHeader(Constant.H_REALIP);
	String gcspId = null;

	CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

	// optToken 체크
	if (StringUtils.isBlank(otpToken)) {

	    logger.debug("tokenAuthGCSP invalid param => otpToken : {}", otpToken);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.required.param"), commonHeaderVo, null, model);
	}

	if (StringUtils.isNotEmpty(clientCert)) {
	    clientCert = clientCert.replaceAll("[\n\t\r]", " ");
	    gcspId = ParseCertificate.parseCert(clientCert);

	    if (gcspId == null) {
		logger.debug("tokenAuthGCSP clientCert parse Exection");

		return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
			MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
			model);
	    }

	    logger.debug("tokenAuthGCSP clientId[{}]\n cert{}", gcspId, clientCert);
	} else {

	    logger.debug("tokenAuthGCSP clientCert is empty");

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
	}

	logger.debug("tokenAuthGCSP clientIp[" + clientIp + "]");

	if (StringUtils.isEmpty(clientIp)) {
	    clientIp = CommonUtils.getClientIP();

	    logger.debug("tokenAuthGCSP Header clientIp is empty. Common Ip[" + clientIp + "]");
	}

	if (!StringUtils.equalsIgnoreCase(Constant.GCSP_API_IP_CHECK_YN, "Y"))
	    clientIp = null;

	GcspVO gcspVO = authService.checkGcspInfo(gcspId, clientIp);

	if (gcspVO == null) {

	    logger.debug(
		    "tokenAuthGCSP failed. Gcsp Info is null. check request gcsp Info - gcspId : {}, clientIp : {}",
		    gcspId, clientIp);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.gcsp") + " ID[" + gcspId + "] IP["
			    + clientIp + "]",
		    commonHeaderVo, null, model);
	}

	// 사용자 OTP 인증 체크
	UserVO userVO = authService.checkLoginByOtpToken(otpToken, gcspId);

	if (userVO != null) {
	    // 응답 정보 생성
	    Map<String, Object> resultData = authService.setGcspAuth(userVO, gcspId, clientIp, otpToken);

	    logger.debug("tokenAuthGCSP succeed. userId[{}] gcspId[{}] clientId[{}]", userVO.getUserId(), gcspId);
	    logger.debug("tokenAuthGCSP resultData - ");
	    resultData.forEach((key, value) -> {
		logger.debug("{} : {}", key, value.toString());
	    });

	    return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo,
		    resultData, model);
	}
	// 인증 실패
	else {
	    LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
	    loginHistoryVO.setToken(otpToken);
	    loginHistoryVO.setActTp(Constant.LOGIN_ACTION_TYPE_OTP_LOGIN);
	    loginHistoryVO.setClientId(gcspId);
	    loginHistoryVO.setIp(clientIp);
	    loginHistoryVO.setResponseCd(Constant.COMMON_MSG_FAIL);

	    logService.insertLoginHistory(loginHistoryVO);

	    logger.debug("tokenAuthGCSP failed. gcspId[{}] otpToken[{}] userVO is null", gcspId, otpToken);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail"), commonHeaderVo, null, model);
	}
    }

    @RequestMapping(value = "/userlist", method = { RequestMethod.POST })
    public @ResponseBody Map<?, ?> checkUsers(@RequestParam(value = "user_list", required = false) List<String> users,
	    HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {

	String clientCert = req.getHeader(Constant.H_CERT);
	String clientIp = req.getHeader(Constant.H_REALIP);
	String gcspId = null;

	CommonHeaderVO commonHeaderVo = new CommonHeaderVO();

	if (users == null || users.size() <= 0) {

	    logger.debug("checkUsers invalid param => users is empty");

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.required.param"), commonHeaderVo, null, model);
	}

	logger.debug("checkUsers params => users[{}] : {}", users.size(), users.toString());

	if (StringUtils.isNotEmpty(clientCert)) {
	    clientCert = clientCert.replaceAll("[\n\t\r]", " ");
	    gcspId = ParseCertificate.parseCert(clientCert);

	    if (gcspId == null) {
		logger.debug("checkUsers clientCert parse Exection");

		return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
			MessageSourceHelper.getMessage("common.msg.auth.fail.invalid.cert"), commonHeaderVo, null,
			model);
	    }

	    logger.debug("checkUsers clientId[{}] \n cert{}", gcspId, clientCert);
	} else {

	    logger.debug("checkUsers clientCert is empty");

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.empty.cert"), commonHeaderVo, null, model);
	}

	logger.debug("tokenAuthGCSP clientIp[" + clientIp + "]");

	if (StringUtils.isEmpty(clientIp)) {
	    clientIp = CommonUtils.getClientIP();

	    logger.debug("tokenAuthGCSP Header clientIp is empty. Common Ip[" + clientIp + "]");
	}

	if (!StringUtils.equalsIgnoreCase(Constant.GCSP_API_IP_CHECK_YN, "Y"))
	    clientIp = null;

	GcspVO gcspVO = authService.checkGcspInfo(gcspId, clientIp);

	if (gcspVO == null) {

	    logger.debug("checkUsers failed. Gcsp Info is null. check request gcsp Info - gcspId : {}, clientIp : {}",
		    gcspId, clientIp);

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.gcsp") + " ID[" + gcspId + "] IP["
			    + clientIp + "]",
		    commonHeaderVo, null, model);
	}

	List<?> userList = authService.getUserList(users, gcspId);
	Map<String, Object> resultData = new HashMap<>();
	resultData.put("userList", userList);

	if (userList != null && userList.size() > 0) {

	    logger.debug("checkUsers succeed. userList size : {}", userList.size());
	    logger.debug("result UserList : {}]", users.toString());

	    return CommonUtils.createResult(Constant.COMMON_MSG_SUCCESS, Constant.RSP_CODE_OK, "", commonHeaderVo,
		    resultData, model);
	} else {

	    logger.debug("checkUsers failed. userList is empty");

	    return CommonUtils.createResult(Constant.COMMON_MSG_FAIL, Constant.RSP_CODE_AUTH_FAIL,
		    MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.user"), commonHeaderVo, null, model);
	}
    }
}