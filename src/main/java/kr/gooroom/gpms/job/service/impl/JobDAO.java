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

package kr.gooroom.gpms.job.service.impl;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * data access object class for gooroom job management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("jobDAO")
public class JobDAO extends SqlSessionMetaDAO {

	/**
	 * create job master by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobMaster(JobVO jobVO) throws SQLException {

		return (long) sqlSessionMeta.insert("insertJobMaster", jobVO);

	}

	/**
	 * create job target by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createJobTarget(JobVO jobVO) throws SQLException {

		return (long) sqlSessionMeta.insert("insertJobTarget", jobVO);

	}

}
