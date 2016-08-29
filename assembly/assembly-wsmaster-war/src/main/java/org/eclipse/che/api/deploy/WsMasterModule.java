/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.deploy;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import org.eclipse.che.api.agent.server.launcher.AgentLauncher;
import org.eclipse.che.api.machine.shared.Constants;
import org.eclipse.che.inject.DynaModule;

/** @author andrew00x */
@DynaModule
public class WsMasterModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(org.eclipse.che.api.core.rest.ApiInfoService.class);
        bind(org.eclipse.che.api.project.server.template.ProjectTemplateDescriptionLoader.class).asEagerSingleton();
        bind(org.eclipse.che.api.project.server.template.ProjectTemplateRegistry.class);
        bind(org.eclipse.che.api.project.server.template.ProjectTemplateService.class);
        bind(org.eclipse.che.api.ssh.server.SshService.class);
        bind(org.eclipse.che.api.machine.server.recipe.RecipeService.class);
        bind(org.eclipse.che.api.user.server.UserService.class);
        bind(org.eclipse.che.api.user.server.ProfileService.class);
        bind(org.eclipse.che.api.user.server.PreferencesService.class);
        bind(org.eclipse.che.api.workspace.server.stack.StackLoader.class);
        bind(org.eclipse.che.api.workspace.server.stack.StackService.class);
        bind(org.eclipse.che.api.workspace.server.WorkspaceService.class);
        bind(org.eclipse.che.api.workspace.server.event.WorkspaceMessenger.class).asEagerSingleton();
        bind(org.eclipse.che.plugin.docker.machine.ext.DockerMachineExtServerChecker.class);
        bind(org.eclipse.che.plugin.docker.machine.ext.DockerMachineTerminalChecker.class);
        bind(org.eclipse.che.everrest.EverrestDownloadFileResponseFilter.class);
        bind(org.eclipse.che.everrest.ETagResponseFilter.class);

        bind(org.eclipse.che.security.oauth.OAuthAuthenticatorProvider.class)
                .to(org.eclipse.che.security.oauth.OAuthAuthenticatorProviderImpl.class);
        bind(org.eclipse.che.api.auth.oauth.OAuthTokenProvider.class)
                .to(org.eclipse.che.security.oauth.OAuthAuthenticatorTokenProvider.class);
        bind(org.eclipse.che.security.oauth.OAuthAuthenticationService.class);

        bind(org.eclipse.che.api.core.notification.WSocketEventBusServer.class);
        // additional ports for development of extensions
        Multibinder<org.eclipse.che.api.core.model.machine.ServerConf> machineServers = Multibinder.newSetBinder(binder(),
                                                                                   org.eclipse.che.api.core.model.machine.ServerConf.class,
                                                                                   Names.named("machine.docker.dev_machine.machine_servers"));
        machineServers.addBinding().toInstance(
                new org.eclipse.che.api.machine.server.model.impl.ServerConfImpl(Constants.WSAGENT_DEBUG_REFERENCE, "4403/tcp", "http",
                                                                                 null));

        bind(org.eclipse.che.api.machine.server.recipe.RecipeLoader.class);
        Multibinder.newSetBinder(binder(), String.class, Names.named("predefined.recipe.path"))
                   .addBinding()
                   .toInstance("predefined-recipes.json");


        bind(org.eclipse.che.api.workspace.server.WorkspaceValidator.class)
                .to(org.eclipse.che.api.workspace.server.DefaultWorkspaceValidator.class);

        bind(org.eclipse.che.api.workspace.server.event.MachineStateListener.class).asEagerSingleton();

        bind(org.eclipse.che.api.agent.server.AgentRegistry.class).to(org.eclipse.che.api.agent.server.impl.LocalAgentRegistryImpl.class);

        Multibinder<AgentLauncher> agentLaunchers = Multibinder.newSetBinder(binder(), AgentLauncher.class);
        agentLaunchers.addBinding().to(org.eclipse.che.api.workspace.server.launcher.WsAgentLauncherImpl.class);
        agentLaunchers.addBinding().to(org.eclipse.che.api.workspace.server.launcher.TerminalAgentLauncherImpl.class);
        agentLaunchers.addBinding().to(org.eclipse.che.api.workspace.server.launcher.SshAgentLauncherImpl.class);

        bind(org.eclipse.che.api.deploy.WsMasterAnalyticsAddresser.class);

        Multibinder<org.eclipse.che.api.machine.server.spi.InstanceProvider> machineImageProviderMultibinder =
                Multibinder.newSetBinder(binder(), org.eclipse.che.api.machine.server.spi.InstanceProvider.class);
        machineImageProviderMultibinder.addBinding().to(org.eclipse.che.plugin.docker.machine.DockerInstanceProvider.class);

        install(new org.eclipse.che.api.core.rest.CoreRestModule());
        install(new org.eclipse.che.api.core.util.FileCleaner.FileCleanerModule());
        install(new org.eclipse.che.plugin.docker.machine.local.LocalDockerModule());
        install(new org.eclipse.che.api.machine.server.MachineModule());
        install(new org.eclipse.che.api.agent.server.AgentModule());
        install(new org.eclipse.che.api.local.LocalInfrastructureModule());
        install(new org.eclipse.che.plugin.docker.machine.ext.DockerExtServerModule());
        install(new org.eclipse.che.plugin.docker.machine.ext.DockerTerminalModule());
        install(new org.eclipse.che.swagger.deploy.DocsModule());
        install(new org.eclipse.che.plugin.machine.ssh.SshMachineModule());
        install(new org.eclipse.che.plugin.docker.machine.proxy.DockerProxyModule());
        install(new org.eclipse.che.commons.schedule.executor.ScheduleModule());
    }
}
