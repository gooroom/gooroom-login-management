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

/**
 * DesktopInfo 관리 서비스
 *
 * @author ymlee
 * @since 2017
 * @version 1.0
 *
 *          <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------       --------    ---------------------------
 *   2017.
 *          </pre>
 */

public interface DesktopInfoService {

    /**
     * DesktopInfo 반환
     * 
     * @param confId
     * @return
     * @throws Exception
     */
    public DesktopInfoVO getDesktopInfo(String confId) throws Exception;

    /**
     * DesktopInfo 반환
     * 
     * @param confName
     * @return
     * @throws Exception
     */
    public DesktopInfoVO getDesktopInfoByName(String confName) throws Exception;

    /**
     * DesktopInfo 반환
     * 
     * @param clientId
     * @return
     * @throws Exception
     */
    public DesktopInfoVO getDesktopInfoByUserAndClient(String userId, String clientId) throws Exception;

}
