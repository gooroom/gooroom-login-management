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

package kr.gooroom.gpms.common.service;

import kr.gooroom.gpms.glm.service.IpRulesVO;

public interface CommonService {

    /**
     * 접속 IP 정책 조회 이력 기록
     * 
     * @return
     * @throws Exception
     */
    public IpRulesVO getAvailableIpRule(String clientId) throws Exception;

    /**
     * 접속 IP 가 허용되는지 확인
     * 
     * @return
     * @throws Exception
     */
    public CommonResultVO isAvailableIp(String clientId, String clientIp) throws Exception;

}
