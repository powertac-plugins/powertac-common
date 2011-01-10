/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common.interfaces;

import org.powertac.common.Competition;
import org.powertac.common.exceptions.CompetitionFinalizationException;
import org.powertac.common.exceptions.CompetitionInitializationException;
import org.powertac.common.exceptions.CompetitionResetException;

/**
 * This interface defines base methods that are
 * invoked upon competition status changes.
 *
 * @author Carsten Block
 * @version 0.1, Date: 02.01.11
 */
public interface CompetitionBaseEvents {

  /**
   * This method is invoked upon competition initialization.
   * Each module implementing this interface should put all business logic that needs
   * to be executed *before* a competition starts into this method.
   *
   * @param competition the competition to be initialized
   * @throws org.powertac.common.exceptions.CompetitionInitializationException thrown if competition initialization fails
   */
  public void competitionBeforeStart(Competition competition) throws CompetitionInitializationException;

  /**
   * This method is invoked upon competition start.
   * Each module implementing this interface should put all business logic that needs
   * to be executed immediately *after* a competition starts into this method.
   *
   * @param competition the competition just started
   * @throws org.powertac.common.exceptions.CompetitionInitializationException thrown if competition initialization fails
   */
  public void competitionAfterStart(Competition competition) throws CompetitionInitializationException;

  /**
   * This method is invoked immediately before competition end.
   * Each module implementing this interface should put all business logic here
   * that needs to be executed immediately *before* a competition is stopped.
   *
   * @param competition the competition to be stopped
   * @throws org.powertac.common.exceptions.CompetitionFinalizationException thrown if an error occurs during competition shutdown
   */
  public void competitionBeforeStop(Competition competition) throws CompetitionFinalizationException;

  /**
   * Each module implementing this interface should put all business logic here
   * that needs to be executed immediately *after* a competition is stopped.
   *
   * @param competition the competition to be stopped
   * @throws org.powertac.common.exceptions.CompetitionFinalizationException thrown if an error occurs during competition shutdown
   */
  public void competitionAfterStop(Competition competition) throws CompetitionFinalizationException;

  /**
   * This method is invoked after the competition end if the competiton is competitionReset.
   * Each module implementing this interface should put all business logic here
   * that needs to be executed upon competitionReset of a competition.
   *
   * @param competition the competition to be reset
   * @throws org.powertac.common.exceptions.CompetitionResetException thrown if an error occurs during competition reset
   */
  public void competitionReset(Competition competition) throws CompetitionResetException;
}
