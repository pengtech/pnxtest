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

import com.pnxtest.http.api.Header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Headers {

    private List<Header> headers = new ArrayList<>();

    public Headers() {
    }

    public Headers(Collection<Entry> entries) {
        entries.forEach(e -> add(e.name, e.value));
    }

    public void add(String name, String value) {
        add(name, () -> value);
    }


    public void add(String name, Supplier<String> value) {
        if (Objects.nonNull(name)) {
            headers.add(new Entry(name, value));
        }
    }


    public void replace(String name, String value) {
        remove(name);
        add(name, value);
    }

    private void remove(String name) {
        headers.removeIf(h -> isName(h, name));
    }

    public int size() {
        return headers.stream().map(Header::getName).collect(toSet()).size();
    }

    public List<String> get(String name) {
        return headers.stream()
                .filter(h -> isName(h, name))
                .map(Header::getValue)
                .collect(toList());
    }


    public void putAll(Headers header) {
        this.headers.addAll(header.headers);
    }


    public boolean containsKey(String name) {
        return this.headers.stream().anyMatch(h -> isName(h, name));
    }


    public void clear() {
        this.headers.clear();
    }


    public String getFirst(String key) {
        return headers
                .stream()
                .filter(h -> isName(h, key))
                .findFirst()
                .map(Header::getValue)
                .orElse("");
    }

    public List<Header> all() {
        return this.headers;
    }

    private boolean isName(Header h, String name) {
        return HttpUtil.nullToEmpty(name).equalsIgnoreCase(h.getName());
    }

    static class Entry implements Header {

        private final String name;
        private final Supplier<String> value;

        public Entry(String name, String value) {
            this.name = name;
            this.value = () -> value;
        }

        public Entry(String name, Supplier<String> value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value.get();
        }
    }
}
