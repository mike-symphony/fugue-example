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

package org.symphonyoss.s2.fugue.example.hello;

import org.slf4j.LoggerFactory;
import org.symphonyoss.s2.fugue.FugueServer;

/**
 * A simple hello world server which exposes a single servlet.
 * 
 * @author Bruce Skingle
 *
 */
public class HelloWorld extends FugueServer
{
  /**
   * Constructor.
   */
  public HelloWorld()
  {
    super("HelloWorld", 8080);
    
    register(new HelloWorldServlet());
  }

  @Override
  public FugueServer start()
  {
    super.start();
    
    openBrowser(null);
    
    return this;
  }
  
  /**
   * Main.
   * 
   * @param argv Command line arguments, ignored.
   */
  public static void main(String[] argv)
  {
    try
    {
      HelloWorld server = new HelloWorld();
      
      server.start().join();
    }
    catch (RuntimeException | InterruptedException e)
    {
      LoggerFactory.getLogger(HelloWorld.class).error("Failed", e);
      System.exit(1);
    }
  }
}
