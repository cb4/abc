import org.aspectj.testing.Tester;
/***
 * This test must be run with the default value of cgq, which for now is "sealed"
 */

global jpi void JP() : execution(* *());

Open class A {
	
	exhibits void JP() : execution(void foo());
	
	public static void foo() {}
}

class B {
	exhibits void JP() : execution(void foo());
	
	public static void foo() {}	
}

Open aspect asp {
	
    public static int executionCounter = 0;
	
	public static void bar() {}
	
	public static void main(String[] args) {
		A.foo();
		B.foo();
		bar();
		Tester.checkEqual(asp.executionCounter,3,"expected 3 matches but saw "+asp.executionCounter);
	}
	
	void around JP() {
		asp.executionCounter++;
		proceed();
	}
}