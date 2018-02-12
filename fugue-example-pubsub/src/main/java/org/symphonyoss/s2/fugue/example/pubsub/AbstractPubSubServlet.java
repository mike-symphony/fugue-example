/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.s2.fugue.example.pubsub;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.symphonyoss.s2.fugue.ServletComponent;
import org.symphonyoss.s2.fugue.di.ComponentDescriptor;

public abstract class AbstractPubSubServlet extends ServletComponent
{
  private static final long serialVersionUID = 1L;

  private final String      name_;
  private IPubSubExmple     pubSubExample_;
  
  public AbstractPubSubServlet(String name)
  {
    name_ = name;
  }

  @Override
  public ComponentDescriptor getComponentDescriptor()
  {
    return super.getComponentDescriptor()
        .addDependency(IPubSubExmple.class, (v) -> pubSubExample_ = v);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter out = resp.getWriter();
    
    header(out);
    handleGet(out);
    footer(out);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter out = resp.getWriter();
    
    header(out);
    handlePost(out, req);
    footer(out);
  }

  public void handleGet(PrintWriter out)
  {
    notSupported(out, "GET");
  }

  public void handlePost(PrintWriter out, HttpServletRequest req)
  {
    notSupported(out, "POST");
  }

  public void notSupported(PrintWriter out, String method)
  {
    out.println("<h1>Method " + method + " is not supported</h1>");
  }
  
  public void header(PrintWriter out)
  {
    out.println("<html>");
    out.println(  "<head>");
    out.println(    "<title>" + name_ + " Servlet</title>");
    out.println(  "</head>");
    out.println(  "<body>");
    out.println(    "<h1>" + name_ + " Servlet</h1>");
    out.println(    "<h2>Status</h2>");
    out.println(    "<pre>" + pubSubExample_.getStatus() + "</pre>");
    
    
  }
  
  public void footer(PrintWriter out)
  {
    out.println(    "<hr/>");
    out.println(    "<a href=/pub>Publish</a>");
    out.println(    "<a href=/sub>Subscribe</a>");
    out.println(  "</body>");
    out.println("</html>");
  }
  
  public void error(String format, Object ...args)
  {
    pubSubExample_.appendStatus(String.format(format, args));
  }
}
