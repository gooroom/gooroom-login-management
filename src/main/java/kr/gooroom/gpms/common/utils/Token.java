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

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Token {

    private String salt;
    private String issuer;
    private long expInterval;

    public Token(String salt, String issuer, String expInterval) {
	this.salt = salt;
	this.issuer = issuer;
	this.expInterval = Long.parseLong(expInterval);
    }

    /**
     * create token
     *
     * @param String
     *            clientId, String userId
     * @return String
     * @throws JwtException
     */
    public String genLoginToken(String clientId, String userId) throws Exception {
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	long nowMillis = cal.getTimeInMillis() + expInterval;
	Date now = new Date(nowMillis);

	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(clientId + salt + issuer);
	Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

	JwtBuilder builder = Jwts.builder().setId(userId).setExpiration(now).signWith(signatureAlgorithm, key);

	return builder.compact();
    }

    /**
     * create token
     *
     * @param String
     *            clientIp, String userId
     * @return String
     * @throws JwtException
     */
    public String genOtpToken(String clientIp, String gcspId, String userId) throws Exception {
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	long nowMillis = cal.getTimeInMillis() + expInterval;
	Date now = new Date(nowMillis);

	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(clientIp + gcspId + salt + issuer);
	Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

	JwtBuilder builder = Jwts.builder().setId(userId).setExpiration(now).signWith(signatureAlgorithm, key);

	return builder.compact();
    }

    /**
     * parse token
     *
     * @param String
     *            token, String userId
     * @return String
     * @throws JwtException
     */
    public String parseToken(String token, String userId) throws Exception {
	Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(userId + salt + issuer))
		.parseClaimsJws(token).getBody();

	return claims.getId();
    }
}
