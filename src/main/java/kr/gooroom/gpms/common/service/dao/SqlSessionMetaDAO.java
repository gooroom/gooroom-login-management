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

package kr.gooroom.gpms.common.service.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * @Class Name : SqlSessionMetaDAO.java
 * @Description : SqlSessionMetaDAO Class
 * @Modification Information
 * @
 *   @ 수정일 수정자 수정내용
 *   @ ---------- --------- -------------------------------
 *   @ 2017.05.08 LDH 최초생성
 *
 * @since 2017.05.08
 * @version 1.0
 * @see
 *
 *      Copyright (C) by Hancom All right reserved.
 */

public abstract class SqlSessionMetaDAO {

    @Resource(name = "sqlSessionMeta")
    protected SqlSessionTemplate sqlSessionMeta;

}
