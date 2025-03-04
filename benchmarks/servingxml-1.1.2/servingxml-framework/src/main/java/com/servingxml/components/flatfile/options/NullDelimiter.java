/**
 *  ServingXML
 *  
 *  Copyright (C) 2006  Daniel Parker
 *    daniel.parker@servingxml.com 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 **/

package com.servingxml.components.flatfile.options;

import java.nio.charset.Charset;
import com.servingxml.util.ServingXmlException;
import com.servingxml.components.flatfile.RecordOutput;

public class NullDelimiter implements Delimiter {
  public void writeEndDelimiterTo(StringBuilder buf) {
  }
  public void writeEndDelimiterTo(RecordOutput recordOutput) {
  }
  public boolean isEmpty() {
    return true;
  }
  public char charAt(int i) {
    throw new ServingXmlException("Out of bounds");
  }
  public boolean occursIn(String s) {
    return false;
  }
  public boolean equalsString(String s) {
    return false;
  }

  public DelimiterExtractor createDelimiterExtractor(Charset charset) {
    return new DelimiterExtractor(ByteDelimiterExtractor.NULL,
                                  CharDelimiterExtractor.NULL);
  }

  public ByteDelimiterExtractor createByteDelimiterExtractor(Charset charset) {
    return ByteDelimiterExtractor.NULL;
  }

  public CharDelimiterExtractor createCharDelimiterExtractor() {
    return CharDelimiterExtractor.NULL;
  }

  public boolean forReading() {
    return true;
  }

  public boolean forWriting() {
    return true;
  }
}
