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

package kr.gooroom.gpms.common.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseCertificate {

    private final static Logger logger = LoggerFactory.getLogger(ParseCertificate.class);

    public static String parseCert(String cert) {

	String certCn = null;

	cert = parseWebServerCert(cert);

	logger.debug("ParseCertificate parseWebServerCert[{}]", cert);

	try {
	    InputStream stream = new ByteArrayInputStream(cert.getBytes("UTF-8"));

	    CertificateFactory fact = CertificateFactory.getInstance("X.509");
	    X509Certificate myCert = (X509Certificate) fact.generateCertificate(stream);

	    X500Name x500name = new JcaX509CertificateHolder(myCert).getSubject();
	    RDN cn = x500name.getRDNs(BCStyle.CN)[0];
	    certCn = IETFUtils.valueToString(cn.getFirst().getValue());
		certCn = certCn.replaceAll("\\\\", "");

	} catch (Exception e) {
	    e.printStackTrace();
	    certCn = null;
	}

	return certCn;
    }

	private static String parseWebServerCert(String cert) {

		String paserCert = null;

		if (StringUtils.indexOf(cert, "                                                                                                   ") > 0) {

			logger.debug("Nginx clientcert data replace");

			// Nginx clientcert data replace
			paserCert = cert.replace("-----BEGIN CERTIFICATE-----", "-----BEGIN CERTIFICATE-----\n");
			paserCert = paserCert.replace("-----END CERTIFICATE-----", "\n-----END CERTIFICATE-----");
			paserCert = paserCert.replaceAll("                                                                                                   ", "\n");

		} else {

			logger.debug("Apache clientcert data replace");

			// Apache clientcert data replace
			paserCert = cert.replace("-----BEGIN CERTIFICATE-----", "-----BEGINCERTIFICATE-----");
			paserCert = paserCert.replace("-----END CERTIFICATE-----", "-----ENDCERTIFICATE-----");
			paserCert = paserCert.replaceAll(" ", "\n");
			paserCert = paserCert.replace("-----BEGINCERTIFICATE-----", "-----BEGIN CERTIFICATE-----");
			paserCert = paserCert.replace("-----ENDCERTIFICATE-----", "-----END CERTIFICATE-----");
		}

		return paserCert;
	}
}
