/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.http.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpMethod {
	private static final Map<String, HttpMethod> REGISTRY = new HashMap<>();

	public static final HttpMethod GET = valueOf("GET");
	public static final HttpMethod POST = valueOf("POST");
	public static final HttpMethod PUT = valueOf("PUT");
	public static final HttpMethod DELETE = valueOf("DELETE");
	public static final HttpMethod PATCH = valueOf("PATCH");
	public static final HttpMethod HEAD = valueOf("HEAD");
	public static final HttpMethod OPTIONS = valueOf("OPTIONS");

	private final String name;

	private HttpMethod(String name){
		this.name = name;
	}

	public static HttpMethod valueOf(String verb){
		return REGISTRY.computeIfAbsent(verb, HttpMethod::new);
	}

	public Set<HttpMethod> all(){
		return new HashSet<>(REGISTRY.values());
	}

	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
