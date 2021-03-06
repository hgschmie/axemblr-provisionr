/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.PrintStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "pools", description = "List active pools")
public class ListPoolsCommand extends OsgiCommandSupport {

    private PrintStream out = System.out;

    @Option(name = "-k", aliases = "--key", description = "Key for filtering a specific pool",
        required = false)
    private String key = "";

    private final ProcessEngine processEngine;

    public ListPoolsCommand(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        List<ProcessInstance> processes;
        if (key.isEmpty()) {
            processes = processEngine.getRuntimeService().createProcessInstanceQuery().list();
        } else {
            processes = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceBusinessKey(key).list();
        }

        if (processes.isEmpty()) {
            out.println("No active pools found. You can create one using provisionr:create");
            return null;
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (ProcessInstance instance : processes) {
            Pool pool = (Pool) processEngine.getRuntimeService()
                .getVariable(instance.getId(), CoreProcessVariables.POOL);
            if (pool == null) {
                continue; /* skip - this process is not a provisionr process */
            }

            String businessKey = (String) processEngine.getRuntimeService()
                .getVariable(instance.getId(), CoreProcessVariables.POOL_BUSINESS_KEY);
            if (!Objects.equal(instance.getBusinessKey(), businessKey)) {
                continue; /* ignore - this is a process started by the main pool management process */
            }

            @SuppressWarnings("unchecked")
            List<Machine> machines = (List<Machine>) processEngine.getRuntimeService()
                .getVariable(instance.getId(), CoreProcessVariables.MACHINES);

            out.println("****** Pool Description ******");
            out.println(gson.toJson(pool));

            out.println("****** List of Machines ******");
            if (machines != null) {
                out.println(gson.toJson(machines, new TypeToken<List<Machine>>() {
                }.getType()));
            }

            out.println("Pool Key: " + instance.getBusinessKey());
            out.println();
        }

        return null;
    }

    @VisibleForTesting
    void setOut(PrintStream out) {
        this.out = checkNotNull(out, "out is null");
    }

    @VisibleForTesting
    void setKey(String key) {
        this.key = checkNotNull(key, "key is null");
    }
}
