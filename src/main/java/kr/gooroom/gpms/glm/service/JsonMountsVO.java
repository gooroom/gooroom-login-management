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
 * mount application information data bean for json
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class JsonMountsVO implements Serializable {

    private static final long serialVersionUID = 3538239955449723982L;

    private String name;
    private String protocol;
    private String url;
    private String mountpoint;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getProtocol() {
	return protocol;
    }

    public void setProtocol(String protocol) {
	this.protocol = protocol;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getMountpoint() {
	return mountpoint;
    }

    public void setMountpoint(String mountpoint) {
	this.mountpoint = mountpoint;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

}
