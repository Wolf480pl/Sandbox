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
package com.github.wolf480pl.sandbox;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.objectweb.asm.Opcodes;

public class Bootstraps {

    private Bootstraps() {
    }

    public static CallSite wrapInvoke(MethodHandles.Lookup caller, String invokedName, MethodType invokedType, int opcode, String owner, MethodType originalType) throws NoSuchMethodException,
            IllegalAccessException, ClassNotFoundException {
        InvocationType invType = InvocationType.fromID(opcode);
        // TODO access checks maybe?
        MethodHandle handle = makeHandle(caller, invokedName, invokedType, invType, owner, originalType);
        return new ConstantCallSite(handle);
    }

    public static MethodHandle makeHandle(MethodHandles.Lookup caller, String invokedName, MethodType invokedType, InvocationType invType, String owner, MethodType originalType) throws NoSuchMethodException,
            IllegalAccessException, ClassNotFoundException {
        System.err.println(caller + " wants " + owner + "." + invokedName + " " + invokedType); // TODO: Remove once we implement policies
        Class<?> ownerCls = caller.lookupClass().getClassLoader().loadClass(owner);
        final MethodHandle handle;


        switch (invType) {
            case INVOKEINTERFACE:
            case INVOKEVIRTUAL:
                handle = caller.findVirtual(ownerCls, invokedName, originalType);
                break;
            case INVOKESTATIC:
                handle = caller.findStatic(ownerCls, invokedName, originalType);
                break;
            case INVOKESPECIAL:
                handle = caller.findSpecial(ownerCls, invokedName, originalType, caller.lookupClass());
                break;
            case INVOKENEWSPECIAL:
                handle = caller.findConstructor(ownerCls, originalType);
                break;
            default:
                throw new IllegalArgumentException("Unknown invoke type: " + invType);
        }
        return handle;
    }

    public static CallSite wrapConstructor(MethodHandles.Lookup caller, String invokedName, MethodType invokedType, String owner, MethodType originalType) throws NoSuchMethodException,
            IllegalAccessException, ClassNotFoundException {
        return wrapInvoke(caller, "<init>", invokedType, InvocationType.INVOKENEWSPECIAL.id(), owner, originalType);
    }

    public static CallSite wrapInvokeDynamic(MethodHandles.Lookup caller, String invokedName, MethodType invokedType, Object... args) {
        // TODO
        return null;
    }

    public static CallSite wrapHandle(MethodHandles.Lookup caller, String invokedName, MethodType invokedType, int opcode, String owner, MethodType originalType) throws NoSuchMethodException,
            IllegalAccessException, ClassNotFoundException {
        InvocationType invType = InvocationType.fromID(opcode);
        // TODO access checks maybe?
        MethodHandle handle = makeHandle(caller, invokedName, invokedType, invType, owner, originalType);
        return new ConstantCallSite(MethodHandles.constant(MethodHandle.class, handle));
    }

}
