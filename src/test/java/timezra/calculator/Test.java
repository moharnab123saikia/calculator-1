package timezra.calculator;

import java.math.BigInteger;

import timezra.calculator.Main.Calculator;

public class Test {

	private void shouldAddTwoNumbers() throws Exception {
		assertEqual(BigInteger.valueOf(3), new Calculator().eval("add(1, 2)"));
	}

	private void shouldMultiplyTwoNumbers() throws Exception {
		assertEqual(BigInteger.valueOf(6), new Calculator().eval("mult(2, 3)"));
	}

	private void shouldSubtractOneNumberFromAnother() throws Exception {
		assertEqual(BigInteger.valueOf(5), new Calculator().eval("sub(8, 3)"));
	}

	private void shouldDivideOneNumberByAnother() throws Exception {
		assertEqual(BigInteger.valueOf(3), new Calculator().eval("div(9, 3)"));
	}

	private void shouldHandleAnExpressionAsAnArgument() throws Exception {
		assertEqual(BigInteger.valueOf(7), new Calculator().eval("add(1, mult(2, 3))"));
	}

	private void shouldHandleExpressionsAsBothArguments() throws Exception {
		assertEqual(BigInteger.valueOf(12), new Calculator().eval("mult(add(2, 2), div(9, 3))"));
	}

	private void shouldTruncateDivision() throws Exception {
		assertEqual(BigInteger.valueOf(2), new Calculator().eval("div(11, 4)"));
	}

	private void shouldHandleRationalNumberMathProperly() throws Exception {
		assertEqual(BigInteger.valueOf(6), new Calculator().eval("add(div(13, 4), div(11, 4))"));
	}

	private void shouldAssignValuesToVariables() throws Exception {
		assertEqual(BigInteger.valueOf(10), new Calculator().eval("let(a, 5, add(a, a))"));
	}

	private void shouldAssignValuesToVariablesInUsageExpressions() throws Exception {
		assertEqual(BigInteger.valueOf(55), new Calculator().eval("let(a, 5, let(b, mult(a, 10), add(b, a)))"));
	}

	private void shouldAssignValuesToVariablesInValueExpressions() throws Exception {
		assertEqual(BigInteger.valueOf(40),
				new Calculator().eval("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))"));
	}

	private void shouldShadowVariableExpressionsInSubexpressions() throws Exception {
		assertEqual(BigInteger.valueOf(80),
				new Calculator().eval("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, let(b, 30, add(b, b)))))"));
	}

	private void shouldFailForUnboundVariables() throws Exception {
		try {
			new Calculator().eval("add(1, a)");
			assert true == false : "Expected exception.";
		} catch (final IllegalArgumentException e) {
			// pass
		}
	}

	private <T> void assertEqual(final T expected, final T actual) {
		assert actual == null && expected == null || actual != null && actual.equals(expected) : actual + " is not "
				+ expected;
	}

	public static void main(final String[] args) throws Exception {
		final Test suite = new Test();
		suite.shouldAddTwoNumbers();
		suite.shouldMultiplyTwoNumbers();
		suite.shouldSubtractOneNumberFromAnother();
		suite.shouldDivideOneNumberByAnother();
		suite.shouldHandleAnExpressionAsAnArgument();
		suite.shouldHandleExpressionsAsBothArguments();
		suite.shouldTruncateDivision();
		suite.shouldHandleRationalNumberMathProperly();
		suite.shouldAssignValuesToVariables();
		suite.shouldAssignValuesToVariablesInUsageExpressions();
		suite.shouldAssignValuesToVariablesInValueExpressions();
		suite.shouldShadowVariableExpressionsInSubexpressions();
		suite.shouldFailForUnboundVariables();

		System.out.println("All passed!");
	}
}
