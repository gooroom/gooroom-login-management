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
 * Configuration item data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class CtrlItemVO implements Serializable {

    private static final long serialVersionUID = 2170216387668746133L;

    private String objId;
    private String propId;
    private String propNm;
    private String propValue;
    private String link;
    private String modUserId;
    private Date modDt;

    public String getObjId() {
	return objId;
    }

    public void setObjId(String objId) {
	this.objId = objId;
    }

    public String getPropId() {
	return propId;
    }

    public void setPropId(String propId) {
	this.propId = propId;
    }

    public String getPropNm() {
	return propNm;
    }

    public void setPropNm(String propNm) {
	this.propNm = propNm;
    }

    public String getPropValue() {
	return propValue;
    }

    public void setPropValue(String propValue) {
	this.propValue = propValue;
    }

    public String getLink() {
	return link;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public String getModUserId() {
	return modUserId;
    }

    public void setModUserId(String modUserId) {
	this.modUserId = modUserId;
    }

    public Date getModDt() {
	return modDt;
    }

    public void setModDt(Date modDt) {
	this.modDt = modDt;
    }

}
