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

import org.bigtesting.interpolatd.SubstitutionHandler;
import org.bigtesting.interpolatd.Substitutor;

/**
 * 
 * @author Luis Antunes
 */
public abstract class SubstitutionHandlerImpl implements SubstitutionHandler, Interpolating {

    protected Substitutor substitutor;
    
    public void handleWith(Substitutor substitutor) {
        
        this.substitutor = substitutor;
    }

    protected abstract Pattern getPattern();
    
    protected abstract String getCaptured(String found);
    
    public List<Substitution> interpolate(String toInterpolate, Object arg) {
        
        List<Substitution> substitutions = new ArrayList<Substitution>(); 
        if (substitutor != null) {
            Matcher m = getPattern().matcher(toInterpolate);
            while (m.find()) {
                
                String found = m.group(1);
                String captured = getCaptured(found);
                String substitution = substitutor.substitute(captured, arg);
                
                substitutions.add(new Substitution(found, substitution, m.start(), m.end()));
            }
        }
        
        return substitutions;
    }
}
