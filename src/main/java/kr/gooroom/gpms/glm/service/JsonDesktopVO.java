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
 * desktop application information data bean for json
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class JsonDesktopVO implements Serializable {

    private static final long serialVersionUID = 3538239955449723982L;

    private String Name;
    private String Comment;
    private String Exec;
    private String Icon;

    public String getName() {
	return Name;
    }

    public void setName(String name) {
	this.Name = name;
    }

    public String getComment() {
	return Comment;
    }

    public void setComment(String comment) {
	this.Comment = comment;
    }

    public String getExec() {
	return Exec;
    }

    public void setExec(String exec) {
	this.Exec = exec;
    }

    public String getIcon() {
	return Icon;
    }

    public void setIcon(String icon) {
	this.Icon = icon;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
