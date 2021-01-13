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

import java.io.Serializable;
import java.util.Date;

/**
 * login history data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class LoginHistoryVO implements Serializable {

    private static final long serialVersionUID = -820302124680871913L;

    private String userId;
    private String actTp;
    private String clientId;
    private String ip;
    private String token;
    private String responseCd;
    private Date regDt;

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public String getActTp() {
	return actTp;
    }

    public void setActTp(String actTp) {
	this.actTp = actTp;
    }

    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    public String getIp() {
	return ip;
    }

    public void setIp(String ip) {
	this.ip = ip;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public String getResponseCd() {
	return responseCd;
    }

    public void setResponseCd(String responseCd) {
	this.responseCd = responseCd;
    }

    public Date getRegDt() {
	return regDt;
    }

    public void setRegDt(Date regDt) {
	this.regDt = regDt;
    }

}
