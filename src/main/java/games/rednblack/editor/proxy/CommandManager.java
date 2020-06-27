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

package games.rednblack.editor.proxy;

import java.util.ArrayList;

import com.puremvc.patterns.proxy.BaseProxy;
import games.rednblack.editor.HyperLap2DFacade;
import games.rednblack.editor.controller.commands.EntityModifyRevertableCommand;
import games.rednblack.editor.controller.commands.RevertableCommand;
import games.rednblack.editor.controller.commands.TransactiveCommand;
import games.rednblack.editor.view.stage.Sandbox;

/**
 * Created by azakhary on 5/14/2015.
 */
public class CommandManager extends BaseProxy {
    private static final String TAG = CommandManager.class.getCanonicalName();
    public static final String NAME = TAG;

    private int cursor = -1;
    private int modifiedCursor = 0;

    private ArrayList<RevertableCommand> commands = new ArrayList<>();

    public CommandManager() {
        super(NAME);
    }

    @Override
    public void onRegister() {
        super.onRegister();
        facade = HyperLap2DFacade.getInstance();
    }

    public void addCommand(RevertableCommand revertableCommand) {
        //remove all commands after the cursor
        for(int i = commands.size()-1; i > cursor; i--) {
            commands.remove(i);
        }
        commands.add(revertableCommand);
        cursor = commands.indexOf(revertableCommand);
        if (revertableCommand instanceof EntityModifyRevertableCommand
                || revertableCommand instanceof TransactiveCommand) {
            modifiedCursor++;
        }

        updateWindowTitle();
    }

    public void undoCommand() {
        updateWindowTitle();

        if(cursor < 0) return;
        RevertableCommand command = commands.get(cursor);
        if(command.isStateDone()) {
            command.callUndoAction();
            command.setStateDone(false);
        }
        cursor--;

        if (command instanceof EntityModifyRevertableCommand
                || command instanceof TransactiveCommand) {
            modifiedCursor--;
        }
    }

    public void saveEvent() {
        modifiedCursor = 0;
        updateWindowTitle();
    }

    public void updateWindowTitle() {
        ProjectManager projectManager = facade.retrieveProxy(ProjectManager.NAME);
        projectManager.appendSaveHintTitle(modifiedCursor > 0);
    }

    public boolean isModified() {
        return modifiedCursor > 0;
    }

    public void redoCommand() {
        if(cursor + 1 >= commands.size()) return;
        RevertableCommand command = commands.get(cursor+1);
        if(!command.isStateDone()) {
            cursor++;
            command.callDoAction();
            command.setStateDone(true);
        }
    }

    public void clearHistory() {
        cursor = -1;
        commands.clear();
    }
}