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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * user information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class UserVO implements Serializable {

	private static final long serialVersionUID = 3538239955449723982L;

	private String userId;
	private String userNm;
	private String loginId;
	private String status;
	private Date firstLoginDt;
	private Date lastLoginDt;
	private String deptCd;
	private String modUserId;
	private Date modDt;
	private String statusCd;
	private String checkCd;
	private String nfcSecretData;
	private Date lastPasswdChDt;
	private String userPasswdStatus;
	private String isPasswdExpire;

	private Date expireDt;
	private String expireRemainDay;
	private String remainLoginTrial;
	
	private int loginTrial;
	private int loginElapsedTime;
	private int loginRemainMinutes;
	private String userEmail;

	private Date deptExpireDt;
	private String deptExpireRemainDay;
	private Date pwdExpireDt;
	private String pwdExpireRemainDay;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getFirstLoginDt() {
		return firstLoginDt;
	}

	public void setFirstLoginDt(Date firstLoginDt) {
		if (firstLoginDt != null)
			this.firstLoginDt = firstLoginDt;
	}

	public Date getLastLoginDt() {
		return lastLoginDt;
	}

	public void setLastLoginDt(Date lastLoginDt) {
		if (lastLoginDt != null)
			this.lastLoginDt = lastLoginDt;
	}

	public String getDeptCd() {
		return deptCd;
	}

	public void setDeptCd(String deptCd) {
		this.deptCd = deptCd;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserDd) {
		this.modUserId = modUserDd;
	}

	public Date getModDt() {
		return modDt;
	}

	public void setModDt(Date modDt) {
		if (modDt != null)
			this.modDt = modDt;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public String getCheckCd() {
		return checkCd;
	}

	public void setCheckCd(String checkCd) {
		this.checkCd = checkCd;
	}

	public String getNfcSecretData() {
		return nfcSecretData;
	}

	public void setNfcSecretData(String nfcSecretData) {
		this.nfcSecretData = nfcSecretData;
	}

	public Date getLastPasswdChDt() {
		return lastPasswdChDt;
	}

	public void setLastPasswdChDt(Date lastPasswdChDt) {
		this.lastPasswdChDt = lastPasswdChDt;
	}

	public String getUserPasswdStatus() {
		return userPasswdStatus;
	}

	public void setUserPasswdStatus(String userPasswdStatus) {
		this.userPasswdStatus = userPasswdStatus;
	}

	public String getIsPasswdExpire() {
		return isPasswdExpire;
	}

	public void setIsPasswdExpire(String isPasswdExpire) {
		this.isPasswdExpire = isPasswdExpire;
	}

	public Date getExpireDt() {
		return expireDt;
	}

	public void setExpireDt(Date expireDt) {
		this.expireDt = expireDt;
	}

	public String getExpireRemainDay() {
		return expireRemainDay;
	}

	public void setExpireRemainDay(String expireRemainDay) {
		this.expireRemainDay = expireRemainDay;
	}

	public String getRemainLoginTrial() {
		return remainLoginTrial;
	}

	public void setRemainLoginTrial(String remainLoginTrial) {
		this.remainLoginTrial = remainLoginTrial;
	}

	public int getLoginTrial() {
		return loginTrial;
	}

	public void setLoginTrial(int loginTrial) {
		this.loginTrial = loginTrial;
	}
	
	public int getLoginElapsedTime() {
		return loginElapsedTime;
	}

	public void setLoginElapsedTime(int loginElapsedTime) {
		this.loginElapsedTime = loginElapsedTime;
	}

	public int getLoginRemainMinutes() {
		return loginRemainMinutes;
	}

	public void setLoginRemainMinutes(int loginRemainMinutes) {
		this.loginRemainMinutes = loginRemainMinutes;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getDeptExpireDt() {
		return deptExpireDt;
	}

	public void setDeptExpireDt(Date deptExpireDt) {
		this.deptExpireDt = deptExpireDt;
	}

	public String getDeptExpireRemainDay() {
		return deptExpireRemainDay;
	}

	public void setDeptExpireRemainDay(String deptExpireRemainDay) {
		this.deptExpireRemainDay = deptExpireRemainDay;
	}

	public Date getPwdExpireDt() {
		return pwdExpireDt;
	}

	public void setPwdExpireDt(Date pwdExpireDt) {
		this.pwdExpireDt = pwdExpireDt;
	}

	public String getPwdExpireRemainDay() {
		return pwdExpireRemainDay;
	}

	public void setPwdExpireRemainDay(String pwdExpireRemainDay) {
		this.pwdExpireRemainDay = pwdExpireRemainDay;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
