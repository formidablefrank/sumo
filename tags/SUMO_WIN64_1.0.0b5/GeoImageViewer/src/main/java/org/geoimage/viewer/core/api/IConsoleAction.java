/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geoimage.viewer.core.api;

import java.util.List;

/**
 *
 * @author thoorfr
 */
public interface IConsoleAction {

    public String getName();
    public String getDescription();
    /**
     * Gets the path to access the action from the menubar
     * Should be of the form "Tools|Action|myAction|"
     * @return
     */
    public String getPath();
    public boolean execute(String[] args);
    public List<Argument> getArgumentTypes();
}
