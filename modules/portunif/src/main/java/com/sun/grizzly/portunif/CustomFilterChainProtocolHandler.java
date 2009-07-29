/*
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */

package com.sun.grizzly.portunif;

import com.sun.grizzly.Context;
import com.sun.grizzly.Controller;
import com.sun.grizzly.ProtocolChain;
import com.sun.grizzly.ProtocolChainInstanceHandler;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;

/**
 * ProtocolHandler, which passes PU request processing to a custom
 * {@link FilterChain}.
 * 
 * @author Alexey Stashok
 */
public abstract class CustomFilterChainProtocolHandler implements ProtocolHandler {

    private final ProtocolChainInstanceHandler pcih;

    public CustomFilterChainProtocolHandler(ProtocolChainInstanceHandler pcih) {
        this.pcih = pcih;
    }

    public ProtocolChainInstanceHandler getProtocolChainInstanceHandler() {
        return pcih;
    }
    
    public boolean handle(Context context, PUProtocolRequest protocolRequest) 
            throws IOException {
        ProtocolChain protocolChain = pcih.poll();
        try {
            try {
                protocolChain.execute(context);
            } catch (Exception e) {
                Controller.logger().log(Level.WARNING,
                        "Error executing custom filter chain", e);
            }
        } finally {
            pcih.offer(protocolChain);
        }
        
        return false;
    }

    public boolean expireKey(SelectionKey key) {
        return true;
    }
}
