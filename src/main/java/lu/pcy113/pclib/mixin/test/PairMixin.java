package lu.pcy113.pclib.mixin.test;

import lu.pcy113.pclib.mixin.annotations.MixinClass;
import lu.pcy113.pclib.mixin.annotations.MixinMethod;

import net.bytebuddy.asm.Advice;

@MixinClass("lu.pcy113.pclib.datastructure.pair.Pair")
public class PairMixin {

	@MixinMethod(name = "toString", returnType = String.class, parameterTypes = {})
	public static class PairToString {
		
		@Advice.OnMethodExit
		public static void toStringExit(@Advice.This Object pair, @Advice.Return(readOnly = false) String qsd) {
			qsd = "Modified by Mixin";
		}
		
	}

}
