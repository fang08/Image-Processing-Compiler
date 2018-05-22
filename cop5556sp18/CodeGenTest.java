package cop5556sp18;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.CodeGenUtils.DynamicClassLoader;
import cop5556sp18.AST.Program;

public class CodeGenTest {
	
	//determines whether show prints anything
	static boolean doPrint = true;
	
	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	//determines whether a classfile is created
	static boolean doCreateFile = false;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	

	//values passed to CodeGenerator constructor to control grading and debugging output
	//private boolean devel = true; //if true, print devel output
	private boolean devel = false;
	//private boolean grade = true;
	private boolean grade = true; //if true, print grade output
	
//	private boolean devel = false; 
//	private boolean grade = false; 
	
	//sets the default width and height of newly created images.  Should be small enough to fit on screen.
	public static final int defaultWidth = 1024;
	public static final int defaultHeight = 1024;

	
	/**
	 * Generates bytecode for given input.
	 * Throws exceptions for Lexical, Syntax, and Type checking errors
	 * 
	 * @param input   String containing source code
	 * @return        Generated bytecode
	 * @throws Exception
	 */
	byte[] genCode(String input) throws Exception {
		
		//scan, parse, and type check
		Scanner scanner = new Scanner(input);
		show(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Program program = parser.parse();
		TypeChecker v = new TypeChecker();
		program.visit(v, null);
//		show(program);  //It may be useful useful to show this here if code generation fails

		//generate code
		CodeGenerator cv = new CodeGenerator(devel, grade, null, defaultWidth, defaultHeight);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		show(program); //doing it here shows the values filled in during code gen
		//display the generated bytecode
		show(CodeGenUtils.bytecodeToString(bytecode));
		
		//write byte code to file 
		if (doCreateFile) {
			String name = ((Program) program).progName;
			String classFileName = "bin/" + name + ".class";
			OutputStream output = new FileOutputStream(classFileName);
			output.write(bytecode);
			output.close();
			System.out.println("wrote classfile to " + classFileName);
		}
		
		//return generated classfile as byte array
		return bytecode;
	}
	
	/**
	 * Run main method in given class
	 * 
	 * @param className    
	 * @param bytecode    
	 * @param commandLineArgs  String array containing command line arguments, empty array if none
	 * @throws + 
	 * @throws Throwable 
	 */
	void runCode(String className, byte[] bytecode, String[] commandLineArgs) throws Exception  {
		RuntimeLog.initLog(); //initialize log used for grading.
		DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
		Class<?> testClass = loader.define(className, bytecode);
		@SuppressWarnings("rawtypes")
		Class[] argTypes = {commandLineArgs.getClass()};
		Method m = testClass.getMethod("main", argTypes );
		show("Output from " + m + ":");  //print name of method to be executed
		Object passedArgs[] = {commandLineArgs};  //create array containing params, in this case a single array.
		try {
		m.invoke(null, passedArgs);	
		}
		catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				Exception ec = (Exception) e.getCause();
				throw ec;
			}
			throw  e;
		}
	}
	

	/**
	 * When invoked from JUnit, Frames containing images will be shown and then immediately deleted.
	 * To prevent this behavior, waitForKey will pause until a key is pressed.
	 * 
	 * @throws IOException
	 */
	void waitForKey() throws IOException {
		System.out.println("enter any char to exit");
		System.in.read();
	}

	/**
	 * When invoked from JUnit, Frames containing images will be shown and then immediately deleted.
	 * To prevent this behavior, keepFrame will keep the frame visible for 5000 milliseconds.
	 * 
	 * @throws Exception
	 */
	void keepFrame() throws Exception {
		Thread.sleep(5000);
	}
	
	
	
	


	/**
	 * Since we are not doing any optimization, the compiler will 
	 * still create a class with a main method and the JUnit test will
	 * execute it.  
	 * 
	 * The only thing it will do is append the "entering main" and "leaving main" messages to the log.
	 * 
	 * @throws Exception
	 */
	@Test
	public void emptyProg() throws Exception {
		String prog = "emptyProg";	
		String input = prog + "{}";
		byte[] bytecode = genCode(input);
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);
		show("Log:\n "+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	

	
	@Test
	public void integerLit() throws Exception {
		String prog = "intgegerLit";
		String input = prog + "{show 3;} ";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {}; //create command line argument array to initialize params, none in this case		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("3;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_1() throws Exception {
		String prog = "test_1";
		String input = prog + "{int j; boolean b;} ";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_2() throws Exception {
		String prog = "test_2";
		String input = prog + "{int j; boolean b; if(true){int x; boolean y; image z;};}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_3() throws Exception {
		String prog = "test_3";
		String input = prog + "{image i[1 , 2];}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_4() throws Exception {
		String prog = "test_4";
		String input = prog + "{float i; input i from @ 0; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"1.234"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("1.234;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_5() throws Exception {
		String prog = "test_5";
		String input = prog + "{image a; filename b; input b from @ 0;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_6() throws Exception {
		String prog = "test_6";
		String input = prog + "{image a; input a from @ 0; show a; sleep 1500;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_7() throws Exception {
		String prog = "test_7";
		String input = prog + "{image a; input a from @ 0; show a; sleep 1500;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"http://www.bestmintonhk.com/forum/data/attachment/forum/201408/04/123550mn4hnamdlqq8qhnd.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_8() throws Exception {
		String prog = "test_8";
		String input = prog + "{image b; boolean c; input c from @ 0; if(c) {input b from @ 1; show b; sleep 1500;};}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"true", "http://www.smash-net.tv/sys_img/photo/2015/4032/4032.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_9() throws Exception {
		String prog = "test_9";
		String input = prog + "{image a; filename b; input a from @ 0; input b from @ 1; write a to b;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"http://www.bestmintonhk.com/forum/data/attachment/forum/201408/04/123550mn4hnamdlqq8qhnd.jpg", "Images/test2.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_10() throws Exception {
		String prog = "test_10";
		String input = prog + "{int i; input i from @ 0; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"6"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("6;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_11() throws Exception {
		String prog = "test_11";
		String input = prog + "{boolean i; input i from @ 0; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"false"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("false;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_12() throws Exception {
		String prog = "test_12";
		String input = prog + "{boolean a; boolean b; a:= true; b := a; show b;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_13() throws Exception {
		String prog = "test_13";
		String input = prog + "{boolean i; i := true; int t; input t from @ 0; while(t > 0) {show i; t := t - 1;};}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"3"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;true;true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_14() throws Exception {
		String prog = "test_14";
		String input = prog + "{int i; i := (1 + 2 * 3 - 4 / 1) ** 4 % 10; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("1;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_15() throws Exception {
		String prog = "test_15";
		String input = prog + "{image a[768, 768]; input a from @ 0; show a; sleep 1500;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_16() throws Exception {
		String prog = "test_16";
		String input = prog + "{image a; input a from @ 0; green(a[100, 125]) := 10; show a; sleep 1500;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"http://www.smash-net.tv/sys_img/photo/2015/4032/4032.jpg"};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_17() throws Exception {
		String prog = "test_17";
		String input = prog + "{int i; i := (0 == 1) ? 1 : 2; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("2;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_18() throws Exception {
		String prog = "test_18";
		String input = prog + "{int i; i := <<1, 255, 51, 68>>; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("33502020;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_19() throws Exception {
		String prog = "test_19";
		String input = prog + "{int i; image j; input j from @ 0; i := j[55, 77]; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"https://www.colorcombos.com/images/colors/FFFFFF.png"};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-1;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_20() throws Exception {
		String prog = "test_20";
		String input = prog + "{int i; i := cart_x[3.5, 15.3]; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-3;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_21() throws Exception {
		String prog = "test_21";
		String input = prog + "{int i; i := cart_y[10.0, .3]; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("2;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_22() throws Exception {
		String prog = "test_22";
		String input = prog + "{float i; i := polar_a[5, 7]; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("0.95054686;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_23() throws Exception {
		String prog = "test_23";
		String input = prog + "{float i; i := polar_r[5, 7]; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("8.602325;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_24() throws Exception {
		String prog = "test_24";
		String input = prog + "{int i; i := abs(5-7); show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("2;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_25() throws Exception {
		String prog = "test_25";
		String input = prog + "{float i; i := abs(55.3); show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("55.3;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_26() throws Exception {
		String prog = "test_26";
		String input = prog + "{boolean i; boolean j; j := false; i := j; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("false;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_27() throws Exception {
		String prog = "test_27";
		String input = prog + "{image i; image j[960, 480]; input j from @ 0; i := j; show i; sleep 1500;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_28() throws Exception {
		String prog = "test_28";
		String input = prog + "{filename i; filename j; input j from @ 0; i := j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_29() throws Exception {
		String prog = "test_29";
		String input = prog + "{int i; input i from @ 0; image j; j[55, 77] := i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"100"};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_30() throws Exception {
		String prog = "test_30";
		String input = prog + "{image j; alpha(j[55, 77]) := 125;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_31() throws Exception {
		String prog = "test_31";
		String input = prog + "{image j; red(j[55, 77]) := 125;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_32() throws Exception {
		String prog = "test_32";
		String input = prog + "{image j; blue(j[55, 77]) := 125;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_33() throws Exception {
		String prog = "test_33";
		String input = prog + "{int i; i := (0 != 1) ? 1 : 2; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("1;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_34() throws Exception {
		String prog = "test_34";
		String input = prog + "{int i; i := +-2; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-2;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_35() throws Exception {
		String prog = "test_35";
		String input = prog + "{float i; i := -+-7.4; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("7.4;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_36() throws Exception {
		String prog = "test_36";
		String input = prog + "{int i; i := !12345678; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-12345679;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_37() throws Exception {
		String prog = "test_37";
		String input = prog + "{int i; i := !!10953; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("10953;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_38() throws Exception {
		String prog = "test_38";
		String input = prog + "{boolean i; i := !false; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_39() throws Exception {
		String prog = "test_39";
		String input = prog + "{boolean i; i := !true; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("false;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_40() throws Exception {
		String prog = "test_40";
		String input = prog + "{int i; image j; i := j[55, 77];}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_41() throws Exception {
		String prog = "test_41";
		String input = prog + "{image j[default_width, default_height];}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_42() throws Exception {
		String prog = "test_42";
		String input = prog + "{}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_43() throws Exception {
		String prog = "test_43";
		String input = prog + "{int i; image j; input j from @ 0; i := height(j); show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {"Images/test1.jpg"};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("400;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_44() throws Exception {
		String prog = "test_44";
		String input = prog + "{int i; float j; j := 353.7; i := int(j); show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("353;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_45() throws Exception {
		String prog = "test_45";
		String input = prog + "{int i; float j; i := 860; j := float(i); show j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("860.0;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_46() throws Exception {
		String prog = "test_46";
		String input = prog + "{int i; int j; i := 860; j := blue(i); show j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("92;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_47() throws Exception {
		String prog = "test_47";
		String input = prog + "{float i; float j; i := 10.5; j := sin(i); show j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-0.8796958;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_48() throws Exception {
		String prog = "test_48";
		String input = prog + "{float i; float j; i := -5.0; j := cos(i); show j;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("0.2836622;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_49() throws Exception {
		String prog = "test_49";
		String input = prog + "{float i; float j; i := -1.7; j := atan(i); show j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("-1.0390723;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_50() throws Exception {
		String prog = "test_50";
		String input = prog + "{float i; float j; i := 4.6; j := log(i); show j;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("1.5260563;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_51() throws Exception {
		String prog = "test_51";
		String input = prog + "{show ((5 - int(7.2 / 2)) ** 2.0 * 2.0) ** 2;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("64.0;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_52() throws Exception {
		String prog = "test_52";
		String input = prog + "{show true & true;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_53() throws Exception {
		String prog = "test_53";
		String input = prog + "{show false | true;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_54() throws Exception {
		String prog = "test_54";
		String input = prog + "{show 7 & 4;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("4;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_55() throws Exception {
		String prog = "test_55";
		String input = prog + "{show 7 | 8;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("15;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_56() throws Exception {
		String prog = "test_56";
		String input = prog + "{show 76 >= 3 & 0 != 2;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_57() throws Exception {
		String prog = "test_57";
		String input = prog + "{show 7.6 == 7.6 & 3.0 < 2875.35 & 1.0 > .7 & 5.6 <= 5.6 & 4.8 >= 3.4;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_58() throws Exception {
		String prog = "test_58";
		String input = prog + "{show !(false > true) & true == true & false <= false;}";
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("true;",RuntimeLog.globalLog.toString());
	}
	
	@Test
	public void test_59() throws Exception {
		String prog = "test_59";
		String input = prog + "{int i; i := !-987125070; show i;}";	
		byte[] bytecode = genCode(input);		
		String[] commandLineArgs = {};		
		runCode(prog, bytecode, commandLineArgs);	
		show("Log:\n"+RuntimeLog.globalLog);
		assertEquals("987125069;",RuntimeLog.globalLog.toString());
	}
	
	
	
}