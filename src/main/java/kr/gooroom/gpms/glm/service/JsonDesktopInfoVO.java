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
import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * dekstop information data bean for json format
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class JsonDesktopInfoVO implements Serializable {

    private static final long serialVersionUID = 3538239955449723982L;

    private ArrayList<JsonAppsVO> apps;
    private ArrayList<JsonMountsVO> mounts;

    public ArrayList<JsonAppsVO> getApps() {
	return apps;
    }

    public void setApps(ArrayList<JsonAppsVO> apps) {
	this.apps = apps;
    }

    public ArrayList<JsonMountsVO> getMounts() {
	return mounts;
    }

    public void setMounts(ArrayList<JsonMountsVO> mounts) {
	this.mounts = mounts;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
