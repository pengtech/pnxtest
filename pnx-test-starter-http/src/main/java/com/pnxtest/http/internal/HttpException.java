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

import java.util.Collection;
import java.util.stream.Collectors;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = -3067438387805337395L;

	public HttpException(Exception e) {
		super(e);
	}

	public HttpException(String msg) {
		super(msg);
	}

	public HttpException(Throwable ex) {
		super(ex);
	}

	public HttpException(String message, Throwable e) {
		super(message, e);
	}


	public HttpException(Collection<Exception> ex) {
		super(ex.stream().map(e -> e.getClass().getName() + " " + e.getMessage()).collect(Collectors.joining("\n")));
	}
}
