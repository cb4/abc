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

package com.servingxml.components.recordio;

import com.servingxml.app.ServiceContext;
import com.servingxml.app.Flow;
import com.servingxml.util.NamePath;
import com.servingxml.util.record.Value;

/**
 *
 * 
 * @author  Daniel A. Parker
 */


public class RecordRestriction implements Restriction {
  private final RecordAccepter accepter;

  public RecordRestriction(RecordAccepter accepter) {
    this.accepter = accepter;
  }

  public boolean accept(ServiceContext context, Flow flow, Value value) {
    
    return accepter.accept(context, flow, value);
  }
}
