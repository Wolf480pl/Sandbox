/*
 * Copyright (c) 2014 Wolf480pl <wolf480@interia.pl>
 * This program is licensed under the GNU Lesser General Public License.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.wolf480pl.sandbox.core.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;

public interface RuntimePolicy {

    // TODO: Maybe it should be allowed to throw something?
    MethodHandle intercept(Lookup caller, MethodHandlePrototype method);


    // Useful implementations
    // TODO: Move these elsewhere
    public static class PassthruPolicy implements RuntimePolicy {
        @Override
        public MethodHandle intercept(Lookup caller, MethodHandlePrototype method) {
            try {
                return method.bake(caller);
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                // TODO: are we sure this is the correct handling?
                throw new RuntimeException(e);
            }
        }
    }

    public static class LoggingPolicy implements RuntimePolicy {
        private final RuntimePolicy pol;

        public LoggingPolicy(RuntimePolicy pol) {
            this.pol = pol;
        }

        @Override
        public MethodHandle intercept(Lookup caller, MethodHandlePrototype method) {
            System.err.println(caller + " wants " + method.getOwner() + "." + method.getName() + " " + method.getMethodType());
            return pol.intercept(caller, method);
        }
    }
}