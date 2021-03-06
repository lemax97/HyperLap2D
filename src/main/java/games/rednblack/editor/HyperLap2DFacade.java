/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package games.rednblack.editor;

import com.puremvc.patterns.facade.SimpleFacade;
import com.puremvc.patterns.observer.BaseNotification;
import games.rednblack.editor.controller.StartupCommand;
import games.rednblack.editor.splash.SplashMediator;
import games.rednblack.h2d.common.view.ui.StandardWidgetsFactory;

/**
 * Created by sargis on 3/30/15.
 */
public class HyperLap2DFacade extends SimpleFacade {
    public static final String STARTUP = "startup";
    private static HyperLap2DFacade instance = null;
    private HyperLap2D hyperlap2D;

    protected HyperLap2DFacade() {
        super();
    }

    /**
     * Facade Singleton Factory method
     *
     * @return The Singleton instance of the Facade
     */
    public synchronized static HyperLap2DFacade getInstance() {
        if (instance == null) {
            instance = new HyperLap2DFacade();
            instance.registerMediator(new SplashMediator());
        }
        return instance;
    }

    public void startup(HyperLap2D hyperlap2D) {
        this.hyperlap2D = hyperlap2D;
        notifyObservers(new BaseNotification(STARTUP, null, null));
    }

    @Override
    protected void initializeFacade() {
        super.initializeFacade();
    }

    @Override
    protected void initializeController() {
        super.initializeController();
        registerCommand(STARTUP, StartupCommand.class);
    }

    @Override
    protected void initializeModel() {
        super.initializeModel();
    }

    @Override
    protected void initializeView() {
        StandardWidgetsFactory.init(this);
        super.initializeView();
    }
}
