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

import org.bigtesting.interpolatd.EnclosureOpeningHandler;
import org.bigtesting.interpolatd.InterpolationHandler;
import org.bigtesting.interpolatd.PrefixHandler;

/**
 * 
 * @author Luis Antunes
 */
public class InterpolationHandlerImpl<T> implements InterpolationHandler<T>, Interpolating<T> {

    private PrefixHandlerImpl<T> prefixHandler;
    
    private EnclosureOpeningHandlerImpl<T> enclosureOpeningHandler;
    
    private final String characterClass;
    
    public InterpolationHandlerImpl() {
        this(null);
    }
    
    public InterpolationHandlerImpl(String characterClass) {
        this.characterClass = characterClass;
    }
    
    public PrefixHandler<T> prefixedBy(String prefix) {
        
        PrefixHandlerImpl<T> prefixHandler = new PrefixHandlerImpl<T>(prefix, characterClass);
        this.prefixHandler = prefixHandler;
        return prefixHandler;
    }
    
    public EnclosureOpeningHandler<T> enclosedBy(String opening) {
        
        EnclosureOpeningHandlerImpl<T> enclosureOpeningHandler = 
                new EnclosureOpeningHandlerImpl<T>(opening, characterClass);
        this.enclosureOpeningHandler = enclosureOpeningHandler;
        return enclosureOpeningHandler;
    }
    
    public List<Substitution> interpolate(String toInterpolate, T arg) {
        
        List<Substitution> substitutions = new ArrayList<Substitution>();
        if (prefixHandler != null) {
            
            substitutions.addAll(prefixHandler.interpolate(toInterpolate, arg));
            
        } else if (enclosureOpeningHandler != null) {
            
            substitutions.addAll(enclosureOpeningHandler.getEnclosureClosingHandler()
                                                   .interpolate(toInterpolate, arg));
        }
        
        return substitutions;
    }
}
