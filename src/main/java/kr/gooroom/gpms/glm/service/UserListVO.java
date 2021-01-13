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

import org.apache.commons.lang3.StringUtils;

import kr.gooroom.gpms.common.utils.Constant;

/**
 * user list information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class UserListVO implements Serializable {

	private static final long serialVersionUID = 2679941845375921592L;

	private String loginId;
	private String userNm;
	private String status;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMsg() {
		if (StringUtils.equals(status, Constant.COMMON_STATUS_OK))
			status = "정상";
		else if (StringUtils.equals(status, Constant.COMMON_STATUS_DELETE))
			status = "삭제";
		return status;
	}

	public void setStatusMsg(String status) {
		this.status = status;
	}

}
