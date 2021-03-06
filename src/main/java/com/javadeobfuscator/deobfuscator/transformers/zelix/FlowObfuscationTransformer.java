package com.javadeobfuscator.deobfuscator.transformers.zelix;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.javadeobfuscator.deobfuscator.org.objectweb.asm.Opcodes;
import com.javadeobfuscator.deobfuscator.transformers.Transformer;
import com.javadeobfuscator.deobfuscator.utils.WrappedClassNode;

public class FlowObfuscationTransformer extends Transformer
{
	public FlowObfuscationTransformer(Map<String, WrappedClassNode> classes,
		Map<String, WrappedClassNode> classpath)
	{
		super(classes, classpath);
	}

	@Override
	public void transform() throws Throwable
	{
		System.out.println("[Zelix] [FlowObfuscationTransformer] Starting");
		AtomicInteger counter = new AtomicInteger();
		
		classNodes().stream()
			.map(wrappedClassNode -> wrappedClassNode.classNode)
			.forEach(classNode -> classNode.methods.stream()
				.filter(
					methodNode -> methodNode.instructions.getFirst() != null)
				.forEach(methodNode -> {
					counter.addAndGet(methodNode.tryCatchBlocks.size());
					methodNode.tryCatchBlocks.removeIf(tc -> (tc.handler
						.getNext().getOpcode() == Opcodes.INVOKESTATIC
						&& tc.handler.getNext().getNext()
							.getOpcode() == Opcodes.ATHROW) 
						|| tc.handler.getNext().getOpcode() == Opcodes.ATHROW);
					counter.addAndGet(-methodNode.tryCatchBlocks.size());
				}));
		
		System.out.println("[Zelix] [FlowObfuscationTransformer] Removed "
			+ counter.get() + " fake try-catch blocks");
		System.out.println("[Zelix] [FlowObfuscationTransformer] Done");
	}
}
