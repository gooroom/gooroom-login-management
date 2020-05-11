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

package kr.gooroom.gpms.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constant {

    private static Properties prop = new Properties();
    private static final String GOOROOM_PROPERTIES = "/properties/gooroomapi.properties";

    static {
	InputStream is = Constant.class.getClassLoader().getResourceAsStream(GOOROOM_PROPERTIES);
	try {
	    prop.load(is);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static final String COMMON_MSG_SUCCESS = "SUCCESS";
    public static final String COMMON_MSG_FAIL = "FAIL";

    public static final String AUTH_TOKEN_API = prop.getProperty("gooroom.api.authToken");

    public static final String AUTH_PAM_API = prop.getProperty("gooroom.api.glm.pam.auth");
    public static final String AUTH_GCSP_API = prop.getProperty("gooroom.api.glm.gcsp.auth");

    public static final String AUTH_PAM_API_PREFIX = prop.getProperty("gooroom.api.glm.pam.prefix");
    public static final String AUTH_GCSP_API_PREFIX = prop.getProperty("gooroom.api.glm.gcsp.prefix");

    public static final String TOKEN_SALT = prop.getProperty("gooroom.token.salt");
    public static final String TOKEN_ISSUER = prop.getProperty("gooroom.token.issuer");
    public static final String TOKEN_INTERVAL = prop.getProperty("gooroom.token.interval");

    public static final String TOKEN_OTP_ALLOW_CNT = prop.getProperty("gooroom.token.otp.allow.cnt");
    public static final String TOKEN_OTP_EXPIRATION_MINUTE = prop.getProperty("gooroom.token.otp.expiration.minute");

    public static final String GCSP_API_IP_CHECK_YN = prop.getProperty("gooroom.api.gcsp.ip.check.yn");

    public static final int TOKEN_OTP_INIT_CNT = 0;
    public static final int TOKEN_PASS_VALUE = -1;

    // token status_cd
    public static final String TOKEN_LOGIN_STATUS_CODE_VALID = "TOKEN_VALID";
    public static final String TOKEN_LOGIN_STATUS_CODE_EXPIRE = "TOKEN_EXPIRE";
    public static final String TOKEN_OTP_STATUS_CODE_VALID = "OTP_VALID";
    public static final String TOKEN_OTP_STATUS_CODE_EXPIRE = "OTP_EXPIRE";

    public static final String RSP_CODE_OK = "200";
    public static final String RSP_CODE_NOT_AUTH = "401";
    public static final String RSP_CODE_NO_DATA = "404";
    public static final String RSP_CODE_INTERNAL_ERR = "444";
    
    public static final String RSP_CODE_USEREXPIRE = "GR45";
    public static final String RSP_CODE_DEPTEXPIRE = "GR44";
    public static final String RSP_CODE_LOGINTRIALEXPIRE = "GR46";
    public static final String RSP_CODE_BEFOREELAPSEDLOGINTIME = "GR47";
    public static final String RSP_CODE_NODUPLICATELOGIN = "GR48";
// don't setup error code - request from gooroom terminal by cjheo
//  public static final String RSP_CODE_PASSWORDEXPIRE = "GR49";

    public static final String RSP_CODE_AUTH_FAIL = "ELM000AUTHF";
    public static final String RSP_CODE_AUTH_FAIL_NOTEXIST_ID = "ELM001AUTHF";
    public static final String RSP_CODE_AUTH_FAIL_INVALID_PW = "ELM002AUTHF";
    public static final String RSP_CODE_AUTH_EXPIRED = "ELM005AUTHF";

    public static final String RSP_CODE_AUTH_FAIL_NOTEXIST_IPRULE = "ELM006AUTHF";
    public static final String RSP_CODE_AUTH_FAIL_NOTALLOW_CLIENTIP = "ELM007AUTHF";

    public static final String COMMON_HEADER = "status";
    public static final String COMMON_BODY = "data";

    public static final String COMMON_HEADER_RESULT = "result";
    public static final String COMMON_HEADER_RESULTCODE = "resultCode";
    public static final String COMMON_HEADER_MESSAGE = "message";

    public static final String H_AUTH = "auth";
    public static final String H_CID = "client_id";
    public static final String H_CERT = "gooroom-client-cert";
    public static final String H_TOKEN = "gooroom-client-token";
    public static final String H_REALIP = "gooroom-real-ip";
    
    public static final String H_SERVICEID = "service_id";

    public static final String COMMON_STATUS_OK = "STAT010";
    public static final String COMMON_STATUS_DELETE = "STAT020";
    public static final String COMMON_STATUS_TEMP = "STAT030";

    public static final String GCSP_TEMP_CLIENT_ID = "GCSP_SERVER_CLIENT_001";

    public static final String DESKTOPINFO_OTPTOKEN = "\\{otpToken\\}";

    // loginHistory ActionType
    public static final String LOGIN_ACTION_TYPE_PAM_LOGIN = "PAM_LOGIN";
    public static final String LOGIN_ACTION_TYPE_PAM_LOGIN_TOKEN = "PAM_LOGIN_TOKEN";
    public static final String LOGIN_ACTION_TYPE_PAM_LOGIN_OTP = "PAM_LOGIN_OTP";
    public static final String LOGIN_ACTION_TYPE_PAM_LOGOUT = "PAM_LOGOUT";
    public static final String LOGIN_ACTION_TYPE_GCSP_LOGIN = "GCSP_LOGIN";
    public static final String LOGIN_ACTION_TYPE_GCSP_LOGOUT = "GCSP_LOGOUT";
    public static final String LOGIN_ACTION_TYPE_OTP_LOGIN = "OTP_LOGIN";
    public static final String LOGIN_ACTION_TYPE_OTP_LOGOUT = "OTP_LOGOUT";

    public static final String PROP_PASSWORD_TIME = "password_time";
    public static final String PROP_SCREEN_TIME = "screen_time";

    public static final String GCSP_OTP_TOKEN_VALUE = "gooroomGcspOtpTokenValue1234";
    
    // LOGGING CODE
    public static final String CODE_SYSERROR = "ERR9999";
	public static final String MSG_SYSERROR = "system.common.error";
	
	public static final String CODE_SELECT = "GRSM0010";
	public static final String CODE_SELECTERROR = "GRSM3010";
	public static final String CODE_INSERT = "GRSM0011";
	public static final String CODE_INSERTERROR = "GRSM3011";
	public static final String CODE_DELETE = "GRSM0012";
	public static final String CODE_DELETEERROR = "GRSM3012";
	public static final String CODE_UPDATE = "GRSM0013";
	public static final String CODE_UPDATEERROR = "GRSM3013";
    
    // icon path
    public static final String ICON_SERVER_PROTOCOL = prop.getProperty("gooroom.icon.server.protocol");
    public static final String ICON_SERVERPATH = prop.getProperty("gooroom.icon.server.path");
    public static final String PATH_FOR_ICONURL = prop.getProperty("gooroom.icon.url.path");

	// Mail
	public static final String CFG_MAIL_HOST = prop.getProperty("gooroom.mail.host");
	public static final String CFG_MAIL_PORT = prop.getProperty("gooroom.mail.port");
	public static final String CFG_MAIL_USESSL = prop.getProperty("gooroom.mail.usessl");
	public static final String CFG_MAIL_AUTH_USERNAME = prop.getProperty("gooroom.mail.auth.username");
	public static final String CFG_MAIL_AUTH_PASSWORD = prop.getProperty("gooroom.mail.auth.password");
}
