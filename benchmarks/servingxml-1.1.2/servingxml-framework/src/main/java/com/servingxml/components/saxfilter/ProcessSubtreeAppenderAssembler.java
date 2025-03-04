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

package com.servingxml.components.saxfilter;

import com.servingxml.app.ParameterDescriptor;
import com.servingxml.util.MessageFormatter;
import com.servingxml.util.ServingXmlMessages;
import com.servingxml.util.ServingXmlException;
import com.servingxml.ioc.components.ConfigurationContext;
import com.servingxml.expr.saxpath.RestrictedMatchPattern;
import com.servingxml.expr.ExpressionException;
import com.servingxml.expr.saxpath.RestrictedMatchParser;
import com.servingxml.components.task.Task;
import com.servingxml.components.task.TaskPrefilter;
import com.servingxml.components.content.Content;
import com.servingxml.app.Environment;

public class ProcessSubtreeAppenderAssembler {

  private ParameterDescriptor[] parameterDescriptors = ParameterDescriptor.EMPTY_ARRAY;
  private String path = null;
  private Task[] tasks = new Task[0];

  public void setPath(String path) {
    this.path = path;
  }

  public void injectComponent(ParameterDescriptor[] parameterDescriptors) {
    this.parameterDescriptors = parameterDescriptors;
  }

  public void injectComponent(Task[] tasks) {
    this.tasks = tasks;
  }

  public Content assemble(ConfigurationContext context) {

    try {
      if (path == null) {
        String message = MessageFormatter.getInstance().getMessage(ServingXmlMessages.COMPONENT_ATTRIBUTE_REQUIRED,
          context.getElement().getTagName(),"path");
        throw new ServingXmlException(message);
      }
  
      if (tasks.length == 0) {
        String message = MessageFormatter.getInstance().getMessage(ServingXmlMessages.COMPONENT_ELEMENT_REQUIRED,
          context.getElement().getTagName(),"sx:task");
        throw new ServingXmlException(message);
      }

      RestrictedMatchParser parser = new RestrictedMatchParser(context.getQnameContext(),path);
      RestrictedMatchPattern expr = parser.parse();
      
      Environment env = new Environment(parameterDescriptors,context.getQnameContext());
      Content filterFactory = new ProcessSubtreeAppender(env, expr, tasks);
      return filterFactory;
    } catch (ExpressionException e) {
      String message = MessageFormatter.getInstance().getMessage(ServingXmlMessages.COMPONENT_EXPR_PARSE_FAILED,
        context.getElement().getTagName(),path);
      throw new ServingXmlException(message + ".  " + e.getMessage(), e);
    }
  }
}


