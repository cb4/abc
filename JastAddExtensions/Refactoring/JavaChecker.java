import AST.BytecodeParser;
import AST.CompilationUnit;
import AST.Frontend;
import AST.JavaParser;
import AST.RefactoringException;
import AST.TypeDecl;

class JavaChecker extends Frontend {

	public static void main(String args[]) throws RefactoringException {
		JavaChecker checker = new JavaChecker();
		checker.compile(args);
	}

	public boolean compile(String args[]) {
		return process(
				args,
				new BytecodeParser(),
				new JavaParser() {
					public CompilationUnit parse(java.io.InputStream is, String fileName) throws java.io.IOException, beaver.Parser.Exception {
						return new parser.JavaParser().parse(is, fileName);
					}
				}
		);
	}

}
