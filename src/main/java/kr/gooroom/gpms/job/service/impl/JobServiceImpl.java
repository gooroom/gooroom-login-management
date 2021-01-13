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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * Gooroom job management service implemts class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("jobService")
public class JobServiceImpl implements JobService {

	private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	@Resource(name = "jobDAO")
	private JobDAO jobDAO;

	/**
	 * create new job by job data bean.
	 * 
	 * @param jobVO JobVO job configuration data bean.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createJob(JobVO jobVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long reCnt1 = jobDAO.createJobMaster(jobVO);

			if (reCnt1 > 0) {
				long reCnt2 = 0;
				String[] clientArray = jobVO.getClientIds();
				for (int i = 0; i < clientArray.length; i++) {
					jobVO.setClientId(clientArray[i]);
					reCnt2 = jobDAO.createJobTarget(jobVO);
				}

				if (reCnt2 > 0) {
					statusVO.setResultInfo("success", Constant.CODE_INSERT,
							MessageSourceHelper.getMessage("job.result.insert"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo("fail", Constant.CODE_INSERTERROR,
							MessageSourceHelper.getMessage("job.result.noinsert"));
				}
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo("fail", Constant.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("job.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createJob (by bean) : {}, {}, {}", Constant.CODE_SYSERROR,
					MessageSourceHelper.getMessage(Constant.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo("fail", Constant.CODE_SYSERROR,
						MessageSourceHelper.getMessage(Constant.MSG_SYSERROR));
			}
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createJob (by bean) : {}, {}, {}", Constant.CODE_SYSERROR,
					MessageSourceHelper.getMessage(Constant.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo("fail", Constant.CODE_SYSERROR,
						MessageSourceHelper.getMessage(Constant.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

}
