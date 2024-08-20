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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * user information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class UserClientUseHistoryVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 3538239955449723982L;

	private String userId;
	private String useClientId;
	private String actTp;

	private Date redDt;
	private Date onLastCheckDt;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUseClientId() {
		return useClientId;
	}

	public void setUseClientId(String useClientId) {
		this.useClientId = useClientId;
	}

	public String getActTp() {
		return actTp;
	}

	public void setActTp(String actTp) {
		this.actTp = actTp;
	}

	public Date getRedDt() {
		return redDt;
	}

	public void setRedDt(Date redDt) {
		this.redDt = redDt;
	}

	public Date getOnLastCheckDt() {
		return onLastCheckDt;
	}

	public void setOnLastCheckDt(Date onLastCheckDt) {
		this.onLastCheckDt = onLastCheckDt;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
