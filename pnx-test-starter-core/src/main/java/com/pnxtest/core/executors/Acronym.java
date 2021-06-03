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

package com.pnxtest.core.executors;


import com.pnxtest.core.util.StringUtil;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class Acronym {
    private final String acronymText;
    private final int start;
    private final int end;

    Acronym(String acronym, int start, int end) {
        this.acronymText = acronym;
        this.start = start;
        this.end = end;
    }

    public static Set<Acronym> acronymsIn(String text) {
        Set<Acronym> acronyms = new HashSet();
        List<String> words = Arrays.stream(text.split(Pattern.compile("\\W").pattern())).collect(Collectors.toList());
        for (String word : words) {
            if (!StringUtil.isBlank(word) && !StringUtil.isEmpty(word) && isAnAcronym(word)) {
                acronyms.addAll(appearencesOf(word, text));
            }
        }
        return acronyms;
    }

    public String restoreIn(String text) {
        String prefix = (start > 0) ? text.substring(0, start) : "";
        String suffix = text.substring(end, text.length());
        return prefix + acronymText + suffix;
    }

    private static Set<Acronym> appearencesOf(String word, String text) {
        Set<Acronym> acronyms = new HashSet();

        int startAt = 0;
        while(startAt < text.length()) {
            int wordFoundAt = text.indexOf(word, startAt);
            if (wordFoundAt == -1) { break; }

            acronyms.add(new Acronym(word, wordFoundAt, wordFoundAt + word.length()));
            startAt = startAt + word.length();
        }
        return acronyms;
    }

    public static boolean isAnAcronym(String word) {
        return (word.length() > 1) && Character.isUpperCase(firstLetterIn(word)) && Character.isUpperCase(lastLetterIn(word));
    }

    private static char firstLetterIn(String word) {
        return word.toCharArray()[0];
    }

    private static char lastLetterIn(String word) {
        return word.toCharArray()[word.length() - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acronym acronym = (Acronym) o;
        return start == acronym.start &&
                end == acronym.end &&
                Objects.equals(acronymText, acronym.acronymText);
    }

    @Override
    public int hashCode() {
        int result = acronymText != null ? acronymText.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Acronym{");
        sb.append("acronymText='").append(acronymText).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }

}
