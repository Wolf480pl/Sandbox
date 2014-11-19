package com.github.wolf480pl.sandbox.util;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SequenceMethodVisitor extends MethodVisitor {

    public SequenceMethodVisitor() {
        super(Opcodes.ASM5);
    }

    public SequenceMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    protected void visitOtherInsn() {
    }

    @Override
    public void visitInsn(int opcode) {
        visitOtherInsn();
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        visitOtherInsn();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        visitOtherInsn();
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        visitOtherInsn();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        visitOtherInsn();
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        visitOtherInsn();
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        visitOtherInsn();
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        visitOtherInsn();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        visitOtherInsn();
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        visitOtherInsn();
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        visitOtherInsn();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        visitOtherInsn();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        visitOtherInsn();
        super.visitMultiANewArrayInsn(desc, dims);
    }
}
