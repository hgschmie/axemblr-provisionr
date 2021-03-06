/*
 * Copyright 2012 Cisco Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.karaf.commands;

import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author Srinivasan Chikkala
 */
@Command(scope = "activiti", name = "kill", description = "Kills any active Activiti process instances")
public class KillActivitiCommand extends ActivitiCommand {

    @Argument(index = 0, name = "instanceIDs", description = "Instance IDs to kill set of active process instances",
        required = false, multiValued = true)
    private String[] instanceIDs;

    @Option(name = "-a", aliases = "--all", description = "Kill all active process instances")
    private boolean killAll;

    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine processEngine = this.getProcessEngine();
        if (processEngine == null) {
            out().println("Process Engine NOT Found!");
            return null;
        }

        RuntimeService runtimeService = processEngine.getRuntimeService();

        if (this.instanceIDs != null && this.instanceIDs.length > 0) {
            for (String instanceID : instanceIDs) {
                runtimeService.deleteProcessInstance(instanceID, "Forcefully terminating the instance");
                out().printf("Process instance %s terminated\n", instanceID);
            }
            return null;
        }

        if (!killAll) {
            out().println("Process instance IDs required or use the command with -a or --all option");
            return null;
        } else {
            out().println("Signalling all executions in all active process instances...");
            List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().asc().list();
            for (ProcessInstance pi : piList) {
                String instanceID = pi.getProcessInstanceId();
                runtimeService.deleteProcessInstance(instanceID, "Forcefully terminating the instance");
                out().printf("Process instance %s terminated\n", instanceID);
            }
        }

        return null;
    }
}
