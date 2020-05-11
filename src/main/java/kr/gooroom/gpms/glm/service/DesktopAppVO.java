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

/**
 * desktop information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class DesktopAppVO implements Serializable {

    private static final long serialVersionUID = 601168556065068176L;

    private String desktopId;
    private String desktopGubun;
    private String position;
    private String order;
    private String themeId;

    private DesktopAppInfoVO desktop;

    public String getDesktopId() {
        return desktopId;
    }

    public void setDesktopId(String desktopId) {
        this.desktopId = desktopId;
    }

    public String getDesktopGubun() {
        return desktopGubun;
    }

    public void setDesktopGubun(String desktopGubun) {
        this.desktopGubun = desktopGubun;
    }

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

    public DesktopAppInfoVO getDesktop() {
	return desktop;
    }

    public void setDesktop(DesktopAppInfoVO desktop) {
	this.desktop = desktop;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }


}
