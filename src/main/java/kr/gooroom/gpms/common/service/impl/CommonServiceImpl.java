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

package kr.gooroom.gpms.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.service.CommonResultVO;
import kr.gooroom.gpms.common.service.CommonService;
import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.glm.service.CtrlItemVO;
import kr.gooroom.gpms.glm.service.IpRulesVO;
import kr.gooroom.gpms.glm.service.impl.CtrlItemDAO;

@Service("commonService")
public class CommonServiceImpl implements CommonService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "ctrlItemDAO")
	private CtrlItemDAO ctrlItemDAO;

    @Override
    public IpRulesVO getAvailableIpRule(String clientId) throws Exception {
    	
    	List<CtrlItemVO> re = ctrlItemDAO.selectAvailableIpRule(clientId);
    	if(re != null && re.size() > 0) {
    		IpRulesVO rulesVO = new IpRulesVO();
    		ArrayList<String> list = new ArrayList<String>();
    		for(CtrlItemVO vo : re) {
    			if("WHITEIPALL".equals(vo.getPropNm())) {
    				if("true".equalsIgnoreCase(vo.getPropValue())) {
    					rulesVO.setAllAllow(true);
    				} else {
    					rulesVO.setAllAllow(false);
    				}
    			} else if("WHITEIPS".equals(vo.getPropNm())) {
    				list.add(vo.getPropValue());
    			}
    		}
    		
    		if(list != null && list.size() > 0) {
    			rulesVO.setAllowIpList(list);
    		}
    		
    		return rulesVO;
    		
    	} else {
    		return null;	
    	}
    	
    }

	@Override
	public CommonResultVO isAvailableIp(String clientId, String clientIp) throws Exception {
		
		CommonResultVO result = new CommonResultVO();
		
		IpRulesVO ips = getAvailableIpRule(clientId);
		
		if(ips != null) {
			if(ips.isAllAllow()) {
				// all enable.
				result.setSuccess(true);
			} else {
				// check, compare client ip
				List<String> ipList = ips.getAllowIpList();
				for(String iprule : ipList) {
					if(iprule.indexOf("-") > -1) {
						String[] ip_period = iprule.split("-");
						long fromValue = ipToLong(ip_period[0]); 
						long toValue = ipToLong(ip_period[1]); 
						long clientIpValue = ipToLong(clientIp);
						if(fromValue > -1 && toValue > -1 && clientIpValue > -1) {
							if(ipToLong(ip_period[0]) <= ipToLong(clientIp) && ipToLong(ip_period[1]) >= ipToLong(clientIp)) {
								result.setSuccess(true);
								break;
							}
						}
					} else {
						if(ipToLong(iprule) == ipToLong(clientIp)) {
							result.setSuccess(true);
							break;
						}
					}
				}
				
				if(!result.isSuccess()) {
					// NOT ALLOW IP RULE
					result.setResultCode(Constant.RSP_CODE_AUTH_FAIL_NOTALLOW_CLIENTIP);
					result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.not.allow.clientip"));
					result.setSuccess(false);
					logger.debug("auth failed. clientId[{}] clientIp[{}] not allow clientip", clientId, clientIp);
				}
			}
		} else {
			// RULE MISSING, NOT ALLOW
			result.setResultCode(Constant.RSP_CODE_AUTH_FAIL_NOTEXIST_IPRULE);
			result.setResultMessage(MessageSourceHelper.getMessage("common.msg.auth.fail.not.exist.iprule"));
			result.setSuccess(false);
			logger.debug("auth failed. clientId[{}] clientIp[{}] not exist ip rule", clientId, clientIp);
		}
		
		return result;
	}
	
	
	private long ipToLong(String ipAddress) {
		if(ipAddress != null) {
			ipAddress = ipAddress.trim();
		
			String[] ipAddressInArray = ipAddress.split("\\.");

			long result = 0;
			for (int i = 0; i < ipAddressInArray.length; i++) {

				int power = 3 - i;
				int ip = Integer.parseInt(ipAddressInArray[i]);
				result += ip * Math.pow(256, power);

			}

			return result;
		} else {
			return -1;
		}
	}

}
