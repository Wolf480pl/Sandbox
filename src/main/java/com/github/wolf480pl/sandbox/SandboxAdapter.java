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

import static org.objectweb.asm.Type.getType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SandboxAdapter extends ClassVisitor {

    public SandboxAdapter(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        // TODO Auto-generated constructor stub
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodAdapter(cv.visitMethod(access, name, desc, signature, exceptions), name.equals("<init>"));
    }

    public static class MethodAdapter extends MethodVisitor {
        public static final String WRAPINVOKE_NAME = "wrapInvoke";
        public static final String WRAPINVOKE_DESC = Type.getMethodDescriptor(getType(CallSite.class), getType(MethodHandles.Lookup.class), getType(String.class), getType(MethodType.class),
                Type.INT_TYPE, getType(String.class), getType(MethodType.class));

        public static final String WRAPCONSTRUCTOR_NAME = "wrapConstructor";
        public static final String WRAPCONSTRUCTOR_DESC = Type.getMethodDescriptor(getType(CallSite.class), getType(MethodHandles.Lookup.class), getType(String.class), getType(MethodType.class),
                getType(String.class), getType(MethodType.class));

        public static final String WRAPHANDLE_NAME = "wrapHandle";
        public static final String WRAPHANDLE_DESC = Type.getMethodDescriptor(getType(CallSite.class), getType(MethodHandles.Lookup.class), getType(String.class), getType(MethodType.class),
                Type.INT_TYPE, getType(String.class), getType(MethodType.class));

        private boolean skip;

        public MethodAdapter(MethodVisitor mv, boolean constructor) {
            super(Opcodes.ASM5, mv);
            this.skip = constructor;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (skip) {
                skip = false;
                mv.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }

            Type ownerType = Type.getObjectType(owner);
            Type methType = Type.getMethodType(desc);

            if (opcode == Opcodes.INVOKESPECIAL) {
                if (name.equals("<init>")) {
                    Type nt = Type.getMethodType(ownerType, methType.getArgumentTypes());
                    desc = nt.getDescriptor();

                    mv.visitInvokeDynamicInsn("init", desc,
                            new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Bootstraps.class), WRAPCONSTRUCTOR_NAME, WRAPCONSTRUCTOR_DESC),
                            ownerType.getClassName(), methType);
                    return;
                }
            }

            if (opcode != Opcodes.INVOKESTATIC) {
                Type[] argTypes = methType.getArgumentTypes();
                Type[] newArgTypes = new Type[argTypes.length + 1];
                newArgTypes[0] = ownerType;
                System.arraycopy(argTypes, 0, newArgTypes, 1, argTypes.length);
                Type nt = Type.getMethodType(methType.getReturnType(), newArgTypes);
                desc = nt.getDescriptor();
            }

            mv.visitInvokeDynamicInsn(name, desc,
                    new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Bootstraps.class), WRAPINVOKE_NAME, WRAPINVOKE_DESC),
                    opcode, ownerType.getClassName(), methType);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            // TODO
            super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (cst instanceof Handle) {
                Handle handle = (Handle) cst;
                mv.visitInvokeDynamicInsn(handle.getName(), Type.getMethodDescriptor(Type.getType(MethodHandle.class)), new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Bootstraps.class),
                        WRAPHANDLE_NAME, WRAPHANDLE_DESC), hopcodeToInsn(handle.getTag()), Type.getObjectType(handle.getOwner()).getClassName(), Type.getMethodType(handle.getDesc()));
                return;
            }
            mv.visitLdcInsn(cst);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.NEW) {
                return; // We remove it because we convert <init> to invokedynamic
            }
            mv.visitTypeInsn(opcode, type);
        }
    }

    public static int hopcodeToInsn(int hopcode) {
        switch (hopcode) {
            case Opcodes.H_INVOKEINTERFACE:
                return Opcodes.INVOKEINTERFACE;
            case Opcodes.H_INVOKESPECIAL:
            case Opcodes.H_NEWINVOKESPECIAL:
                return Opcodes.INVOKESPECIAL;
            case Opcodes.H_INVOKESTATIC:
                return Opcodes.INVOKESTATIC;
            case Opcodes.H_INVOKEVIRTUAL:
                return Opcodes.INVOKEVIRTUAL;
            default:
                throw new IllegalArgumentException("Can't convert handle opcode: " + hopcode);
        }
    }
}
