package p;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class A {
    void m() throws FileNotFoundException {
	int i;
	i = 2;
	for(int j=0;j<i;++j) {
	    if(j==4)
		throw new FileNotFoundException("");
	    ++i;
	}
	i = 4;
    }
}