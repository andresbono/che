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
package org.eclipse.che.api.core.rest;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.dto.server.DtoFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Roman Nikitenko
 */
@Provider
@Singleton
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        String errorMessage = exception.getLocalizedMessage();
        if (!isNullOrEmpty(errorMessage)) {
            ServiceError serviceError = DtoFactory.newDto(ServiceError.class).withMessage(errorMessage);
            return Response.serverError()
                           .entity(DtoFactory.getInstance().toJson(serviceError))
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
        return Response.serverError().build();
    }
}