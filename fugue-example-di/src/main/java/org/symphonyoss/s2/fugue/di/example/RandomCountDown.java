/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.fugue.di.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.symphonyoss.s2.fugue.FugueServer;
import org.symphonyoss.s2.fugue.di.ComponentDescriptor;
import org.symphonyoss.s2.fugue.di.IDIContext;
import org.symphonyoss.s2.fugue.di.component.ILogComponent;
import org.symphonyoss.s2.fugue.di.component.IRandomNumberProvider;
import org.symphonyoss.s2.fugue.di.component.impl.RandomNumberProvider;
import org.symphonyoss.s2.fugue.di.component.impl.Slf4jLogComponent;

public class RandomCountDown extends FugueServer
{
  private ILogComponent log_;
  private IRandomNumberProvider random_;

  public RandomCountDown()
  {
    super("RandomCountDown", 8080);
  }
  
  @Override
  public ComponentDescriptor getComponentDescriptor()
  {
    return super.getComponentDescriptor()
        .addDependency(IRandomNumberProvider.class, (v) -> random_ = v)
        .addDependency(ILogComponent.class,         (v) -> log_ = v)
        .addStart(() -> startCountdown())
        .addStop(() -> log_.info("RandomCountDown stopped."));
  }

  @Override
  protected void registerComponents(IDIContext diContext)
  {
    diContext.register(new RandomNumberProvider())
      .register(new Slf4jLogComponent());;
  }

  public static void main(String[] args) throws InterruptedException
  {
    new RandomCountDown().start().join();
  }
  
  public void startCountdown()
  {
    log_.info("RandomCountDown started.");
    
    submit(() ->
    {
      int count = random_.nextInt(10);
      
      while(count-- > 0)
      {
        log_.info("Count " + count);
        try
        {
          Thread.sleep(1000);
        } catch (InterruptedException e)
        {
          log_.error("Interrupted", e);
        }
      }
      
      stop();
    });
  }
}
