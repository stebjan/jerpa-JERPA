/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info;

import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see IObserver
 */
public class SignalInfoProvider implements IObserver {

	private SignalSessionManager session;
	private SignalInfoPanel infoPanel;
	private GlobalInfoTableDataModel globalInfoDataModel;
	private ChannelsInfoTableDataModel channelsInfoDataModel;
	
	public SignalInfoProvider(SignalSessionManager session) {
		this.session = session;
		this.globalInfoDataModel = new GlobalInfoTableDataModel(session
				.getCurrentHeader());
		this.channelsInfoDataModel = new ChannelsInfoTableDataModel(session
				.getCurrentHeader());
		this.infoPanel = new SignalInfoPanel(this);
		SignalPerspectiveObservable.getInstance().attach(this);
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object state) {

	}

	@Override
	public void update(IObservable o, Object arg) {
		int msg;
		if (arg instanceof Integer) {
			msg = ((Integer) arg).intValue();
		} else {
			return;
		}

		switch (msg) {
		case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
			Header header = ((SignalProject)session.getCurrentProject()).getHeader();
			globalInfoDataModel.setHeader(header);
			channelsInfoDataModel.setHeader(header);
			infoPanel.setItemsEnabled(true);
			infoPanel.refresh();
			break;

		case SignalPerspectiveObservable.MSG_PROJECT_CLOSED:
			globalInfoDataModel.setHeader(null);
			channelsInfoDataModel.setHeader(null);
			infoPanel.setItemsEnabled(false);
			infoPanel.refresh();
		}

	}

	public JPanel getPanel() {
		return infoPanel;
	}

	protected GlobalInfoTableDataModel getGlobalInfoDataModel() {
		return globalInfoDataModel;
	}

	protected ChannelsInfoTableDataModel getChannelsInfoDataModel() {
		return channelsInfoDataModel;
	}

}
