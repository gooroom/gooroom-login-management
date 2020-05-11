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
import java.util.List;

@SuppressWarnings("serial")
public class IpRulesVO implements Serializable {

	private boolean allAllow;
	private List<String> allowIpList;
	
	public IpRulesVO() {
		allowIpList = new ArrayList<String>();
		allAllow = false;
	}

	public boolean isAllAllow() {
		return allAllow;
	}

	public void setAllAllow(boolean allAllow) {
		this.allAllow = allAllow;
	}

	public List<String> getAllowIpList() {
		return allowIpList;
	}

	public void setAllowIpList(List<String> allowIpList) {
		this.allowIpList = allowIpList;
	}

	
		

}
