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

package com.servingxml.components.flatfile.recordtype;

import com.servingxml.ioc.components.ConfigurationContext;
import com.servingxml.components.flatfile.options.FlatFileOptionsFactoryAssembler;

//  Revisit

/**
 * The <code>WhenFlatRecordTypeFactoryAssembler</code> implements an assembler for
 * assembling flat file record type objects.
 *
 * 
 * @author Daniel A. Parker (daniel.parker@servingxml.com)
 */

public class OtherwiseFlatRecordTypeFactoryAssembler extends FlatFileOptionsFactoryAssembler {
  private FlatRecordTypeFactory flatRecordTypeFactory = null;

  public void injectComponent(FlatRecordTypeFactory flatRecordTypeFactory) {
    this.flatRecordTypeFactory = flatRecordTypeFactory;
  }

  public FlatRecordTypeSelectionFactory assemble(ConfigurationContext context) {

    FlatRecordTypeSelectionFactory selection = new FlatRecordTypeSelectionFactoryImpl(flatRecordTypeFactory, "", assembleFlatFileOptions(context));
    return selection;
  }
}
