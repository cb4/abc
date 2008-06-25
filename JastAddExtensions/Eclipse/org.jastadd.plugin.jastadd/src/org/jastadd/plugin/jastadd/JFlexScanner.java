package org.jastadd.plugin.jastadd;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.jastadd.plugin.editor.highlight.JastAddColors;
import org.jastadd.plugin.jastaddj.editor.highlight.JastAddJScanner;

public class JFlexScanner extends JastAddJScanner {
	
	public JFlexScanner(JastAddColors colors) {
		super(colors);
	}

	protected Token codeToken;
	protected Token flexKeyword;
	
	/*
	protected void addRules() {
		super.addRules();
		rules.add(new MultiLineRule("%{", "%}", codeToken));
	}
	*/
	
	
	protected void createTokens() {
		super.createTokens();
		this.flexKeyword = new Token(new TextAttribute(colors.get(new RGB(0xfd, 0x96, 0x04)),
				colors.get(new RGB(0xff, 0xff, 0xff)), SWT.BOLD));
		//this.codeToken= new Token(new TextAttribute(colors.get(new RGB(0x00, 0x00, 0x00)), 
		//		colors.get(new RGB(0xfd, 0xef, 0xb0)), SWT.BOLD));
	}
	
	protected void registerWords() {
		super.registerWords();
		String[] flexKeywords = {
				"type", "yylexthrow", "init", "initthrow", "eof", "eofthrow",
				"function",
				"include", 
				"debug",
				"scanerror",
				"line",
				"column",
				"state" 
		};
		for(int i = 0; i < flexKeywords.length; i++) {
			words.addWord(flexKeywords[i], flexKeyword);
		}
	}

	
}
