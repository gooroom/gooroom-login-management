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

package kr.gooroom.gpms.job;

import java.io.StringWriter;
import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.common.utils.Constant;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.nodes.Job;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * Gooroom Job management class.
 * <p>
 * job create and target select process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Component
public class CustomJobMaker {

	private static final Logger logger = LoggerFactory.getLogger(CustomJobMaker.class);

	@Resource(name = "jobService")
	private JobService jobService;

	/**
	 * create job with clients list and job name
	 * 
	 * @param jobName   string job name.
	 * @param clientIds string user id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobWithClientIds(String moduleName, String taskName, String[] clientArray, HashMap<String, String> map) throws Exception {

		try {
			// create job
			Job[] jobs = new Job[1];
			
			if (map != null && map.size() > 0) {
				jobs[0] = Job.generateJobWithMap(moduleName, taskName, map);
			} else {
				jobs[0] = Job.generateJob(moduleName, taskName);
			}

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("CustomJobMaker.createJobWithClientIds (make json) Exception occurred. ", jsonex);
			} finally {
				try {
					if (outputWriter != null) {
						outputWriter.close();
					}
				} catch (Exception finalex) {
				}
			}

			JobVO jobVO = new JobVO();
			jobVO.setJobData(jsonStr);
			jobVO.setJobName(taskName);
			jobVO.setRegUserId("LOGIN_SYSTEM");
			jobVO.setClientIds(clientArray);

			jobService.createJob(jobVO);

		} catch (Exception ex) {
			logger.error("error in createJobWithClientIds - customJobMaker : {}, {}, {}", Constant.CODE_SYSERROR,
					MessageSourceHelper.getMessage(Constant.MSG_SYSERROR), ex.toString());
		}
	}

}
