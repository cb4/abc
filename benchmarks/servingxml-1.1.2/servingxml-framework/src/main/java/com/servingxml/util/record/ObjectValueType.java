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

package com.servingxml.util.record;

import java.sql.Types;


public class ObjectValueType implements ValueType {

  public final Object[] fromStringArray(String[] sa) {
    return sa;
  }

  public Object fromString(String s) {
    return s;
  }

  public Object getSqlValue(Object[] sa) {
    Object value = null;
    if (sa.length == 1) {
      value = sa[0];
    } else if (sa.length > 1) {
      value = sa;
    }

    return value;
  }

  public int getSqlType(Object[] a) {
    return a.length > 1 ? Types.ARRAY : Types.OTHER;
  }

  public String toString(Object o) {
    return o == null ? "" : o.toString();
  }

  public Object getSqlValue(Object o) {
    return o;
  }

  public int getSqlType() {
    return Types.VARCHAR;
  }
}
