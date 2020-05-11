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



import java.util.HashMap;

/**
 * task data bean for gooroom job
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class Task {

	private String task_name;
	private HashMap<String, String> in;

	public Task() {
		this.task_name = "";
		this.in = null;
	}

	public Task(String task_name, HashMap<String, String> in) {
		this.task_name = task_name;
		this.in = in;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public HashMap<String, String> getIn() {
		return in;
	}

	public void setIn(HashMap<String, String> in) {
		this.in = in;
	}

}
