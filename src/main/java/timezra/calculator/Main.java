package timezra.calculator;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static final class Rational {
		private final BigInteger numerator;
		private final BigInteger denominator;

		Rational(final BigInteger numerator, final BigInteger denominator) {
			this.numerator = numerator;
			this.denominator = denominator;
		}

		Rational add(final Rational theOther) {
			final BigInteger lcm = lcm(denominator, theOther.denominator);

			final BigInteger scaledNumerator = numerator.multiply(lcm.divide(denominator));

			final BigInteger theOtherScaledNumerator = theOther.numerator.multiply(lcm.divide(theOther.denominator));

			return new Rational(scaledNumerator.add(theOtherScaledNumerator), lcm);
		}

		Rational mult(final Rational theOther) {
			return new Rational(numerator.multiply(theOther.numerator), denominator.multiply(theOther.denominator));
		}

		Rational div(final Rational theOther) {
			return mult(new Rational(theOther.denominator, theOther.numerator));
		}

		Rational negate() {
			return new Rational(numerator.negate(), denominator);
		}

		Number reduce() {
			return numerator.divide(denominator);
		}

		static BigInteger lcm(final BigInteger a, final BigInteger b) {
			return a.multiply(b).divide(a.gcd(b));
		}
	}

	/**
	 * The grammar:
	 *
	 * <pre>
	 * S      --> N | V | ADD | SUB | MULT | DIV | LET
	 * N      --> Integer.MIN_VALUE..Integer.MAX_VALUE
	 * V      --> a-Z
	 * ADD    --> add(S, S)
	 * SUB    --> sub(S, S)
	 * MULT   --> mult(S, S)
	 * DIV    --> div(S, S)
	 * LET    --> let(V, S, S)
	 * </pre>
	 *
	 */
	private static final class Parser {

		List<ASTNode> scanArguments(final String expression, final int beginning, final int end) throws ParseException {
			final List<ASTNode> args = new ArrayList<>();
			int stack = 0;
			int start = beginning;
			for (int i = start; i < end; i++) {
				final char nextCharacter = expression.charAt(i);
				if (nextCharacter == '(') {
					if (stack == 0) {
						start = i + 1;
					}
					stack++;
				} else if (nextCharacter == ')') {
					stack--;
					if (stack == 0) {
						args.add(parse(expression.substring(start, i)));
					}
				} else if (nextCharacter == ',' && stack == 1) {
					args.add(parse(expression.substring(start, i)));
					start = i + 1;
				}
			}
			return args;
		}

		ASTNode parse(final String expression) throws ParseException {
			try {
				final int n = Integer.parseInt(expression);
				return (final Context context) -> new Rational(BigInteger.valueOf(n), BigInteger.ONE);
			} catch (final NumberFormatException e) {
				// NaN
			}
			if (expression.matches("[a-zA-Z]+")) {
				return new Variable(expression);
			}
			if (expression.startsWith("add")) {
				return parseAdd(expression);
			} else if (expression.startsWith("sub")) {
				return parseSubtract(expression);
			} else if (expression.startsWith("mult")) {
				return parseMultiplication(expression);
			} else if (expression.startsWith("div")) {
				return parseDivision(expression);
			} else if (expression.startsWith("let")) {
				return parseVariableUsage(expression);
			}
			throw new ParseException("Unable to parse the expression: " + expression, 0);
		}

		private ASTNode parseVariableUsage(final String expression) throws ParseException {
			final List<ASTNode> args = scanArguments(expression, 3, expression.length());
			if (args.size() < 3) {
				throw new ParseException("Not enough arguments for let: " + expression, 3);
			}
			final ASTNode usage = args.get(2);
			final ASTNode declaration = args.get(0);
			final ASTNode valueExpression = args.get(1);
			return (final Context context) -> usage.eval(new Context(context, declaration, valueExpression
					.eval(context)));
		}

		private ASTNode parseDivision(final String expression) throws ParseException {
			final List<ASTNode> args = scanArguments(expression, 3, expression.length());
			if (args.size() < 2) {
				throw new ParseException("Not enough arguments for div: " + expression, 3);
			}
			final ASTNode divisor = args.get(0);
			final ASTNode dividend = args.get(1);
			return (final Context context) -> divisor.eval(context).div(dividend.eval(context));
		}

		private ASTNode parseMultiplication(final String expression) throws ParseException {
			final List<ASTNode> args = scanArguments(expression, 4, expression.length());
			if (args.size() < 2) {
				throw new ParseException("Not enough arguments for mult: " + expression, 4);
			}
			final ASTNode first = args.get(0);
			final ASTNode second = args.get(1);
			return (final Context context) -> first.eval(context).mult(second.eval(context));
		}

		private ASTNode parseSubtract(final String expression) throws ParseException {
			final List<ASTNode> args = scanArguments(expression, 3, expression.length());
			if (args.size() < 2) {
				throw new ParseException("Not enough arguments for sub: " + expression, 3);
			}
			final ASTNode minuend = args.get(0);
			final ASTNode subtrahend = args.get(1);
			return (final Context context) -> minuend.eval(context).add(subtrahend.eval(context).negate());
		}

		private ASTNode parseAdd(final String expression) throws ParseException {
			final List<ASTNode> args = scanArguments(expression, 3, expression.length());
			if (args.size() < 2) {
				throw new ParseException("Not enough arguments for add: " + expression, 3);
			}
			final ASTNode first = args.get(0);
			final ASTNode second = args.get(1);
			return (final Context context) -> first.eval(context).add(second.eval(context));
		}
	}

	private static final class Context {
		private final Context parent;
		private final ASTNode declaration;
		private final Rational value;

		Context(final Context parent, final ASTNode declaration, final Rational value) {
			this.parent = parent;
			this.declaration = declaration;
			this.value = value;
		}

		Rational get(final ASTNode variable) {
			if (!declaration.equals(variable)) {
				if (parent == null) {
					throw new IllegalArgumentException("Unbound variable: " + variable);
				}
				return parent.get(variable);
			}
			return value;
		}
	}

	private static interface ASTNode {
		Rational eval(final Context context);
	}

	private static final class Variable implements ASTNode {
		private final String name;

		Variable(final String name) {
			this.name = name;
		}

		@Override
		public Rational eval(final Context context) {
			if (context == null) {
				throw new IllegalArgumentException("Unbound variable " + name);
			}
			return context.get(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Variable other = (Variable) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static final class Calculator {
		Number eval(final String expression) throws ParseException {
			final Parser parser = new Parser();
			final ASTNode node = parser.parse(expression.replaceAll("\\s+", ""));
			return node.eval(null).reduce();
		}
	}

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.out.println(usage());
		} else {
			try {
				System.out.println(new Calculator().eval(args[0]));
			} catch (final ParseException e) {
				System.out.println("Unable to parse the expression: " + args[0]);
				System.out.println("  Error: " + e.getMessage());
				System.out.println(usage());
			} catch (final Exception e) {
				System.out.println("Unexpected error while evaluating the expression: " + args[0]);
				System.out.println("  Error: " + e.getMessage());
				System.out.println(usage());
			}
		}
	}

	private static String usage() {
		return "Usage:\n"
				+ "java calculator.Main <expression>\n"
				+ "\n"
				+ "  An expression is one of the of the following:\n"
				+ "    Numbers: integers between Integer.MIN_VALUE and Integer.MAX_VALUE\n"
				+ "    Variables: strings of characters, where each character is one of a-z, A-Z\n"
				+ "    Arithmetic functions: add, sub, mult, div, each taking two arbitrary expressions as arguments. In other words, each argument may be any of the expressions on this list.\n"
				+ "    A \"let\" operator for assigning values to variables:\n"
				+ "      let(<variable name>, <value expression>, <expression where variable is used>)\n"
				+ "  As with arithmetic functions, the value expression and the expression where the variable is used may be an arbitrary expression from this list.\n"
				+ "\n" + "Examples:\n" + "  add(1, 2) --> 3\n" + "  add(1, mult(2, 3)) --> 7\n"
				+ "  mult(add(2, 2), div(9, 3)) --> 12\n" + "  let(a, 5, add(a, a)) --> 10\n"
				+ "  let(a, 5, let(b, mult(a, 10), add(b, a))) --> 55\n"
				+ "  let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b))) --> 40";
	}
}
