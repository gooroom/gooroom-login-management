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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * login information data bean for PAM.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class PamLoginInfoVO extends GcspLoginInfoVO implements Serializable {

	private static final long serialVersionUID = 7448144044386506154L;

	private String login_token;
	private String pwd_last_day;
	private String pwd_max_day;
	private String pwd_temp_yn;

	private String passphrase;
	private String expire_dt;
	private String expire_remain_day;
	private String dept_expire_dt;
	private String dept_expire_remain_day;
	private String pwd_expire_dt;
	private String pwd_expire_remain_day;

	public String getLogin_token() {
		return login_token;
	}

	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}

	public String getPwd_last_day() {
		return pwd_last_day;
	}

	public void setPwd_last_day(String pwd_last_day) {
		this.pwd_last_day = pwd_last_day;
	}

	public String getPwd_max_day() {
		return pwd_max_day;
	}

	public void setPwd_max_day(String pwd_max_day) {
		this.pwd_max_day = pwd_max_day;
	}

	public String getPwd_temp_yn() {
		return pwd_temp_yn;
	}

	public void setPwd_temp_yn(String pwd_temp_yn) {
		this.pwd_temp_yn = pwd_temp_yn;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getExpire_dt() {
		return expire_dt;
	}

	public void setExpire_dt(String expire_dt) {
		this.expire_dt = expire_dt;
	}

	public String getExpire_remain_day() {
		return expire_remain_day;
	}

	public void setExpire_remain_day(String expire_remain_day) {
		this.expire_remain_day = expire_remain_day;
	}

	public String getDept_expire_dt() {
		return dept_expire_dt;
	}

	public void setDept_expire_dt(String dept_expire_dt) {
		this.dept_expire_dt = dept_expire_dt;
	}

	public String getDept_expire_remain_day() {
		return dept_expire_remain_day;
	}

	public void setDept_expire_remain_day(String dept_expire_remain_day) {
		this.dept_expire_remain_day = dept_expire_remain_day;
	}
	
	public String getPwd_expire_dt() {
		return pwd_expire_dt;
	}

	public void setPwd_expire_dt(String pwd_expire_dt) {
		this.pwd_expire_dt = pwd_expire_dt;
	}

	public String getPwd_expire_remain_day() {
		return pwd_expire_remain_day;
	}

	public void setPwd_expire_remain_day(String pwd_expire_remain_day) {
		this.pwd_expire_remain_day = pwd_expire_remain_day;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
