/*
 * FengGUI - Java GUIs in OpenGL (http://www.fenggui.org)
 * 
 * Copyright (C) 2005, 2006 FengGUI Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details:
 * http://www.gnu.org/copyleft/lesser.html#TOC3
 * 
 * Created on Oct 8, 2006
 * $Id: MouseExitedEvent.java 114 2006-12-12 22:06:22Z schabby $
 */
package org.fenggui.event.mouse;

import org.fenggui.IWidget;
import org.fenggui.event.Event;

public class MouseExitedEvent extends Event
{
	private IWidget entered = null;
	private IWidget exited = null;
	
	public MouseExitedEvent(IWidget entered, IWidget exited)
	{
		super(exited);
		this.entered = entered;
		this.exited = exited;
	}
	
	public IWidget getEntered()
	{
		return entered;
	}
	public IWidget getExited()
	{
		return exited;
	}
	
	
}
