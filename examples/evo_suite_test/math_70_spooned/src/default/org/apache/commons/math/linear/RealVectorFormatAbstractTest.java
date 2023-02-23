package org.apache.commons.math.linear;


public abstract class RealVectorFormatAbstractTest extends junit.framework.TestCase {
	org.apache.commons.math.linear.RealVectorFormat realVectorFormat = null;

	org.apache.commons.math.linear.RealVectorFormat realVectorFormatSquare = null;

	protected abstract java.util.Locale getLocale();

	protected abstract char getDecimalCharacter();

	@java.lang.Override
	public void setUp() throws java.lang.Exception {
		realVectorFormat = org.apache.commons.math.linear.RealVectorFormat.getInstance(getLocale());
		final java.text.NumberFormat nf = java.text.NumberFormat.getInstance(getLocale());
		nf.setMaximumFractionDigits(2);
		realVectorFormatSquare = new org.apache.commons.math.linear.RealVectorFormat("[" , "]" , " : " , nf);
	}

	public void testSimpleNoDecimals() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1 , 1 , 1 });
		java.lang.String expected = "{1; 1; 1}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testSimpleWithDecimals() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.23 , 1.43 , 1.63 });
		java.lang.String expected = ((((("{1" + (getDecimalCharacter())) + "23; 1") + (getDecimalCharacter())) + "43; 1") + (getDecimalCharacter())) + "63}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testSimpleWithDecimalsTrunc() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , 1.4343 , 1.6333 });
		java.lang.String expected = ((((("{1" + (getDecimalCharacter())) + "23; 1") + (getDecimalCharacter())) + "43; 1") + (getDecimalCharacter())) + "63}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeX() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -1.2323 , 1.4343 , 1.6333 });
		java.lang.String expected = ((((("{-1" + (getDecimalCharacter())) + "23; 1") + (getDecimalCharacter())) + "43; 1") + (getDecimalCharacter())) + "63}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeY() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , -1.4343 , 1.6333 });
		java.lang.String expected = ((((("{1" + (getDecimalCharacter())) + "23; -1") + (getDecimalCharacter())) + "43; 1") + (getDecimalCharacter())) + "63}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeZ() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , 1.4343 , -1.6333 });
		java.lang.String expected = ((((("{1" + (getDecimalCharacter())) + "23; 1") + (getDecimalCharacter())) + "43; -1") + (getDecimalCharacter())) + "63}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNonDefaultSetting() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1 , 1 , 1 });
		java.lang.String expected = "[1 : 1 : 1]";
		java.lang.String actual = realVectorFormatSquare.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testStaticFormatRealVectorImpl() {
		java.util.Locale defaultLocal = java.util.Locale.getDefault();
		java.util.Locale.setDefault(getLocale());
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 232.222 , -342.33 , 432.444 });
		java.lang.String expected = ((((("{232" + (getDecimalCharacter())) + "22; -342") + (getDecimalCharacter())) + "33; 432") + (getDecimalCharacter())) + "44}";
		java.lang.String actual = org.apache.commons.math.linear.RealVectorFormat.formatRealVector(c);
		junit.framework.Assert.assertEquals(expected, actual);
		java.util.Locale.setDefault(defaultLocal);
	}

	public void testNan() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , java.lang.Double.NaN , java.lang.Double.NaN });
		java.lang.String expected = "{(NaN); (NaN); (NaN)}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testPositiveInfinity() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY });
		java.lang.String expected = "{(Infinity); (Infinity); (Infinity)}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void tesNegativeInfinity() {
		org.apache.commons.math.linear.ArrayRealVector c = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY });
		java.lang.String expected = "{(-Infinity); (-Infinity); (-Infinity)}";
		java.lang.String actual = realVectorFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testParseSimpleNoDecimals() {
		java.lang.String source = "{1; 1; 1}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1 , 1 , 1 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseIgnoredWhitespace() {
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1 , 1 , 1 });
		java.text.ParsePosition pos1 = new java.text.ParsePosition(0);
		java.lang.String source1 = "{1;1;1}";
		junit.framework.Assert.assertEquals(expected, realVectorFormat.parseObject(source1, pos1));
		junit.framework.Assert.assertEquals(source1.length(), pos1.getIndex());
		java.text.ParsePosition pos2 = new java.text.ParsePosition(0);
		java.lang.String source2 = " { 1 ; 1 ; 1 } ";
		junit.framework.Assert.assertEquals(expected, realVectorFormat.parseObject(source2, pos2));
		junit.framework.Assert.assertEquals(((source2.length()) - 1), pos2.getIndex());
	}

	public void testParseSimpleWithDecimals() {
		java.lang.String source = ((((("{1" + (getDecimalCharacter())) + "23; 1") + (getDecimalCharacter())) + "43; 1") + (getDecimalCharacter())) + "63}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.23 , 1.43 , 1.63 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseSimpleWithDecimalsTrunc() {
		java.lang.String source = ((((("{1" + (getDecimalCharacter())) + "2323; 1") + (getDecimalCharacter())) + "4343; 1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , 1.4343 , 1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeX() {
		java.lang.String source = ((((("{-1" + (getDecimalCharacter())) + "2323; 1") + (getDecimalCharacter())) + "4343; 1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -1.2323 , 1.4343 , 1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeY() {
		java.lang.String source = ((((("{1" + (getDecimalCharacter())) + "2323; -1") + (getDecimalCharacter())) + "4343; 1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , -1.4343 , 1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeZ() {
		java.lang.String source = ((((("{1" + (getDecimalCharacter())) + "2323; 1") + (getDecimalCharacter())) + "4343; -1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , 1.4343 , -1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeAll() {
		java.lang.String source = ((((("{-1" + (getDecimalCharacter())) + "2323; -1") + (getDecimalCharacter())) + "4343; -1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ -1.2323 , -1.4343 , -1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseZeroX() {
		java.lang.String source = ((((("{0" + (getDecimalCharacter())) + "0; -1") + (getDecimalCharacter())) + "4343; 1") + (getDecimalCharacter())) + "6333}";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 0.0 , -1.4343 , 1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNonDefaultSetting() {
		java.lang.String source = ((((("[1" + (getDecimalCharacter())) + "2323 : 1") + (getDecimalCharacter())) + "4343 : 1") + (getDecimalCharacter())) + "6333]";
		org.apache.commons.math.linear.ArrayRealVector expected = new org.apache.commons.math.linear.ArrayRealVector(new double[]{ 1.2323 , 1.4343 , 1.6333 });
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormatSquare.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNan() {
		java.lang.String source = "{(NaN); (NaN); (NaN)}";
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NaN , java.lang.Double.NaN , java.lang.Double.NaN }), actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParsePositiveInfinity() {
		java.lang.String source = "{(Infinity); (Infinity); (Infinity)}";
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY }), actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeInfinity() {
		java.lang.String source = "{(-Infinity); (-Infinity); (-Infinity)}";
		try {
			org.apache.commons.math.linear.ArrayRealVector actual = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.ArrayRealVector(new double[]{ java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY }), actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNoComponents() {
		try {
			realVectorFormat.parseObject("{ }");
		} catch (java.text.ParseException pe) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testParseManyComponents() throws java.text.ParseException {
		org.apache.commons.math.linear.ArrayRealVector parsed = ((org.apache.commons.math.linear.ArrayRealVector)(realVectorFormat.parseObject("{0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0}")));
		junit.framework.Assert.assertEquals(24, parsed.getDimension());
	}

	public void testConstructorSingleFormat() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		org.apache.commons.math.linear.RealVectorFormat cf = new org.apache.commons.math.linear.RealVectorFormat(nf);
		junit.framework.Assert.assertNotNull(cf);
		junit.framework.Assert.assertEquals(nf, cf.getFormat());
	}

	public void testFormatObject() {
		try {
			org.apache.commons.math.util.CompositeFormat cf = new org.apache.commons.math.linear.RealVectorFormat();
			java.lang.Object object = new java.lang.Object();
			cf.format(object);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testForgottenPrefix() {
		java.text.ParsePosition pos = new java.text.ParsePosition(0);
		final java.lang.String source = "1; 1; 1}";
		junit.framework.Assert.assertNull((("Should not parse <" + source) + ">"), new org.apache.commons.math.linear.RealVectorFormat().parse(source, pos));
		junit.framework.Assert.assertEquals(0, pos.getErrorIndex());
	}

	public void testForgottenSeparator() {
		java.text.ParsePosition pos = new java.text.ParsePosition(0);
		final java.lang.String source = "{1; 1 1}";
		junit.framework.Assert.assertNull((("Should not parse <" + source) + ">"), new org.apache.commons.math.linear.RealVectorFormat().parse(source, pos));
		junit.framework.Assert.assertEquals(6, pos.getErrorIndex());
	}

	public void testForgottenSuffix() {
		java.text.ParsePosition pos = new java.text.ParsePosition(0);
		final java.lang.String source = "{1; 1; 1 ";
		junit.framework.Assert.assertNull((("Should not parse <" + source) + ">"), new org.apache.commons.math.linear.RealVectorFormat().parse(source, pos));
		junit.framework.Assert.assertEquals(8, pos.getErrorIndex());
	}
}

