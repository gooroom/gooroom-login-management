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

import java.io.Serializable;

/**
 * common header data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class CommonHeaderVO implements Serializable {

    private static final long serialVersionUID = -4276539527633969279L;

    private String result;
    private String resultCode;
    private String message;

    public void setResultInfo(String result, String resultCode, String message) {
	this.result = result;
	this.resultCode = resultCode;
	this.message = message;
    }

    public String getResult() {
	return result;
    }

    public void setResult(String result) {
	this.result = result;
    }

    public String getResultCode() {
	return resultCode;
    }

    public void setResultCode(String resultCode) {
	this.resultCode = resultCode;
    }

    public String getErrMsg() {
	return message;
    }

    public void setErrMsg(String message) {
	this.message = message;
    }

}
