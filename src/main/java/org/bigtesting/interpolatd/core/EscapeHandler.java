/*
 * Copyright (C) 2014 BigTesting.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bigtesting.interpolatd.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Luis Antunes
 */
public class EscapeHandler<T> implements Interpolating<T> {

    private final String escape;
    private final Pattern pattern;
    
    public EscapeHandler(String escape) {
        
        this.escape = escape;
        this.pattern = Pattern.compile("(" + Pattern.quote(escape) + ")");
    }

    public List<Substitution> interpolate(String toInterpolate, T arg) {
        
        List<Substitution> substitutions = new ArrayList<Substitution>(); 
        Matcher m = pattern.matcher(toInterpolate);
        while (m.find()) {
            substitutions.add(new Substitution(escape, "", m.start(), m.end(), true));
        }

        return substitutions;
    }
}
