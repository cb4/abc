package bytecode;

import java.io.FileNotFoundException;

import AST.ClassDecl;
import AST.IdDecl;
import AST.InterfaceDecl;
import AST.List;
import AST.MemberClass;
import AST.MemberInterface;
import AST.MemberType;
import AST.TypeDecl;


class Attributes {
	private Parser p;
	private List exceptionList;
	private boolean isSynthetic;
	private CONSTANT_Info constantValue;

	public Attributes(Parser parser) {
		this(parser, null, null);
	}
	
	public Attributes(Parser parser, TypeDecl typeDecl, TypeDecl outerTypeDecl) {
		p = parser;
		exceptionList = new List();
		isSynthetic = false;
		
		int attributes_count = p.u2();
    p.println("    " + attributes_count + " attributes:");
		for (int j = 0; j < attributes_count; j++) {
			int attribute_name_index = p.u2();
			int attribute_length = p.u4();
			String attribute_name = p.getCONSTANT_Utf8_Info(attribute_name_index).string();
			p.println("    Attribute: " + attribute_name + ", length: "
					+ attribute_length);
			if (attribute_name.equals("Exceptions")) {
				exceptions();
			} else if(attribute_name.equals("ConstantValue") && attribute_length == 2) {
				constantValues();
			} else if (attribute_name.equals("InnerClasses")) {
				innerClasses(typeDecl, outerTypeDecl);
			} else if (attribute_name.equals("Synthetic")) {
				isSynthetic = true;
			} else {
				this.p.skip(attribute_length);
			}
		}
		
	}

	
	public void innerClasses(TypeDecl typeDecl, TypeDecl outerTypeDecl) {
		int number_of_classes = this.p.u2();
		p.println("    Number of classes: " + number_of_classes);
    p.println("    begin");
    for (int i = 0; i < number_of_classes; i++) {
      p.print("      " + i + "(" + number_of_classes + ")" +  ":");
      int inner_class_info_index = this.p.u2();
      int outer_class_info_index = this.p.u2();
      int inner_name_index = this.p.u2();
      int inner_class_access_flags = this.p.u2();
      String inner_name = "";
      if(inner_class_info_index > 0 && outer_class_info_index > 0 && inner_name_index >  0) {
        CONSTANT_Class_Info inner_class_info = this.p.getCONSTANT_Class_Info(inner_class_info_index);
        CONSTANT_Class_Info outer_class_info = this.p.getCONSTANT_Class_Info(outer_class_info_index);
        if(inner_class_info == null || outer_class_info == null) {
          System.out.println("Null");
        }
        String inner_class_name = inner_class_info.name();
        String outer_class_name = outer_class_info.name();

        this.p.println("      inner: " + inner_class_name + ", outer: " + outer_class_name);

        if (inner_name_index != 0) {
          inner_name = this.p.getCONSTANT_Utf8_Info(inner_name_index).string();
        } else {
          inner_name = inner_class_info.simpleName();
        }

        if (inner_class_info.name().equals(p.classInfo.name())) {
          p.println("      Class " + inner_class_name + " is inner");
          typeDecl.setIdDecl(new IdDecl(inner_name));
          typeDecl.setModifiers(Parser.modifiers(inner_class_access_flags & 0x041f));
          if (this.p.outerClassInfo != null && this.p.outerClassInfo.name().equals(outer_class_info.name())) {
            MemberType m = null;
            if (typeDecl instanceof ClassDecl) {
              m = new MemberClass((ClassDecl)typeDecl);
              outerTypeDecl.addBodyDecl(m);
            } else if (typeDecl instanceof InterfaceDecl) {
              m = new MemberInterface((InterfaceDecl)typeDecl);
              outerTypeDecl.addBodyDecl(m);
            }
          }
        }
        if (outer_class_info.name().equals(this.p.classInfo.name())) {
          p.println("      Class " + this.p.classInfo.name()
              + " has inner class: " + inner_class_name);
          p.println("Begin processing: " + inner_class_name);
          try {
            AST.ClassFile file = new AST.ClassFile(inner_class_name);
            if(file.exists()) {
              Parser p = new Parser(file.buffer(), file.size(), this.p.name);
              p.parse(typeDecl, outer_class_info);
            }
            else {
              System.out.println("Error: ClassFile " + inner_class_name
                  + " not found");
            }
          } catch (FileNotFoundException e) {
            System.out.println("Error: " + inner_class_name
                + " not found");
          } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
          }
          p.println("End processing: " + inner_class_name);
        }
      }

    }
    p.println("    end");
  }
	
	private void exceptions() {
		int number_of_exceptions = this.p.u2();
    p.println("      " + number_of_exceptions + " exceptions:");
		for (int i = 0; i < number_of_exceptions; i++) {
			CONSTANT_Class_Info exception = this.p.getCONSTANT_Class_Info(this.p.u2());
      p.println("        exception " + exception.name());
			exceptionList.add(exception.access());
		}
	}
	
	private void constantValues() {
		int constantvalue_index = this.p.u2();
		constantValue = this.p.getCONSTANT_Info(constantvalue_index);
	}
	
	public List exceptionList() {
		return exceptionList;
	}
	
	public CONSTANT_Info constantValue() {
		return constantValue;
	}
	
	public boolean isSynthetic() {
		return isSynthetic;
	}

}
