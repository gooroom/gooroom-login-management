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
 * application information data bean for json format
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class JsonAppsVO implements Serializable {

    private static final long serialVersionUID = 3538239955449723982L;

    private String position;
    private String order;
    private JsonDesktopVO desktop;

    public String getPosition() {
	return position;
    }

    public void setPosition(String position) {
	this.position = position;
    }

    public String getOrder() {
	return order;
    }

    public void setOrder(String order) {
	this.order = order;
    }

    public JsonDesktopVO getDesktop() {
	return desktop;
    }

    public void setDesktop(JsonDesktopVO desktop) {
	this.desktop = desktop;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
